package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CustomButtonAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetAvailableButtonsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        int positionType = (int) context.get(FacilioConstants.ContextNames.POSITION_TYPE);
        if (moduleData != null) {
            CustomButtonRuleContext.PositionType positionTypeEnum = CustomButtonRuleContext.PositionType.valueOf(positionType);
            if (positionTypeEnum == null) {
                throw new IllegalArgumentException("Position type cannot be empty");
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            List<WorkflowRuleContext> customButtons = CustomButtonAPI.getCustomButtons(module, positionTypeEnum);

            if (CollectionUtils.isNotEmpty(customButtons)) {
                Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, moduleData, WorkflowRuleAPI.getOrgPlaceHolders());
                Iterator<WorkflowRuleContext> iterator = customButtons.iterator();
                while (iterator.hasNext()) {
                    WorkflowRuleContext stateFlow = iterator.next();
                    boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateFlow, moduleName, moduleData, null, recordPlaceHolders, (FacilioContext) context, false);
                    if (!evaluate) {
                        iterator.remove();
                    }
                }
            }
            context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, customButtons);
        }
        return false;
    }
}
