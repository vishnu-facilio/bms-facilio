package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class GetAvailableSystemButtonsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        int positionType = (int) context.get(FacilioConstants.ContextNames.POSITION_TYPE);
        long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
        FacilioContext summary = V3Util.getSummary(moduleName, Collections.singletonList(recordId));
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = Constants.getRecordListFromContext(summary,moduleName);
        ModuleBaseWithCustomFields recordData = CollectionUtils.isNotEmpty(moduleBaseWithCustomFields) ? moduleBaseWithCustomFields.get(0) : null;

        if(positionType < 4 && recordData == null){
            throw new IllegalArgumentException("Invalid record");
        }

        CustomButtonRuleContext.PositionType positionTypeEnum = CustomButtonRuleContext.PositionType.valueOf(positionType);
        if (positionTypeEnum == null) {
            throw new IllegalArgumentException("Position type cannot be empty");
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        if (module == null){
            throw new IllegalArgumentException("Module cannot be null");
        }

        List<WorkflowRuleContext> systemButtons = SystemButtonApi.getSystemButtons(module,positionTypeEnum);

        if (CollectionUtils.isNotEmpty(systemButtons)){
            Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, recordData, WorkflowRuleAPI.getOrgPlaceHolders());
            Iterator<WorkflowRuleContext> iterator = systemButtons.iterator();
            while (iterator.hasNext()){
                WorkflowRuleContext workflowRule = iterator.next();
                boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(workflowRule, moduleName, recordData, null, recordPlaceHolders, (FacilioContext) context, false);
                if (!evaluate){
                    iterator.remove();
                }
            }
        }

        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, systemButtons);

        return false;
    }

}
