package com.cm.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.cm.model.MainTemplateConfig;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * 输出允许用户传入的动态参数的信息
 *
 * @author 语仄无言
 */

@CommandLine.Command(name = "config", description = "查看参数信息", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        //实现config命令的逻辑
        System.out.println("查看参数信息");

        //获取要打印属性信息的类
        //Class<?> myClass = MainTemplateConfig.class;
        ////获取类的所有字段
        //Field[] fields = myClass.getDeclaredFields();
        //使用反射，动态获取参数的信息
        Field[] fields = ReflectUtil.getFields(MainTemplateConfig.class);

        //遍历打印每个字段的信息
        for (Field field : fields) {
            System.out.println("字段名称：" + field.getName());
            System.out.println("字段类型：" + field.getType());
            System.out.println("------");
        }
    }
}
