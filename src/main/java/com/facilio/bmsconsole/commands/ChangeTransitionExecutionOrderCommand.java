package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChangeTransitionExecutionOrderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<StateFlowRuleContext> stateFlows = (List<StateFlowRuleContext>) context.get(FacilioConstants.ContextNames.STATE_FLOW_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (CollectionUtils.isNotEmpty(stateFlows) && StringUtils.isNotEmpty(moduleName)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<StateFlowRuleContext> allStateFlow = (List<StateFlowRuleContext>) StateFlowRulesAPI.getAllStateFlow(module);
			
			if (allStateFlow.size() - 1 != stateFlows.size()) {
				throw new IllegalArgumentException("Stateflow count mismatch");
			}
			Map<Long, StateFlowRuleContext> stateFlowAsMap = allStateFlow.stream().collect(Collectors.toMap(StateFlowRuleContext::getId, Function.identity()));
			for (StateFlowRuleContext stateFlowRule : stateFlows) {
				if (!(stateFlowAsMap.containsKey(stateFlowRule.getId()))) {
					throw new IllegalArgumentException("Stateflow count mismatch");
				}
				
				StateFlowRuleContext stateFlowRuleContext = stateFlowAsMap.remove(stateFlowRule.getId());
				if (stateFlowRuleContext.getDefaltStateFlow()) {
					throw new IllegalArgumentException("Cannot rearrange default stateflow");
				}
			}
			
			FacilioField executionOrderField = FieldFactory.getField("executionOrder", "EXECUTION_ORDER", ModuleFactory.getWorkflowRuleModule(), FieldType.NUMBER);
			for (int i = 0; i < stateFlows.size(); i++) {
				StateFlowRuleContext stateFlowRuleContext = stateFlows.get(i);
				
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getWorkflowRuleModule().getTableName())
						.fields(Collections.singletonList(executionOrderField))
						.andCondition(CriteriaAPI.getIdCondition(stateFlowRuleContext.getId(), ModuleFactory.getWorkflowRuleModule()))
//						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getWorkflowRuleModule()));
						;
				
				Map map = new HashMap<>();
				map.put("executionOrder", i + 1);
				builder.update(map);
			}
		}
		return false;
	}

}
