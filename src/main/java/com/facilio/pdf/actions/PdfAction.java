package com.facilio.pdf.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.pdf.PdfUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class PdfAction extends ActionSupport {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url;
    private long orgId;
    private String username;
    private InputStream fileInputStream;

    private static Logger log = LogManager.getLogger(PdfAction.class.getName());

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public InputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(InputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public String createPdf() {
        String fileName  = PdfUtil.convertUrlToPdf(getOrgId(), getUsername(), getUrl(), null);
        if(fileName != null){
            try {
                fileInputStream = new FileInputStream(new File(fileName));
            } catch (FileNotFoundException e) {
                log.info("Exception occurred ", e);
            }
            return Action.SUCCESS;
        }
        return Action.ERROR;
    }
}
