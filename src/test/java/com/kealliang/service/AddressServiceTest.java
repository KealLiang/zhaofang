package com.kealliang.service;

import com.kealliang.ApplicationTests;
import com.kealliang.base.ServiceResult;
import com.kealliang.base.search.BaiduMapLocation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class AddressServiceTest extends ApplicationTests {

    @Autowired
    private AddressService addressService;

    @Test
    public void getBaiduMapLocation() {
        String city = "佛山";
        String address = "佛山市顺德区北滘镇林上路2号";
        ServiceResult<BaiduMapLocation> result = addressService.getBaiduMapLocation(city, address);
        System.err.println(result.getResult());
        Assert.assertTrue(result.isSuccess());
    }
}