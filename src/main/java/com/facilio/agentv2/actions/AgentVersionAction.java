package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.upgrade.AgentVersionApi;
import com.facilio.service.FacilioService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;

public class AgentVersionAction extends AgentActionV2 {

    private static final Logger LOGGER = LogManager.getLogger(AgentVersionAction.class.getName());

    @NotNull
    private String version;

    @NotNull
    private String description;

    @NotNull
    private String createdBy;

    @NotNull
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String addAgentVersion() {
        try {
            FacilioService.runAsService(() -> AgentVersionApi.addAgentVersion(version, description, createdBy, fileName));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while adding agent version", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
}
