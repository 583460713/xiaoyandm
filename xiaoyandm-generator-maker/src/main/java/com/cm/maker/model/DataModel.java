package com.cm.maker.model;

import lombok.Data;

/**
 * 静态模板设置
 *
 * @author 语仄无言
 */
@Data
public class DataModel {

    /**
     * 作者
     */
    private String author;

    /**
     * 输出信息
     */
    private String outputText;

    /**
     * 是否循环（开关）
     */
    private boolean loop;
}
