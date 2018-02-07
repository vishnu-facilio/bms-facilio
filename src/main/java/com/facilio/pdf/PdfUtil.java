package com.facilio.pdf;

import com.facilio.executor.CommandExecutor;

import java.io.File;
import java.io.IOException;

public class PdfUtil {

    private static final String WKHTMLTOPDF_CMD = System.getProperty("user.home")+"/wkhtmltox/bin/wkhtmltopdf";

    public static void convertUrlToPdf(long orgId, String url) {
        File pdfDirectory = new File(System.getProperty("java.io.tmpdir")+"/"+orgId+"/");
        boolean directoryExits = (pdfDirectory.exists() && pdfDirectory.isDirectory());
        if( ! directoryExits) {
            directoryExits = pdfDirectory.mkdirs();
        }
        if(directoryExits){
            try {
                File pdfFile = File.createTempFile("report-", ".pdf", pdfDirectory);
                String[] command = new String[]{WKHTMLTOPDF_CMD, url, pdfFile.getAbsolutePath()};
                int exitStatus = CommandExecutor.execute(command);
                System.out.println("Converted to pdf with exit status" + exitStatus + " and file " + pdfFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
