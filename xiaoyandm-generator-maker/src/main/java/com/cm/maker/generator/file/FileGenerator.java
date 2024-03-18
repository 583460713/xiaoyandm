package com.cm.maker.generator.file;

import com.cm.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 核心代码生成器
 * @author 语仄无言
 */
public class FileGenerator {

    public static void doGenerate(Object model) throws TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        //整个项目的根路径
        //File parentFile1 = new File(projectPath).getParentFile().getParentFile();
        File parentFile = new File(projectPath).getParentFile();
        //当前项目的根路径
        //String parentFile2 = new File(projectPath).getParentFile().getAbsolutePath();
        //输入路径
        String inputPath = new File(parentFile, "xiaoyandm-generator-demo-projects/acm-template").getAbsolutePath();
        String outputPath = projectPath;
        //生成静态文件
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);
        //生成动态文件
        //String inputDynamicPath = parentFile2 + File.separator + "src/main/resources/templates/MainTemplate.java.ftl.ftl";
        //String inputDynamicPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl.ftl";
        String inputDynamicPath = projectPath + File.separator + "src/main/resources/templates/DataModel.java.ftl.ftl";
        String outputDynamicPath = outputPath + File.separator + "acm-template/src/com/cm/acm/MainTemplate.java.ftl";
        DynamicFileGenerator.doGenerate(inputDynamicPath,outputDynamicPath,model);
    }
}
