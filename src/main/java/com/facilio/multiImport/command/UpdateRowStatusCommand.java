package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.logging.Logger;

public class UpdateRowStatusCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(UpdateRowStatusCommand.class.getName());
    private static final List<FacilioField> IMPORT_LOG_UPDATE_FIELDS = Collections.unmodifiableList(geImportLogtUpdateField());
    ImportDataDetails importDataDetails = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("UpdateRowStatusCommand started time:"+System.currentTimeMillis());

        List<ImportRowContext> rowContextList = ImportConstants.getRowContextList(context);
        ImportFileSheetsContext importSheet = ImportConstants.getImportSheet(context);
        importDataDetails = ImportConstants.getImportDataDetails(context);

        boolean hasErrorRecords = importDataDetails.isHasErrorRecords();  //old value

        FacilioModule module = ModuleFactory.getMultiImportProcessLogModule();

        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();

        for (ImportRowContext rowContext : rowContextList) {
            long logId = rowContext.getId();

            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchValue.setWhereId(logId);

            Map<String, Object> prop = FieldUtil.getAsProperties(rowContext);

            batchValue.setUpdateValue(prop);
            batchUpdates.add(batchValue);

            if(!hasErrorRecords && rowContext.isErrorOccurredRow()){
              hasErrorRecords = true;
            }
        }
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(IMPORT_LOG_UPDATE_FIELDS)
                .table(module.getTableName());
        updateBuilder.batchUpdateById(batchUpdates);

        if(!importDataDetails.isHasErrorRecords() && hasErrorRecords){
         importDataDetails.setHasErrorRecords(true);  // update has error record for error sheet download in ui
         MultiImportApi.updateImportDataDetails(importDataDetails);
        }

        LOGGER.info("UpdateRowStatusCommand completed time:"+System.currentTimeMillis());
        return false;
    }


    private static List<FacilioField> geImportLogtUpdateField() {
        List<FacilioField> fields = FieldFactory.getMultiImportProcessLogFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get("errorMessage"));
        updateFields.add(fieldMap.get("rowStatus"));
        updateFields.add(fieldMap.get("errorOccurredRow"));

        return updateFields;
    }

}
