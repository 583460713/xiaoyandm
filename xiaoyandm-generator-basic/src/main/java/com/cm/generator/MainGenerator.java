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

    //public static void doGenerate(Object model) throws TemplateException, IOException {
    //    String projectPath = System.getProperty("user.dir");
    //    //整个项目的根路径
    //    File parentFile1 = new File(projectPath).getParentFile().getParentFile();
    //    //当前项目的根路径
    //    String parentFile2 = new File(projectPath).getParentFile().getAbsolutePath();
    //    //输入路径
    //    String inputPath = new File(parentFile1, "xiaoyandm-generator-demo-projects/acm-template").getAbsolutePath();
    //    String outputPath = parentFile2;
    //    //生成静态文件
    //    StaticGenerator.copyFilesByHutool(inputPath, outputPath);
    //    //生成动态文件
    //    String inputDynamicPath = parentFile2 + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
    //    String outputDynamicPath = outputPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";
    //    DynamicGenerator.doGenerate(inputDynamicPath,outputDynamicPath,model);
    //}

    public static void doGenerate(Object model) throws TemplateException, IOException {

        String inputRootPath = "E:\\代码集\\我的项目\\xiaoyandm-generator\\xiaoyandm-generator-demo-projects\\acm-template-pro";
        String outputRootPath = "E:\\代码集\\我的项目\\xiaoyandm-generator\\acm-template-pro";

        String inputPath;
        String outputPath;

        inputPath = new File(inputRootPath, "src/com/cm/acm/MainTemplate.java.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath, "src/com/cm/acm/MainTemplate.java").getAbsolutePath();
        DynamicGenerator.doGenerate(inputPath, outputPath,model);

        inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
        outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();
        StaticGenerator.copyFilesByHutool(inputPath,outputPath);

        inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
        outputPath = new File(outputRootPath, "README.md").getAbsolutePath();
        StaticGenerator.copyFilesByHutool(inputPath,outputPath);

    }

    //public static void main(String[] args) throws TemplateException, IOException {
    //    MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
    //    mainTemplateConfig.setAuthor("chenmin");
    //    mainTemplateConfig.setLoop(false);
    //    mainTemplateConfig.setOutputText("求和结果：");
    //    doGenerate(mainTemplateConfig);
    //}
    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("chenmin");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("求和结果：");
        doGenerate(mainTemplateConfig);
    }
}
