package com.facilio.emailtemplate.command;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.TemplateAPI;
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
import org.apache.commons.lang3.StringUtils;

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

        validateEmailStructure(emailStructure);

        if (emailStructure.getId() > 0) {
            // delete old message file
            Template template = TemplateAPI.getTemplate(emailStructure.getId());
            if (!(template instanceof EMailStructure)) {
                throw new IllegalArgumentException("Invalid Email Template");
            }
            EMailStructure oldEmailStructure = (EMailStructure) template;
            if (oldEmailStructure == null) {
                throw new IllegalArgumentException("Invalid Email Structure");
            }

            TemplateAPI.deleteTemplateFile(oldEmailStructure.getBodyId());

            // add new file
            User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
            emailStructure.setBodyId(FacilioFactory.getFileStore(superAdmin.getId()).addFile("Email_Template_"+emailStructure.getName(), emailStructure.getMessage(), "text/plain"));
            TemplateAPI.updateTemplatesWithExtendedProps(emailStructure.getId(), ModuleFactory.getEMailStructureModule(), FieldFactory.getEMailStructureFields(), FieldUtil.getAsProperties(emailStructure));
        } else {
            TemplateAPI.addTemplate(emailStructure);
        }

        return false;
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
