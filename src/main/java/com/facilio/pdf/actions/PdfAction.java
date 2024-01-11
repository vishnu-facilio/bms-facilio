package com.facilio.pdf.actions;

import java.io.InputStream;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.services.filestore.FileStoreFactory;
import com.facilio.services.pdf.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.pdf.PdfUtil;

@Log4j
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
	
    @Getter @Setter
	private Boolean isNewExport=false;

    public String createPdf() throws Exception {
		String fileUrl = null;
    	if(this.getIsNewExport()){
			long fileId = PDFServiceFactory.getPDFService().exportURL("download-"+System.currentTimeMillis(),getUrl(), PDFService.ExportType.PDF,null);
			LOGGER.info("PDFFileId : "+fileId);
			fileUrl = FileStoreFactory.getInstance().getFileStore().getDownloadUrl(fileId);
			LOGGER.info("PDFFileURL : "+fileUrl);
		} else {
			fileUrl = PdfUtil.exportUrlAsPdf(getUrl(), false, "download-" + System.currentTimeMillis(), additionalInfo, fileFormat != null ? fileFormat : FileFormat.PDF);
		}
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

	private JSONObject additionalInfo;
	public JSONObject getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(JSONObject additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	private JSONObject options;
	private String fileName;

	public JSONObject getOptions() {
		return options;
	}

	public void setOptions(JSONObject options) {
		this.options = options;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Getter
	@Setter
	private String pageName;
	@Getter
	@Setter
	private JSONObject pageParams;

	public String generatePdf() throws Exception {

		if (fileName == null) {
			fileName = "download-" + System.currentTimeMillis() + ".pdf";
		}
		if (options == null) {
			options = new JSONObject();
		}
		PDFOptions pdfOptions = (PDFOptions) FieldUtil.getAsBeanFromJson(options, PDFOptions.class);
		long fileId = PDFServiceFactory.getPDFService().exportPage(fileName, AccountUtil.getCurrentApp().getLinkName(), getPageName(), getPageParams(), PDFService.ExportType.PDF, pdfOptions);

		String fileUrl = null;
		if (fileId > 0) {
			fileUrl = FileStoreFactory.getInstance().getFileStore().getDownloadUrl(fileId);
		}
		setResult("fileUrl", fileUrl);
		return SUCCESS;
	}

	public String generateScreenshot() throws Exception {

		if (options == null) {
			options = new JSONObject();
		}
		ScreenshotOptions screenshotOptions = (ScreenshotOptions) FieldUtil.getAsBeanFromJson(options, ScreenshotOptions.class);
		if (fileName == null) {
			fileName = "download-" + System.currentTimeMillis() + "." + (screenshotOptions.getFormat() != null ? screenshotOptions.getFormat() : "png");
		}

		long fileId = PDFServiceFactory.getPDFService().exportPage(fileName, AccountUtil.getCurrentApp().getLinkName(), getPageName(), getPageParams(), PDFService.ExportType.SCREENSHOT, screenshotOptions);

		String fileUrl = null;
		if (fileId > 0) {
			fileUrl = FileStoreFactory.getInstance().getFileStore().getDownloadUrl(fileId);
		}
		setResult("fileUrl", fileUrl);
		return SUCCESS;
	}
}