package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.services.factory.FacilioFactory;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;

public class AgentFileDownloadAction extends ActionSupport {
    private static final Logger LOGGER = LogManager.getLogger(AgentFileDownloadAction.class.getName());

    public String fileName;
    public Long orgId;
    public Long fileId;
    public Long agentId;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileInputStream fileInputStream;

    public FileInputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    public String downloadFile() {
        LOGGER.info("Params : " + fileName + " : " + orgId + " : " + fileId + " : " + agentId);
        String key = "Org_" + this.orgId
                + "Agent_" + this.agentId
                + System.currentTimeMillis()
                + "agent.tmp";

        File file = new File("/tmp/" + key);
        try {
            AccountUtil.setCurrentAccount(orgId);
            try (FileOutputStream outputStream = new FileOutputStream(file); InputStream inputStream = FacilioFactory.getFileStore().readFile(fileId);) {

                int read;
                byte[] bytes = new byte[1024];

                while (inputStream != null && (read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

            } catch (IOException e) {
                LOGGER.info("Error while downloading file " + e.getMessage());
            }
            fileInputStream = new FileInputStream(file);
            return SUCCESS;
        } catch (Exception e) {

            return ERROR;
        }
    }
}
