package com.sf;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan(basePackages = {"com.sf.mapper"})
@EnableAspectJAutoProxy
public class SpeakFreelyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeakFreelyApplication.class, args);
    }

}
