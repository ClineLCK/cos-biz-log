package com.coco.terminal.cocobizlog.service.es.impl;

import com.alibaba.fastjson.JSONObject;
import com.coco.terminal.cocobizlog.bean.LogEntityBaseSearchDTO;
import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.bean.LogEntitySearchDTO;
import com.coco.terminal.cocobizlog.bean.PagingResult;
import com.coco.terminal.cocobizlog.enums.LogEventFieldTypeEnum;
import com.coco.terminal.cocobizlog.enums.LogFieldQueryTypeEnum;
import com.coco.terminal.cocobizlog.service.es.EsBaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.coco.terminal.cocobizlog.config.EsInitFactory;
import com.coco.terminal.cocobizlog.entity.EsEntity;
import com.coco.terminal.cocobizlog.util.EsIndexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * es 操作业务日志
 *
 * @author ckli01
 * @date 2019-03-28
 */
@Service("BizLogEsService")
@Slf4j
public class BizLogEsServiceImpl implements EsBaseService {


    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public boolean bulkAdd(List<EsEntity> esEntities) {
        try {
            if (CollectionUtils.isEmpty(esEntities)) {
                log.info("BizLogEsServiceImpl bulk esEntities is empty");
            } else {
                BulkRequest request = new BulkRequest();
                esEntities.forEach(t ->
                        {
                            IndexRequest index = new IndexRequest(t.getIndex(), t.getType(), t.getId());
                            index.source(t.getDoc(), XContentType.JSON);
                            request.add(index);
                        }
                );

                BulkResponse bulkResponse = EsInitFactory.getClient().bulk(request);
                //4、处理响应
                if (!bulkResponse.hasFailures()) {
                    return true;
                } else {
                    log.error("BizLogEsServiceImpl bulk result error for : {}",
                            bulkResponse.buildFailureMessage());
                }
            }

        } catch (Exception e) {
            log.error("BizLogEsServiceImpl bulk error for : {}", e.getMessage(), e);
        }
        return false;
    }


    @Override
    public PagingResult<LogEntityDO> list(LogEntitySearchDTO logEntitySearchDTO) throws Exception {
        String[] index = EsIndexUtil.indexes(logEntitySearchDTO.getBaseSearchDTO());
        log.info("es will search for index: {}", index);

        BoolQueryBuilder logEntityQuery = logEntityQuery(logEntitySearchDTO.getBaseSearchDTO());
        BoolQueryBuilder prefixSuffixLogEntityQuery = prefixSuffixLogEntityQuery(
                logEntitySearchDTO.getPrefixSuffix());
        BoolQueryBuilder entityJsonLogEntityQuery = entityJsonLogEntityQuery(
                logEntitySearchDTO.getEntityJson());

        // 查询条件整合
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(logEntityQuery)
                .must(prefixSuffixLogEntityQuery)
                .must(entityJsonLogEntityQuery);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.types(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);

        searchSourceBuilder.query(boolQueryBuilder);

        log.info("index: {} will find from {} size {}", index, logEntitySearchDTO.getStartRow(),
                logEntitySearchDTO.getPageSize());

        searchSourceBuilder.from(logEntitySearchDTO.getStartRow().intValue());
        searchSourceBuilder.size(logEntitySearchDTO.getPageSize());

        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchSourceBuilder.sort(
                SortBuilders.fieldSort("utctimestamp").unmappedType("long").order(SortOrder.DESC));

        PagingResult<LogEntityDO> pageList = new PagingResult<>();
        pageList.setPageIndex(logEntitySearchDTO.getCurrentPage());
        pageList.setPageSize(logEntitySearchDTO.getPageSize());

        log.info("index -> {}  search sql is -> {}", index, searchRequest.source());
        SearchResponse searchResponse = EsInitFactory.getClient().search(searchRequest);
        List<LogEntityDO> esEntities = Lists.newArrayList();
        long s = searchResponse.getHits().totalHits;
        pageList.setTotal(s);
        pageList.setData(esEntities);

        log.info("index: {} hits: {}", index, s);

        if (searchResponse.getHits() != null) {
            for (SearchHit searchHit : searchResponse.getHits()) {
                LogEntityDO entity = objectMapper
                        .readValue(searchHit.getSourceAsString(), LogEntityDO.class);
                esEntities.add(entity);
            }
        }
        return pageList;
    }

