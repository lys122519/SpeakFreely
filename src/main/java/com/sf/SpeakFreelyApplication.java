package com.sf;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = {"com.sf.mapper"})
@EnableAspectJAutoProxy
@EnableScheduling

public class SpeakFreelyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeakFreelyApplication.class, args);
    }

}
