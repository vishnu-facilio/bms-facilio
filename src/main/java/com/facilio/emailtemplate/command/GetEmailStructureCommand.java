package com.facilio.emailtemplate.command;

import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TemplateAttachmentUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class GetEmailStructureCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);

        Template template = TemplateAPI.getTemplate(id);
        if (template == null || !(template instanceof EMailStructure)) {
            throw new IllegalArgumentException("Email Template not found");
        }

        EMailStructure emailStructure = (EMailStructure) template;
        if (emailStructure.getIsAttachmentAdded()) {
            List<TemplateAttachment> templateAttachments = TemplateAttachmentUtil.fetchAttachments(emailStructure.getId());
            if (CollectionUtils.isNotEmpty(templateAttachments)) {
                for (TemplateAttachment attachment : templateAttachments) {
                    emailStructure.addAttachment(attachment);
                }
            }
        }

        context.put(FacilioConstants.ContextNames.EMAIL_STRUCTURE, emailStructure);

        return false;
    }
}
