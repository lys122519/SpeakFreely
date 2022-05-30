package com.sf.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import springfox.documentation.service.Contact;

/**
 * @Description:
 * @author: leung
 * @date: 2022-05-30 16:18
 */
@Configuration
@EnableSwagger2WebMvc
// 访问路径 http://localhost:9090/doc.html#/home
public class SwaggerConfig {

    @Bean(value = "defaultApi")
    public Docket defaultApi() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //分组名称
                .groupName("2.X版本")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.sf.controller"))
                //这里指定扫描有ApiOperation注解的类
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //所有路径
                .paths(PathSelectors.any())
                //不包含^/inner/.*的路径
                //.paths(input -> !input.matches("^/inner/.*"))
                .build();
        return docket;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SpeakFreely的RESTful APIs")
                .description("构建RESTful APIs")
                .termsOfServiceUrl("")
                .contact(new Contact("SpeakFreely",
                        "",
                        "514955048@qq.com"))
                .version("1.0")
                .build();
    }
}
