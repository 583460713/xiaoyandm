package com.cm.maker.model;

import lombok.Data;

/**
 * 静态模板设置
 * @author 语仄无言
 */
@Data
public class DataModel {

    /**
     * 作者
     */
    public String author;

    /**
     * 输出信息
     */
    public String outputText;

    /**
     * 是否循环（开关）
     */
    public boolean loop;

    /**
     * 核心模板
     */
    public MainTemplate mainTemplate = new MainTemplate();

    /**
     * 用于生成核心模板文件
     */
    @Data
    public static class MainTemplate {
        /**
         * 作者注释
         */
        public String author = "chenmin";

        /**
         * 输出信息
         */
        public String outputText = "sum = ";
    }
}
