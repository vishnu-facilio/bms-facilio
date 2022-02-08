package com.facilio.emailtemplate.command;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;
import org.apache.commons.chain.Context;

public class GetEmailStructureCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        Template template = TemplateAPI.getTemplate(id);
        if (template == null || !(template instanceof EMailStructure)) {
            throw new IllegalArgumentException("Email Template not found");
        }

        EMailStructure emailStructure = (EMailStructure) template;
        context.put(FacilioConstants.ContextNames.EMAIL_STRUCTURE, emailStructure);

        return false;
    }
}
