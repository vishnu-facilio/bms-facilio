package com.facilio.moduleBuilder.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.moduleBuilder.util.ResponseFormatUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAutomationModuleBasedOnTabType extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> resultMap = (Map<String, Object>) context.getOrDefault(FacilioConstants.ContextNames.RESULT, new HashMap<>());
        List<String> responseFields = (List<String>) context.get(FacilioConstants.ModuleListConfig.RESPONSE_FIELDS);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(AccountUtil.getCurrentTab() != null && AccountUtil.getCurrentTab().getTypeEnum() == WebTabContext.Type.EMAIL_TEMPLATES) {
            FacilioModule flaggedEventModule = modBean.getModule(FlaggedEventModule.MODULE_NAME);
            if(flaggedEventModule != null) {
                Map<String, Object> flaggedModuleMap = ResponseFormatUtil.formatModuleBasedOnResponseFields(flaggedEventModule, responseFields, true);
                ((List<Map<String, Object>>)resultMap.get(FacilioConstants.ContextNames.MODULE_LIST)).add(flaggedModuleMap);
                context.put(FacilioConstants.ContextNames.RESULT, resultMap);
            }
        }
        return false;
    }
}
