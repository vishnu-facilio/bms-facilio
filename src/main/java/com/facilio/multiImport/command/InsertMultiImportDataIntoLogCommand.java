package com.facilio.multiImport.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.multiImport.context.*;
import com.facilio.multiImport.util.MultiImportApi;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class InsertMultiImportDataIntoLogCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportFileSheetsContext importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);
        ImportDataDetails importDataDetails = (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
        List<ImportRowContext> importRowContextList = (List<ImportRowContext>) context.get(FacilioConstants.ContextNames.IMPORT_ROW_CONTEXT_LIST);
        MultiImportApi.insertIntoMultiImportProcessLogTable(importRowContextList);
        return false;
    }
}
