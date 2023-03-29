package com.facilio.multiImport.command;

import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.multiImport.enums.ImportDataStatus;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddImportDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportDataDetails importDataDetails = new ImportDataDetails();


        importDataDetails.setModifiedTime(DateTimeUtil.getCurrenTime());
        importDataDetails.setModifiedBy(AccountUtil.getCurrentUser().getOuid());
        importDataDetails.setCreatedTime(DateTimeUtil.getCurrenTime());
        importDataDetails.setCreatedBy(AccountUtil.getCurrentUser().getOuid());

        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getImportDataDetailsModule().getTableName())
                .fields(FieldFactory.getImportDataDetailsFields());

        Map<String, Object> props = FieldUtil.getAsProperties(importDataDetails);
        insertRecordBuilder.addRecord(props);
        insertRecordBuilder.save();

        Long importId = (Long) props.get("id");
        importDataDetails.setId(importId);

        context.put(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, importDataDetails);

        return false;
    }
}
