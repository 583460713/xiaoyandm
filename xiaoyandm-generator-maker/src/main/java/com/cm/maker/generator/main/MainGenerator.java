package com.cm.maker.generator.main;


/**
 * 生成代码生成器
 * @author 语仄无言
 */
public class MainGenerator extends GenerateTemplate {
    @Override
    protected void buildDist(String outputPath,String sourceCopyDestPath,String jarPath,String shellOutputFilePath) {
        System.out.println("不要给我输出 dist 啦！");
    }
}