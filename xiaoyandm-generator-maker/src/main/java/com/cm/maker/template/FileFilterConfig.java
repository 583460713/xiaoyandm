package com.cm.maker.template;

import lombok.Builder;
import lombok.Data;

/**
 * @author 语仄无言
 */
@Data
@Builder
public class FileFilterConfig {

    /**
     * 过滤范围
     */
    private String range;

    /**
     * 过滤规则
     */
    private String rule;

    /**
     * 过滤值
     */
    private String value;
}
