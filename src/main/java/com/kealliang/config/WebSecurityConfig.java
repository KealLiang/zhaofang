package com.kealliang.config;

import com.kealliang.security.AuthProvider;
import com.kealliang.security.LoginAuthFailHandler;
import com.kealliang.security.LoginUrlEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lsr
 * @ClassName WebSecurityConfig
 * @Date 2019-01-31
 * @Desc
 * @Vertion 1.0
 */
@Component
@EnableWebSecurity
@ConfigurationProperties(prefix = "auth.pattern")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /** 
     * 从yaml中读取list不能直接使用Value注解；
     * 需要通过bean注入的方式，三步：
     *  1、bean上加ConfigurationProperties注解配置前缀
     *  2、实例化各个List（名称要和yml中对应）
     *  3、并提供set方法才能顺利注入
     */
    //    @Value("${auth.pattern.public}")
    private List<String> publicPattern = new ArrayList<>();
    private List<String> adminPattern = new ArrayList<>();
    private List<String> loginPattern = new ArrayList<>();

    public void setPublicPattern(List<String> publicPattern) {
        this.publicPattern = publicPattern;
    }

    public void setAdminPattern(List<String> adminPattern) {
        this.adminPattern = adminPattern;
    }

    public void setLoginPattern(List<String> loginPattern) {
        this.loginPattern = loginPattern;
    }

    /**
     * http权限控制
     * @author lsr
     * @description configure
     * @Date 21:09 2019/1/31
     * @Param [http]
     * @return void
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 资源访问权限
        String[] publics = this.publicPattern.toArray(new String[]{});
        String[] admins = this.adminPattern.toArray(new String[]{});
        String[] logins = this.loginPattern.toArray(new String[]{});
        http.authorizeRequests()
                .antMatchers(publics).permitAll()
                .antMatchers(admins).hasRole("ADMIN")
                .antMatchers(logins).hasAnyRole("ADMIN", "USER")
                .and()
                .formLogin()
                .loginProcessingUrl("/login") // 配置登录处理入口
                .failureHandler(loginAuthFailHandler())
                .and()
                .logout()
                .logoutUrl("/logout") // 处理还是用原来的
                .logoutSuccessUrl("/logout/page")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(loginUrlEntryPoint()) // 未登录状态跳转的页面
                .accessDeniedPage("/403") // 无权访问的页面
                .and();

        // 关闭csrf防御策略
        http.csrf().disable();
        // i-frame需要同源策略
        http.headers().frameOptions().sameOrigin();
    }

    /**
     * 自定义认证策略
     * @author lsr
     * @description configGlobal
     * @Date 22:33 2019/1/31
     * @Param [managerBuilder]
     * @return void
     */
    @Autowired
    public void configGlobal(AuthenticationManagerBuilder managerBuilder) throws Exception {
//        managerBuilder.inMemoryAuthentication()
//                .withUser("admin").password("admin")
//                .roles("ADMIN")
//                .and();
        managerBuilder.authenticationProvider(authProvider()).eraseCredentials(true);
    }

    @Bean
    public AuthProvider authProvider() {
        return new AuthProvider();
    }

    @Bean
    public LoginUrlEntryPoint loginUrlEntryPoint() {
        // 传入的是默认要走的登录入口，如果uriMapper里都匹配不上就走默认
        return new LoginUrlEntryPoint("/user/login");
    }

    @Bean
    public LoginAuthFailHandler loginAuthFailHandler() {
        return new LoginAuthFailHandler(loginUrlEntryPoint());
    }
}




