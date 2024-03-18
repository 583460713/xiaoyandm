package com.cm.maker.template.model;

import com.cm.maker.meta.Meta;
import lombok.Data;

/**
 * 模板制作配置
 * @author 语仄无言
 */

@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;

    TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
