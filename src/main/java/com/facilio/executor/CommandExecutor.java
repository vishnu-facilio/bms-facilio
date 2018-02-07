package com.facilio.executor;

import java.io.IOException;

public class CommandExecutor {

    public static int execute(String[] commandWithArgs){
        ProcessBuilder builder = new ProcessBuilder(commandWithArgs);
        int exitStatus = -1;
        try {
            Process process = builder.start();
            process.waitFor();
            exitStatus = process.exitValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return exitStatus;
    }
}
