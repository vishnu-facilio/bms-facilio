package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.CustomButtonAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomButtonForDataListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<? extends ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(records)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            List<WorkflowRuleContext> customButtons = CustomButtonAPI.getCustomButtons(module, CustomButtonRuleContext.PositionType.LIST_BAR, CustomButtonRuleContext.PositionType.LIST_ITEM);
            Set<WorkflowRuleContext> evaluatedCustomButtons = new HashSet<>();
            if (CollectionUtils.isNotEmpty(customButtons)) {
                for (ModuleBaseWithCustomFields record : records) {
                    List<WorkflowRuleContext> executableCustomButtons = CustomButtonAPI.getExecutableCustomButtons(customButtons, moduleName, record, context);
                    if (CollectionUtils.isNotEmpty(executableCustomButtons)) {
                        evaluatedCustomButtons.addAll(executableCustomButtons);
                        record.setEvaluatedButtonIds(executableCustomButtons.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList()));
                    }
                }
            }
            context.put(Constants.CUSTOM_BUTTONS, evaluatedCustomButtons);
        }
        return false;
    }
}
