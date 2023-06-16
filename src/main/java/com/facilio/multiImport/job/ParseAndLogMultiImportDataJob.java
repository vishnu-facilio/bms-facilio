package com.facilio.multiImport.job;

import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.backgroundactivity.util.ChildActivityService;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.multiImport.importFileReader.AbstractImportFileReader;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

public class ParseAndLogMultiImportDataJob extends FacilioJob {
    public static final String JOB_NAME = "ParseAndLogMultiImportDataJob";
    private static final Logger LOGGER = Logger.getLogger(ParseAndLogMultiImportDataJob.class.getName());

    ImportDataDetails importDataDetails = null;
    Long importId = null;
    Integer processedRowCount = 0;

    private static final List<FacilioField> IMPORT_SHEET_UPDATE_FIELDS = Collections.unmodifiableList(getSheetUpdateFields());

    BackgroundActivityService backgroundActivityService = null;

    ChildActivityService parsingActivityService = null;

    @Override
    public void execute(JobContext jobContext) throws Exception {

        try {
            importId = jobContext.getJobId();
            backgroundActivityService = new BackgroundActivityService(BackgroundActivityAPI.parentActivityForRecordIdAndType(importId,"import"));
            ChildActivityService childActivityService = null;
            if(backgroundActivityService != null) {
                backgroundActivityService.deleteAllChildActivities();
                backgroundActivityService.updateActivity(1,"Parsing started for importId:" + importId);
                childActivityService = backgroundActivityService.addChildActivity(importId,"import","Parsing Service for import",null);
                parsingActivityService = childActivityService;
            }
            LOGGER.info("ParseAndLogMultiImportDataJob called for importId---------- " + importId);
            importDataDetails = MultiImportApi.getImportData(importId);

            List<ImportFileContext> importFiles = MultiImportApi.getImportFilesByImportId(importId, true);

            importDataDetails.setImportFiles(importFiles);
            List<ImportFileSheetsContext> importSheets = MultiImportApi.getSortedImportSheetsFromAllFiles(importFiles);
            if(backgroundActivityService != null) {
                for (ImportFileSheetsContext importSheet : importSheets) {
                    new ChildActivityService(backgroundActivityService.getActivityId(), importSheet.getId(), "import_sheet", "Import For Sheet -> " + importSheet.getName(), null);
                }
            }
            if(childActivityService != null) {
                childActivityService.updateActivity(2, "Initial loading started for import");
            }
            HashMap<Long, AbstractImportFileReader> importFileIdVsImportReaderMap = getImportFiledIdVsImportReaderMap(importFiles);
            importDataDetails.setTotalRecords(MultiImportApi.getImportTotalRecordsCount(importFiles));
            if(childActivityService != null) {
                childActivityService.updateActivity(10, "Loaded the sheet(s) for import");
            }
            LOGGER.info("Parsing started for ImportId:" + importId); //parsing status updated in parseAndImportData API
            //parsing
            parseImportSheet(importFileIdVsImportReaderMap, importSheets, childActivityService);

            importDataDetails.setStatus(ImportDataStatus.PARSING_COMPLETED);

            MultiImportApi.updateImportDataDetails(importDataDetails);
            LOGGER.info("Parsing completed for ImportId:" + importId);

            closeAllReadersStreams(importFileIdVsImportReaderMap);

            // remove any existing job
            LOGGER.info("MultiImportDataJob scheduled for importId:" + importId);
            FacilioTimer.deleteJob(importId, MultiImportDataJob.JOB_NAME);
            FacilioTimer.scheduleOneTimeJobWithTimestampInSec(importId, MultiImportDataJob.JOB_NAME, 10, "priority");

        } catch (Exception e) {
            LOGGER.severe("MultiImport Parsing failed for importId:" + importId);
            LOGGER.severe("Exception:" + e);
            importDataDetails.setStatus(ImportDataStatus.PARSING_FAILED);
            if(parsingActivityService != null) {
                parsingActivityService.failActivity("Parsing failed due to exception");
            }
            MultiImportApi.updateImportStatus(importDataDetails);
            e.printStackTrace();
        }

    }

