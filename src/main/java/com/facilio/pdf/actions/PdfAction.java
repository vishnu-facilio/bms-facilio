package com.facilio.pdf.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.pdf.PdfUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
        String fileName  = PdfUtil.convertUrlToPdf(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentUser().getEmail(), getUrl());
        downloadStream = new FileInputStream(new File(fileName));
        setContentType("application/octet-stream");
        return SUCCESS;
    }
}
