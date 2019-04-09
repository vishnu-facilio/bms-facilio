package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
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
		FacilioField field = modBean.getField("moduleState", moduleName);
		
		List<UpdateChangeSet> changeSet = new ArrayList<>();
		UpdateChangeSet updateChangeSet = new UpdateChangeSet();
		updateChangeSet.setFieldId(field.getFieldId());
		updateChangeSet.setRecordId(recordId);
		changeSet.add(updateChangeSet);
		return changeSet;
	}

	public static List<WorkflowRuleContext> getAvailableState(long stateFlowId, long fromStateId, String moduleName, ModuleBaseWithCustomFields record, FacilioContext context) throws Exception {
		return getAvailableState(stateFlowId, fromStateId, -1, moduleName, record, context);
	}

	public static List<WorkflowRuleContext> getAvailableState(long stateFlowId, long fromStateId, long toStateId, String moduleName, ModuleBaseWithCustomFields record,
			Context context) throws Exception {
		FacilioModule stateRuleModule = ModuleFactory.getStateRuleTransistionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields(); 
		fields.addAll(FieldFactory.getStateRuleTransistionFields());
		fields.addAll(FieldFactory.getWorkflowEventFields());
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.innerJoin(stateRuleModule.getTableName()).on("Workflow_Rule.ID = " + stateRuleModule.getTableName() + ".ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("stateFlowId"), String.valueOf(stateFlowId), NumberOperators.EQUALS))
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

	public static void addOrUpdateStateFlow(StateFlowContext stateFlow, boolean add) throws Exception {
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
		if (add) {
			stateFlow.setId(addRecord(module, FieldFactory.getStateFlowFields(), props));
		} 
		else {
			updateRecord(module, FieldFactory.getStateFlowFields(), stateFlow.getId(), props);
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

	public static void addOrUpdateState(StateContext state) throws Exception {
		if (state == null) {
			return;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.STATE);
		
		Map<String, Object> props = FieldUtil.getAsProperties(state);
		if (state.getId() < 0) {
			state.setId(addRecord(module, modBean.getAllFields(module.getName()), props));
		} 
		else {
			updateRecord(module, modBean.getAllFields(module.getName()), state.getId(), props);
		}
	}
	
	private static long addRecord(FacilioModule module, List<FacilioField> fields, Map<String, Object> props) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields);
		builder.addRecord(props);
		builder.save();
		return ((long) props.get("id"));
	}
	
	private static void updateRecord(FacilioModule module, List<FacilioField> fields, long id, Map<String, Object> props) throws Exception {
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		builder.update(props);
	}

}
