package com.facilio.bmsconsole.commands;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.chain.Context;
import org.apache.struts2.ServletActionContext;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.bmsconsole.util.FileJWTUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class V3FilePreviewCommad extends FacilioCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		String token = (String) context.get(FacilioConstants.ContextNames.FILE_TOKEN_STRING);
		if (token != null) {
			InputStream downloadStream;
			boolean isDownload = false;
			if (context.get(FacilioConstants.ContextNames.IS_DOWNLOAD) != null) {
				isDownload = (boolean) context.get(FacilioConstants.ContextNames.IS_DOWNLOAD);
			}
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			long fileID;
			Map<String, String> decodedjwtClaims = FileJWTUtil.validateJWT(token);
			if (decodedjwtClaims != null && !decodedjwtClaims.isEmpty()) {
				long expiresAt = Long.valueOf(decodedjwtClaims.get("expiresAt"));
				if(expiresAt == -1 || expiresAt > System.currentTimeMillis()) {
					String namespace = decodedjwtClaims.get("namespace");
					fileID = Long.valueOf(decodedjwtClaims.get("fileId"));
					String modifiedHeader = request.getHeader("If-Modified-Since"); 
					if (modifiedHeader == null) {
						if (fileID > 0) {
							FileStore fs = FacilioFactory.getFileStore();
							FileInfo fileInfo;
							fileInfo = namespace == null ? fs.getFileInfo(fileID) : fs.getFileInfo(namespace, fileID);
//							if (width > 0 || height > 0) {
//								if (height < 0) {
//									fileInfo = fs.getResizedFileInfo(fileID, width, width);
//								} else {
//									fileInfo = fs.getResizedFileInfo(fileID, width, height);
//								}
//							} else {
//								fileInfo = fs.getFileInfo(fileID);
//							}
							if (fileInfo != null) {

								downloadStream = fs.readFile(fileInfo);
								if (downloadStream != null) {
									String dateStamp = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").format(new Date());
									response.setHeader("Last-Modified", dateStamp);
									if (isDownload) {
										context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, "application/x-download");
										context.put(FacilioConstants.ContextNames.FILE_NAME, fileInfo.getFileName());
									} else {
										context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, fileInfo.getContentType());
									}
									context.put(FacilioConstants.ContextNames.FILE_DOWNLOAD_STREAM, downloadStream);
								} else {
									throw new IllegalArgumentException("File not Found");
								}
							} else {
								response.setStatus(404);
								context.put(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 404);
							}
						}
						return false;
					} else {
						response.setStatus(304);
						context.put(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 304);
						return false;
					}
				}
				else {
					response.setStatus(404);
					context.put(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 404);
				}
			}
		}
		return false;
	}

}
