package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CustomButtonAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GetCustomButtonsForModuleDataListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if(CollectionUtils.isNotEmpty(records)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            List<WorkflowRuleContext> customButtons = CustomButtonAPI.getCustomButtons(module, CustomButtonRuleContext.PositionType.LIST_BAR, CustomButtonRuleContext.PositionType.LIST_ITEM);
            if (CollectionUtils.isNotEmpty(customButtons)) {
                for (ModuleBaseWithCustomFields record : records) {
                    List<WorkflowRuleContext> executableCustomButtons = CustomButtonAPI.getExecutableCustomButtons(customButtons, moduleName, record, context);
                    if (CollectionUtils.isNotEmpty(executableCustomButtons)) {
                        record.setEvaluatedButtonIds(executableCustomButtons.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList()));
                    }
                }
            }
            context.put(FacilioConstants.ContextNames.CUSTOM_BUTTONS, customButtons);
        }
        return false;
    }
}
