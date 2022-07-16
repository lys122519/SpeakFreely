//package com.sf.utils;
//
//import com.baomidou.mybatisplus.generator.FastAutoGenerator;
//import com.baomidou.mybatisplus.generator.config.OutputFile;
//import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
//
//import java.util.Collections;
//
///**
// * @Description: MP代码生成器
// * @author: leung
// * @date: 2022-03-26 17:28
// */
//public class CodeGenerator {
//    public static void main(String[] args) {
//        generateCode();
//    }
//
//    private static void generateCode() {
//        FastAutoGenerator.create("jdbc:mysql://localhost:3306/speakfreely?serverTimeZone=GMT%2B8",
//                        "root", "lys122519")
//                .globalConfig(builder -> {
//                    builder.author("leung") // 设置作者
//                            .enableSwagger() // 开启 swagger 模式
//                            .fileOverride() // 覆盖已生成文件
//                            .outputDir("E:\\desktop\\SpeakFreely\\src\\main\\java"); // 指定输出目录
//                })
//                .packageConfig(builder -> {
//                    builder.parent("com.sf") // 设置父包名
//                            .moduleName(null) // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "E:\\desktop\\SpeakFreely\\src\\main\\resources\\mapper\\")); // 设置mapperXml生成路径
//                })
//                .strategyConfig(builder -> {
//                    builder.mapperBuilder().enableMapperAnnotation().build();
//                    builder.controllerBuilder().enableHyphenStyle()  // 开启驼峰转连字符
//                            .enableRestStyle();  // 开启生成@RestController 控制器
//                    builder.addInclude("tb_active_user") // 设置需要生成的表名
//                            .addTablePrefix("t_", "tb_"); // 设置过滤表前缀
//                })
//                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .execute();
//
//    }
//}
