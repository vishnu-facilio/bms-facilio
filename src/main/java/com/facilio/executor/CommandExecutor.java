package com.facilio.executor;

import java.io.IOException;
import java.util.Arrays;

public class CommandExecutor {

    public static int execute(String[] commandWithArgs){
        ProcessBuilder builder = new ProcessBuilder(commandWithArgs);
        int exitStatus = -1;
        try {
            System.out.println("command is : " + Arrays.asList(commandWithArgs));
            Process process = builder.start();
            process.waitFor();
            exitStatus = process.exitValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return exitStatus;
    }
}
