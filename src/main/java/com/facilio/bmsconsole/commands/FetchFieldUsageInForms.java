package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FieldFormUsage;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchFieldUsageInForms extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(FetchFieldUsageInForms.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		Long fieldId = (Long) context.get(FacilioConstants.ContextNames.FIELD_ID);
		List<FacilioForm> forms = (List<FacilioForm>) context.get(FacilioConstants.ContextNames.FORMS);

		//Map<Long, FacilioForm> formVsIdMap = (forms != null) ? forms.stream().collect(Collectors.toMap(FacilioForm::getId, Function.identity())) : null;
		Map<Long, FacilioForm> formVsIdMap = new HashMap<>();
		for(FacilioForm form: forms) {
			if(formVsIdMap.containsKey(form.getId())) {
				LOGGER.info(MessageFormat.format("Duplicate Form present in List - formID - {0} ", form.getId()));
			}
			else {
				formVsIdMap.put(form.getId(), form);
			}
		}

		FacilioModule module = ModuleFactory.getFormFieldsModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormFieldsFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(fieldId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("FORMID", "formId", StringUtils.join(formVsIdMap.keySet(), ","), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		if(CollectionUtils.isEmpty(props)) {
			return false;
		}

		List<FacilioForm> formList = null;
		for (Map<String, Object> prop : props) {
			FormField formField = FieldUtil.getAsBeanFromMap(prop, FormField.class);
			Long formId = (Long) formField.getFormId();
			FacilioForm form = formVsIdMap.get(formId);
			if (formList == null) {
				formList = new ArrayList<>();
			}
			formList.add(form);
		}

		List<Long> buttonIds = new ArrayList<>();
		List<Long> approvalIds = new ArrayList<>();
		List<Long> transitionIds = new ArrayList<>();
		List<FieldFormUsage> usageDetailsList = new ArrayList<FieldFormUsage>();
		for(FacilioForm form : formList) {
			FieldFormUsage usageDetails = new FieldFormUsage();
			Map<FieldFormUsage.FormType, Long> result = new HashMap<>();
			if (form.getName().startsWith("__approval_")) {
				usageDetails.setFormType(FieldFormUsage.FormType.APPROVAL_PROCESS);
				usageDetails.setComponentId(Long.parseLong(form.getName().replace("__approval_", "").split("_")[0]));
				approvalIds.add(usageDetails.getComponentId());
			} else if (form.getName().startsWith("__custom_button_")) {
				usageDetails.setFormType(FieldFormUsage.FormType.CUSTOM_BUTTON);
				usageDetails.setComponentId(Long.parseLong(form.getName().replace("__custom_button_", "").split("_")[0]));
				buttonIds.add(usageDetails.getComponentId());
			} else if (form.getName().startsWith("__state_transition")) {
				usageDetails.setFormType(FieldFormUsage.FormType.STATEFLOW_TRANSITION);
				usageDetails.setComponentId(Long.parseLong(form.getName().replace("__state_transition_", "").split("_")[0]));
				transitionIds.add(usageDetails.getComponentId());
			} else {
				usageDetails.setFormType(FieldFormUsage.FormType.TEMPLATE);
				usageDetails.setComponentId(form.getId());
				usageDetails.setComponentName(form.getDisplayName());
			}
			usageDetailsList.add(usageDetails);
		}
		List<Long> workFlowIds = new ArrayList<Long>();
		workFlowIds.addAll(buttonIds);
		workFlowIds.addAll(transitionIds);
		workFlowIds.addAll(approvalIds);
		Map<Long, Long> transitionVsStateFlowId = (transitionIds.size() > 0) ? WorkflowUtil.getStateFlowIdFromTransitionId(transitionIds) : null;
		if(transitionVsStateFlowId != null && transitionVsStateFlowId.size() > 0) {
			workFlowIds.addAll(transitionVsStateFlowId.values());
		}
		Map<Long, String> componentNameMap = (workFlowIds.size() > 0) ? WorkflowUtil.getWorkFlowNameFromId(workFlowIds) : null;

		for(FieldFormUsage formusage : usageDetailsList) {
			switch(formusage.getFormType()) {
				case APPROVAL_PROCESS:
				case CUSTOM_BUTTON:
					formusage.setComponentName(componentNameMap.get(formusage.getComponentId()));
					break;
				case STATEFLOW_TRANSITION:
					formusage.setComponentName(componentNameMap.get(formusage.getComponentId()));
					formusage.setParentFeature("STATE_FLOW");
					formusage.setParentFeatureName(componentNameMap.get(transitionVsStateFlowId.get(formusage.getComponentId())));
					break;
				default:
					break;
			}
		}

		context.put(FacilioConstants.ContextNames.FIELD_FORM_USAGE_DETAILS, usageDetailsList);
		return false;
	}

	

}
