package com.kealliang.service;

import com.kealliang.base.ServiceMultiResult;
import com.kealliang.base.ServiceResult;
import com.kealliang.dto.HouseDTO;
import com.kealliang.dto.form.DatatableSearch;
import com.kealliang.dto.form.HouseForm;
import com.kealliang.dto.form.MapSearch;
import com.kealliang.dto.form.RentSearch;

/**
 * @author lsr
 * @ClassName HouseService
 * @Date 2019-02-03
 * @Desc 房屋服务
 * @Vertion 1.0
 */
public interface HouseService {

    ServiceResult<HouseDTO> save(HouseForm houseForm);

    ServiceResult<HouseDTO> findCompleteOne(Long id);

    ServiceResult update(HouseForm houseForm);

    ServiceResult removePhoto(Long id);

    ServiceResult updateCover(Long coverId, Long targetId);

    ServiceResult addTag(Long houseId, String tag);

    ServiceResult removeTag(Long houseId, String tag);

    ServiceResult updateStatus(Long id, int status);

    ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);

    ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);

    /** 
     * 全地图查询
     * @author lsr
     * @description wholeMapQuery
     * @Date 23:50 2019/2/16
     * @Param [mapSearch]
     * @return com.kealliang.base.ServiceMultiResult<com.kealliang.dto.HouseDTO>
     */
    ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch);

    /** 
     * 矩形区域查询
     * @author lsr
     * @description boundMapQuery
     * @Date 0:19 2019/2/17
     * @Param [mapSearch]
     * @return com.kealliang.base.ServiceMultiResult<com.kealliang.dto.HouseDTO>
     */
    ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch);
}
