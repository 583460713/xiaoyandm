package com.cm.maker.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

/**
 * 辅助命令，作用是遍历输出所有要生成的文件列表
 *
 * @author 语仄无言
 */

@CommandLine.Command(name = "list", description = "查看文件列表",mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {
    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");

        //整个项目的根路径
        File parentFile = new File(projectPath).getParentFile();
        //输入路径
        String inputPath = new File(parentFile, "xiaoyandm-generator-demo-projects/acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }
    }
}