    @Override
    public Boolean initIndex(List<LogEntityDO> logEntityDOS) {
        for (LogEntityDO logEntityDO : logEntityDOS) {
            String index = EsIndexUtil.index(logEntityDO);
            try {
                Response response = EsInitFactory.getClient().getLowLevelClient().performRequest("HEAD", index);
                boolean exist = response.getStatusLine().getReasonPhrase().equals("OK");

                if (exist) {
                    // todo    更新
                    log.info("BizLogEsServiceImpl initIndex for {} will update ...", index);
                    PutMappingRequest request = new PutMappingRequest("twitter");

                } else {
                    //   新建
                    log.info("BizLogEsServiceImpl initIndex for {} will create ...", index);
                    Map<Object, Object> filedsMap = logEntityDO.getEntityJson();
                    Map<String, Object> rootMap = Maps.newHashMap();
                    Map<String, Object> esMapping = Maps.newHashMap();
                    // entityJson 字段类型配置以及子属性挂钩
                    Map<String, Object> entityJsonTypeMap = Maps.newHashMap();
                    entityJsonTypeMap.put("type", "object");
                    entityJsonTypeMap.put("properties", esMapping);

                    // entityJson 字段设置
                    Map<String, Object> entityJsonMap = Maps.newHashMap();
                    entityJsonMap.put("entityJson", entityJsonTypeMap);

                    rootMap.put("properties", entityJsonMap);

                    filedsMap.forEach((k, v) -> {
                        packageStringToMap((String) k, esMapping);
                    });
                    createIndex(index, rootMap);
                }
            } catch (Exception e) {
                log.error("BizLogEsServiceImpl initIndex failed for index  : {} , error is {}", index, e.getMessage(), e);
                return false;
            }
        }
        return true;
    }


    /**
     * 将 字符串组装为 es 识别的 map 结构
     *
     * @param str
     * @param map
     */
    private void packageStringToMap(String str, Map<String, Object> map) {
        int firstPointIndex = str.indexOf(".");
        if (firstPointIndex < 0) {
            map.put(str, textEsFiledType());
        } else {
            Map<String, Object> childMap;
            Map<String, Object> filedMap;
            String key = str.substring(0, firstPointIndex);
            String nextKey = str.substring(firstPointIndex + 1);

            boolean nested = false;
            String realKey;
            // 判断是否 是 nested 结构
            if (nested(key)) {
                nested = true;
                realKey = splitByLogQueryField(key, LogFieldQueryTypeEnum.LIST);
            } else {
                realKey = key;
            }

            if (map.containsKey(realKey)) {
                filedMap = (Map<String, Object>) map.get(realKey);
                childMap = (Map<String, Object>) filedMap.get("properties");
            } else {
                childMap = Maps.newHashMap();
                filedMap = Maps.newHashMap();
                filedMap.put("properties", childMap);

                // 判断是否 是 nested 结构
                if (nested) {
                    filedMap.put("type", "nested");
                    map.put(realKey, filedMap);
                } else {
                    map.put(realKey, filedMap);
                }
            }
            // 判断是否 还有子级 结构
            if (nextKey.contains(".")) {
                packageStringToMap(nextKey, childMap);
            } else {
                childMap.put(nextKey, textEsFiledType());
            }
        }
    }


    /**
     * text es 字段类型
     *
     * @return
     */
    private Map<String, Object> textEsFiledType() {
        Map<String, Object> textMap = Maps.newHashMap();
        textMap.put("type", "text");
        return textMap;
    }

