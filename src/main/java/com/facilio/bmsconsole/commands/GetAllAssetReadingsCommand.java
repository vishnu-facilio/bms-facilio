package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class GetAllAssetReadingsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		String searchText = (String) context.get(FacilioConstants.ContextNames.SEARCH);
		Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		if (fetchCount == null) {
			fetchCount = false;
		}
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		String moduleType = (String) context.get(FacilioConstants.ContextNames.MODULE_TYPE);

		FacilioModule fieldsModule = ModuleFactory.getFieldsModule();
		List<FacilioField> fields = FieldFactory.getSelectFieldFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		FacilioModule module = ModuleFactory.getModuleModule();
		List<FacilioField> moduleFields = FieldFactory.getModuleFields();
		fields.addAll(moduleFields);
		Map<String, FacilioField> moduleFieldMap = FieldFactory.getAsMap(moduleFields);
		FacilioField moduleIdField = moduleFieldMap.get("moduleId");
		FacilioField moduleNameField = moduleFieldMap.get("name");
		FacilioField moduleDisplayNameField = moduleFieldMap.get("displayName");
		moduleIdField.setName("facilioModuleId");
		moduleNameField.setName("moduleName");
		moduleDisplayNameField.setName("moduleDisplayName");

		FacilioModule subModule = ModuleFactory.getSubModulesRelModule();
		Map<String, FacilioField> subModuleFieldMap = FieldFactory.getAsMap(FieldFactory.getSubModuleRelFields(subModule));


		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
		Map<String, FacilioField> categoryFieldMap = FieldFactory.getAsMap(modBean.getAllFields("assetcategory"));
		FacilioField assetModuleField = categoryFieldMap.get("assetModuleID");
		FacilioField categoryIdField = FieldFactory.getIdField(assetCategoryModule);
		categoryIdField.setName("categoryId");
		fields.add(categoryIdField);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(assetCategoryModule.getTableName())
				.innerJoin(subModule.getTableName())
				.on(assetModuleField.getCompleteColumnName() + "=" + subModuleFieldMap.get("parentModuleId").getCompleteColumnName())
				.innerJoin(fieldsModule.getTableName())
				.on(subModuleFieldMap.get("childModuleId").getCompleteColumnName()+"="+fieldMap.get("moduleId").getCompleteColumnName())
				.innerJoin(module.getTableName())
				.on(fieldMap.get("moduleId").getCompleteColumnName()+"="+moduleFieldMap.get("moduleId").getCompleteColumnName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), StringUtils.join(ReadingsAPI.getDefaultReadingFieldNames(), ","), StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(moduleFieldMap.get("type"), String.valueOf(ModuleType.SCHEDULED_FORMULA.getValue()), NumberOperators.NOT_EQUALS))
				;

		if (moduleType != null) {
			NumberOperators operator = moduleType.equals("formula") ? NumberOperators.EQUALS : NumberOperators.NOT_EQUALS;
			selectBuilder.andCondition(CriteriaAPI.getCondition(moduleFieldMap.get("type"), String.valueOf(ModuleType.LIVE_FORMULA.getValue()), operator))
			;
		}

		if (searchText != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("displayName"), searchText, StringOperators.CONTAINS));
		}

		if (fetchCount) {
			long count = 0;
			selectBuilder.select(FieldFactory.getCountField(fieldsModule));
			Map<String, Object> prop = selectBuilder.fetchFirst();
			if (MapUtils.isNotEmpty(prop)) {
				count = (long) prop.get("count");
			}
			context.put(FacilioConstants.ContextNames.COUNT, count);
			return false;
		}

		selectBuilder.select(fields);

		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			if (perPage != -1) {
				int offset = ((page-1) * perPage);
				if (offset < 0) {
					offset = 0;
				}
				selectBuilder.offset(offset);
				selectBuilder.limit(perPage);
			}
		}

		List<Map<String, Object>> fieldProps = selectBuilder.get();

		if (CollectionUtils.isNotEmpty(fieldProps)) {
			Map<Long, FacilioModule> moduleMap = new HashMap<Long, FacilioModule>();
			Map<Long, Long> moduleVsCategory = new HashMap<Long, Long>();
			for(Map<String, Object> prop : fieldProps) {
				long moduleId = (long) prop.get("facilioModuleId");
				if (!moduleMap.containsKey(moduleId)) {
					FacilioModule fieldModule = FieldUtil.getAsBeanFromMap(prop, FacilioModule.class);
					fieldModule.setModuleId(moduleId);
					fieldModule.setName((String) prop.get("moduleName"));
					fieldModule.setDisplayName((String) prop.get("moduleDisplayName"));
					moduleMap.put(moduleId, fieldModule);
					moduleVsCategory.put(moduleId, (Long) prop.get("categoryId"));
				}
			}
			List<FacilioField> fieldList = modBean.getFieldFromPropList(fieldProps, moduleMap);
			Map<Long, List<FacilioField>> fieldByCategoryMap = new HashMap<>();
			for(FacilioField field: fieldList) {
				long categoryId = moduleVsCategory.get(field.getModuleId());
				List<FacilioField> fieldsOfCategory = fieldByCategoryMap.get(categoryId);
				if (fieldsOfCategory == null) {
					fieldsOfCategory = new ArrayList<FacilioField>();
					fieldByCategoryMap.put(categoryId, fieldsOfCategory);
				}
				fieldsOfCategory.add(field);
			}
			context.put(ContextNames.READING_FIELDS, fieldByCategoryMap);
			
			List<Long> fieldIds = fieldList.stream().map(field -> field.getId()).collect(Collectors.toList());
			List<FacilioField> ruleFields = FieldFactory.getWorkflowRuleFields();
			ruleFields.addAll(FieldFactory.getReadingRuleFields());
			Map<String, FacilioField> ruleFieldMap = FieldFactory.getAsMap(ruleFields);

			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(ruleFieldMap.get("readingFieldId"), fieldIds, NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition(ruleFieldMap.get("ruleType"), String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
			List<ReadingRuleContext> readingRules = ReadingRuleAPI.getReadingRules(criteria, ruleFields);

			if (readingRules != null && !readingRules.isEmpty()) {
				List<Long> workFlowIds = readingRules.stream().map(ReadingRuleContext::getWorkflowId).collect(Collectors.toList());
				Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workFlowIds, true);
				Map<Long, List<ReadingRuleContext>> fieldVsRules = new HashMap<>();
				for (ReadingRuleContext rule:  readingRules) {
					if (rule.getReadingFieldId() != -1) { 
						List<ReadingRuleContext> rules = fieldVsRules.get(rule.getReadingFieldId());
						if (rules == null) {
							rules = new ArrayList<>();
							fieldVsRules.put(rule.getReadingFieldId(), rules);
						}
						rules.add(rule);
					}
					long workflowId = rule.getWorkflowId();
					if (workflowId != -1) {
						rule.setWorkflow(workflowMap.get(workflowId));
					}
					List<ActionContext> actions = ActionAPI.getActiveActionsFromWorkflowRule(rule.getId());
					rule.setActions(actions);
				}
				context.put(FacilioConstants.ContextNames.VALIDATION_RULES, fieldVsRules);
			}
		}
		return false;
	}

}

