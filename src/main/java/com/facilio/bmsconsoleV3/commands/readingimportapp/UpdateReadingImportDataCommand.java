package com.facilio.bmsconsoleV3.commands.readingimportapp;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class UpdateReadingImportDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3ReadingImportAppContext readingImportContext = (V3ReadingImportAppContext) context.get("READING_IMPORT_CONTEXT");
        FacilioUtil.throwIllegalArgumentException(readingImportContext == null, "Reading import context should not be null");

        readingImportContext.setModifiedBy(AccountUtil.getCurrentUser().getOuid());
        readingImportContext.setModifiedTime(System.currentTimeMillis());

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getReadingImportFields())
                .table(ModuleFactory.getReadingImportAPPModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(readingImportContext.getId(), ModuleFactory.getReadingImportAPPModule()));

        Map<String, Object> props = FieldUtil.getAsProperties(readingImportContext);
        updateBuilder.update(props);

        return false;
    }
}
