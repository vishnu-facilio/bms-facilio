package com.facilio.readingrule.faulttowo.command;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class AddRuleWoDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
       Map<String,Object> ruleWorkOrder= (Map<String, Object>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_MODULE);
       Long ruleId= (Long) context.get(FacilioConstants.ContextNames.RULE_ID);
       if(MapUtils.isNotEmpty(ruleWorkOrder)){
           for(Map.Entry prop:ruleWorkOrder.entrySet()){
               Map<String,Object> ruleWoDetail= (Map<String, Object>) prop.getValue();
               if(ruleWoDetail!=null) {
                   ReadingRuleWorkOrderRelContext ruleWoCtx = FieldUtil.getAsBeanFromMap(ruleWoDetail, ReadingRuleWorkOrderRelContext.class);
                   RuleWoAPI.updateRuleWoDependencies(ruleWoCtx,ruleId);
                   addWorkFlowDetails(ruleWoCtx);
               }
           }
       }
        return false;
    }
    private Long addWorkFlowDetails(ReadingRuleWorkOrderRelContext wfRule) throws Exception {

        FacilioChain chain = TransactionChainFactory.getAddModuleWorkflowRuleChain();
        FacilioContext addWorkflowContext = chain.getContext();
        addWorkflowContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, wfRule);
        addWorkflowContext.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.NEW_READING_ALARM);
        chain.execute();

        Long workflowRuleId=(Long)addWorkflowContext.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
        return workflowRuleId;
    }

}
