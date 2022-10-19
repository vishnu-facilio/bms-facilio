package com.facilio.bmsconsoleV3.commands.formrelation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingFormRelationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddFormRelationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3SpaceBookingFormRelationContext relationconatext = (V3SpaceBookingFormRelationContext) context.get("FORM_RELATION_CONTEXT");

        FacilioUtil.throwIllegalArgumentException(relationconatext == null, "Form Relation context should not be null");


        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getSpaceBookingFormRelationModule().getTableName())
                .fields(FieldFactory.getSpaceCategoryFormRelationFields());

        Map<String, Object> props = FieldUtil.getAsProperties(relationconatext);

        insertBuilder.addRecord(props);
        insertBuilder.save();

        long importId = (Long) props.get("id");
        relationconatext.setId(importId);
        context.put("FORM_RELATION_DATA", relationconatext);

        return false;
    }
}