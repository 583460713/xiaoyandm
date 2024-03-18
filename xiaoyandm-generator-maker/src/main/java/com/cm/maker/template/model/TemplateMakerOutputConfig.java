package com.cm.maker.template.model;

import lombok.Data;

/**
 * 输出配置类
 * @author 语仄无言
 */

@Data
public class TemplateMakerOutputConfig {

    //从未分组文件中移除组内的同名文件
    private boolean removeGroupFilesFromRoot = true;
}
