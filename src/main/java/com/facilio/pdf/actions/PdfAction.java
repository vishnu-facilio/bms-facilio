package com.facilio.pdf.actions;

import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.pdf.PdfUtil;
import com.facilio.util.FacilioUtil;

public class PdfAction extends FacilioAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url;
    private InputStream downloadStream;

    private static Logger log = LogManager.getLogger(PdfAction.class.getName());

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getDownloadStream() {
        return downloadStream;
    }

    public void setDownloadStream(InputStream downloadStream) {
        this.downloadStream = downloadStream;
    }

    public String createPdf() throws Exception {
    	
    		JSONObject additionalInfo = null;
    		if (additionalInfoStr != null) {
    			additionalInfo = FacilioUtil.parseJson(additionalInfoStr);
    		}
    	
        String fileUrl = PdfUtil.exportUrlAsPdf(getUrl(), false, "download-"+System.currentTimeMillis(), additionalInfo, fileFormat != null ? fileFormat : FileFormat.PDF);
        setResult("fileUrl", fileUrl);
        return SUCCESS;
    }
    
    private FileFormat fileFormat;
	public FileFormat getFileFormatEnum() {
		return fileFormat;
	}
	public void setFileFormat(int format) {
		this.fileFormat = FileFormat.getFileFormat(format);
	}
	public void setFileFormatEnum(FileFormat format) {
		this.fileFormat = format;
	}
	
	private String htmlContent;
	
	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	private String additionalInfoStr;
	public String getAdditionalInfoStr() {
		return additionalInfoStr;
	}
	public void setAdditionalInfoStr(String additionalInfoStr) {
		this.additionalInfoStr = additionalInfoStr;
	}
}
