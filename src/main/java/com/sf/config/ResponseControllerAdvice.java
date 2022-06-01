package com.sf.config;


import com.sf.common.NotResultWrap;
import com.sf.common.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Description: 接口返回值包装
 * @author: leung
 * @date: 2022-06-01 15:36
 */

@RestControllerAdvice(basePackages = "com.sf.controller")
public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger log = LoggerFactory.getLogger(ResponseControllerAdvice.class);

    /**
     * 判断哪些接口需要进行返回值包装
     * 返回 true 才会执行 beforeBodyWrite 方法；返回 false 则不执行。
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        log.info("判断是否需要进行返回值包装");
        //如果接口方法返回 Result 不需要再次包装
        //如果接口方法使用了 @NotResultWrap 注解，表示不需要包装了
        //只对成功的请求进行返回包装，异常情况统一放在全局异常中进行处理
        return !(returnType.getParameterType().equals(Result.class)
                || returnType.hasMethodAnnotation(NotResultWrap.class));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        log.info("进行返回值包装");
        Result<Object> result = new Result<>();
        //String 类型不能直接包装，需要进行特殊处理
        if (returnType.getParameterType().equals(String.class)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                //使用 jackson 将返回数据转换为 json
                return objectMapper.writeValueAsString(result.success(body));
            } catch (JsonProcessingException e) {
                //这里会走统一异常处理
                throw new RuntimeException("String类型返回值包装异常");
            }
        }
        return result.success(body);
    }
}
