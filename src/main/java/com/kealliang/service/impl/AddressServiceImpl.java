package com.kealliang.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kealliang.base.ServiceMultiResult;
import com.kealliang.base.ServiceResult;
import com.kealliang.base.search.BaiduMapLocation;
import com.kealliang.dto.SubwayDTO;
import com.kealliang.dto.SubwayStationDTO;
import com.kealliang.dto.SupportAddressDTO;
import com.kealliang.entity.Subway;
import com.kealliang.entity.SubwayStation;
import com.kealliang.entity.SupportAddress;
import com.kealliang.repository.SubwayRepository;
import com.kealliang.repository.SubwayStationRepository;
import com.kealliang.repository.SupportAddressRepository;
import com.kealliang.service.AddressService;
import com.kealliang.utils.ModelMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lsr
 * @ClassName AddressServiceImpl
 * @Date 2019-02-03
 * @Desc
 * @Vertion 1.0
 */
@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger LOG = LoggerFactory.getLogger(AddressServiceImpl.class);

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {
        List<SupportAddress> cities = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDTO> addressDTOS = getSupportAddressDTOS(cities);

        return new ServiceMultiResult<>(addressDTOS.size(), addressDTOS);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityEnName) {
        List<SupportAddress> regions = supportAddressRepository.findAllByLevelAndBelongTo(SupportAddress.Level.REGION.getValue(), cityEnName);
        List<SupportAddressDTO> addressDTOS = getSupportAddressDTOS(regions);

        return new ServiceMultiResult<>(addressDTOS.size(), addressDTOS);
    }

    @Override
    public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
        List<Subway> subways = subwayRepository.findAllByCityEnName(cityEnName);
        return subways.stream().map(subway -> {
            SubwayDTO subwayDTO = new SubwayDTO();
            BeanUtils.copyProperties(subway, subwayDTO);
            return subwayDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SubwayStationDTO> findAllStationBySubway(Long subwayId) {
        List<SubwayStation> subwayStations = subwayStationRepository.findAllBySubwayId(subwayId);
        return subwayStations.stream().map(subwayStation -> {
            SubwayStationDTO stationDTO = new SubwayStationDTO();
            BeanUtils.copyProperties(subwayStation, stationDTO);
            return stationDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddressDTO> result = new HashMap<>();

        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY
                .getValue());
        SupportAddress region = supportAddressRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());

        result.put(SupportAddress.Level.CITY, modelMapper.map(city, SupportAddressDTO.class));
        result.put(SupportAddress.Level.REGION, modelMapper.map(region, SupportAddressDTO.class));
        return result;
    }

    @Override
    public ServiceResult<SubwayDTO> findSubway(Long subwayId) {
        if (subwayId == null) {
            return ServiceResult.notFound();
        }
        Subway subway = subwayRepository.findById(subwayId).orElse(null);
        if (subway == null) {
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(subway, SubwayDTO.class));
    }

    @Override
    public ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId) {
        if (subwayStationId == null) {
            return ServiceResult.notFound();
        }
        SubwayStation station = subwayStationRepository.findById(subwayStationId).orElse(null);
        if (station == null) {
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(station, SubwayStationDTO.class));
    }

    @Override
    public ServiceResult<SupportAddressDTO> findCity(String cityEnName) {
        if (cityEnName == null) {
            return ServiceResult.notFound();
        }

        SupportAddress supportAddress = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        if (supportAddress == null) {
            return ServiceResult.notFound();
        }

        SupportAddressDTO addressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
        return ServiceResult.of(addressDTO);
    }

    @Override
    public ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address) {
        String encodeAddress;
        String encodeCity;

        try {
            encodeAddress = URLEncoder.encode(address, "UTF-8");
            encodeCity = URLEncoder.encode(city, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("MAP: error to encode house and address");
            return new ServiceResult<>(false, "error to encode house and address");
        }

        HttpClient httpClient = HttpClients.createDefault();
        StringBuilder sb = new StringBuilder(BAIDU_MAP_GEOCONV_API);
        sb.append("address=").append(encodeAddress).append("&")
                .append("city=").append(encodeCity).append("&")
                .append("output=json&")
                .append("ak=").append(BAIDU_MAP_KEY);

        HttpGet get = new HttpGet(sb.toString());
        try {
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return new ServiceResult<>(false, "MAP: can not get baidu map location");
            }
            String s = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonNode jsonNode = objectMapper.readTree(s);
            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                return new ServiceResult<>(false, "MAP: error to get map location for status " + status);
            }
            BaiduMapLocation baiduMapLocation = new BaiduMapLocation();
            JsonNode jsonLocation = jsonNode.get("result").get("location");
            baiduMapLocation.setLongitude(jsonLocation.get("lng").asDouble());
            baiduMapLocation.setLatitude(jsonLocation.get("lat").asDouble());
            return ServiceResult.of(baiduMapLocation);

        } catch (IOException e) {
            LOG.error("MAP: error to fetch baidu map api");
            return new ServiceResult<>(false, "error to fetch baidu map api");
        }

    }


    private List<SupportAddressDTO> getSupportAddressDTOS(List<SupportAddress> cities) {
        return cities.stream().map(city -> {
            SupportAddressDTO addressDTO = new SupportAddressDTO();
            BeanUtils.copyProperties(city, addressDTO);
            return addressDTO;
        }).collect(Collectors.toList());
    }
}
