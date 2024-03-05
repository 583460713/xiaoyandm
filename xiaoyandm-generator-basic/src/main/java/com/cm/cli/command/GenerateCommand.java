package com.cm.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.cm.generator.MainGenerator;
import com.cm.model.MainTemplateConfig;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author 语仄无言
 */

@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {

    /**
     * arity:限制参数长度
     * interactive：是否通过交互式获取参数信息
     * echo：是否对用户显示输入信息
     */

    @CommandLine.Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否循环", interactive = true, echo = true)
    private boolean loop;

    @CommandLine.Option(names = {"-a", "--author"}, arity = "0..1", description = "作者", interactive = true, echo = true)
    private String author = "chenmin";

    @CommandLine.Option(names = {"-o","--outputText"},arity = "0..1",description = "输出文本",interactive = true,echo = true)
    private String outputText = "sum = ";

    @Override
    public Integer call() throws Exception {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        System.out.println("配置信息：" + mainTemplateConfig);
        MainGenerator.doGemerate(mainTemplateConfig);
        return 0;
    }
}
