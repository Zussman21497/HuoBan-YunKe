package com.xcc.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class Generator {
    static String username = "huoban";
    static String password = "ShsM4ZiEtFLtdzx3";
    static String url = "jdbc:mysql://47.109.72.79:3306/huoban?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    static String outputDir = "C:\\Users\\wanrz\\Desktop\\code";
    static String[] tables = {"huoban"};
    static String parent = "com.xcc", module = "";

    /**
     * https://baomidou.com/pages/981406/
     *
     * @param args
     */
    public static void main(String[] args) {
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("wan") // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            .outputDir(outputDir) // 指定输出目录
                            .dateType(DateType.ONLY_DATE)                            .commentDate("yyyy-MM-dd HH:mm:ss")
                    ;
                })
                .packageConfig(builder -> {
                    builder.parent(parent) // 设置父包名
                            .moduleName(module) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, outputDir + "\\mapper")) // 设置mapperXml生成路径
                    ;
                })
                .strategyConfig(builder -> {
                    builder.controllerBuilder().enableRestStyle().enableHyphenStyle();
                    builder.entityBuilder().enableActiveRecord().enableLombok().enableChainModel()
                            .enableTableFieldAnnotation()
                    ;
                    builder.mapperBuilder().enableMapperAnnotation();
                    builder.addInclude(tables) // 设置需要生成的表名
                            .addTablePrefix("sys_") // 设置过滤表前缀
                    ;
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }
}
