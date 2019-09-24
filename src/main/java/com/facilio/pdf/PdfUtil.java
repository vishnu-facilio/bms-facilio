package com.facilio.pdf;

import java.io.File;
import java.io.IOException;

import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FacilioFileStore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.UserUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.executor.CommandExecutor;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class PdfUtil {

    private static final String PDF_CMD = System.getProperty("user.home")+"/pdf/slimerjs/slimerjs";
    private static final String NODE = "/usr/bin/node";
    private static final String RENDER_JS = FacilioProperties.getPdfjsLocation()+"/render.js";
    private static final String RENDER_PUPETTEER_JS = FacilioProperties.getPdfjsLocation() + "/puppeteer-render.js";
    private static final Logger LOGGER = LogManager.getLogger(PdfUtil.class.getName());

    public static String convertUrlToPdf(long orgId, String username, String url, FileFormat... formats) {

        FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}
        File pdfDirectory = new File(System.getProperty("java.io.tmpdir")+"/"+orgId+"/");
        String pdfFileLocation = null;
        boolean directoryExits = (pdfDirectory.exists() && pdfDirectory.isDirectory());
        if( ! directoryExits) {
            directoryExits = pdfDirectory.mkdirs();
        }
        if(directoryExits){
            try {
                String token = UserUtil.createJWT("id", "auth0", String.valueOf(AccountUtil.getCurrentUser().getUid()), System.currentTimeMillis()+60*60000);
                File pdfFile = File.createTempFile("report-", format.getExtention(), pdfDirectory);
                pdfFileLocation = pdfFile.getAbsolutePath();
                String serverName = FacilioProperties.getAppDomain();
                if(serverName != null) {
                    String[] server = serverName.split(":");
                    serverName = server[0];
                }
                String[] command = new String[]{PDF_CMD, RENDER_JS ,"\"" + url + "\"", pdfFileLocation , token , serverName , "--debug=true"};
                int exitStatus = CommandExecutor.execute(command);
                LOGGER.info("Converted to pdf with exit status : " + exitStatus + " and file " + pdfFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.info("Exception occurred ", e);
            }
        }
        return pdfFileLocation;
    }
    
    public static String convertUrlToPdfNew(Long orgId, String url, FileFormat... formats) {
    	FileFormat format = FileFormat.PDF;
  		if (formats != null && formats.length > 0) {
  			format = formats[0];
  		}
          File pdfDirectory = new File(System.getProperty("java.io.tmpdir")+"/"+orgId+"/");
          String pdfFileLocation = null;
          boolean directoryExits = (pdfDirectory.exists() && pdfDirectory.isDirectory());
          if( ! directoryExits) {
              directoryExits = pdfDirectory.mkdirs();
          }
          
          if(directoryExits) {
        	  try {
        		  String token = UserUtil.createJWT("id", "auth0", String.valueOf(AccountUtil.getCurrentUser().getUid()), System.currentTimeMillis()+60*60000);
        		  File pdfFile = File.createTempFile("report-", format.getExtention(), pdfDirectory);
                  pdfFileLocation = pdfFile.getAbsolutePath();
                  String serverName = FacilioProperties.getAppDomain();
                  if(serverName != null) {
                      String[] server = serverName.split(":");
                      serverName = server[0];
                  }
                  String[] command = new String[] {NODE, RENDER_PUPETTEER_JS, url, pdfFileLocation, token, serverName};
                  int exitStatus = CommandExecutor.execute(command);
                  LOGGER.info("Converted to pdf with exit status : " + exitStatus + " and file " + pdfFile.getAbsolutePath());
        	  }catch(IOException e) {
        		  LOGGER.info("Exception occurred", e);
        	  }
          }
          
          return pdfFileLocation;
          
    }
    
    public static String exportUrlAsPdf(String url, FileFormat... formats){
    		return exportUrlAsPdf(url, false, null, formats);
    }
    
    public static String exportUrlAsPdf(String url, boolean isPublicUrl, String name, FileFormat... formats){
        FileFormat format = FileFormat.PDF;
        if (formats != null && formats.length > 0) {
            format = formats[0];
        }
        String pdfFileLocation = convertUrlToPdfNew(AccountUtil.getCurrentOrg().getOrgId(), url, format);
        File pdfFile = new File(pdfFileLocation);
        if(pdfFileLocation != null) {
            if(!FacilioProperties.isProduction() || FacilioProperties.isServicesEnabled()){
                com.facilio.services.filestore.FileStore fs =  com.facilio.services.factory.FacilioFactory.getFileStore();
                long fileId = 0;
                try {
                    fileId = fs.addFile(name != null ? name+format.getExtention() : pdfFile.getName(), pdfFile, format.getContentType());
                    if (isPublicUrl) {
                        return fs.getOrgiDownloadUrl(fileId);
                    }
                    return fs.getDownloadUrl(fileId);
                } catch (Exception e) {
                    LOGGER.info("Exception occurred ", e);
                }

            }else{
                FileStore fs = FileStoreFactory.getInstance().getFileStore();
                long fileId = 0;
                try {
                    fileId = fs.addFile(name != null ? name+format.getExtention() : pdfFile.getName(), pdfFile, format.getContentType());
                    if (isPublicUrl) {
                        return fs.getOrgiDownloadUrl(fileId);
                    }
                    return fs.getDownloadUrl(fileId);
                } catch (Exception e) {
                    LOGGER.info("Exception occurred ", e);
                }
            }

        }
        return null;
    }
}
