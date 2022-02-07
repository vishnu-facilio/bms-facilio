package com.facilio.emailtemplate.command;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;
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
        TemplateAPI.deleteTemplate(emailStructure.getId());

        return false;
    }
}
