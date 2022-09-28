package com.facilio.bmsconsoleV3.commands.formrelation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingFormRelationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Map;

public class UpdateFormRelationCommand extends FacilioCommand  {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        V3SpaceBookingFormRelationContext relationconatext = (V3SpaceBookingFormRelationContext) context.get("FORM_RELATION_CONTEXT");
        FacilioUtil.throwIllegalArgumentException(relationconatext == null, "Form Relation context should not be null");

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(FieldFactory.getSpaceCategoryFormRelationFields())
                .table(ModuleFactory.getSpaceBookingFormRelationModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(relationconatext.getId(), ModuleFactory.getSpaceBookingFormRelationModule()));

        Map<String, Object> props = FieldUtil.getAsProperties(relationconatext);
        updateBuilder.update(props);

        return false;
    }
}
