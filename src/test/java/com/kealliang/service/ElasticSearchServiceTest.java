package com.kealliang.service;

import com.kealliang.ApplicationTests;
import com.kealliang.base.ServiceMultiResult;
import com.kealliang.dto.form.RentSearch;
import org.elasticsearch.search.SearchService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * ES测试
 * @author lsr
 * @description
 * @Date 21:24 2019/2/7
 * @Param
 * @return
 */
public class ElasticSearchServiceTest extends ApplicationTests {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Test
    public void index() {
        Long houseId = 20L;
        boolean result = elasticSearchService.index(houseId);
        Assert.assertTrue(result);
    }

    @Test
    public void remove() {
        Long houseId = 20L;
        boolean result = elasticSearchService.remove(houseId);
        Assert.assertTrue(result);
    }

    @Test
    public void query() {
        RentSearch rentSearch = new RentSearch();
        rentSearch.setCityEnName("bj");
        rentSearch.setStart(0);
        rentSearch.setSize(10);
        ServiceMultiResult<Long> result = elasticSearchService.query(rentSearch);
        Assert.assertEquals(2, result.getTotal());
    }
}