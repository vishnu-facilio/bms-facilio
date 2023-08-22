package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValidationRulesAPI {

	public static void addOrUpdateFormValidations(ValidationContext validationContext, FacilioModule validationModule) throws Exception {
		List<FacilioField> fields = FieldFactory.getValidationRuleFields(validationModule);
		WorkflowContext workflowContext= validationContext.getErrorMessagePlaceHolderScript();

		if (workflowContext != null) {
			Long workflowId=workflowContext.getId();
			if(workflowId!=null && workflowId > 0){
				WorkflowUtil.updateWorkflow(workflowContext, workflowId);
			}
			else{
				workflowId = WorkflowUtil.addWorkflow(workflowContext);
			}
			validationContext.setErrorMessagePlaceHolderScriptId(workflowId);
		}
		if(validationContext.getId() > 0) {
			ValidationContext existingValidation = getValidationById(validationContext.getId(), validationModule);

			FacilioUtil.throwIllegalArgumentException((existingValidation == null), "Invalid Id");
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(validationModule.getTableName())
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(validationContext.getId(), validationModule));
			updateBuilder.update(FieldUtil.getAsProperties(validationContext));
		}
		else {
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(validationModule.getTableName())
					.fields(fields);

			long id = builder.insert(FieldUtil.getAsProperties(validationContext));
			validationContext.setId(id);
		}

	}

	public static int deleteValidations(List<Long> validationIds, FacilioModule validationModule) throws Exception {
		if (CollectionUtils.isEmpty(validationIds)) {
			return 0;
		}

		List<ValidationContext> validations = getValidationsById(validationIds, validationModule);
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(validationModule.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(validationIds, validationModule));
		int count = builder.delete();

		List<Long> workflowIds = new ArrayList<>();
		for (ValidationContext validationContext : validations) {
			if (validationContext.getErrorMessagePlaceHolderScriptId() > 0) {
				workflowIds.add(validationContext.getErrorMessagePlaceHolderScriptId());
			}
		}
		if (CollectionUtils.isNotEmpty(workflowIds)) {
			WorkflowUtil.deleteWorkflows(workflowIds);
		}
		return count;
	}

	public static List<ValidationContext> getValidationsByParentId(Long parentId, FacilioModule validationModule, JSONObject pagination, String searchString) throws Exception {
		return getValidations(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS), validationModule, pagination, searchString);
	}

	public static ValidationContext getValidationById(Long id, FacilioModule validationModule) throws Exception {
		List<ValidationContext> validations = getValidationsById(Collections.singletonList(id), validationModule);
		if(CollectionUtils.isNotEmpty(validations)) {
			return validations.get(0);
		}
		return null;
	}

	public static List<ValidationContext> getValidationsById(List<Long> ids, FacilioModule validationModule) throws Exception {
		return getValidations(CriteriaAPI.getCondition("ID", "id", StringUtils.join(ids, ","), NumberOperators.EQUALS), validationModule, null, null);
	}

	private static List<ValidationContext> getValidations(Condition condition, FacilioModule validationModule, JSONObject pagination, String searchString) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(validationModule.getTableName())
				.select(FieldFactory.getValidationRuleFields(validationModule))
				.andCondition(condition);
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}

			builder.offset(offset);
			builder.limit(perPage);
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(searchString)) {
			builder.andCondition(CriteriaAPI.getCondition("NAME", "name", searchString, StringOperators.CONTAINS));
		}

		List<Map<String, Object>> list = builder.get();
		List<ValidationContext> validations = FieldUtil.getAsBeanListFromMapList(list, ValidationContext.class);

		List<Long> namedCriteriaIds = new ArrayList<>();
		for (ValidationContext validation: validations) {
			if (validation.getNamedCriteriaId() > 0) {
				namedCriteriaIds.add(validation.getNamedCriteriaId());
			}
		}
		if (CollectionUtils.isNotEmpty(namedCriteriaIds)) {
			Map<Long, NamedCriteria> criteriaMap = NamedCriteriaAPI.getCriteriaAsMap(namedCriteriaIds);

			for (ValidationContext validation: validations) {
				if (validation.getNamedCriteriaId() > 0) {
					NamedCriteria namedCriteria = criteriaMap.get(validation.getNamedCriteriaId());
					validation.setNamedCriteria(namedCriteria);
				}
				if (validation.getErrorMessagePlaceHolderScriptId() > 0) {
					validation.setErrorMessagePlaceHolderScript(WorkflowUtil.getWorkflowContext(validation.getErrorMessagePlaceHolderScriptId()));
				}
			}
		}
		return validations;
	}

	public static void validateRecord(String moduleName, ModuleBaseWithCustomFields record, List<ValidationContext> validations) throws Exception {
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
		for (ValidationContext validation : validations) {
			if (!validation.validate(record, null, recordPlaceHolders)) {
				throw new IllegalArgumentException(validation.getResolvedErrorMessage(record));
			}
		}

	}

	public static NamedCriteria getNamedCriteria(Criteria criteria) throws Exception {

		if (criteria == null) {
			return null;
		}

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getNamedCriteriaModule().getTableName())
				.select(FieldFactory.getNamedCriteriaFields())
				.andCriteria(criteria);
		NamedCriteria namedCriteria = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), NamedCriteria.class);

		return namedCriteria;

	}

	public static long getFormValidationId(ValidationContext validationRule) throws Exception {

		FacilioModule validationModule = ModuleFactory.getFormValidationRuleModule();

		GenericSelectRecordBuilder ruleSelectBuilder = new GenericSelectRecordBuilder()
				.table(validationModule.getTableName())
				.select(FieldFactory.getValidationRuleFields(validationModule));

		if (validationRule.getParentId() > 0) {
			ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(validationRule.getParentId()), NumberOperators.EQUALS));
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(validationRule.getName())) {
			ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", validationRule.getName(), StringOperators.IS));
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(validationRule.getErrorMessage())) {
			ruleSelectBuilder.andCondition(CriteriaAPI.getCondition("ERROR_MESSAGE", "errorMessage", validationRule.getErrorMessage(), StringOperators.IS));
		}

		Map<String, Object> prop = ruleSelectBuilder.fetchFirst();
		return MapUtils.isNotEmpty(prop) ? (Long) prop.get("id") : -1;

	}


}
