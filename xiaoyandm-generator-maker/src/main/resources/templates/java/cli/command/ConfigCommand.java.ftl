package ${basePackage}.cli.command;

import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

@CommandLine.Command(name = "config", description = "查看参数信息", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    public void run() {
        //实现config命令的逻辑
        System.out.println("查看参数信息");
        //使用反射，动态获取参数的信息
        Field[] fields = ReflectUtil.getFields(DataModel.class);

        //遍历打印每个字段的信息
        for (Field field : fields) {
            System.out.println("字段名称：" + field.getName());
            System.out.println("字段类型：" + field.getType());
            System.out.println("------");
        }
    }
}
