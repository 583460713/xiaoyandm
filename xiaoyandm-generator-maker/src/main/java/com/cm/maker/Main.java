package com.cm.maker;

import com.cm.maker.cli.CommandExecutor;

/**
 * @author 语仄无言
 */
public class Main {
    public static void main(String[] args) {

        //args = new String[]{"generate","-l","-a","-o"};
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}