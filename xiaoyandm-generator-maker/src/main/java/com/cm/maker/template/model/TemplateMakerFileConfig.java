package com.cm.maker.template.model;

import com.cm.maker.template.FileFilterConfig;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 语仄无言
 */
@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    private FileGroupConfig fileGroupConfig;

    @NoArgsConstructor
    @Data
    public static class FileInfoConfig{

        private String path;

        private String condition;

        private List<FileFilterConfig> filterConfigList;
    }

    @Data
    public static class FileGroupConfig{

        private String condition;

        private String groupKey;

        private String groupName;

        private String type;

        private String description;
    }
}
