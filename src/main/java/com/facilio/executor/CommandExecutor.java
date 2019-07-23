package com.facilio.executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            
            BufferedReader br;
            String line = null;
            StringBuilder sb = new StringBuilder();
            try (InputStream stdout = process.getInputStream()){
            		br = new BufferedReader(new InputStreamReader(stdout));
                while ((line = br.readLine()) != null) {
                		sb.append(line);
                }
                log.info("Std out is: " + sb.toString());
            }
            catch (Exception e) {
            		log.info("Exception occurred ", e);
			}
            
            try (InputStream stderr = process.getErrorStream()){
            		br = new BufferedReader(new InputStreamReader(stderr));
                line = null;
                sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                		sb.append(line);
                }
                log.info("Std error is: " + sb.toString());
            }
            
            exitStatus = process.exitValue();
        } catch (IOException | InterruptedException e) {
            log.info("Exception occurred ", e);
        }
        return exitStatus;
    }
}
