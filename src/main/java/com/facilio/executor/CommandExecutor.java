package com.facilio.executor;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.LogManager;

public class CommandExecutor {

    private static org.apache.log4j.Logger log = LogManager.getLogger(CommandExecutor.class.getName());

    public static int execute(String[] commandWithArgs){
        ProcessBuilder builder = new ProcessBuilder(commandWithArgs);
        int exitStatus = -1;
        try {
        		log.info("command is : " + Arrays.asList(commandWithArgs));
            Process process = builder.start();
            process.waitFor();
            exitStatus = process.exitValue();
        } catch (IOException | InterruptedException e) {
            log.info("Exception occurred ", e);
        }
        return exitStatus;
    }
}
