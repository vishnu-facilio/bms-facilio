package com.facilio.pdf.actions;

import com.facilio.pdf.PdfUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PdfAction extends ActionSupport {

    private String url;
    private long orgId;
    private String username;
    private InputStream fileInputStream;

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
        String fileName  = PdfUtil.convertUrlToPdf(getOrgId(), getUsername(), getUrl());
        if(fileName != null){
            try {
                fileInputStream = new FileInputStream(new File(fileName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return Action.SUCCESS;
        }
        return Action.ERROR;
    }
}