    private void parseImportSheet(Map<Long, AbstractImportFileReader> importFileIdVsImportReaderMap, List<ImportFileSheetsContext> importSheets, ChildActivityService childActivityService) throws Exception {
        int totalSheets = importSheets.size();
        int i = 0;
        for (ImportFileSheetsContext importSheet : importSheets) {
            i++;
            if(childActivityService != null) {
                childActivityService.updateActivity(((i / totalSheets) * 100) - 10, "Parsing started for the sheet(s)");
            }
            try {
                FacilioModule module = importSheet.getModule();
                String moduleName = importSheet.getModuleName();
                if (module == null) {   //skip if module not mapped for any sheet
                    continue;
                }

                importSheet.setStatus(ImportDataStatus.PARSING_STARTED);
                MultiImportApi.updateImportSheetStaus(importSheet);

                FacilioChain chain = MultiImportChainUtil.getParseChain(moduleName);
                FacilioContext parseContext = chain.getContext();
                parseContext.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
                parseContext.put(FacilioConstants.ContextNames.IMPORT_SHEET, importSheet);
                parseContext.put(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS,importDataDetails);
                parseContext.put(FacilioConstants.ContextNames.IMPORT_FILE_READERS_MAP,importFileIdVsImportReaderMap);
                parseContext.put(FacilioConstants.ContextNames.PROCESSED_ROW_COUNT,processedRowCount);
                chain.execute();

                updateSheetDetails(importSheet);
                processedRowCount =(int)parseContext.get(FacilioConstants.ContextNames.PROCESSED_ROW_COUNT);

            } catch (Exception e) {
                String message = e.getMessage();
                importSheet.setStatus(ImportDataStatus.PARSING_FAILED);
                MultiImportApi.updateImportSheetStaus(importSheet);
                if(childActivityService != null) {
                    childActivityService.failActivity("Parsing failed for sheetId - " + importSheet.getId() + " :" + message);
                }
                LOGGER.severe("Parsing failed for sheetId - " + importSheet.getId() + " :" + message);
                throw e;
            }
        }
        if(childActivityService != null) {
            childActivityService.completeActivity("Parsing completed for the sheet(s)");
        }
    }

    private static void updateSheetDetails(ImportFileSheetsContext importSheet) throws Exception {

        importSheet.setStatus(ImportDataStatus.PARSING_COMPLETED);

        MultiImportApi.batchUpdateImportSheetBySheetId(Arrays.asList(importSheet), IMPORT_SHEET_UPDATE_FIELDS);
    }

    private static HashMap<Long, AbstractImportFileReader> getImportFiledIdVsImportReaderMap(List<ImportFileContext> importFiles) throws Exception {
        HashMap<Long, AbstractImportFileReader> importFileIdVsImportReaderMap = new HashMap<>();

        for (ImportFileContext importFile : importFiles) {

            long fileId = importFile.getFileId();
            FileStore fs = FacilioFactory.getFileStore();
            InputStream is = fs.readFile(fileId);

            String fileType = MultiImportApi.getFileType(importFile);
            AbstractImportFileReader fileReader = null;

            try {
                fileReader = MultiImportApi.getImportFileReader(fileType, is);
            } catch (Exception e) {
                LOGGER.severe("File Reader creation failed for :" + importFile.getId() + " Reason:" + e.toString());
                e.printStackTrace();
                throw e;
            }
            importFileIdVsImportReaderMap.put(importFile.getId(), fileReader);

        }
        return importFileIdVsImportReaderMap;
    }

    private static void closeAllReadersStreams(HashMap<Long, AbstractImportFileReader> importFileIdVsImportReaderMap) throws IOException {
        for (Map.Entry<Long, AbstractImportFileReader> key : importFileIdVsImportReaderMap.entrySet()) {
            AbstractImportFileReader reader = key.getValue();
            reader.close();
        }
    }

    private static List<FacilioField> getSheetUpdateFields() {
        List<FacilioField> fields = FieldFactory.getImportFileSheetsFields();
        Map<String, FacilioField> mapFields = FieldFactory.getAsMap(fields);
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(mapFields.get("processedRowCount"));
        updateFields.add(mapFields.get("status"));
        return updateFields;
    }
}


