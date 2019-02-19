package com.kealliang.base.intercept;

import com.kealliang.base.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * @author lsr
 * @ClassName AppErrorIntercept
 * @Date 2019-01-31
 * @Desc 页面与api的异常拦截
 * @Vertion 1.0
 */
@Controller
public class AppErrorIntercept implements ErrorController {

    public static final String ERROR_PATH = "/error";

    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    // ErrorAttribute注入
    @Autowired
    public AppErrorIntercept(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * web 页面错误跳转
     * 注意，这里的两个端点是靠produces区分的，text/html告诉了springMvc去找错误页面
     * @author lsr
     * @description
     * @Date 15:52 2019/1/31
     * @Param
     * @return
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
        switch(response.getStatus()) {
            case 403: return "error/403";
            case 404: return "error/404";
            case 500: return "error/500";
        }
        return "index";
    }

    /**
     * api 接口错误统一返回结构，例如JSON/XML等
     * ResponseBody注解相当于 produces="application/json"
     * @author lsr
     * @description
     * @Date 16:09 2019/1/31
     * @Param
     * @return
     */
    @RequestMapping(ERROR_PATH)
    @ResponseBody
    public ApiResponse errorApiHandler(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(webRequest, false);

        return ApiResponse.ofMessage(getStatus(request),
                String.valueOf(attr.getOrDefault("message", "Just Error")));
    }

    private int getStatus(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (!Objects.isNull(status)) {
            return status;
        }
        return 500;
    }


}
