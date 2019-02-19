package com.kealliang.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

/**
 * @author lsr
 * @ClassName FileManageConfig
 * @Date 2019-02-01
 * @Desc 文件上传配置 SpringBoot2.x之后不需要这些配置也能上传（只需配置spring.servlet.multipart参数）
 * @Vertion 1.0
 */
@Configuration
@ConditionalOnClass({Servlet.class, StandardServletMultipartResolver.class, MultipartConfigElement.class})
@ConditionalOnProperty(prefix = "spring.servlet.multipart", name = "enable", matchIfMissing = true)
@EnableConfigurationProperties(MultipartProperties.class)
public class WebFileUploadConfig {

    private final MultipartProperties multipartProperties;

    @Autowired
    public WebFileUploadConfig(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    /** 
     * 上传配置，缺失才创建
     * @author lsr
     * @description multipartConfigElement
     * @Date 23:05 2019/2/1
     * @Param []
     * @return javax.servlet.MultipartConfigElement
     */
    @Bean
    @ConditionalOnMissingBean
    public MultipartConfigElement multipartConfigElement() {
        return this.multipartProperties.createMultipartConfig();
    }
    
    /** 
     * 注册解析器，解析文件用
     * @author lsr
     * @description 
     * @Date 23:08 2019/2/1
     * @Param 
     * @return 
     */
    @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    @ConditionalOnMissingBean(MultipartResolver.class)
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
        multipartResolver.setResolveLazily(this.multipartProperties.isResolveLazily());
        return multipartResolver;
    }

    /** 
     * 七牛云相关配置
     * @author lsr
     * @description configuration
     * @Date 23:35 2019/2/1
     * @Param []
     * @return com.qiniu.storage.Configuration
     */
    @Bean
    public com.qiniu.storage.Configuration configuration() {
        // 华南机房配置
        return new com.qiniu.storage.Configuration(Zone.zone2());
    }
    
    /** 
     * 七牛云上传管理器
     * @author lsr
     * @description uploadManager
     * @Date 23:36 2019/2/1
     * @Param []
     * @return com.qiniu.storage.UploadManager
     */
    @Bean
    public UploadManager uploadManager() {
        return new UploadManager(configuration());
    }

    @Value("${cloud.qiniu.accessKey}")
    private String accessKey;
    @Value("${cloud.qiniu.secretKey}")
    private String secretKey;

    /** 
     * 上传认证
     * @author lsr
     * @description auth
     * @Date 0:08 2019/2/2
     * @Param []
     * @return com.qiniu.util.Auth
     */
    @Bean
    public Auth auth() {
        return Auth.create(accessKey, secretKey);
    }
    
    /** 
     * 七牛云空间管理
     * @author lsr
     * @description bucketManager
     * @Date 0:20 2019/2/2
     * @Param []
     * @return com.qiniu.storage.BucketManager
     */
    @Bean
    public BucketManager bucketManager() {
        return new BucketManager(auth(), configuration());
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setLenient() // 宽容模式
                .create();
    }

}
