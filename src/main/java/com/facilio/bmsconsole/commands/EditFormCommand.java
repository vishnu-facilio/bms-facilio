package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class EditFormCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		FacilioForm editedForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);

		User currentUser = AccountUtil.getCurrentUser();
		long systemTime = System.currentTimeMillis();
		editedForm.setSysModifiedTime(systemTime);

		if (currentUser != null) {
			editedForm.setSysModifiedBy(AccountUtil.getCurrentUser().getPeopleId());
		}

		Map<String, Object> props = FieldUtil.getAsProperties(editedForm);
		FacilioModule formModule = ModuleFactory.getFormModule();

		FacilioForm existingForm = FormsAPI.getFormFromDB(editedForm.getId(), false);
		removeExistingStateFlow(existingForm);

		GenericUpdateRecordBuilder formUpdateBuilder = new GenericUpdateRecordBuilder()
				.table(formModule.getTableName())
				.fields(FieldFactory.getFormFields())
				.andCondition(CriteriaAPI.getIdCondition(editedForm.getId(), formModule));
		
		formUpdateBuilder.update(props);

		// Update stateFlow property
		if (editedForm.getStateFlowId() > 0) {
			StateFlowRuleContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(editedForm.getStateFlowId());
			if (stateFlowContext == null) {
				throw new IllegalArgumentException("Invalid StateFlow");
			}
			if (stateFlowContext.getModuleId() != existingForm.getModuleId()) {
				throw new IllegalArgumentException("StateFlow and Form is from different module");
			}
			StateFlowRulesAPI.updateFormLevel(editedForm.getStateFlowId(), true);
		}
		
		if (CollectionUtils.isNotEmpty(editedForm.getSections())) {
			FormsAPI.initFormFields(editedForm);
		}
		
		return false;
	}

	private void removeExistingStateFlow(FacilioForm form) throws Exception {
		if (form == null) {
			return;
		}
		Long stateFlowId = form.getStateFlowId();
		if (stateFlowId != null && stateFlowId > 0) {
			boolean stateFlowUsedInAnyForm = FormsAPI.isStateFlowUsedInAnyForm(stateFlowId, form.getId());
			if (!stateFlowUsedInAnyForm) {
				StateFlowRulesAPI.updateFormLevel(stateFlowId, false);
			}
		}
	}

}
