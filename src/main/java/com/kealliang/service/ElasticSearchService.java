package com.kealliang.service;

import com.kealliang.base.ServiceMultiResult;
import com.kealliang.base.ServiceResult;
import com.kealliang.dto.HouseBucketDTO;
import com.kealliang.dto.form.MapSearch;
import com.kealliang.dto.form.RentSearch;

import java.util.List;

/**
 * @author lsr
 * @ClassName ElasticSearchService
 * @Date 2019-02-07
 * @Desc ES服务接口
 * @Vertion 1.0
 */
public interface ElasticSearchService {

    String INDEX_NAME = "zhaofang";
    String INDEX_TYPE = "house";
    String INDEX_TOPIC = "house_topic";

    /** 使用kafka的开关，若为false需同时注释 @KafkaListener 注解 */
    boolean USE_KAFKA = false;

    /** 
     * 索引目标房源
     * @author lsr
     * @description index
     * @Date 20:18 2019/2/7
     * @Param [houseId]
     * @return void
     */
    boolean index(Long houseId);

    /** 
     * 移除索引
     * @author lsr
     * @description remove
     * @Date 20:19 2019/2/7
     * @Param [houseId]
     * @return void
     */
    boolean remove(Long houseId);

    /** 
     * 从ES查询id集合
     * @author lsr
     * @description query
     * @Date 18:47 2019/2/10
     * @Param [rentSearch]
     * @return com.kealliang.base.ServiceMultiResult<java.lang.Long>
     */
    ServiceMultiResult<Long> query(RentSearch rentSearch);

    /** 
     * 查询搜索建议
     * @author lsr
     * @description suggest
     * @Date 18:58 2019/2/11
     * @Param [prefix]
     * @return com.kealliang.base.ServiceResult<java.util.List<java.lang.String>>
     */
    ServiceResult<List<String>> suggest(String prefix);
    
    /** 
     * 聚合特定小区的房源数
     * @author lsr
     * @description aggregateDistrictHouses
     * @Date 20:48 2019/2/11
     * @Param [cityEnName, regionEnName, district]
     * @return com.kealliang.base.ServiceResult<java.lang.Long>
     */
    ServiceResult<Long> aggregateDistrictHouses(String cityEnName, String regionEnName, String district);

    /** 
     * 聚合城市数据
     * @author lsr
     * @description mapAggregate
     * @Date 12:22 2019/2/16
     * @Param [cityEnName]
     * @return com.kealliang.base.ServiceMultiResult<com.kealliang.dto.HouseBucketDTO>
     */
    ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName);

    /** 
     * 城市级别查询
     * @author lsr
     * @description mapQuery
     * @Date 23:55 2019/2/16
     * @Param [cityEnName, orderBy, orderDirection, start, size]
     * @return com.kealliang.base.ServiceMultiResult<java.lang.Long>
     */
    ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy, String orderDirection, int start, int size);

    /**
     * 精确范围数据查询
     * @author lsr
     * @description mapQuery
     * @Date 0:23 2019/2/17
     * @Param [mapSearch]
     * @return com.kealliang.base.ServiceMultiResult<java.lang.Long>
     */
    ServiceMultiResult<Long> mapQuery(MapSearch mapSearch);
}
