package com.facilio.bmsconsoleV3.commands.readingimportapp;

import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class FetchReadingImportDataById extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3ReadingImportAppContext readingImportContext = (V3ReadingImportAppContext) context.get("READING_IMPORT_CONTEXT");

        FacilioUtil.throwIllegalArgumentException(readingImportContext == null, "Reading import context should not be null");

        long readingId = (long) readingImportContext.getId();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getReadingImportFields())
                .table(ModuleFactory.getReadingImportAPPModule().getTableName())
                .andCustomWhere("ID = ?", readingId);


        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            V3ReadingImportAppContext readingData = FieldUtil.getAsBeanFromMap(props.get(0), V3ReadingImportAppContext.class);
            context.put("READING_IMPORT_DATA", readingData);
        }
        return false;
    }
}
