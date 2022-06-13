package com.facilio.bmsconsoleV3.commands.readingimportapp;

import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class DeleteReadingImportDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3ReadingImportAppContext readingImportContext = (V3ReadingImportAppContext) context.get("READING_IMPORT_CONTEXT");

        FacilioUtil.throwIllegalArgumentException(readingImportContext == null, "Reading import context should not be null");

        FacilioModule module = ModuleFactory.getReadingImportAPPModule();

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(readingImportContext.getId(),module));
        builder.delete();

       return builder.delete()==1;

    }
}
