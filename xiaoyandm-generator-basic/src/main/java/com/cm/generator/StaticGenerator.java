package com.cm.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 静态文件生成器
 * @author 语仄无言
 */
public class StaticGenerator {
    public static void main(String[] args) {
        String propertyPath = System.getProperty("user.dir");
        //输入路径
        String inputPath = propertyPath + File.separator + "xiaoyandm-generator-demo-projects" + File.separator + "acm-template";
        //输出路径
        String outputPath = propertyPath;
        copyFilesByHutool(inputPath,outputPath);
    }

    /**
     * 拷贝文件（Hutool实现，会将输入目录完整拷贝到输出目录下）
     * @param inputPath     输入路径
     * @param outputPath    输出路径
     */
    public static void copyFilesByHutool(String inputPath,String outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }
}


