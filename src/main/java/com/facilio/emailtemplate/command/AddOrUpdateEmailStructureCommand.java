package com.facilio.emailtemplate.command;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TemplateAttachmentUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class AddOrUpdateEmailStructureCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        EMailStructure emailStructure = (EMailStructure) context.get(FacilioConstants.ContextNames.EMAIL_STRUCTURE);
        emailStructure.setModuleId(module.getModuleId());

        List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.ATTACHMENT_LIST);
        setAttachments(emailStructure, attachmentList);

        validateEmailStructure(emailStructure);

        if (emailStructure.getId() > 0) {
            // delete old message file

            EMailStructure oldEmailStructure = fetchOldEmailStructure(emailStructure.getId());
            TemplateAPI.deleteTemplateFile(oldEmailStructure.getBodyId());

            // delete all attachments
            TemplateAttachmentUtil.deleteAttachments(emailStructure.getId());
            TemplateAPI.deleteDefaultProps(oldEmailStructure);

            // add Default Props
            TemplateAPI.addDefaultProps(emailStructure);

            // add new file
            User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
            emailStructure.setBodyId(FacilioFactory.getFileStore(superAdmin.getId()).addFile("Email_Template_"+emailStructure.getName(), emailStructure.getMessage(), "text/plain"));
            TemplateAPI.updateTemplatesWithExtendedProps(emailStructure.getId(), ModuleFactory.getEMailStructureModule(), FieldFactory.getEMailStructureFields(), FieldUtil.getAsProperties(emailStructure));

            // add the attachments again
            if (CollectionUtils.isNotEmpty(emailStructure.getAttachments())) {
                TemplateAttachmentUtil.addAttachments(oldEmailStructure.getId(), emailStructure.getAttachments());
            }
        } else {
            TemplateAPI.addTemplate(emailStructure);
        }

        return false;
    }

    private EMailStructure fetchOldEmailStructure(long id) throws Exception {
        Template oldTemplate = TemplateAPI.getTemplate(id);
        if (!(oldTemplate instanceof EMailStructure)) {
            throw new IllegalArgumentException("Invalid Email Template");
        }
        EMailStructure oldEmailStructure = (EMailStructure) oldTemplate;
        if (oldEmailStructure == null) {
            throw new IllegalArgumentException("Invalid Email Structure");
        }
        return oldEmailStructure;
    }

    private void setAttachments(EMailStructure emailStructure, List<Map<String, Object>> attachmentList) {
        TemplateAPI.setAttachments(attachmentList, emailStructure);
    }

    private void validateEmailStructure(EMailStructure emailStructure) {
        if (StringUtils.isEmpty(emailStructure.getName())) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (StringUtils.isEmpty(emailStructure.getSubject())) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }

        if (StringUtils.isEmpty(emailStructure.getMessage())) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
    }
}
