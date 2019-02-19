package com.kealliang.service;

import com.kealliang.base.ServiceMultiResult;
import com.kealliang.base.ServiceResult;
import com.kealliang.base.search.BaiduMapLocation;
import com.kealliang.dto.SubwayDTO;
import com.kealliang.dto.SubwayStationDTO;
import com.kealliang.dto.SupportAddressDTO;
import com.kealliang.entity.SupportAddress;

import java.util.List;
import java.util.Map;

/**
 * @author lsr
 * @ClassName AddressService
 * @Date 2019-02-03
 * @Desc 地址服务
 * @Vertion 1.0
 */
public interface AddressService {

    String BAIDU_MAP_KEY = "mgXswKgCkGQrcsoOOWQSnyjrsoTgoLmT";
    String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoder/v2/?";

     ServiceMultiResult<SupportAddressDTO> findAllCities();

    ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityEnName);

    List<SubwayDTO> findAllSubwayByCity(String cityEnName);

    List<SubwayStationDTO> findAllStationBySubway(Long subwayId);

    Map<SupportAddress.Level,SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    ServiceResult<SubwayDTO> findSubway(Long subwayId);

    ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId);

    ServiceResult<SupportAddressDTO> findCity(String cityEnName);

    /** 
     * 根据城市及具体地址获取百度地图的经纬度
     * @author lsr
     * @description getBaiduMapLocation
     * @Date 21:46 2019/2/16
     * @Param [city, address]
     * @return com.kealliang.base.ServiceResult<com.kealliang.base.search.BaiduMapLocation>
     */
    ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address);
}
