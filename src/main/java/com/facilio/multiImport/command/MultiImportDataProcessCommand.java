package com.facilio.multiImport.command;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.backgroundactivity.util.ChildActivityService;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.config.ImportConfig;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.*;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.job.DownloadMultiImportErrorRecordsJob;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Logger;

public class MultiImportDataProcessCommand extends FacilioCommand  implements PostTransactionCommand {
    private static final Logger LOGGER = Logger.getLogger(MultiImportDataProcessCommand.class.getName());
    private static final int BATCH_SIZE = 5000;
    private static final int MAXIMUM_RECORDS_PER_THREAD = 25000;
    private static final List<FacilioField> IMPORT_SHEET_UPDATE_FIELDS = Collections.unmodifiableList(getSheetUpdateFields());
    ImportDataDetails importDataDetails = null;
    long recordsProcessedCount = 0;
    Long importId = null;
    String errorMessage = null;

    BackgroundActivityService backgroundActivityService = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        importDataDetails = (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);

        //background activity service
        backgroundActivityService = new BackgroundActivityService(BackgroundActivityAPI.parentActivityForRecordIdAndType(importId, "import"));

        List<ImportFileContext> importFiles = importDataDetails.getImportFiles();
        List<ImportFileSheetsContext> importSheets = MultiImportApi.getSortedImportSheetsFromAllFiles(importFiles);

        checkImportProcessdRecordsCountAndUpdateTheImportStatus();

        importData(importSheets);

        checkImportProcessdRecordsCountAndUpdateTheImportStatus();

        if(backgroundActivityService != null) {
            backgroundActivityService.completeActivity("Import Completed");
        }
        
