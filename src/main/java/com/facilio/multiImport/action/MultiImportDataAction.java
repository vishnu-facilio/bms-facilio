package com.facilio.multiImport.action;

import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.multiImport.chain.MultiImportChain;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class MultiImportDataAction extends V3Action {

    public long importId = -1;
    ImportDataDetails importDataDetails;
    private List<File> file;
    private List<String> fileContentType;
    List<ImportFileSheetsContext> importSheetList;
    private Long importFileId = -1L;
    ImportFileSheetsContext importSheet;
    private boolean myImport;
    private boolean skipValidation;

    public String createImportDataDetails() throws Exception {
        FacilioChain chain = MultiImportChain.getCreateImportDataChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setData(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS));
        return SUCCESS;
    }
    public String fetchImportDataDetails() throws Exception {
        FacilioChain chain = MultiImportChain.getImportDataDetails();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        chain.execute();

        setData(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS));
        return SUCCESS;
    }

    public String addImportFile() throws Exception {
        FacilioChain chain = MultiImportChain.getImportFileUploadChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.FILE, file);
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, fileContentType);
        chain.execute();

        setData(FacilioConstants.ContextNames.IMPORT_FILE_LIST, context.get(FacilioConstants.ContextNames.IMPORT_FILE_LIST));
        return SUCCESS;
    }

    public String sheetMapping() throws Exception {
        FacilioChain chain = MultiImportChain.getImportSheetMappingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_SHEET_LIST, importSheetList);
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        chain.execute();

        setData(FacilioConstants.ContextNames.IMPORT_SHEETS, context.get(FacilioConstants.ContextNames.IMPORT_SHEETS));
        return SUCCESS;
    }

    public String predictSheetExecution() throws Exception {
        FacilioChain chain = MultiImportChain.getPredictSheetExecutionChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        chain.execute();

        setData(FacilioConstants.ContextNames.IS_PREDICTABLE, context.get(FacilioConstants.ContextNames.IS_PREDICTABLE));
        setData(FacilioConstants.ContextNames.IMPORT_SHEETS, context.get(FacilioConstants.ContextNames.IMPORT_SHEETS));
        return SUCCESS;
    }

    public String reorderSheetsExecution() throws Exception {
        FacilioChain chain = MultiImportChain.getReorderSheetExecutionChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_SHEET_LIST, importSheetList);
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        chain.execute();

        setData(FacilioConstants.ContextNames.IMPORT_SHEETS, context.get(FacilioConstants.ContextNames.IMPORT_SHEETS));
        return SUCCESS;
    }

    public String getMultiImportFileList() throws Exception {
        FacilioChain chain = MultiImportChain.getImportFileListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        chain.execute();

        setData(FacilioConstants.ContextNames.IMPORT_FILE_LIST, context.get(FacilioConstants.ContextNames.IMPORT_FILE_LIST));
        return SUCCESS;
    }

    public String getMultiImportSheetList() throws Exception {
        FacilioChain chain = MultiImportChain.getImportSheetsListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        chain.execute();

        setData(FacilioConstants.ContextNames.IMPORT_SHEETS, context.get(FacilioConstants.ContextNames.IMPORT_SHEETS));
        return SUCCESS;
    }

    public String deleteImportFile() throws Exception {
        FacilioChain chain = MultiImportChain.getDeleteImportFileChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        context.put(FacilioConstants.ContextNames.IMPORT_FILE_ID, importFileId);

        chain.execute();
        return SUCCESS;
    }

    public String fieldMapping() throws Exception {
        FacilioChain chain = MultiImportChain.getImportFieldMappingChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_SHEET, importSheet);
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        context.put(FacilioConstants.ContextNames.SKIP_VALIDATION,skipValidation);
        chain.execute();
        setData(FacilioConstants.ContextNames.IMPORT_SHEET, context.get(FacilioConstants.ContextNames.IMPORT_SHEET));
        return SUCCESS;
    }

    public String count() throws Exception {
        FacilioChain chain = MultiImportChain.getCountChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.MULTI_MODULE_IMPORT);
        context.put(FacilioConstants.ContextNames.MY_IMPORT, myImport);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        context.put(FacilioConstants.ContextNames.MY_IMPORT, myImport);

        if (StringUtils.isNotEmpty(getFilters())) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(getFilters());
            context.put(Constants.FILTERS, json);
        }
        chain.execute();

        setData(Constants.COUNT, context.get(Constants.COUNT));
        return SUCCESS;

    }

    public String parseAndImportData() throws Exception {

        new BackgroundActivityService(importId,"import","Import Id: #"+importId,"Scheduled Import Id: #"+importId+".");
        FacilioChain chain = MultiImportChain.scheduleParseAndImportDataJob();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        chain.execute();

        return SUCCESS;
    }


    public String importData() throws Exception {

        FacilioChain chain = MultiImportChain.scheduleV3_MultiImportDataJob();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
        chain.execute();
        return SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = MultiImportChain.getImportListChain();
        FacilioContext context = chain.getContext();
        JSONObject pagination = new JSONObject();
        pagination.put("page", getPage());
        pagination.put("perPage", getPerPage());
        if (getPerPage() < 0) {
            pagination.put("perPage", 5000);
        }
        if (StringUtils.isNotEmpty(getFilters())) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(getFilters());
            context.put(Constants.FILTERS, json);
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.MULTI_MODULE_IMPORT);
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
        context.put(FacilioConstants.ContextNames.MY_IMPORT, myImport);

        chain.execute();

        setData(FacilioConstants.ContextNames.IMPORT_LIST, context.get(FacilioConstants.ContextNames.IMPORT_LIST));
        setData(FacilioConstants.ContextNames.SUPPLEMENTS, context.get(FacilioConstants.ContextNames.SUPPLEMENTS));
        return SUCCESS;
    }
    public String downloadErrorRecords() throws Exception{
        FacilioChain chain = MultiImportChain.getDownloadErrorRecordsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);

        chain.execute();

        setData(FacilioConstants.ContextNames.FILE_URL,context.get(FacilioConstants.ContextNames.FILE_URL));
        setData(FacilioConstants.ContextNames.MESSAGE,context.get(FacilioConstants.ContextNames.MESSAGE));
        return SUCCESS;
    }
    public String abortImport() throws Exception {
        FacilioChain chain = MultiImportChain.getAbortChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID,importId);

        chain.execute();
        setData(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS));
        return SUCCESS;
    }
    public String summary() throws Exception{
        FacilioChain chain = MultiImportChain.getMultiImportSummary();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.IMPORT_ID,importId);

        chain.execute();
        setData(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS));
        return SUCCESS;
    }
    public String getMultiImportModules() throws Exception{
        FacilioChain chain = MultiImportChain.getMultiImportModulesChain();
        FacilioContext context = chain.getContext();

        chain.execute();

        setData(FacilioConstants.ContextNames.MODULE_LIST,context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }
    public String getMultiImportFields() throws Exception{
        FacilioChain chain = MultiImportChain.getMultiImportFieldsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,getModuleName());
        chain.execute();

        setData(FacilioConstants.ContextNames.FIELDS,context.get(FacilioConstants.ContextNames.FIELDS));
        return SUCCESS;
    }
}
