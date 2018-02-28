package com.facilio.pdf;

import java.io.File;
import java.io.IOException;

import com.facilio.aws.util.AwsUtil;
import com.facilio.executor.CommandExecutor;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.auth.CognitoUtil;

public class PdfUtil {

    private static final String PDF_CMD = System.getProperty("user.home")+"/slimerjs-0.10.3/slimerjs";
    private static final String RENDER_JS = System.getProperty("user.home")+"/render.js";
    private static final String SERVER_NAME = AwsUtil.getConfig("api.servername");

    public static String convertUrlToPdf(long orgId, String username, String url, FileFormat format) {
    		if (format == null) {
			format = FileFormat.PDF;
		}
        File pdfDirectory = new File(System.getProperty("java.io.tmpdir")+"/"+orgId+"/");
        String pdfFileLocation = null;
        boolean directoryExits = (pdfDirectory.exists() && pdfDirectory.isDirectory());
        if( ! directoryExits) {
            directoryExits = pdfDirectory.mkdirs();
        }
        if(directoryExits){
            try {
                String token = CognitoUtil.createJWT("id", "auth0", username, System.currentTimeMillis()+1*60*60000);
                File pdfFile = File.createTempFile("report-", format.getExtention(), pdfDirectory);
                pdfFileLocation = pdfFile.getAbsolutePath();
                String serverName = SERVER_NAME;
                if(SERVER_NAME != null) {
                    String[] server = SERVER_NAME.split(":");
                    serverName = server[0];
                }
                String[] command = new String[]{PDF_CMD, RENDER_JS, url, pdfFileLocation, token, serverName};
                int exitStatus = CommandExecutor.execute(command);
                System.out.println("Converted to pdf with exit status" + exitStatus + " and file " + pdfFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pdfFileLocation;
    }

    public static String exportUrlAsPdf(long orgId, String username, String url, FileFormat format){
    		if (format == null) {
    			format = FileFormat.PDF;
    		}
        String pdfFileLocation = convertUrlToPdf(orgId, username, url, format);
        File pdfFile = new File(pdfFileLocation);
        if(pdfFileLocation != null) {
            FileStore fs = FileStoreFactory.getInstance().getFileStore();
            long fileId = 0;
            try {
                fileId = fs.addFile(pdfFile.getName(), pdfFile, format.getContentType());
                return fs.getPrivateUrl(fileId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
