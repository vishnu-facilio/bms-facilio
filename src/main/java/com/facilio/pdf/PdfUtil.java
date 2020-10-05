package com.facilio.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.executor.CommandExecutor;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.filestore.PublicFileUtil;

public class PdfUtil {

    private static final String NODE = FacilioProperties.getNodeJSLocation();
    private static final String RENDER_PUPETTEER_JS = FacilioProperties.getPdfjsLocation() + "/puppeteer-render.js";
    private static final Logger LOGGER = LogManager.getLogger(PdfUtil.class.getName());

	public static String convertUrlToPdf(String url, String htmlContent, JSONObject additionalInfo , FileFormat... formats) {
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}
		File pdfDirectory = new File(System.getProperty("java.io.tmpdir")+"/"+AccountUtil.getCurrentOrg().getOrgId()+"/");
		String pdfFileLocation = null;
		boolean directoryExits = (pdfDirectory.exists() && pdfDirectory.isDirectory());
		if( ! directoryExits) {
			directoryExits = pdfDirectory.mkdirs();
		}

		if(directoryExits) {
			try {
				String token = IAMUserUtil.createJWT("id", "auth0", String.valueOf(AccountUtil.getCurrentUser().getUid()), System.currentTimeMillis()+60*60000);
				File pdfFile = File.createTempFile("report-", format.getExtention(), pdfDirectory);
				pdfFileLocation = pdfFile.getAbsolutePath();
				String serverName = FacilioProperties.getAppDomain();
				if(serverName != null) {
					String[] server = serverName.split(":");
					serverName = server[0];
				}
				if (StringUtils.isEmpty(htmlContent)) {
					htmlContent = "false";
				}
				if (additionalInfo == null) {
					additionalInfo = new JSONObject();
				}
				additionalInfo.put("orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()));
				additionalInfo.put("orgDomain", AccountUtil.getCurrentOrg().getDomain());
				if (AccountUtil.getCurrentSiteId() != -1) {
					additionalInfo.put("currentSite", AccountUtil.getCurrentSiteId());
				}
				

				String[] command = new String[] {NODE, RENDER_PUPETTEER_JS, url, pdfFileLocation, token, serverName, htmlContent, additionalInfo.toString()};
				int exitStatus = CommandExecutor.execute(command);
				LOGGER.debug("Converted to pdf with exit status : " + exitStatus + " and file " + pdfFile.getAbsolutePath());
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
		return exportUrlAsPdf(url, isPublicUrl, name, null, formats);
	}

	public static String exportUrlAsPdf(String url, boolean isPublicUrl, String name, JSONObject additionalInfo, FileFormat... formats){      	
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}                
		String pdfFileLocation = convertUrlToPdf(url, null, additionalInfo, format);         
		File pdfFile = new File(pdfFileLocation);
		if(pdfFileLocation != null) {

			FileStore fs = FacilioFactory.getFileStore();
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
		return null;
	}

	public static String exportUrlAsPublicFilePdf(String url, boolean isPublicUrl, String name, JSONObject additionalInfo, long expiry, FileFormat... formats){      	
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}                
		String pdfFileLocation = convertUrlToPdf(url, null, additionalInfo, format);         
		File pdfFile = new File(pdfFileLocation);
		if(pdfFileLocation != null) {
			try {
				String publicFileUrl = PublicFileUtil.createPublicFile(pdfFile, name != null ? name+format.getExtention() : pdfFile.getName(), format.getExtention().substring(1), format.getContentType(), expiry);
				return publicFileUrl;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static File exportUrlAsFile(String url, String name, String htmlContent, JSONObject additionalInfo, FileFormat... formats){	
		FileFormat format = FileFormat.PDF;
		if (formats != null && formats.length > 0) {
			format = formats[0];
		}        

		String pdfFileLocation = convertUrlToPdf(url, htmlContent, additionalInfo, format);

		File pdfFile = new File(pdfFileLocation);
		return pdfFile;
	}

	public static File exportUrlAsFile(String url, String name, FileFormat... formats){	
		return exportUrlAsFile(url, name, null, null, formats);
	}

}
