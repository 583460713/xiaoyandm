package com.cm.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * 单例模式创建Meta对象
 * @author 语仄无言
 */
public class MetaManager {
    private static final  Meta meta = initMeta();

    private MetaManager(){
        //私有函数构造，防止外部调用
    }

    public static Meta getMetaObject() {
        //可使用双检索进行并发控制，防止重复实例化，本项目暂时用不上
        //if (meta == null){
        //    synchronized (MetaManager.class){
        //        if (meta == null){
        //            meta = initMeta();
        //        }
        //    }
        //}
        return meta;
    }

    private static Meta initMeta() {
        String property = System.getProperty("user.dir");
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        //Meta.FileConfigDTO fileConfig = newMeta.getFileConfig();
        //todo 校验和处理默认值
        return newMeta;
    }
}
