package com.facilio.bmsconsoleV3.commands.readingimportapp;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Map;


public class AddReadingImportAppDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3ReadingImportAppContext readingImportContext = (V3ReadingImportAppContext) context.get("READING_IMPORT_CONTEXT");

        FacilioUtil.throwIllegalArgumentException(readingImportContext == null, "Reading import context should not be null");

        readingImportContext.setCreatedBy(AccountUtil.getCurrentUser().getOuid());
        readingImportContext.setCreatedTime(System.currentTimeMillis());

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getReadingImportAPPModule().getTableName())
                .fields(FieldFactory.getReadingImportFields());

        Map<String, Object> props = FieldUtil.getAsProperties(readingImportContext);

        insertBuilder.addRecord(props);
        insertBuilder.save();

        long importId = (Long) props.get("id");
        readingImportContext.setId(importId);
        context.put("READING_IMPORT_CONTEXT", readingImportContext);

        return false;
    }
}
