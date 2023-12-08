package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ExecuteCardWorkflowCommand;
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
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReportFactoryFields;
import com.facilio.time.DateRange;
import com.facilio.workflows.util.WorkflowUtil;

public class KPIUtil {
	private static final Logger LOGGER = LogManager.getLogger(KPIUtil.class.getName());
	
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
		long currentMillis = System.currentTimeMillis();

		FacilioModule module = ModuleFactory.getKpiModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getKPIFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		KPIContext kpi= fetchKPIFromProps(selectBuilder.get(), fetchCurrentValue).get(0);
		long executionTime = System.currentTimeMillis() - currentMillis;
		LOGGER.debug("### time taken for getKpi: " + executionTime);

		return kpi;
		
		
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
					try {
						kpi.setCurrentValue(getKPIValue(kpi));
					}
					catch(Exception exception){
                       LOGGER.error("Error Occurred on KPI: "+kpi.getId(),exception);
					}
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
			if(kpi.getCriteria() != null)
			{
				Criteria kpiCriteria = kpi.getCriteria();
				if(kpiCriteria.getConditions() != null)
				{
					for (String key : kpiCriteria.getConditions().keySet()) {
						Condition condition = kpiCriteria.getConditions().get(key);
						if (kpi.getModuleName() != null) {
							try {
								ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
								FacilioField field = modBean.getField(condition.getFieldName(), kpi.getModuleName());
								condition.setField(field);
							}catch(Exception e){
								LOGGER.debug("Error in fetching field details in kpi"+ kpi.getModuleName());
							}
						}
					}
				}
			}
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
		return getKPIValue(kpi, null);
	}
	
	public static Map<String, Object> getKPIRecord(KPIContext kpi) throws Exception {
		FacilioModule module = kpi.getModule();
		FacilioField dateField = kpi.getDateField();
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.andCriteria(kpi.getCriteria())
				;
		if (kpi.getSiteId() != -1) {
			builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(kpi.getSiteId()), NumberOperators.EQUALS));
		}

		if (dateField != null) {
			builder.andCondition(CriteriaAPI.getCondition(dateField, kpi.getDateValue(), kpi.getDateOperatorEnum()));
		}
		
		
			List<Map<String, Object>> props = builder.getAsProps();
			
			if (CollectionUtils.isNotEmpty(props)) {
				return props.get(0);
			}
		
		return null;
	}
	
	public static Object getKPIList(KPIContext kpi, String baselineName) throws Exception {
		FacilioModule module = kpi.getModule();
		String moduleName =  kpi.getModuleName();
		FacilioField dateField = kpi.getDateField();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		List<FacilioField> fields = modBean.getAllFields(moduleName);

		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.select(fields).limit(1)
				.andCriteria(kpi.getCriteria());
		if (kpi.getSiteId() != -1) {
			builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(kpi.getSiteId()), NumberOperators.EQUALS));
		}
