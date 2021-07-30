package com.coco.terminal.cocobizlog.search;

import com.coco.terminal.cocobizlog.bean.LogEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.coco.terminal.cocobizlog.config.EsInitFactory;
import com.coco.terminal.cocobizlog.entity.EsEntity;
import com.coco.terminal.cocobizlog.service.es.EsBaseService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author ckli01
 * @date 2019-03-28
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class EsServiceTest {

    @Autowired
    private EsBaseService esBaseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void bulkTest() throws JsonProcessingException {
        EsEntity esEntity = new EsEntity();
        esEntity.setIndex("lck_test_bulk");
        esEntity.setType("lck_test_bulk");

        esEntity.setId(null);


        esEntity.setDoc(objectMapper.writeValueAsString(esEntity));

        EsEntity esEntity1 = new EsEntity();
        esEntity1.setIndex("lck_test_bulk");
        esEntity1.setType("lck_test_bulk");
        esEntity1.setId("lckj_2");

        esEntity1.setDoc(objectMapper.writeValueAsString(esEntity));

//        long t1= System.currentTimeMillis();
//        boolean ts = esBaseService.bulkAdd(Lists.newArrayList(esEntity, esEntity1));
//        long t2= System.currentTimeMillis();


        int i = 0;

        while (i < 10) {
            long t1 = System.currentTimeMillis();
            boolean ts = esBaseService.bulkAdd(Lists.newArrayList(esEntity, esEntity1));
            long t2 = System.currentTimeMillis();
            log.info("t2-t1: {}", t2 - t1);
            i++;

        }


//        Assert.assertTrue(ts);
    }


    @Test
    public void search() {
        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.types()

        searchRequest.indices("coco-biz-log");
        searchRequest.types("coco-biz-log-type");


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);


//        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("entity", "经营品牌:");
// 启动模糊查询
//        matchQueryBuilder.fuzziness(Fuzziness.AUTO);
// 在匹配查询上设置前缀 长度选项
//        matchQueryBuilder.prefixLength(3);
// 设置最大扩展选项以控制查询的模糊过程
//        matchQueryBuilder.maxExpansions(10);


//        searchSourceBuilder.query(matchQueryBuilder);


        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders
                .matchPhraseQuery("entityJson.bizlog_prefix", "id: 45887");

        MatchPhraseQueryBuilder matchPhraseQueryBuilder1 = QueryBuilders
                .matchPhraseQuery("entityJson.bizlog_prefix", "code: YST16100000001335");

        TermQueryBuilder termEventQueryBuilder = QueryBuilders.termQuery("event", "2");
        TermQueryBuilder termModuleQueryBuilder = QueryBuilders.termQuery("module", "1");

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("date");
        rangeQueryBuilder.lt(1568773279998L);
        rangeQueryBuilder.gt(1568773271998L);


        BoolQueryBuilder queryBuilder1 = QueryBuilders.boolQuery()
                .should(matchPhraseQueryBuilder)
                .should(matchPhraseQueryBuilder1);

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must(queryBuilder1)
                .must(termModuleQueryBuilder)
                .must(termEventQueryBuilder);


        searchSourceBuilder.query(queryBuilder);


        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10);

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


//        String[] includeFields = new String[] {"title", "user", "innerObject.*"};
//        String[] excludeFields = new String[] {"_type"};
//        searchSourceBuilder.fetchSource(includeFields, excludeFields);

        try {
            SearchResponse searchResponse = EsInitFactory.getClient().search(searchRequest);


            List<LogEntity> esEntities = Lists.newArrayList();
            long s = searchResponse.getHits().totalHits;
            for (SearchHit searchHit : searchResponse.getHits()) {
                Map source = searchHit.getSourceAsMap();
                LogEntity entity = objectMapper.readValue(objectMapper.writeValueAsString(source), LogEntity.class);
                esEntities.add(entity);
            }

            System.out.println(1);
        } catch (IOException e) {

            Assert.fail();

        }


    }


    @Test
    public void searchByMin() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("coco-elk-error-log*");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);

        searchSourceBuilder.sort(new FieldSortBuilder("@timestamp").order(SortOrder.DESC));

//        DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("heihei")
//                .field("@timestamp")
//                .dateHistogramInterval(DateHistogramInterval.minutes(1))
//                .timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai")))
//                .minDocCount(1)
//                ;

//        TermsAggregationBuilder aggregationBuilder= AggregationBuilders.terms("fields.serviceName")
//        .subAggregation(AggregationBuilders.dateHistogram("heihei")
//                .field("@timestamp")
//                .dateHistogramInterval(DateHistogramInterval.minutes(1))
//                .timeZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai")))
//                .minDocCount(1))
//        ;

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
        rangeQueryBuilder.gte(1565838736794L);
        rangeQueryBuilder.lte(1565839636794L);
        rangeQueryBuilder.format("epoch_millis");

        queryBuilder.must(matchAllQueryBuilder).must(rangeQueryBuilder);

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.docValueField("@timestamp");
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        try {
            SearchResponse searchResponse = EsInitFactory.getClient().search(searchRequest);
            List<Aggregation> list = searchResponse.getAggregations().asList();
            if (!CollectionUtils.isEmpty(list)) {
                for (Aggregation aggregation : list) {
                    ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggregation;

                    if (!CollectionUtils.isEmpty(parsedStringTerms.getBuckets())) {
                        List<ParsedStringTerms.ParsedBucket> buckets = (List<ParsedStringTerms.ParsedBucket>) parsedStringTerms.getBuckets();

                        for (ParsedStringTerms.ParsedBucket bucket : buckets) {
                            String str = (String) bucket.getKey();
                            System.out.println(1);
//                            List<Aggregations> subAggs=bucket.getAggregations();
//                            System.out.println(servicveName);
                        }
                    }
                }

                ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram) list.get(0);
                List<ParsedDateHistogram.ParsedBucket> parsedBuckets = (List<ParsedDateHistogram.ParsedBucket>) parsedDateHistogram.getBuckets();
                parsedBuckets.forEach(t -> {
                    String str = new String((byte[]) t.getKey());
                    System.out.println(t.getKey() + " : " + t.getDocCount());
                });
//                list.stream().collect(Collectors.toMap(Aggregation::))
            }

            List<LogEntity> esEntities = Lists.newArrayList();
            long s = searchResponse.getHits().totalHits;
            for (SearchHit searchHit : searchResponse.getHits()) {
                Map source = searchHit.getSourceAsMap();
                LogEntity entity = objectMapper.readValue(objectMapper.writeValueAsString(source), LogEntity.class);
                esEntities.add(entity);
            }
            System.out.println(1);
        } catch (IOException e) {
            Assert.fail();
        }
    }


}

    
    
  