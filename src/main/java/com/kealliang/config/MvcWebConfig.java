package com.kealliang.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

/**
 * @author lsr
 * @ClassName MvcWebConfig
 * @Date 2019-01-30
 * @Desc mvc相关配置
 * @Vertion 1.0
 */
@Configuration
public class MvcWebConfig implements WebMvcConfigurer, ApplicationContextAware {

    @Value("${spring.thymeleaf.cache:true}")
    private boolean thymeleafCacheEnable;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 静态资源加载配置
     * @author lsr
     * @description addResourceHandlers
     * @Date 22:08 2019/1/30
     * @Param [registry]
     * @return void
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 模板资源解析器
     * @author lsr
     * @description templateResolver
     * @Date 20:33 2019/1/30
     * @Param []
     * @return org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.thymeleaf")
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setCacheable(thymeleafCacheEnable);
        return templateResolver;
    }

    /**
     * 模板引擎
     * @author lsr
     * @description templateEngine
     * @Date 20:35 2019/1/30
     * @Param []
     * @return org.thymeleaf.spring5.SpringTemplateEngine
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        // 支持spring EL表达式
        templateEngine.setEnableSpringELCompiler(true);

        // 支持springSecurity方言
        SpringSecurityDialect securityDialect = new SpringSecurityDialect();
        templateEngine.addDialect(securityDialect);
        return templateEngine;
    }

    /** 
     * 视图解析器
     * @author lsr
     * @description viewResolver
     * @Date 20:37 2019/1/30
     * @Param []
     * @return org.thymeleaf.spring5.view.ThymeleafViewResolver
     */
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        return viewResolver;
    }
}
