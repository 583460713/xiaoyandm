package com.cm.maker.generator;


import java.io.*;

/**
 * @author 语仄无言
 */
public class JarGenerator {
    public static void doGenerate(String projectDir) throws InterruptedException, IOException {
        //清理之前的构建并打包
        //注意不同的操作系统,执行的命令不同

        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand = winMavenCommand;

        // 这里一定要拆分！
        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        processBuilder.directory(new File(projectDir));

        Process process = processBuilder.start();

        // 读取命令的输出
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待命令执行完成
        int exitCode = process.waitFor();
        System.out.println("命令执行结束，退出码：" + exitCode);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate("E:\\代码集\\我的项目\\xiaoyandm-generator\\xiaoyandm-generator-maker\\generated\\acm-template-pro-generator");
    }
}
