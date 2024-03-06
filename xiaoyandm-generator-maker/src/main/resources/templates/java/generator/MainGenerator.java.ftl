package ${basePackage}.generator;

import com.cm.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 核心生成器
 */
public class MainGenerator {

    /**
     * 生成
     *
     * @param model 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
    <#list fileConfig.files as files>

        inputPath = new File(inputRootPath, "${files.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath, "${files.outputPath}").getAbsolutePath();
        <#if files.generateType == "static">
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);
        <#else>
        DynamicGenerator.doGenerate(inputPath, outputPath, model);
        </#if>
    </#list>
    }
}