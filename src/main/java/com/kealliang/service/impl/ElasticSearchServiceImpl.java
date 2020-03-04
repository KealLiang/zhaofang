package com.kealliang.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.kealliang.base.HouseSort;
import com.kealliang.base.ServiceMultiResult;
import com.kealliang.base.ServiceResult;
import com.kealliang.base.constant.IndexConstant;
import com.kealliang.base.constant.RentValueBlock;
import com.kealliang.base.mq.HouseIndexMessage;
import com.kealliang.base.search.BaiduMapLocation;
import com.kealliang.base.search.HouseIndexTemplate;
import com.kealliang.base.search.HouseSuggest;
import com.kealliang.dto.HouseBucketDTO;
import com.kealliang.dto.form.MapSearch;
import com.kealliang.dto.form.RentSearch;
import com.kealliang.entity.House;
import com.kealliang.entity.HouseDetail;
import com.kealliang.entity.HouseTag;
import com.kealliang.entity.SupportAddress;
import com.kealliang.repository.HouseDetailRepository;
import com.kealliang.repository.HouseRepository;
import com.kealliang.repository.HouseTagRepository;
import com.kealliang.repository.SupportAddressRepository;
import com.kealliang.service.AddressService;
import com.kealliang.service.ElasticSearchService;
import com.kealliang.service.HouseService;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lsr
 * @ClassName ElasticSearchServiceImpl
 * @Date 2019-02-07
 * @Desc
 * @Vertion 1.0
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

    @Autowired
    private HouseService houseService;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /** 
     * 消费index_topic消息
     * @author lsr
     * @description handleMessage
     * @Date 18:44 2019/2/10
     * @Param [content]
     * @return void
     */
