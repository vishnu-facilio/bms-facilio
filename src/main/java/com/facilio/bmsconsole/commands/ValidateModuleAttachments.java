package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class ValidateModuleAttachments extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isValidRequest = true;
        if(AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.THROW_403_WEBTAB)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            isValidRequest = false;
            Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
            if(StringUtils.isEmpty(parentModuleName) && StringUtils.isNotEmpty(moduleName)) {
                FacilioModule attachmentModule = modBean.getModule(moduleName);
                if(attachmentModule != null) {
                    FacilioModule attachmentParentModule = modBean.getParentModule(attachmentModule.getModuleId());
                    if(attachmentParentModule != null) {
                        parentModuleName = attachmentParentModule.getName();
                    }
                }
            }
            if(moduleName.equals(FacilioConstants.ContextNames.TICKET_ATTACHMENTS)) {
                parentModuleName = FacilioConstants.ContextNames.TICKET;
            }
            if(recordId != null && StringUtils.isNotEmpty(moduleName) && StringUtils.isNotEmpty(parentModuleName)) {
                FacilioModule attachmentModule = modBean.getModule(moduleName);
                FacilioModule parentModule = modBean.getModule(parentModuleName);
                FacilioModule recordModule = modBean.getModule(WebTabUtil.getSpecialModule(parentModuleName));
                if(attachmentModule != null && parentModule != null && recordModule != null) {
                    FacilioModule attachmentParentModule = modBean.getParentModule(attachmentModule.getModuleId());
                    if(attachmentParentModule != null && attachmentParentModule.getName().equals(parentModule.getName())) {
                        boolean hasPermission = WebTabUtil.checkModulePermissionForTab(FacilioConstants.ContextNames.READ_PERMISSIONS,recordModule.getName());
                        if(hasPermission) {
                            ModuleBaseWithCustomFields record = V3RecordAPI.getRecord(recordModule.getModuleId(), recordId);
                            if (record != null) {
                                isValidRequest = true;
                            }
                        }
                    }
                }
            }
        }
        context.put("isValidRequest",isValidRequest);
        return false;
    }
}