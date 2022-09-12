package com.facilio.multiImport.action;

import com.facilio.multiImport.chain.MultiImportChain;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.context.ImportFileSheetsContext;

import java.io.File;
import java.util.List;

public class MultiImportDataAction extends FacilioAction {

    ImportDataDetails importDataDetails;

    public ImportDataDetails getImportDataDetails() {
        return importDataDetails;
    }

    public void setImportDataDetails(ImportDataDetails importDataDetails) {
        this.importDataDetails = importDataDetails;
    }

    private List<File> uploadedFiles;

    public List<File> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<File> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public long importId = -1;

    public long getImportId() {
        return importId;
    }

    public void setImportId(long importId) {
        this.importId = importId;
    }

    String fileUploadContentType;

    public String getFileUploadContentType() {
        return fileUploadContentType;
    }

    public void setFileUploadContentType(String fileUploadContentType) {
        this.fileUploadContentType = fileUploadContentType;
    }

    List<ImportFileSheetsContext> importSheetList;

    public void setImportSheetList(List<ImportFileSheetsContext> importSheetList) {
        this.importSheetList = importSheetList;
    }

    public List<ImportFileSheetsContext> getImportSheetList() {
        return importSheetList;
    }

    public String createImportDataDetails() throws Exception{
        FacilioChain chain = MultiImportChain.getCreateImportDataChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setResult(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS,context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS));
        return SUCCESS;
    }

    public String addImportFile() throws Exception{
        FacilioChain chain = MultiImportChain.getImportFileUploadChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FILE, uploadedFiles);
        context.put(FacilioConstants.ContextNames.IMPORT_ID,importId);
        context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, fileUploadContentType);
        chain.execute();

        setResult(FacilioConstants.ContextNames.IMPORT_FILE_DETAILS,context.get(FacilioConstants.ContextNames.IMPORT_FILE_DETAILS));
        return SUCCESS;
    }

    public String sheetMapping() throws Exception{
        FacilioChain chain = MultiImportChain.getImportSheetMappingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_SHEET_LIST,importSheetList);
        chain.execute();

        setResult(FacilioConstants.ContextNames.IMPORT_SHEETS,context.get(FacilioConstants.ContextNames.IMPORT_SHEETS));
        return SUCCESS;
    }
}
