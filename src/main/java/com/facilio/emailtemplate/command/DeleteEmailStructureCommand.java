package com.facilio.emailtemplate.command;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class DeleteEmailStructureCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        Template template = TemplateAPI.getTemplate(id);
        if (!(template instanceof EMailStructure)) {
            // email structure not found, consider it is deleted
            return false;
        }

        EMailStructure emailStructure = (EMailStructure) template;
        if (getEmailTemplate(emailStructure.getId())){
            throw new IllegalArgumentException("Email Template in use");
        }
        TemplateAPI.deleteTemplate(emailStructure.getId());

        return false;
    }

    private boolean getEmailTemplate(long id) throws Exception {

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getEMailTemplatesModule().getTableName())
                .select(FieldFactory.getEMailTemplateFields())
                .andCondition(CriteriaAPI.getCondition("EMAIL_STRUCTURE_ID","emailStructureId", String.valueOf(id), NumberOperators.EQUALS));

        if(selectRecordBuilder.get().size() > 0){
            return true;
        }
        return false;

    }
}
