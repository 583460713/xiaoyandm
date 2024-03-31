package com.cm.maker.generator.main;


/**
 * 生成代码生成器
 * @author 语仄无言
 */
public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath,String sourceCopyDestPath,String jarPath,String shellOutputFilePath){
        String distPath = super.buildDist(outputPath, sourceCopyDestPath, jarPath, shellOutputFilePath);
        return super.buildZip(distPath);
    }

}