//		
//		if (kpi.getAggr() != -1) {
//			builder.aggregate(kpi.getAggrEnum(), kpi.getMetric());
//		}
//		else {
//			builder.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
//		}
		
		// This condition should be last
		if (dateField != null) {
			builder.andCondition(CriteriaAPI.getCondition(dateField, kpi.getDateValue(), kpi.getDateOperatorEnum()));
		}
		else if (baselineName != null) {
			throw new IllegalArgumentException("Date range is mandatory for baseline");
		}
		
		
		
		Object obj = null;
		List<Map<String, Object>> props = builder.getAsProps();
		
		if (CollectionUtils.isNotEmpty(props)) {
			obj = props.get(0);
		}
	
		
		return obj;
	}
	public static Object getKPIModuleFIelds(KPIContext kpi) throws Exception {
		String moduleName =  kpi.getModuleName();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		List<FacilioField> fields = modBean.getAllFields(moduleName);

	
		
		return fields;
	}
	public static Object getKPIValue(KPIContext kpi, String baselineName) throws Exception {
		long currentMillis = System.currentTimeMillis();
		FacilioModule module = kpi.getModule();
		FacilioField dateField = kpi.getDateField();
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.andCriteria(kpi.getCriteria())
				;
		if (kpi.getSiteId() != -1) {
			builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(kpi.getSiteId()), NumberOperators.EQUALS));
		}
		
		String fieldName;
		if (kpi.getAggr() != -1) {
			builder.aggregate(kpi.getAggrEnum(), kpi.getMetric());
			fieldName = kpi.getMetric().getName();
		}
		else {
			builder.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
			fieldName = "id";
		}
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> baseLineBuilder = new SelectRecordsBuilder<>(builder);
		// This condition should be last
		if (dateField != null) {
			if (baselineName != null) {
				BaseLineContext baseline = BaseLineAPI.getBaseLine(baselineName);
				DateRange actualRange = kpi.getDateOperatorEnum().getRange(kpi.getDateValue());
				DateRange baseLineRange = baseline.calculateBaseLineRange(actualRange, baseline.getAdjustTypeEnum());
				baseLineBuilder.andCondition(CriteriaAPI.getCondition(dateField, baseLineRange.toString(), DateOperators.BETWEEN));
			}
			builder.andCondition(CriteriaAPI.getCondition(dateField, kpi.getDateValue(), kpi.getDateOperatorEnum()));
		}
		else if (baselineName != null) {
			throw new IllegalArgumentException("Date range is mandatory for baseline");
		}
		
		
		Object obj = 0;
		List<Map<String, Object>> props = builder.getAsProps();
		
		if (CollectionUtils.isNotEmpty(props)) {
			obj = props.get(0).get(fieldName);
		}
		
		if (baselineName != null) {
			Object baseLineVal = 0;
			List<Map<String, Object>> baselineProps = baseLineBuilder.getAsProps();
			if (CollectionUtils.isNotEmpty(baselineProps)) {
				baseLineVal = baselineProps.get(0).get(fieldName);
			}
			JSONObject value = new JSONObject();
			value.put("value", obj);
			value.put("baseLineValue", baseLineVal);
			return value;
		}
		long executionTime = System.currentTimeMillis() - currentMillis;
		LOGGER.debug("### time taken for getKpiValue: " + executionTime);
		return obj;
	}
	public static Object getKPIBaseValueValue(KPIContext kpi, String baselineName) throws Exception {
		FacilioModule module = kpi.getModule();
		FacilioField dateField = kpi.getDateField();
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.andCriteria(kpi.getCriteria())
				;
		if (kpi.getSiteId() != -1) {
			builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(kpi.getSiteId()), NumberOperators.EQUALS));
		}
		
		String fieldName;
		if (kpi.getAggr() != -1) {
			builder.aggregate(kpi.getAggrEnum(), kpi.getMetric());
			fieldName = kpi.getMetric().getName();
		}
		else {
			builder.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
			fieldName = "id";
		}
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> baseLineBuilder = new SelectRecordsBuilder<>(builder);
		// This condition should be last
		if (dateField != null) {
			if (baselineName != null) {
				BaseLineContext baseline = BaseLineAPI.getBaseLine(baselineName);
				DateRange actualRange = kpi.getDateOperatorEnum().getRange(kpi.getDateValue());
				DateRange baseLineRange = baseline.calculateBaseLineRange(actualRange, baseline.getAdjustTypeEnum());
				baseLineBuilder.andCondition(CriteriaAPI.getCondition(dateField, baseLineRange.toString(), DateOperators.BETWEEN));
			}
			builder.andCondition(CriteriaAPI.getCondition(dateField, kpi.getDateValue(), kpi.getDateOperatorEnum()));
		}
		else if (baselineName != null) {
			throw new IllegalArgumentException("Date range is mandatory for baseline");
		}
		
		
		Object obj = 0;
		List<Map<String, Object>> props = builder.getAsProps();
		
		if (CollectionUtils.isNotEmpty(props)) {
			obj = props.get(0).get(fieldName);
		}
		
		if (baselineName != null) {
			Object baseLineVal = 0;
			List<Map<String, Object>> baselineProps = baseLineBuilder.getAsProps();
			if (CollectionUtils.isNotEmpty(baselineProps)) {
				baseLineVal = baselineProps.get(0).get(fieldName);
			}
			return baseLineVal;
		}
		
		return obj;
	}
	public static Map<Long,KPIContext> getModuleKpiWithId(List<Long> kpiIds,Map<Long,Map<String,String>> metricIdVsField,Map<Long,Map<String,String>> dateFieldIdVsField) throws Exception{
		Map<Long, KPIContext> kpiIdVsMap = new HashMap<>();

		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getKPIFields());
		String tableName = ModuleFactory.getKpiModule().getTableName();
		Map<Long,Criteria> criteriaMap = DashboardUtil.getIdVsCriteria(tableName,fieldMap.get(PackageConstants.DashboardConstants.CRITERIA_ID));
		Map<String,FacilioField> kpiFieldAsMap = FieldFactory.getAsMap(FieldFactory.getKPIFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getKPIFields())
				.table(ModuleFactory.getKpiModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(kpiFieldAsMap.get(PackageConstants.DashboardConstants.ID), StringUtils.join(kpiIds,","), StringOperators.IS));
		List<Map<String,Object>> props = selectBuilder.get();

		if(CollectionUtils.isNotEmpty(props)) {
			for(Map<String,Object> prop : props){
				KPIContext kpiContext = FieldUtil.getAsBeanFromMap(prop, KPIContext.class);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(kpiContext.getModuleId());
				kpiContext.setModuleName(module.getName());
				if(kpiContext.getWorkflowId()>0){
					WorkflowContext workflow = WorkflowUtil.getWorkflowContext(kpiContext.getWorkflowId());
					kpiContext.setWorkFlowString(workflow.getWorkflowString());
				}
				Long metricId = kpiContext.getMetricId();
				if(metricId>0){
                   kpiContext.setMetricFieldObj(metricIdVsField.get(metricId));
				}
				if(kpiContext.getCriteriaId()>0){
					kpiContext.setCriteria(criteriaMap.get(kpiContext.getCriteriaId()));
				}
				Long dateFieldId = kpiContext.getDateFieldId();
				if(dateFieldId!=null){
					kpiContext.setDateFieldObj(dateFieldIdVsField.get(dateFieldId));
				}
				kpiIdVsMap.put(kpiContext.getId(),kpiContext);
			}
		}
		return kpiIdVsMap;
	}
	public static List<Map<String,Object>> getFieldIds(FacilioModule module, FacilioField field) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(field))
				.table(module.getTableName());
		List<Map<String,Object>> props = selectBuilder.get();
		return props;
	}


}