    /**
     * 设置分片
     *
     * @param request
     */
    private void buildSetting(CreateIndexRequest request) {
        request.settings(Settings.builder().put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2));
    }

    /**
     * 创建索引
     *
     * @param index
     * @param mapping
     * @return
     */
    private boolean createIndex(String index, Map<String, Object> mapping) {
        try {
            log.info("BizLogEsServiceImpl createIndex index is {} ,mapping is {} ", index, JSONObject.toJSONString(mapping));
            CreateIndexRequest request = new CreateIndexRequest();
            request.index(index);
            buildSetting(request);
            request.mapping(index, mapping);
            EsInitFactory.getClient().indices().create(request);
        } catch (IOException e) {
            log.error("BizLogEsServiceImpl createIndex error for : {}", e.getMessage(), e);
            return false;
        }
        log.info("BizLogEsServiceImpl createIndex success for Index : {}", index);
        return true;
    }


    /**
     * 将基础查询条件里面所有值转换为es 支持查询语句
     *
     * @param searchDTO
     * @return
     * @throws Exception
     */
    private BoolQueryBuilder logEntityQuery(LogEntityBaseSearchDTO searchDTO) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        Field[] fields = searchDTO.getClass().getDeclaredFields();
        // 父类
        Class superClazz = searchDTO.getClass().getSuperclass();
        Field[] superFields = superClazz.getDeclaredFields();
        List<Field> fieldList = Lists.newArrayList(fields);
        fieldList.addAll(Lists.newArrayList(superFields));

        for (Field field : fieldList) {

            if (field.getName().equals("startDate") || field.getName().equals("endDate") || field.getName().equals("searchMap")) {
                continue;
            }

            field.setAccessible(true);
            Object object = field.get(searchDTO);
            if (Objects.nonNull(object)) {
                if (object instanceof String) {
                    if (((String) object).contains(",")) {
                        String[] strs = ((String) object).split(",");
                        List<String> list = Lists.newArrayList(strs);
                        boolQueryBuilder.must(QueryBuilders.termsQuery(field.getName() + ".keyword", list));
                    } else {
                        boolQueryBuilder.must(QueryBuilders.boolQuery()
                                .should(QueryBuilders.termQuery(field.getName() + ".keyword", object))
                                .should(QueryBuilders.termQuery(field.getName(), object)));
                    }
                } else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(field.getName(), object));
                }
            }
        }

        if (searchDTO.getStartDate() != null || searchDTO.getEndDate() != null) {
            RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery("date");

            if (searchDTO.getStartDate() != null) {
                queryBuilder.gte(searchDTO.getStartDate().getTime());
            }
            if (searchDTO.getEndDate() != null) {
                queryBuilder.lte(searchDTO.getEndDate().getTime());
            }
            boolQueryBuilder.must(queryBuilder);
        }


        if (!CollectionUtils.isEmpty(searchDTO.getSearchMap())) {
            searchDTO.getSearchMap().forEach((k, v) -> {
                boolQueryBuilder.must(
                        QueryBuilders.boolQuery()
                                .should(QueryBuilders.termsQuery("entityJson." + k, v))
                                .should(QueryBuilders.termsQuery("entityJson." + k + ".keyword", v))
                );
            });
        }

        return boolQueryBuilder;
    }


    /**
     * 将自定义 前后缀 条件里面 prefix 、 suffix  所有值转换为es 支持查询语句
     *
     * @param prefixSuffix
     * @return
     * @throws Exception
     */
    private BoolQueryBuilder prefixSuffixLogEntityQuery(List<String> prefixSuffix) throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (!CollectionUtils.isEmpty(prefixSuffix)) {
            boolQueryBuilder.should(QueryBuilders
                    .termsQuery("entityJson." + LogEventFieldTypeEnum.PREFIX.getFiledName() + ".keyword", prefixSuffix));
            boolQueryBuilder.should(QueryBuilders
                    .termsQuery("entityJson." + LogEventFieldTypeEnum.PREFIX.getFiledName(), prefixSuffix));
        }
        return boolQueryBuilder;
    }


    /**
     * 将自定义条件里面 所有值转换为es 支持查询语句
     *
     * @param entityJson
     * @return
     * @throws Exception
     */
    private BoolQueryBuilder entityJsonLogEntityQuery(Map<String, Object> entityJson)
            throws Exception {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (!CollectionUtils.isEmpty(entityJson)) {
            for (Map.Entry<String, Object> entry : entityJson.entrySet()) {

                if (!StringUtils.isEmpty(entry.getValue())) {
                    if (nested(entry.getKey())) {
                        String path = splitByLogQueryField(entry.getKey(), LogFieldQueryTypeEnum.LIST);
                        int lastPoint = path.lastIndexOf(".");
                        String prefix = path.substring(0, lastPoint);
                        boolQueryBuilder.must(QueryBuilders.nestedQuery(prefix, QueryBuilders.matchPhraseQuery(path, entry.getValue()), ScoreMode.None));
                    } else {
                        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
                    }
                } else {
                    if (nested(entry.getKey())) {
                        String path = splitByLogQueryField(entry.getKey(), LogFieldQueryTypeEnum.LIST);

                        int lastPoint = path.lastIndexOf(".");
                        String prefix = path.substring(0, lastPoint);
                        String fieldName = path.substring(lastPoint + 1);

                        boolQueryBuilder.must(QueryBuilders.nestedQuery(prefix, QueryBuilders.existsQuery(path), ScoreMode.None));
                    } else {
                        boolQueryBuilder.must(QueryBuilders.existsQuery(entry.getKey()));
                    }
//                    boolQueryBuilder.mustNot(QueryBuilders.existsQuery(entry.getKey()));
                }
            }
        }
        return boolQueryBuilder;
    }


    /**
     * 判断 字段 是否 LogFieldQueryTypeEnum.LIST 内容
     *
     * @param field
     * @return
     */
    private boolean nested(String field) {
        if (!StringUtils.isEmpty(field) && field.contains(LogFieldQueryTypeEnum.LIST.getType())) {
            return true;
        }
        return false;
    }

    /**
     * 字段 根据 LogFieldQueryTypeEnum 分割
     *
     * @param field
     * @return
     */
    private String splitByLogQueryField(String field, LogFieldQueryTypeEnum logFieldQueryTypeEnum) {
        String[] str = field.split(logFieldQueryTypeEnum.getType());
        StringBuilder stringBuilder = new StringBuilder();
        for (String aa : str) {
            stringBuilder.append(aa);
        }
        return stringBuilder.toString();
    }

}

    
    
  