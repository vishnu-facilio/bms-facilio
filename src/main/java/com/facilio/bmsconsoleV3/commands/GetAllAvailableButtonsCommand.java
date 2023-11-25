package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.CustomButtonAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AbstractStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GetAllAvailableButtonsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if(record == null){
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        FacilioStatus currentState = record.getModuleState();
        long stateFlowId = record.getStateFlowId();

        List<WorkflowRuleContext> availableStateTransition = new ArrayList<>();
        if (currentState != null){
            availableStateTransition = StateFlowRulesAPI.getAvailableState(stateFlowId, currentState.getId(), moduleName,
                    record, (FacilioContext) context);
            StateFlowRulesAPI.removeUnwantedTranstions(availableStateTransition);
        }

        List<WorkflowRuleContext> customButtons = CustomButtonAPI.getCustomButtons(module, CustomButtonRuleContext.PositionType.SUMMARY);

        if (CollectionUtils.isNotEmpty(customButtons)) {
            Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
            Iterator<WorkflowRuleContext> iterator = customButtons.iterator();
            while (iterator.hasNext()) {
                WorkflowRuleContext customButton = iterator.next();
                boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(customButton, moduleName, record, null, recordPlaceHolders, (FacilioContext) context, false);
                if (!evaluate) {
                    iterator.remove();
                }
            }
        }

        List<WorkflowRuleContext> systemButtons = SystemButtonApi.getSystemButtons(module,CustomButtonRuleContext.PositionType.SUMMARY);
        if (CollectionUtils.isNotEmpty(systemButtons)){
            Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
            Iterator<WorkflowRuleContext> iterator = systemButtons.iterator();
            while (iterator.hasNext()){
                WorkflowRuleContext workflowRule = iterator.next();
                boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, moduleName, record, null, recordPlaceHolders, (FacilioContext) context, false);
                if (!evaluate){
                    iterator.remove();
                }
            }
        }

        context.put(FacilioConstants.ContextNames.CURRENT_STATE,currentState);
        context.put(FacilioConstants.ContextNames.AVAILABLE_STATE_TRANSITION, availableStateTransition);
        context.put(FacilioConstants.ContextNames.CUSTOM_BUTTONS,customButtons);
        context.put(FacilioConstants.ContextNames.SYSTEM_BUTTONS,systemButtons);

        return false;
    }

}
