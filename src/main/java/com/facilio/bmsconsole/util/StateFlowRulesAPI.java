package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.StateFlowContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateChangeSet;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class StateFlowRulesAPI extends WorkflowRuleAPI {

	public static WorkflowRuleContext constructStateRuleFromProps(Map<String, Object> prop, ModuleBean modBean) {
		StateflowTransistionContext stateFlowRule = FieldUtil.getAsBeanFromMap(prop, StateflowTransistionContext.class);
		return stateFlowRule;
	}

	public static StateContext getStateContext(long stateId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STATE);
		SelectRecordsBuilder<StateContext> builder = new SelectRecordsBuilder<StateContext>()
				.beanClass(StateContext.class)
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getIdCondition(stateId, module));
		StateContext stateContext = builder.fetchFirst();
		return stateContext;
	}
	
	public static List<UpdateChangeSet> getDefaultFieldChangeSet(String moduleName, long recordId) throws Exception {
		if (StringUtils.isEmpty(moduleName) || recordId == -1) {
			return null;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField("stateFlow", moduleName);
		
		List<UpdateChangeSet> changeSet = new ArrayList<>();
		UpdateChangeSet updateChangeSet = new UpdateChangeSet();
		updateChangeSet.setFieldId(field.getFieldId());
		updateChangeSet.setRecordId(recordId);
		changeSet.add(updateChangeSet);
		return changeSet;
	}

	public static List<WorkflowRuleContext> getAvailableState(long fromStateId, String moduleName, ModuleBaseWithCustomFields record, FacilioContext context) throws Exception {
		return getAvailableState(fromStateId, -1, moduleName, record, context);
	}

	public static List<WorkflowRuleContext> getAvailableState(long fromStateId, long toStateId, String moduleName, ModuleBaseWithCustomFields record,
			Context context) throws Exception {
		FacilioModule stateRuleModule = ModuleFactory.getStateRuleTransistionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields(); 
		fields.addAll(FieldFactory.getStateRuleTransistionFields());
		fields.addAll(FieldFactory.getWorkflowEventFields());
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.innerJoin(stateRuleModule.getTableName()).on("Workflow_Rule.ID = " + stateRuleModule.getTableName() + ".ID")
				.andCondition(CriteriaAPI.getCondition("FROM_STATE_ID", "fromStateId", String.valueOf(fromStateId), NumberOperators.EQUALS));
		
		if (toStateId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("TO_STATE_ID", "toStateId", String.valueOf(toStateId), NumberOperators.EQUALS));
		}
		
		FacilioModule eventModule = ModuleFactory.getWorkflowEventModule();
		builder.innerJoin(eventModule.getTableName())
					.on(module.getTableName()+".EVENT_ID = "+eventModule.getTableName()+".ID");
		

		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
		List<UpdateChangeSet> changeSet = getDefaultFieldChangeSet(moduleName, record.getId());
			
		List<Map<String, Object>> list = builder.get();
		List<WorkflowRuleContext> stateFlows = getWorkFlowsFromMapList(list, true, true, true);
		if (CollectionUtils.isNotEmpty(stateFlows)) {
			Iterator<WorkflowRuleContext> iterator = stateFlows.iterator();
			while (iterator.hasNext()) {
				WorkflowRuleContext stateFlow = iterator.next();
				boolean evaluate = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateFlow, moduleName, record, changeSet, recordPlaceHolders, (FacilioContext) context, false);
				if (!evaluate) {
					iterator.remove();
				}
			}
		}
		
		return stateFlows;
	}

	public static void addOrUpdateStateFlow(StateFlowContext stateFlow) throws Exception {
		if (stateFlow == null) {
			return;
		}
		
		if (stateFlow.getCriteria() != null && !stateFlow.getCriteria().isEmpty()) {
			stateFlow.getCriteria().validatePattern();
			long criteriaId = CriteriaAPI.addCriteria(stateFlow.getCriteria(), AccountUtil.getCurrentOrg().getId());
			stateFlow.setCriteriaId(criteriaId);
		}
		
		FacilioModule module = ModuleFactory.getStateFlowModule();
		
		Map<String, Object> props = FieldUtil.getAsProperties(stateFlow);
		if (stateFlow.getId() < 0) {
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getStateFlowFields());
			builder.addRecord(props);
			builder.save();
			stateFlow.setId((long) props.get("id"));
		} 
		else {
			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getStateFlowFields())
					.andCondition(CriteriaAPI.getIdCondition(stateFlow.getId(), module));
			builder.update(props);
		}
	}
	
	public static StateFlowContext getStateFlowContext(long id) throws Exception {
		FacilioModule stateFlowModule = ModuleFactory.getStateFlowModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(stateFlowModule.getTableName())
				.select(FieldFactory.getStateFlowFields())
				.andCondition(CriteriaAPI.getIdCondition(id, stateFlowModule));
		Map<String, Object> map = builder.fetchFirst();
		StateFlowContext stateFlowContext = FieldUtil.getAsBeanFromMap(map, StateFlowContext.class);
		return stateFlowContext;
	}

}
