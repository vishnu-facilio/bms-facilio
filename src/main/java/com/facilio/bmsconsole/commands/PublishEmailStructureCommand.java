package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.emailtemplate.util.EmailStructureUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.Map;

public class PublishEmailStructureCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        if (id <= 0){
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (EmailStructureUtil.getDraft(id) == false){
            throw new IllegalArgumentException("Email Template has already been published");
        }

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getEMailStructureFields());
        EMailStructure eMailStructure = new EMailStructure();
        eMailStructure.setDraft(false);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getEMailStructureModule().getTableName())
                .fields(Collections.singletonList(fieldMap.get("draft")))
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getEMailStructureModule()));
        updateRecordBuilder.update(FieldUtil.getAsProperties(eMailStructure));

        return false;
    }

}
