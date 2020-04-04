package com.facilio.agentv2.actions;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.modbusrtu.ModbusImportUtils;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class UploadFileAction extends AgentActionV2 {

    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(UploadFileAction.class.getName());


    private String fileUploadContentType;
    private String fileUploadFileName;
    @NotNull
    private File fileUpload;

    public Long getId() {
        return id;
    }

    public Long getAgentId() {
        return id;
    }

    public void setAgentId(Long agentId) {
        this.id = agentId;
    }


    public Long getDeviceId() {
        return id;
    }

    public void setDeviceId(Long deviceId) {
        this.id = deviceId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    private Long id;

    public String getFileUploadContentType() {
        return fileUploadContentType;
    }

    public void setFileUploadContentType(String fileUploadContentType) {
        this.fileUploadContentType = fileUploadContentType;
    }

    public String getFileUploadFileName() {
        return fileUploadFileName;
    }

    public void setFileUploadFileName(String fileUploadFileName) {
        this.fileUploadFileName = fileUploadFileName;
    }

    public File getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(File fileUpload) {
        this.fileUpload = fileUpload;
    }

    public String bulkAddModbusDevice() {
        try {
            Organization currentOrg = AccountUtil.getCurrentOrg();
            Objects.requireNonNull(currentOrg, "account null");
            FileStore fileStore = FacilioFactory.getFileStoreFromOrg(currentOrg.getOrgId());
            long fileId = fileStore.addFile(getControllerConfigFileName(getId()), getFileUpload(), "application/xlsx");
            long importId = ModbusImportUtils.addControllersEntry(getAgentId(), fileId);
            ModbusImportUtils.processFileAndSendAddControllerCommand(getAgentId(),getFileAsInputStream());
            ModbusImportUtils.markImportComplete(importId);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while bulk adding modbus controllers", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            e.printStackTrace();
        }
        return SUCCESS;
    }

    private String getControllerConfigFileName(long agentId){
        return getFileName(agentId)+"_"+ModbusImportUtils.CONTROLLER_IMPORT.toString();
    }
    private String getPointsConfigFileName(long deviceId){
        return getFileName(deviceId)+"_"+ModbusImportUtils.POINTS_IMPORT.toString();
    }
    private String getFileName(Long id) {
        Organization currentOrg = AccountUtil.getCurrentOrg();
        Objects.requireNonNull(currentOrg,"account null");
        return currentOrg.getDomain()+"_"+id;
    }



    public String bulkAddModbusPoint() {
        try {
            Organization currentOrg = AccountUtil.getCurrentOrg();
            Objects.requireNonNull(currentOrg, "account null");
            FileStore fileStore = FacilioFactory.getFileStoreFromOrg(currentOrg.getOrgId());
            long fileId = fileStore.addFile(getPointsConfigFileName(getDeviceId()), fileUpload, "application/xlsx");
            long importId = ModbusImportUtils.addPointsImportEntry(getDeviceId(), fileId);
            ModbusImportUtils.processFileAndSendConfigureModbusPointsCommand(getDeviceId(),getFileAsInputStream());
            ModbusImportUtils.markImportComplete(importId);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while bulk adding points", e);
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }



    public void setFileInputStream(InputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    private InputStream fileInputStream;
    private InputStream getFileAsInputStream() throws FileNotFoundException {
        if(fileInputStream != null){
            return fileInputStream;
        }
        fileInputStream = new FileInputStream(fileUpload);
        return fileInputStream;
    }





}