//    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String content) {
        try {
            HouseIndexMessage message = objectMapper.readValue(content, HouseIndexMessage.class);

            switch (message.getOperation()) {
                case HouseIndexMessage.INDEX: createOrUpdateIndex(message); break;
                case HouseIndexMessage.REMOVE: removeIndex(message); break;
                default: LOG.warn("KF: no qualified operation for: {}", content); break;
            }

        } catch (IOException e) {
            LOG.error("KF: cannot parse json for: {}", content, e);
        }
    }

    @Override
    public boolean index(Long houseId) {
        if (USE_KAFKA) {
            index(houseId, 0);
        } else {
            HouseIndexMessage msg = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, 0);
            createOrUpdateIndex(msg);
        }
        return true;
    }

    /** 
     * 向kafka发送index消息
     * @author lsr
     * @description index
     * @Date 18:44 2019/2/10
     * @Param [houseId, retry]
     * @return void
     */
    private void index(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            LOG.error("KF: retry index over {} times for house {}, please get check!", HouseIndexMessage.MAX_RETRY, houseId);
            return;
        }
        HouseIndexMessage msg = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
        try {
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            LOG.error("KF: json encode error for {}", msg);
        }
    }

    /** 
     * 创建index
     * @author lsr
     * @description createIndex
     * @Date 20:47 2019/2/7
     * @Param [houseTemplate]
     * @return boolean
     */
    private boolean createIndex(HouseIndexTemplate houseTemplate) {
        if (!wrapperSuggest(houseTemplate)) {
            return false;
        }

        try {
            IndexResponse response = esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(houseTemplate), XContentType.JSON)
                    .get();
            if (response.status() == RestStatus.CREATED) {
                LOG.info("ES: house index {} was created!", houseTemplate.getHouseId());
                return true;
            } else {
                LOG.warn("ES: creating house index {} was failed!", houseTemplate.getHouseId());
                return false;
            }
        } catch (JsonProcessingException e) {
            LOG.error("ES: creating index encounter error!", e);
            return false;
        }
    }

    /** 
     * 更新index
     * @author lsr
     * @description updateIndex
     * @Date 20:47 2019/2/7
     * @Param [esId, houseTemplate]
     * @return boolean
     */
    private boolean updateIndex(String esId, HouseIndexTemplate houseTemplate) {
        if (!wrapperSuggest(houseTemplate)) {
            return false;
        }

        try {
            UpdateResponse response = esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
                    .setDoc(objectMapper.writeValueAsBytes(houseTemplate), XContentType.JSON)
                    .get();
            if (response.status() == RestStatus.OK) {
                LOG.info("ES: house index {} was updated!", houseTemplate.getHouseId());
                return true;
            } else {
                LOG.warn("ES: updating house index {} was failed!", houseTemplate.getHouseId());
                return false;
            }
        } catch (JsonProcessingException e) {
            LOG.error("ES: updating index encounter error!", e);
            return false;
        }
    }

    /** 
     * 删除再创建index
     * @author lsr
     * @description dropAndCreateIndex
     * @Date 20:47 2019/2/7
     * @Param [houseTemplate]
     * @return boolean
     */
    private boolean dropAndCreateIndex(long totalHit, HouseIndexTemplate houseTemplate) {
        // 5.x之后只能通过deleteByQuery来删除
        DeleteByQueryRequestBuilder source = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(IndexConstant.HOUSE_ID, houseTemplate.getHouseId()))
                .source(INDEX_NAME);

        LOG.info("ES: house {} index was deleted!", houseTemplate.getHouseId());

        BulkByScrollResponse response = source.get();
        if (totalHit != response.getDeleted()) {
            LOG.warn("ES: expected delete {} but {} was deleted!", totalHit, response.getDeleted());
            return false;
        } else {
            return createIndex(houseTemplate);
        }
    }


    @Override
    public boolean remove(Long houseId) {
        if (USE_KAFKA) {
            remove(houseId, 0);
        } else {
            HouseIndexMessage msg = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, 0);
            removeIndex(msg);
        }
        return true;
    }

    @Override
    public ServiceMultiResult<Long> query(RentSearch rentSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(
                QueryBuilders.termQuery(IndexConstant.CITY_EN_NAME, rentSearch.getCityEnName())
        );
        if (!StringUtils.isEmpty(rentSearch.getRegionEnName()) && !RentSearch.ALL_REGION.equals(rentSearch.getRegionEnName())) {
            boolQuery.filter(
                    QueryBuilders.termQuery(IndexConstant.REGION_EN_NAME, rentSearch.getRegionEnName())
            );
        }

        // 面积
        RentValueBlock area = RentValueBlock.matchArea(rentSearch.getAreaBlock());
        if (!RentValueBlock.ALL.equals(area)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(IndexConstant.AREA);
            if (area.getMax() > 0) {
                rangeQuery.lte(area.getMax());
            }
            if (area.getMin() > 0) {
                rangeQuery.gte(area.getMin());
            }
            boolQuery.filter(rangeQuery);
        }

        // 价格
        RentValueBlock price = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
        if (!RentValueBlock.ALL.equals(price)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(IndexConstant.PRICE);
            if (price.getMax() > 0) {
                rangeQuery.lte(price.getMax());
            }
            if (price.getMin() > 0) {
                rangeQuery.gte(price.getMin());
            }
            boolQuery.filter(rangeQuery);
        }

        // 其他
        if (rentSearch.getDirection() > 0) {
            boolQuery.filter(QueryBuilders.termQuery(IndexConstant.DIRECTION, rentSearch.getDirection()));
        }
        if (rentSearch.getRentWay() > -1) {
            boolQuery.filter(QueryBuilders.termQuery(IndexConstant.RENT_WAY, rentSearch.getRentWay()));
        }

        // 关键词（将被分词）
        boolQuery.must(
                QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
                        IndexConstant.TITLE,
                        IndexConstant.TRAFFIC,
                        IndexConstant.DISTRICT,
                        IndexConstant.ROUND_SERVICE,
                        IndexConstant.SUBWAY_LINE_NAME,
                        IndexConstant.SUBWAY_STATION_NAME)
        );

        SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(HouseSort.getSortKey(rentSearch.getOrderBy()), SortOrder.fromString(rentSearch.getOrderDirection()))
                .setFrom(rentSearch.getStart())
                .setSize(rentSearch.getSize());

        LOG.debug("ES: prepared: {}", requestBuilder.toString());


        List<Long> houseIds = new ArrayList<>();
        SearchResponse searchResponse = requestBuilder.get();
        if (!RestStatus.OK.equals(searchResponse.status())) {
            LOG.warn("ES: search from es has encounter problems! {}", searchResponse);
            return new ServiceMultiResult<>(0, houseIds);
        }

        for (SearchHit hit : searchResponse.getHits()) {
            houseIds.add(Long.valueOf(String.valueOf(hit.getSourceAsMap().get(IndexConstant.HOUSE_ID))));
        }
        return new ServiceMultiResult<>(searchResponse.getHits().getTotalHits(), houseIds);
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix) {
        // 搜索索引定义的suggest的字段，获取5条提示
        CompletionSuggestionBuilder suggestionBuilder = SuggestBuilders.completionSuggestion("suggest")
                .prefix(prefix)
                .size(5);

        // 这里的name随便取
        String suggestionName = "autocomplete";
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion(suggestionName, suggestionBuilder);

        SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .suggest(suggestBuilder);
        LOG.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        // 注意使用这个泛型接收的前提是我们知道一开始addSuggestion的就是这个
        Suggest.Suggestion<CompletionSuggestion.Entry> suggestions = response.getSuggest().getSuggestion(suggestionName);

        // 处理返回的suggestions，去重等
        int maxSuggest = 0;
        Set<String> suggestSet = new HashSet<>();

        for (CompletionSuggestion.Entry item : suggestions.getEntries()) {
            if (item.getOptions().isEmpty()) {
                continue;
            }

            for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                String tip = option.getText().string();
                if (suggestSet.contains(tip)) {
                    continue;
                }
                suggestSet.add(tip);
                maxSuggest++;
            }

            if (maxSuggest > 5) {
                break;
            }
        }
        return ServiceResult.of(Lists.newArrayList(suggestSet));
    }

    @Override
    public ServiceResult<Long> aggregateDistrictHouses(String cityEnName, String regionEnName, String district) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(IndexConstant.CITY_EN_NAME, cityEnName))
                .filter(QueryBuilders.termQuery(IndexConstant.REGION_EN_NAME, regionEnName))
                .filter(QueryBuilders.termQuery(IndexConstant.DISTRICT, district));

        SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addAggregation(
                        AggregationBuilders.terms(IndexConstant.AGG_DISTRICT) // 聚合结果字段名
                        .field(IndexConstant.DISTRICT) // 对哪个字段聚合
                ).setSize(0); // 不需要查原始数据，只要聚合数据
        LOG.debug(searchRequestBuilder.toString());

        SearchResponse response = searchRequestBuilder.get();
        if (!RestStatus.OK.equals(response.status())) {
            LOG.warn("ES: failed to aggregate for {}", IndexConstant.AGG_DISTRICT);
            return ServiceResult.of(0L);
        }
        Terms terms = response.getAggregations().get(IndexConstant.AGG_DISTRICT);
        if (!CollectionUtils.isEmpty(terms.getBuckets())) {
            return ServiceResult.of(terms.getBucketByKey(district).getDocCount());
        }
        return ServiceResult.of(0L);
    }

    @Override
    public ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(IndexConstant.CITY_EN_NAME, cityEnName));

        AggregationBuilder aggregationBuilder = AggregationBuilders.terms(IndexConstant.AGG_REGION) // 起个名字
                .field(IndexConstant.REGION_EN_NAME);// 以哪个字段聚合

        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addAggregation(aggregationBuilder);
        LOG.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        List<HouseBucketDTO> buckets = new ArrayList<>();
        if (response.status() != RestStatus.OK) {
            LOG.warn("ES: aggregate status is not ok for {}", requestBuilder);
            return new ServiceMultiResult<>(0, buckets);
        }

        Terms terms = response.getAggregations().get(IndexConstant.AGG_REGION);
        for (Terms.Bucket bucket : terms.getBuckets()) {
            buckets.add(new HouseBucketDTO(bucket.getKeyAsString(), bucket.getDocCount()));
        }

        return new ServiceMultiResult<>(response.getHits().getTotalHits(), buckets);
    }

    @Override
    public ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(IndexConstant.CITY_EN_NAME, cityEnName));

        SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(HouseSort.getSortKey(orderBy), SortOrder.fromString(orderDirection))
                .setFrom(start)
                .setSize(size);

        List<Long> houseIds = new ArrayList<>();
        SearchResponse response = requestBuilder.get();
        if (response.status() != RestStatus.OK) {
            LOG.warn("ES: search status is not ok for {}", requestBuilder);
            return new ServiceMultiResult<>(0, houseIds);
        }

        for (SearchHit hit : response.getHits()) {
            houseIds.add(Long.valueOf(hit.getSourceAsMap().get(IndexConstant.HOUSE_ID).toString()));
        }

        return new ServiceMultiResult<>(response.getHits().getTotalHits(), houseIds);
    }

    @Override
    public ServiceMultiResult<Long> mapQuery(MapSearch mapSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery(IndexConstant.CITY_EN_NAME, mapSearch.getCityEnName()));

        boolQuery.filter(
                QueryBuilders.geoBoundingBoxQuery("location") // 这里传入索引的对应字段名
                .setCorners( // 这里要注意顺序
                        new GeoPoint(mapSearch.getLeftLatitude(), mapSearch.getLeftLongitude()),
                        new GeoPoint(mapSearch.getRightLatitude(), mapSearch.getRightLongitude())
                )
        );

        SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(HouseSort.getSortKey(mapSearch.getOrderBy()), SortOrder.fromString(mapSearch.getOrderDirection()))
                .setFrom(mapSearch.getStart())
                .setSize(mapSearch.getSize());

        SearchResponse response = requestBuilder.get();
        List<Long> houseIds = new ArrayList<>();
        if (response.status() != RestStatus.OK) {
            LOG.warn("ES: search status is not ok for {}", requestBuilder);
            return new ServiceMultiResult<>(0, houseIds);
        }

        for (SearchHit hit : response.getHits()) {
            houseIds.add(Long.valueOf(hit.getSourceAsMap().get(IndexConstant.HOUSE_ID).toString()));
        }

        return new ServiceMultiResult<>(response.getHits().getTotalHits(), houseIds);
    }

    /**
     * 向kafka发送remove消息
     * @author lsr
     * @description remove
     * @Date 18:43 2019/2/10
     * @Param [houseId, retry]
     * @return void
     */
    private void remove(Long houseId, int retry) {
        if (retry > HouseIndexMessage.MAX_RETRY) {
            LOG.error("KF: retry index over {} times for house {}, please get check!", HouseIndexMessage.MAX_RETRY, houseId);
            return;
        }
        HouseIndexMessage msg = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
        try {
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            LOG.error("KF: json encode error for {}", msg);
        }
    }

    /** 
     * 对接ES的index方法
     * @author lsr
     * @description createOrUpdateIndex
     * @Date 1:33 2019/2/10
     * @Param [message]
     * @return void
     */
    private void createOrUpdateIndex(HouseIndexMessage message) {
        Long houseId = message.getHouseId();

        House house = houseRepository.findById(houseId).orElse(null);
        if (house == null) {
            LOG.error("ES: house {} does not exists!", houseId);
            exceptionMessage(message, houseId);
            return;
        }

        // 获取template模板
        HouseIndexTemplate houseTemplate = null;
        try {
            houseTemplate = getHouseTemplate(house);
        } catch (IOException e) {
            LOG.error("ES: get map location failed");
            exceptionMessage(message, houseId);
            return;
        }

        // 先用houseId查询，根据返回的结果决策
        SearchRequestBuilder builder = esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(IndexConstant.HOUSE_ID, houseId));

        LOG.debug(builder.toString());

        boolean success;

        SearchResponse response = builder.get();
        long totalHits = response.getHits().getTotalHits();
        if (totalHits == 0) {
            // create
            success = createIndex(houseTemplate);
        } else if (totalHits == 1) {
            // update
            String esId = response.getHits().getAt(0).getId();
            success = updateIndex(esId, houseTemplate);
        } else {
            // 存了多条document，有问题
            // delete & create
            success = dropAndCreateIndex(totalHits, houseTemplate);
        }

        if (success) {
            LOG.debug("ES: index {} 成功", houseId);
        }
    }

    /** 
     * 对接ES的remove方法
     * @author lsr
     * @description removeIndex
     * @Date 1:33 2019/2/10
     * @Param [message]
     * @return void
     */
    private void removeIndex(HouseIndexMessage message) {
        Long houseId = message.getHouseId();

        if (houseId == null) {
            LOG.error("ES: houseId must not be null!", houseId);
            exceptionMessage(message, houseId);
            return;
        }

        DeleteByQueryRequestBuilder source = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(IndexConstant.HOUSE_ID, houseId))
                .source(INDEX_NAME);

        LOG.info("ES: house {} index was deleted!", houseId);

        BulkByScrollResponse response = source.get();
        LOG.warn("ES: deleted total {} docs", response.getDeleted());
    }


    /**
     * 获取索引template模板
     * @author lsr
     * @description getHouseTemplate
     * @Date 20:26 2019/2/7
     * @Param [houseId]
     * @return com.kealliang.base.search.HouseIndexTemplate
     */
    private HouseIndexTemplate getHouseTemplate(House house) throws IOException {
        long houseId = house.getId();

        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseId);
        if (houseDetail == null) {
            LOG.error("ES: house {} detail does not exists!", houseId);
        }
        List<HouseTag> houseTags = houseTagRepository.findAllByHouseId(houseId);

        // 获取百度地图地理位置信息
        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(house.getCityEnName(), SupportAddress.Level.CITY.getValue());
        SupportAddress region = supportAddressRepository.findByEnNameAndLevel(house.getRegionEnName(), SupportAddress.Level.REGION.getValue());
        String address = city.getCnName() + region.getCnName() + house.getStreet() + house.getDistrict() + houseDetail.getDetailAddress();
        ServiceResult<BaiduMapLocation> mapLocation = addressService.getBaiduMapLocation(city.getCnName(), address);
        if (!mapLocation.isSuccess()) {
            throw new IOException("获取百度地图位置信息失败");
        }

        HouseIndexTemplate template = new HouseIndexTemplate();
        BeanUtils.copyProperties(house, template);
        BeanUtils.copyProperties(houseDetail, template);
        template.setLocation(mapLocation.getResult());

        if (!CollectionUtils.isEmpty(houseTags)) {
            List<String> tagStr = houseTags.stream().map(houseTag -> houseTag.getName())
                    .collect(Collectors.toList());
            template.setTags(tagStr);
        }
        return template;
    }

    /** 
     * 填充suggest
     * @author lsr
     * @description wrapperSuggest
     * @Date 19:08 2019/2/11
     * @Param [indexTemplate]
     * @return boolean
     */
    private boolean wrapperSuggest(HouseIndexTemplate indexTemplate) {
        // 发起请求对字段分词
        AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(
                esClient, AnalyzeAction.INSTANCE, INDEX_NAME,
                indexTemplate.getTitle(),
                indexTemplate.getLayoutDesc(), indexTemplate.getRoundService(),
                indexTemplate.getSubwayLineName(), indexTemplate.getSubwayStationName(),
                indexTemplate.getDescription());

        requestBuilder.setAnalyzer("ik_smart");

        // 分词结果
        AnalyzeResponse response = requestBuilder.get();
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        if (tokens == null) {
            LOG.warn("ES: cannot analyze token for house {}", indexTemplate.getHouseId());
            return false;
        }

        List<HouseSuggest> suggests = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            // 排除数字类型 | 小于2字符的结果
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }

            suggests.add(new HouseSuggest(token.getTerm()));
        }

        // 定制化：小区名自动补全
        suggests.add(new HouseSuggest(indexTemplate.getDistrict()));

        indexTemplate.setSuggest(suggests);
        return true;
    }


    /**
     * 发送失败消息
     * @author lsr
     * @description exceptionMessage
     * @Date 22:17 2019/2/16
     * @Param [message, houseId]
     * @return void
     */
    private void exceptionMessage(HouseIndexMessage message, Long houseId) {
        if (USE_KAFKA) {
            remove(houseId, message.getRetry() + 1);
        }
    }

}
