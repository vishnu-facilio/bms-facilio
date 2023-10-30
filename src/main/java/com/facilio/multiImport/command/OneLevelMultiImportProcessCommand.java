package com.facilio.multiImport.command;


import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public  class OneLevelMultiImportProcessCommand extends BaseMultiImportProcessCommand {
    private List<ImportFieldMappingContext> currentModuleFieldMappings;
    private String moduleName;
    private Map<Long, ModuleBaseWithCustomFields> logIdVsLookupRecord;
    private long currentLogId;
    private static final Logger LOGGER = Logger.getLogger(OneLevelMultiImportProcessCommand.class.getName());

    public OneLevelMultiImportProcessCommand(String moduleName,
                                             List<ImportFieldMappingContext> currentModuleFieldMappings){
        this.moduleName = moduleName;
        this.currentModuleFieldMappings = currentModuleFieldMappings;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("OneLevelMultiImportProcessCommand started time:"+System.currentTimeMillis());

        init(context);
        processRecords(batchRows);
        ImportConstants.setRowContextList(context, batchRows);

        LOGGER.info("OneLevelMultiImportProcessCommand completed time:"+System.currentTimeMillis());

        return false;
    }
    protected void init(Context context) throws Exception{
        super.init(context);
        logIdVsLookupRecord = (Map<Long, ModuleBaseWithCustomFields>) context.get(MultiImportApi.ImportProcessConstants.LOG_ID_VS_OLD_RECORD_MAP);
    }
    private void processRecords(List<ImportRowContext> rows) throws Exception {
        for (ImportRowContext rowContext : rows) {
            currentLogId = rowContext.getId();
            if (isInsertImport()) {
                checkMandatoryFieldsValueExistsOrNot(rowContext);
            }

            if(rowContext.isErrorOccurredRow()){ //skip if is row is not valid
                continue;
            }

            HashMap<String, Object> processedProps = getProcessedRawRecordMap(rowContext);

            rowContext.setProcessedRawRecordMap(processedProps);
        }
    }

    @Override
    protected List<ImportFieldMappingContext> getCurrentModuleFieldMappings() {
        return this.currentModuleFieldMappings;
    }

    @Override
    protected String getModuleName() {
        return this.moduleName;
    }

    @Override
    protected boolean isOneLevel() {
        return true;
    }

    @Override
    protected boolean isInsertImport() {
        if(MapUtils.isEmpty(logIdVsLookupRecord)){
            return true;
        }
        if(logIdVsLookupRecord.containsKey(currentLogId) && logIdVsLookupRecord.get(currentLogId).getId()>0){
            return false;
        }
        return true;
    }
}
