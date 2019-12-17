package com.facilio.bmsconsole.actions;

import java.io.InputStream;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.FCUContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class V3FileAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String q;

	public String getQ() {
		return q;
	}

	public void setQ(String fileToken) {
		this.q = fileToken;
	}

	private String contentType = "application/octet-stream";

	public String getContentType() {
		return contentType;
	}

	protected void setContentType(String contentType) {
		this.contentType = contentType;
	}

	private String filename;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	private int width;
	private int height;

	public int getHeigth() {
		return height;
	}

	public void setHeigth(int height) {
		this.height = height;
	}
	
	InputStream downloadStream;
	public InputStream getDownloadStream() {
		return downloadStream;
	}
	public void setDownloadStream(InputStream downloadStream) {
		this.downloadStream = downloadStream;
	}

	public String previewFile() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getV3FilePreview();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.FILE_TOKEN_STRING, q);
		chain.execute();
		setContentType((String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE));
		if (context.get(FacilioConstants.ContextNames.FILE_NAME) != null) {
			setFilename((String) context.get(FacilioConstants.ContextNames.FILE_NAME));
		}
		if(context.get(FacilioConstants.ContextNames.FILE_DOWNLOAD_STREAM)!=null) {
			setDownloadStream((InputStream) context.get(FacilioConstants.ContextNames.FILE_DOWNLOAD_STREAM));
		}
		int responseStatusCode = (int) context.getOrDefault(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 200);
		if (responseStatusCode == 404) {
			return ERROR;
		} else if (responseStatusCode == 304) {
			return NONE;
		} 
		return SUCCESS;
	}
}
