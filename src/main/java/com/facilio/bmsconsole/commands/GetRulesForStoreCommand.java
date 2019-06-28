package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetRulesForStoreCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = ModuleFactory.getStoreNotificationConfigModule();
		List<FacilioField> fields = FieldFactory.getStoreNotificationModuleFields();
		Long storeId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("STORE_ID", "storeRoomId", String.valueOf(storeId), NumberOperators.EQUALS));
		List<Map<String,Object>> result = builder.get();
		List<WorkflowRuleContext> workFlowRuleList = new ArrayList<WorkflowRuleContext>();
		if(CollectionUtils.isNotEmpty(result)){
		 for(Map<String,Object> map : result) {
			 Long ruleId = (Long)map.get("workflowRuleId");
			 WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(ruleId);
			 List<ActionContext> actions = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
			 rule.setActions(actions);
			 workFlowRuleList.add(rule);
			
		 }
		}
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, workFlowRuleList);
			
		return false;
	}
	

}
