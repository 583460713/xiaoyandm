package com.cm.generator;

import com.cm.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 核心代码生成器
 * @author 语仄无言
 */
public class MainGenerator {

    public static void doGemerate(Object model) throws TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        //项目的根路径
        File parentFile = new File(projectPath);
        //输入路径
        String inputPath = new File(parentFile, "xiaoyandm-generator-demo-projects/acm-template").getAbsolutePath();
        String outPath = projectPath;
        //生成静态文件
        StaticGenerator.copyFilesByHutool(inputPath, outPath);
        //生成动态文件
        String inputDynamicPath = projectPath + File.separator + "xiaoyandm-generator-basic" + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputDynamicPath = projectPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";
        DynamicGenerator.doGenerate(inputDynamicPath,outputDynamicPath,model);
    }

    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("chenmin");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("求和结果：");
        doGemerate(mainTemplateConfig);
    }
}
