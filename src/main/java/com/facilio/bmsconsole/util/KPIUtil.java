package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.KPICategoryContext;
import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportFactoryFields;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.modulefunctions.FacilioModuleFunctionImpl;

public class KPIUtil {
	
	public static final String KPI_CATEGORY_CONTEXT = "kpiCategoryContext";
	public static final String KPI_CATEGORY_CONTEXTS = "kpiCategoryContexts";
	
	public static void addKPICategoryContext(KPICategoryContext kpiCategoryContext) throws Exception {
		
		kpiCategoryContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getKPICategoryModule().getTableName())
				.fields(FieldFactory.getKPICategoryFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(kpiCategoryContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		kpiCategoryContext.setId((Long) props.get("id"));
	}
	
	public static void updateKPICategoryContext(KPICategoryContext kpiCategoryContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getKPICategoryModule().getTableName())
				.fields(FieldFactory.getKPICategoryFields())
				.andCondition(CriteriaAPI.getIdCondition(kpiCategoryContext.getId(), ModuleFactory.getKPICategoryModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(kpiCategoryContext);
		updateBuilder.update(props);
		
	}
	
	public static void deleteKPICategoryContext(KPICategoryContext kpiCategoryContext) {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getKPICategoryModule().getTableName())
		.andCondition(CriteriaAPI.getCondition("ID", "id", ""+kpiCategoryContext.getId(), NumberOperators.EQUALS));
	}
	
	public static KPICategoryContext getKPICategoryContext(long id) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getKPICategoryFields())
				.table(ModuleFactory.getKPICategoryModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getKPICategoryModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			KPICategoryContext kpiCategoryContext = FieldUtil.getAsBeanFromMap(props.get(0), KPICategoryContext.class);
			return kpiCategoryContext;
		}
		return null;
	}
	
	public static List<KPICategoryContext> getAllKPICategories() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getKPICategoryFields())
				.table(ModuleFactory.getKPICategoryModule().getTableName());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<KPICategoryContext> kpiCategoryContext = FieldUtil.getAsBeanListFromMapList(props, KPICategoryContext.class);
			return kpiCategoryContext;
		}
		return null;
	}
	
	
	public static void setViolationCriteria (FormulaFieldContext formula, FacilioField readingField, ReadingRuleContext violationRule) throws Exception {
		
		NumberOperators operator = null;
		String value = null;
		
		ActionContext action = new ActionContext();
		action.setActionType(ActionType.ADD_VIOLATION_ALARM);
		
		List<Map<String, Object>> fieldMatchers = new ArrayList<>();
		
		JSONObject templateJson = new JSONObject();
		Map<String, Object> fieldObj = new HashMap<>();
		fieldObj.put("field", "message");
		
		StringBuilder valueString = new StringBuilder(formula.getName());
		if (formula.getTarget() == -1) {	// Alarm would have occurred because min target was specified and reading violated it
			operator = NumberOperators.LESS_THAN;
			value = String.valueOf(formula.getMinTarget());
			valueString.append(" is lesser than ").append(formula.getMinTarget());
		}
		else if (formula.getMinTarget() == -1) {
			operator = NumberOperators.GREATER_THAN;
			value = String.valueOf(formula.getTarget());
			valueString.append(" is greater than ").append(formula.getTarget());
		}
		else {
			operator = NumberOperators.NOT_BETWEEN; 
			value = formula.getMinTarget() + "," + formula.getTarget();
			valueString.append(" is not within the safe limit of ").append(formula.getMinTarget()).append(" - ").append(formula.getTarget());
		}
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(readingField, value, operator));
		violationRule.setCriteria(criteria);
		
		fieldObj.put("value", valueString.toString());
		fieldMatchers.add(fieldObj);
		
		fieldObj = new HashMap<>();
		fieldObj.put("field", "formulaId");
		fieldObj.put("value", formula.getId());
		
		fieldMatchers.add(fieldObj);
		templateJson.put("fieldMatcher", fieldMatchers);
		
		action.setTemplateJson(templateJson);
		violationRule.setActions(Collections.singletonList(action));
		violationRule.setPercentage(value);
	}
	
	public static KPIContext getKPI(long id) throws Exception {
		return getKPI(id, true);
	}
	
	public static KPIContext getKPI(long id,  boolean fetchCurrentValue) throws Exception {
		FacilioModule module = ModuleFactory.getKpiModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getKPIFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		return fetchKPIFromProps(selectBuilder.get(), fetchCurrentValue).get(0);
		
	}
	
	public static List<KPIContext> fetchKPIFromProps(List<Map<String, Object>> props, boolean fetchCurrentValue) throws Exception {
		if (CollectionUtils.isNotEmpty(props)) {
			List<KPIContext> kpis = FieldUtil.getAsBeanListFromMapList(props, KPIContext.class);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			Map<String, List<FacilioField>> metrics = new HashMap<>();
			
			List<Long> ids = kpis.stream().map(KPIContext::getCriteriaId).collect(Collectors.toList());
			Map<Long, Criteria> criteriaMap = CriteriaAPI.getCriteriaAsMap(ids);
			
			for(KPIContext kpi: kpis) {
				FacilioModule module = modBean.getModule(kpi.getModuleId());
				kpi.setModule(module);
				
				List<FacilioField> kpiMetrics;
				if (!metrics.containsKey(module.getName())) {
					kpiMetrics = getKPIMetrics(module);
					metrics.put(module.getName(), kpiMetrics);
				}
				else {
					kpiMetrics = metrics.get(module.getName());
				}
				 
				
				if (kpi.getDateFieldId() != -1) {
					kpi.setDateField(modBean.getField(kpi.getDateFieldId()));
				}
				
				kpi.setCriteria(criteriaMap.get(kpi.getCriteriaId()));
				FacilioField metricField = (FacilioField) kpiMetrics.stream().filter(metric -> {
					if (kpi.getMetricId() != -1) {
						return kpi.getMetricId() == metric.getFieldId();
					}
					else{
						return kpi.getMetricName().equals(metric.getName());
					}
				}).findFirst().get();
				kpi.setMetric(metricField);
				
				if (fetchCurrentValue) {
					kpi.setCurrentValue(getKPIValue(kpi));
				}
			}
			
			return kpis;
		}
		return null;
	}
	
	public static void updateChildIds(KPIContext kpi) throws Exception {
		if (kpi.getWorkflow() != null) {
			long workflowId = WorkflowUtil.addWorkflow(kpi.getWorkflow());
			kpi.setWorkflowId(workflowId);
			kpi.setWorkflow(null);
		}
		if (kpi.getCriteria() != null) {
			long criteriaId = CriteriaAPI.addCriteria(kpi.getCriteria());
			kpi.setCriteriaId(criteriaId);
		}
	}
	
	public static void deleteKPIChilds(KPIContext kpi) throws Exception {
		if (kpi.getCriteriaId() != -1) {
			CriteriaAPI.deleteCriteria(kpi.getCriteriaId());
		}
		else if (kpi.getWorkflowId() != -1) {
			WorkflowUtil.deleteWorkflow(kpi.getWorkflowId());
		}
	}
	
	public static List<FacilioField> getKPIMetrics(FacilioModule module) throws Exception {
		FacilioField countField = FieldFactory.getCountField(module).get(0);
		countField.setDisplayName("Number of "+module.getDisplayName());
		List<FacilioField> fields = new ArrayList<>();
		fields.add(countField);
		fields.addAll((List<FacilioField>) ReportFactoryFields.getReportFields(module.getName()).get("metrics"));
		return fields;
	}
	
	public static Object getKPIValue(long kpiId) throws Exception {
		KPIContext kpi = getKPI(kpiId);
		return kpi.getCurrentValue();
	}
	
	public static Object getKPIValue(KPIContext kpi) throws Exception {
		FacilioModule module = kpi.getModule();
		Criteria criteria = kpi.getCriteria().clone();
		
		FacilioField metric = kpi.getMetric();
		FacilioField dateField = kpi.getDateField();
		
		if (dateField != null) {
			criteria.addAndCondition(CriteriaAPI.getCondition(dateField, kpi.getDateValue(), kpi.getDateOperatorEnum()));
		}
		if (kpi.getSiteId() != -1) {
			criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(kpi.getSiteId()), NumberOperators.EQUALS));
		}
		
		DBParamContext dbParamContext = new DBParamContext();
		dbParamContext.setFieldName(metric.getName());
		dbParamContext.setCriteria(criteria);
		if (kpi.getAggr() != -1) {
			dbParamContext.setAggregateString(kpi.getAggrEnum().getStringValue());
		}
		else if (StringUtils.isNotEmpty(kpi.getMetricName()) && kpi.getMetricName().equals("count")) {
			dbParamContext.setFieldName("id");
			dbParamContext.setAggregateString(kpi.getMetricName());
		}
		
		List<Object> params = new ArrayList<>();
		params.add(module);
		params.add(dbParamContext);
		
		FacilioModuleFunctionImpl function = new FacilioModuleFunctionImpl();
		Object obj = function.fetch(null,params);
		if (obj == null) {
			return 0;
		}
		return obj;
	}
}
