package com.coco.terminal.cocobizlog.service.es.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.coco.terminal.cocobizlog.config.EsInitFactory;
import com.coco.terminal.cocobizlog.entity.ErrorLogAlarmEntity;
import com.coco.terminal.cocobizlog.service.es.AlarmBaseService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * 错误日志报警
 *
 * @author ckli01
 * @date 2019-08-15
 */
@Component
public class ErrorLogAlarmServiceImpl implements AlarmBaseService {


    @Override
    public Map<String, List<ErrorLogAlarmEntity>> logAlarm(Long startTime, Long endTime) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("coco-elk-error-log*");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);

        searchSourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.DESC));

        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("serviceName")
                .field("fields.serviceName.keyword")
                .subAggregation(
                        AggregationBuilders.dateHistogram("byMin")
                                .field("@timestamp")
                                .dateHistogramInterval(DateHistogramInterval.minutes(1))
                                .timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai")))
                                .minDocCount(1));


        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

        MatchAllQueryBuilder matchAllQueryBuilder = new MatchAllQueryBuilder();
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("@timestamp");
        rangeQueryBuilder.gte(startTime);
        rangeQueryBuilder.lte(endTime);
        rangeQueryBuilder.format("epoch_millis");

        queryBuilder.must(matchAllQueryBuilder).must(rangeQueryBuilder);

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.docValueField("@timestamp");
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        SearchResponse searchResponse = EsInitFactory.getClient().search(searchRequest);
        List<Aggregation> list = searchResponse.getAggregations().asList();
        return dealResByParsedStringTerms(list);

    }


    /**
     * 根据 字段 分组
     *
     * @param list
     * @return
     */
    private Map<String, List<ErrorLogAlarmEntity>> dealResByParsedStringTerms(List<Aggregation> list) {
        Map<String, List<ErrorLogAlarmEntity>> map = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(list)) {
            for (Aggregation aggregation : list) {
                ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggregation;

                if (!CollectionUtils.isEmpty(parsedStringTerms.getBuckets())) {
                    List<ParsedStringTerms.ParsedBucket> buckets = (List<ParsedStringTerms.ParsedBucket>) parsedStringTerms.getBuckets();

                    for (ParsedStringTerms.ParsedBucket bucket : buckets) {
                        // 服务名称
                        String serviceName = (String) bucket.getKey();
                        List<ErrorLogAlarmEntity> alarmEntities = dealResByParsedDateHistogram(bucket.getAggregations().asList(), serviceName);
                        if (!CollectionUtils.isEmpty(alarmEntities)) {
                            map.put(serviceName, alarmEntities);
                        }
                    }
                }
            }
        }
        return map;
    }


    /**
     * 根据 时间 分组
     *
     * @param list
     * @param serviceName
     * @return
     */
    private List<ErrorLogAlarmEntity> dealResByParsedDateHistogram(List<Aggregation> list, String serviceName) {
        List<ErrorLogAlarmEntity> entities = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(list)) {
            for (Aggregation aggregation : list) {
                ParsedDateHistogram dateHistogram = (ParsedDateHistogram) aggregation;

                if (!CollectionUtils.isEmpty(dateHistogram.getBuckets())) {
                    List<ParsedDateHistogram.ParsedBucket> buckets = (List<ParsedDateHistogram.ParsedBucket>) dateHistogram.getBuckets();

                    for (ParsedDateHistogram.ParsedBucket bucket : buckets) {
                        DateTime dateTime = ((DateTime) bucket.getKey()).withZone(DateTimeZone.forID("Asia/Shanghai"));

                        ErrorLogAlarmEntity errorLogAlarmEntity = new ErrorLogAlarmEntity();

                        if(bucket.getDocCount()<50){
                            continue;
                        }
                        // 数量
                        errorLogAlarmEntity.setCount(bucket.getDocCount());
                        errorLogAlarmEntity.setServiceName(serviceName);

                        DateTimeFormatterFactory dateTimeFormatterFactory = new DateTimeFormatterFactory();
                        dateTimeFormatterFactory.setPattern("yyyy-MM-dd HH:mm:ss");
                        dateTimeFormatterFactory.setIso(DateTimeFormat.ISO.DATE_TIME);
                        DateTimeFormatter dateTimeFormatter = dateTimeFormatterFactory.createDateTimeFormatter();
                        String time = dateTime.toLocalDateTime().toString(dateTimeFormatter);
                        errorLogAlarmEntity.setAlarmTime(time);


                        entities.add(errorLogAlarmEntity);
                    }
                }
            }
        }

        return entities;

    }


}

    
    
  