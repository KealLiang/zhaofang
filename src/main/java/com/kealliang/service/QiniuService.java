package com.kealliang.service;

import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * @author lsr
 * @ClassName QiniuService
 * @Date 2019-02-01
 * @Desc 七牛云服务service
 * @Vertion 1.0
 */
public interface QiniuService {

    Response upload(File file);

    Response upload(InputStream inputStream);

    Response delete(String token);
}
