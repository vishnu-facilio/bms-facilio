package com.facilio.pdf;

import com.facilio.executor.CommandExecutor;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.auth.CognitoUtil;

import java.io.File;
import java.io.IOException;

public class PdfUtil {

    private static final String WKHTMLTOPDF_CMD = System.getProperty("user.home")+"/wkhtmltox/bin/wkhtmltopdf";

    public static String convertUrlToPdf(long orgId, String username, String url) {
        File pdfDirectory = new File(System.getProperty("java.io.tmpdir")+"/"+orgId+"/");
        String pdfFileLocation = null;
        boolean directoryExits = (pdfDirectory.exists() && pdfDirectory.isDirectory());
        if( ! directoryExits) {
            directoryExits = pdfDirectory.mkdirs();
        }
        if(directoryExits){
            try {
                String token = CognitoUtil.createJWT("id", "auth0", username, System.currentTimeMillis()+1*60*60000);
                File pdfFile = File.createTempFile("report-", ".pdf", pdfDirectory);
                pdfFileLocation = pdfFile.getAbsolutePath();
                String[] command = new String[]{WKHTMLTOPDF_CMD, url, "--custom-header-propagation", "--custom-header", "fc.idToken.facilio", token, pdfFileLocation};
                int exitStatus = CommandExecutor.execute(command);
                System.out.println("Converted to pdf with exit status" + exitStatus + " and file " + pdfFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pdfFileLocation;
    }

    public static String exportUrlAsPdf(long orgId, String username, String url){
        String pdfFileLocation = convertUrlToPdf(orgId, username, url);
        File pdfFile = new File(pdfFileLocation);
        if(pdfFileLocation != null) {
            FileStore fs = FileStoreFactory.getInstance().getFileStore();
            long fileId = 0;
            try {
                fileId = fs.addFile(pdfFileLocation, pdfFile, "application/csv");
                return fs.getPrivateUrl(fileId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
