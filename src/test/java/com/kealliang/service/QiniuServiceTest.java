package com.kealliang.service;

import com.kealliang.ApplicationTests;
import com.qiniu.http.Response;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @author lsr
 * @ClassName QiniuServiceTest
 * @Date 2019-02-05
 * @Desc 七牛服务测试
 * @Vertion 1.0
 */
public class QiniuServiceTest extends ApplicationTests {

    @Autowired
    private QiniuService qiniuService;

    @Test
    public void uploadTest() {
        String fileName = "D:\\coding\\core-hr\\zhaofang\\temp\\云中树.jpg";
        File file = new File(fileName);
        Assert.assertTrue(file.exists());

        Response response = qiniuService.upload(file);
        Assert.assertTrue(response.isOK());
        System.out.println(response);
    }

    @Test
    public void deleteTest() {
        String key = "FjJ9qtV2kzlF9fQw7aQzj5krPhi8";
        Response response = qiniuService.delete(key);
        Assert.assertTrue(response.isOK());
    }

}
