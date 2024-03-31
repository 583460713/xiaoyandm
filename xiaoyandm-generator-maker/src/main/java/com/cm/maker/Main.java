package com.cm.maker;


import com.cm.maker.generator.main.GenerateTemplate;
import com.cm.maker.generator.main.MainGenerator;
import com.cm.maker.generator.main.ZipGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @author 语仄无言
 */
public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        MainGenerator mainGenerator = new MainGenerator();
        args = new String[]{"generate","--needGit=false"};
        mainGenerator.doGenerate();
        //GenerateTemplate generateTemplate = new ZipGenerator();
        //generateTemplate.doGenerate();
    }
}