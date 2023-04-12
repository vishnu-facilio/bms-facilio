package com.facilio.bmsconsole.actions;

import java.io.InputStream;
import java.net.URLEncoder;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class V3FileAction extends FacilioAction {
	private static final Logger LOGGER = LogManager.getLogger(V3FileAction.class.getName());

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
	
	private Boolean isDownload;
	public Boolean getIsDownload() {
		if (isDownload == null) {
			isDownload = false;
		}
		return isDownload;
	}

	public void setIsDownload(Boolean isDownload) {
		this.isDownload = isDownload;
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

	private Boolean fetchOriginal;
	public Boolean getFetchOriginal() {
		return fetchOriginal;
	}
	public void setFetchOriginal(Boolean fetchOriginal) {
		this.fetchOriginal = fetchOriginal;
	}
	private boolean isFetchOriginal() {
		if (this.getIsDownload() == true) {
			return true;
		}
		if (this.fetchOriginal == null) {
			return false;
		}
		return this.fetchOriginal;
	}
	InputStream downloadStream;
	public InputStream getDownloadStream() {
		return downloadStream;
	}
	public void setDownloadStream(InputStream downloadStream) {
		this.downloadStream = downloadStream;
	}

	public String previewFile() throws Exception {
		try {
			FacilioChain chain = ReadOnlyChainFactory.getV3FilePreview();
			FacilioContext context = chain.getContext();
			context.put(FacilioConstants.ContextNames.FILE_TOKEN_STRING, q);
			context.put(FacilioConstants.ContextNames.IS_DOWNLOAD, isDownload);
			context.put(FacilioConstants.ContextNames.FETCH_ORIGINAL, isFetchOriginal());
			chain.execute();
			setContentType((String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE));
			if (context.get(FacilioConstants.ContextNames.FILE_NAME) != null) {
				String encodedFilename = URLEncoder.encode((String) context.get(FacilioConstants.ContextNames.FILE_NAME), "UTF-8").replace("+", " ");
				setFilename(encodedFilename);
			}
			if (context.get(FacilioConstants.ContextNames.FILE_DOWNLOAD_STREAM) != null) {
				setDownloadStream((InputStream) context.get(FacilioConstants.ContextNames.FILE_DOWNLOAD_STREAM));
			}
			int responseStatusCode = (int) context.getOrDefault(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 200);
			Boolean isValidRequest = (Boolean) context.get("isValidRequest");
			if (responseStatusCode == 404 || isValidRequest == null || !isValidRequest) {
				return ERROR;
			} else if (responseStatusCode == 304) {
				return NONE;
			}
			return SUCCESS;
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in V3 file action", e);
			return ERROR;
		}
	}
}
