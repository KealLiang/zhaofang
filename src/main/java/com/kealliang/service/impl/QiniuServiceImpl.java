package com.kealliang.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kealliang.controller.admin.ManageController;
import com.kealliang.service.QiniuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author lsr
 * @ClassName QiniuServiceImpl
 * @Date 2019-02-01
 * @Desc
 * @Vertion 1.0
 */
@Service
public class QiniuServiceImpl implements QiniuService, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(QiniuServiceImpl.class);

    @Value("${cloud.qiniu.bucket}")
    private String bucket;
    @Value("${cloud.qiniu.expires}")
    private long expires;

    /** 上传重试次数 */
    public int retry = 3;

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private Auth auth;

    @Autowired
    private Gson gson;


    // 自定义配置
    private StringMap putPolicy;

    @Override
    public Response upload(File file) {
        if (file == null) {
            return null;
        }
        Response response = null;
        try {
            do {
                response = uploadManager.put(file, file.getName(), getUploadToken());
            } while (retry-- > 0 && response.needRetry());
            //解析上传成功的结果
            DefaultPutRet putRet = gson.fromJson(response.bodyString(), DefaultPutRet.class);
            LOG.info("上传七牛云成功，key[{}] hash[{}]", putRet.key, putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            LOG.error("上传七牛云出错：{}", r.toString());
        }
        return response;
    }

    @Override
    public Response upload(InputStream inputStream) {
        Response response = null;
        try {
            do {
                response = uploadManager.put(inputStream, null, getUploadToken(), null, null);
            } while (retry-- > 0 && response.needRetry());
            //解析上传成功的结果
            DefaultPutRet putRet = gson.fromJson(response.bodyString(), DefaultPutRet.class);
            LOG.info("上传七牛云成功，key[{}] hash[{}]", putRet.key, putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            LOG.error("上传七牛云出错：{}", r.toString());
        }
        return response;
    }

    @Override
    public Response delete(String token) {
        Response response = null;
        try {
            do {
                response = bucketManager.delete(bucket, token);
            } while (retry-- > 0 && response.needRetry());
        } catch (QiniuException e) {
            LOG.error("七牛云删除出错：{}", e.toString());
        }
        return response;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 自定义七牛云的返回结构
        putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":\"$(fsize)\",\"width\":\"$(imageInfo.width)\",\"height\":\"$(imageInfo.height)\"}");
    }

    /** 
     * 获取上传凭证
     * @author lsr
     * @description getUploadToken
     * @Date 0:11 2019/2/2
     * @Param []
     * @return java.lang.String
     */
    private String getUploadToken() {
        return auth.uploadToken(bucket, null, expires, putPolicy);
    }


}