        return false;
    }

    private void importData(List<ImportFileSheetsContext> importSheets) throws Exception {
        for (ImportFileSheetsContext importSheet : importSheets) {
            ChildActivityService childService = null;
            if(backgroundActivityService != null) {
                childService = new ChildActivityService(BackgroundActivityAPI.getChildActivity(backgroundActivityService.getActivityId(), importSheet.getId(), "import_sheet"));
            }
            try {
                ImportDataStatus sheetsStatus = importSheet.getStatusEnum();

                if (ImportDataStatus.IMPORT_COMPLETED == sheetsStatus || ImportDataStatus.IMPORT_FAILED == sheetsStatus) { // if sheet import completed or import failed continue next sheet
                    continue;
                }

                if (ImportDataStatus.IMPORT_STARTED != sheetsStatus) {    //update import started status for the current sheet if first thread enters
                    importSheet.setStatus(ImportDataStatus.IMPORT_STARTED);
                    MultiImportApi.updateImportSheetStaus(importSheet);
                    if(childService != null) {
                        childService.updateActivity(1, "Started Importing Sheet " + importSheet.getName() + " Module Name " + importSheet.getModuleName());
                    }
                    LOGGER.info("Import started for sheetId ---:" + importSheet.getId());
                }

                batchImportDataFromSheet(importSheet,childService);

                if (recordsProcessedCount >= MAXIMUM_RECORDS_PER_THREAD) {
                    LOGGER.info("Maximum records per thread reached");
                    return;
                }
            } catch (Exception e) {
                errorMessage = "Import failed for sheetId - " + importSheet.getId() + " :" + e;
                LOGGER.severe(errorMessage);
                if(backgroundActivityService != null && importSheet != null) {
                    backgroundActivityService.failActivity("Import failed for sheet name " + importSheet.getName() + " Module Name " + importSheet.getModuleName());
                }
                throw e;
            }
        }
    }

    private void batchImportDataFromSheet(ImportFileSheetsContext importSheet,ChildActivityService childService) throws Exception {
        LOGGER.info("Batch import started for sheetId ---:" + importSheet.getId());
        FacilioModule module = importSheet.getModule();
        String moduleName = importSheet.getModuleName();

        long processedRowCount = importSheet.getProcessedRowCount();
        long totalRowCount = importSheet.getRowCount();

        long remainingRecordsToImport = totalRowCount - processedRowCount;
        LOGGER.info("remainingRecordsToImport:" + remainingRecordsToImport);
        long remainingCapacity = MAXIMUM_RECORDS_PER_THREAD - recordsProcessedCount;
        LOGGER.info("remainingCapacity:" + remainingCapacity);
        if (remainingCapacity < remainingRecordsToImport) {
            remainingRecordsToImport = remainingCapacity;
        }

        long splitSize = remainingRecordsToImport / BATCH_SIZE;

        int remainder = (int) remainingRecordsToImport % BATCH_SIZE;
        if (remainder != 0) {
            splitSize += 1;
        }

        LOGGER.info("Split size for sheet id:" + importSheet.getId() + " is " + splitSize);
        childService.updateActivity(10,"0 / " + totalRowCount + " records imported");
        for (int i = 1; i <= splitSize; i++) {

            int currentRecordsInChunk = BATCH_SIZE;

            if (i == splitSize && remainder != 0) {
                currentRecordsInChunk = remainder;
            }

            recordsProcessedCount += currentRecordsInChunk;


            LOGGER.info(i + "/" + splitSize + " batch started------start time:"+System.currentTimeMillis());
            LOGGER.info("currentRecordsInChunk:" + currentRecordsInChunk);

            FacilioChain chain = MultiImportChainUtil.getImportProcessChain(moduleName);        //import the sheet data
            ImportConfig importConfig = MultiImportChainUtil.getMultiImportConfig(moduleName);
            Class beanClass = MultiImportChainUtil.getBeanClass(importConfig, module);

            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            context.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
            context.put(FacilioConstants.ContextNames.IMPORT_SHEET, importSheet);
            context.put(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, importDataDetails);
            context.put(FacilioConstants.ContextNames.CHUNK_LIMIT, currentRecordsInChunk);
            context.put(Constants.BEAN_CLASS, beanClass);

            chain.execute();

            LOGGER.info("recordsProcessedCount out of 25000:" + recordsProcessedCount);
            LOGGER.info(i + "/" + splitSize + " batch completed------completed time:"+System.currentTimeMillis());
            updateSheetDetails(importSheet, context);
            if(childService != null) {
                childService.updateActivity((int) (((float) i / (float) splitSize) * 100), (i * BATCH_SIZE) + " / " + totalRowCount + " records imported");
            }
        }
        LOGGER.info("currently processed records count :" + remainingRecordsToImport);
        LOGGER.info("Batch import completed for sheetId ---:" + importSheet.getId());
        if(childService != null) {
            childService.completeActivity("Import Completed");
        }
    }

    private void checkImportProcessdRecordsCountAndUpdateTheImportStatus() throws Exception {
        if (importDataDetails == null) {
            return;
        }
        List<ImportFileContext> importFiles = importDataDetails.getImportFiles();
        long totalImportRecordsCount = MultiImportApi.getImportTotalRecordsCount(importFiles);
        long processedRecordsCount = MultiImportApi.getImportProcessedRecordsCount(importFiles);

        importDataDetails.setTotalRecords(totalImportRecordsCount);
        importDataDetails.setProcessedRecordsCount(processedRecordsCount);

        if (processedRecordsCount == 0) {
            importDataDetails.setStatus(ImportDataStatus.IMPORT_STARTED);
            importDataDetails.setImportStartTime(DateTimeUtil.getCurrenTime());
            MultiImportApi.updateImportDataDetails(importDataDetails);
            LOGGER.info("MultiImport Started for multiImportId--- :" + importDataDetails.getId());
        } else if (totalImportRecordsCount == processedRecordsCount || processedRecordsCount==MAXIMUM_RECORDS_PER_THREAD) {
            importDataDetails.setStatus(ImportDataStatus.IMPORT_COMPLETED);
            importDataDetails.setImportEndTime(DateTimeUtil.getCurrenTime());
            MultiImportApi.updateImportDataDetails(importDataDetails);
            LOGGER.info("MultiImport Completed  for multiImportId--- :" + importDataDetails.getId());
            if(importDataDetails.isHasErrorRecords()){
                FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importId, DownloadMultiImportErrorRecordsJob.JOB_NAME, 10, "priority");
            }
        }
    }

    private void updateSheetDetails(ImportFileSheetsContext importSheet, Context context) throws Exception {
        List<ImportRowContext> batchRows = ImportConstants.getRowContextList(context);

        if (CollectionUtils.isEmpty(batchRows)) {
            return;
        }

        ImportRowContext lastRowContext = batchRows.get(batchRows.size() - 1);
        importSheet.setLastRowIdTaken(lastRowContext.getId());

        Integer insertRecordsCount = (Integer) context.getOrDefault(ImportConstants.INSERT_RECORDS_COUNT,0);
        Integer updateRecordsCount = (Integer) context.getOrDefault(ImportConstants.UPDATE_RECORDS_COUNT,0);
        Integer skipRecordsCount = (Integer) context.getOrDefault(ImportConstants.SKIP_RECORDS_COUNT,0);
        importSheet.setInsertCount(importSheet.getInsertCount()+insertRecordsCount);
        importSheet.setUpdateCount(importSheet.getUpdateCount()+updateRecordsCount);
        importSheet.setSkipCount(importSheet.getSkipCount()+skipRecordsCount);

        long processedRowCount = importSheet.getProcessedRowCount();
        processedRowCount += batchRows.size();
        importSheet.setProcessedRowCount(processedRowCount);

        long totalSheetRowCount = importSheet.getRowCount();

        if (processedRowCount == totalSheetRowCount) {    //update sheet import complete once the all rows are processed
            importSheet.setStatus(ImportDataStatus.IMPORT_COMPLETED);
            LOGGER.info("Import completed for sheetId --- :" + importSheet.getId());
        }
        MultiImportApi.batchUpdateImportSheetBySheetId(Arrays.asList(importSheet), IMPORT_SHEET_UPDATE_FIELDS);
    }
    @Override
    public boolean postExecute() throws Exception {
        if (StringUtils.isNotEmpty(errorMessage)) {
            handleException();
        }else{
            ImportDataStatus status = importDataDetails.getStatusEnum();

            if(status == ImportDataStatus.IMPORT_COMPLETED){
                Map<String,Object> clientJson = new HashMap<>();//send import completed status to wms
                clientJson.put("status",ImportDataStatus.IMPORT_COMPLETED);
                clientJson.put("importStartTime",importDataDetails.getImportStartTime());
                clientJson.put("importEndTime",importDataDetails.getImportEndTime());
                clientJson.put("hasErrorRecords",importDataDetails.isHasErrorRecords());
                clientJson.put("percentage",100);
                MultiImportApi.sendMultiImportProgressToClient(importDataDetails,clientJson);

                long ouid = importDataDetails.getCreatedBy();               //send email if import completed
                User user = AccountUtil.getUserBean().getUserInternal(ouid);
                String message = "Your recent import has been successfully completed";
                MultiImportApi.sendImportCompletedEmail(importDataDetails,user.getEmail(),message);
                LOGGER.info("Import email sent for MultiImportDataJob:" + importId + "to" + user.getEmail());
            }

        }
        return false;
    }

    @Override
    public void onError() throws Exception {
        handleException();
    }
    private void handleException() throws Exception {
        if (importDataDetails != null) {
            importDataDetails.setErrorMessage(errorMessage);
            importDataDetails.setStatus(ImportDataStatus.IMPORT_FAILED);
            MultiImportApi.updateImportDataDetails(importDataDetails);
            Map<String,Object> clientJson = new HashMap<>();
            clientJson.put("status",ImportDataStatus.IMPORT_FAILED);
            clientJson.put("errorMessage",errorMessage);
            clientJson.put("importStartTime",importDataDetails.getImportStartTime());
            clientJson.put("importEndTime",importDataDetails.getImportEndTime());
            MultiImportApi.sendMultiImportProgressToClient(importDataDetails,clientJson);

            long ouid = importDataDetails.getCreatedBy();
            User user = AccountUtil.getUserBean().getUserInternal(ouid);
            String message = "Your recent import has been failed";
            MultiImportApi.sendImportCompletedEmail(importDataDetails,user.getEmail(),message);
            MultiImportApi.sendImportCompletedEmail(importDataDetails,"gowthams@facilio.com",message);
        }
    }

    private static List<FacilioField> getSheetUpdateFields() {
        List<FacilioField> fields = FieldFactory.getImportFileSheetsFields();
        Map<String, FacilioField> mapFields = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(mapFields.get("lastRowIdTaken"));
        updateFields.add(mapFields.get("processedRowCount"));
        updateFields.add(mapFields.get("status"));
        updateFields.add(mapFields.get("insertCount"));
        updateFields.add(mapFields.get("skipCount"));
        updateFields.add(mapFields.get("updateCount"));
        return updateFields;
    }
}
