package com.cm.maker.cli;

import com.cm.maker.cli.command.ConfigCommand;
import com.cm.maker.cli.command.GenerateCommand;
import com.cm.maker.cli.command.ListCommand;
import picocli.CommandLine;

/**
 * 命令执行器，负责绑定所有子命令，并且提供执行命令的方法
 * @author 语仄无言
 */

@CommandLine.Command(name = "xiaoyandm",mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable{
    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        //不输入子命令时，给出友好提示
        System.out.println("请输入具体命令，或者输入 --help 查看命令提示");
    }

    /**
     * 执行命令
     */
    public Integer doExecute(String[] args){
        return commandLine.execute(args);
    }
}
