package com.facilio.trigger.command;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerActionContext;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddOrUpdateTriggerActionAndRelCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
		if(trigger.getTriggerActions() != null) {
			
			List<Long> actionIds = trigger.getTriggerActions().stream().map(TriggerActionContext::getId).collect(Collectors.toList());
			Map<Long, TriggerActionContext> triggerMap = null;
			if (!actionIds.isEmpty()) {
				triggerMap = fetchTriggerActions(actionIds);
			}
			
			for(TriggerActionContext action : trigger.getTriggerActions()) {
				
				addTypeRefObj(action, trigger.getTypeEnum());
				
				if(action.getId() < 0) {
					action.setTriggerId(trigger.getId());
					Map<String, Object> props = FieldUtil.getAsProperties(action);
					GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
							.table(ModuleFactory.getTriggerActionModule().getTableName())
							.fields(FieldFactory.getTriggerActionFields())
							.addRecord(props);
					insert.save();
					action.setId((long)props.get("id"));
				}
				else {
					GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getTriggerActionModule().getTableName())
							.fields(FieldFactory.getTriggerActionFields())
							.andCondition(CriteriaAPI.getIdCondition(action.getId(), ModuleFactory.getTriggerActionModule()));
					builder.update(FieldUtil.getAsProperties(action));
					
					TriggerActionContext oldAction = triggerMap.get(action.getId());
					deleteTypeRefObj(oldAction, action, trigger.getTypeEnum());
				}
			}
		}
		return false;
	}
	
	private Map<Long, TriggerActionContext> fetchTriggerActions(List<Long> ids) throws Exception {
		List<FacilioField> fields = FieldFactory.getTriggerActionFields();
		FacilioModule module = ModuleFactory.getTriggerActionModule();

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(ids, module));

		List<Map<String, Object>> props = select.get();
		List<TriggerActionContext> actions = FieldUtil.getAsBeanListFromMapList(props, TriggerActionContext.class);
		return actions.stream().collect(Collectors.toMap(TriggerActionContext::getId, Function.identity()));
	}
	
	private void addTypeRefObj(TriggerActionContext action, TriggerType triggerType) throws Exception {
		if (action.getTypeRefObj() == null) {
			return;
		}
		
		switch(triggerType) {
			case AGENT_TRIGGER:
				WorkflowContext workflow = FieldUtil.getAsBeanFromMap(action.getTypeRefObj(), WorkflowContext.class);
				Long workflowId = WorkflowUtil.addWorkflow(workflow);
				action.setTypeRefPrimaryId(workflowId);
				break;
		}
	}
	
	private void deleteTypeRefObj(TriggerActionContext oldAction, TriggerActionContext action, TriggerType triggerType) throws Exception {
		if (action.getTypeRefObj() == null) {
			return;
		}
		
		TriggerUtil.deleteTypeRefObj(Collections.singletonList(oldAction), triggerType);
	}

}
