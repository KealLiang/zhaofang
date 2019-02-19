package com.kealliang.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lsr
 * @ClassName LoginUrlEntryPoint
 * @Date 2019-02-01
 * @Desc 登录url分类接入点
 * @Vertion 1.0
 */
public class LoginUrlEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final Logger LOG = LoggerFactory.getLogger(LoginUrlEntryPoint.class);

    private final Map<String, String> uriMapper;

    // 路径分类器
    private PathMatcher pathMatcher = new AntPathMatcher();

    public LoginUrlEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
        uriMapper = new HashMap<>();
        uriMapper.put("/admin/**", "/admin/login");
        uriMapper.put("/user/**", "/user/login");
    }

    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        LOG.info("请求的url为: {} ", url);
        LOG.info("请求的uri为: {} ", uri);
        for (Map.Entry<String, String> entry : uriMapper.entrySet()) {
            if (pathMatcher.match(entry.getKey(), uri)) {
                return entry.getValue();
            }
        }

        return super.determineUrlToUseForThisRequest(request, response, exception);
    }
}
