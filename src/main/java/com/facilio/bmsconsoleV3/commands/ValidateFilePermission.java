package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WebTabUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

@Log4j
public class ValidateFilePermission extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean validRequest = true;
        if(AccountUtil.getCurrentOrg() != null && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.THROW_403_WEBTAB)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Boolean isModuleFile = (Boolean) context.getOrDefault("isModuleFile", false);
            Long fileId = (Long) context.getOrDefault(FacilioConstants.ContextNames.FILE_ID, -1l);
            Long recordId = (Long) context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID, -1l);
            Long moduleId = (Long) context.getOrDefault(FacilioConstants.ContextNames.MODULE_ID, -1l);
            if(isModuleFile != null && isModuleFile) {
                validRequest = false;
            }
            LOGGER.info("File Id ==> " + fileId);
            if (fileId != null && fileId > 0 && isModuleFile && recordId != null && moduleId != null) {
                FacilioModule module = modBean.getModule(moduleId);
                if (module != null) {
                    String moduleName = WebTabUtil.getSpecialModule(module.getName());
                    module = modBean.getModule(moduleName);
                    LOGGER.info("Module Name ==> " + module.getName());
                    if(module != null) {
                        boolean hasPermission = WebTabUtil.checkModulePermissionForTab(FacilioConstants.ContextNames.READ_PERMISSIONS,moduleName);
                        LOGGER.info("Has Permission ==> " + hasPermission);
                        if(hasPermission) {
                            LOGGER.info("Record Id ==> " + recordId);
                            ModuleBaseWithCustomFields record = V3RecordAPI.getRecord(module.getModuleId(), recordId);
                            if (record != null) {
                                validRequest = true;
                            }
                        }
                    }
                }
            }
        }
        context.put("isValidRequest",validRequest);
        return false;
    }
}
