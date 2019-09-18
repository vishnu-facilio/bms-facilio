package com.facilio.bmsconsole.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateStateFlowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		StateFlowRuleContext stateFlow = (StateFlowRuleContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (stateFlow != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule facilioModule = modBean.getModule(stateFlow.getModuleId());
			if (facilioModule == null) {
				facilioModule = modBean.getModule(stateFlow.getModuleName());
			}
			if (facilioModule == null) {
				throw new Exception("Invalid Module");
			}
			stateFlow.setModuleId(facilioModule.getModuleId());
			Boolean defaultStateflow = (Boolean) context.get(FacilioConstants.ContextNames.DEFAULT_STATEFLOW);
			if (defaultStateflow == null || defaultStateflow == false) {
				stateFlow.setDefaltStateFlow(false);
			}
			else {
				stateFlow.setDefaltStateFlow(true);
			}
			
			if (stateFlow.getExecutionOrder() == -1) {
				stateFlow.setExecutionOrder(0);
			}

			stateFlow.setActivityType(EventType.CREATE);
			stateFlow.setModuleId(facilioModule.getModuleId());
			stateFlow.setRuleType(RuleType.STATE_FLOW);
			stateFlow.setDefaltStateFlow(false);
			
			boolean add = false;
			if (stateFlow.getId() < 0) {
				add = true;
			}
			
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateFlow);
			FacilioChain ruleChain;
			if (add) {
				ruleChain = TransactionChainFactory.addWorkflowRuleChain();
			} else {
				ruleChain = TransactionChainFactory.updateWorkflowRuleChain();
			}
			ruleChain.execute(context);
			
			updateStateTransitionExecutionOrder(facilioModule);
		}
		return false;
	}

	private void updateStateTransitionExecutionOrder(FacilioModule module) throws Exception {
		FacilioField executionOrderField = FieldFactory.getField("executionOrder", "EXECUTION_ORDER", ModuleFactory.getWorkflowRuleModule(), FieldType.NUMBER);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleModule().getTableName())
				.innerJoin(ModuleFactory.getStateFlowModule().getTableName())
					.on(ModuleFactory.getWorkflowRuleModule().getTableName() + ".ID = " + ModuleFactory.getStateFlowModule().getTableName() + ".ID")
				.select(Arrays.asList(FieldFactory.getIdField(ModuleFactory.getWorkflowRuleModule()), executionOrderField))
				.andCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.STATE_FLOW.getIntVal()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getWorkflowRuleModule()));
				;
		List<Map<String, Object>> list = selectBuilder.get();
		
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map<String, Object> map : list) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getWorkflowRuleModule().getTableName())
						.fields(Collections.singletonList(executionOrderField))
						.andCondition(CriteriaAPI.getIdCondition((long) map.get("id"), ModuleFactory.getWorkflowRuleModule()));
//						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getWorkflowRuleModule()));
				Integer executionOrder = ((Integer) map.get("executionOrder"));
				if (executionOrder == null) {
					executionOrder = 0;
				}
				map.put("executionOrder", executionOrder + 1);
				builder.update(map);
			}
		}
	}

}
