package com.facilio.bmsconsole.commands;

import java.util.Map;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
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
		Map<String, Object> props = FieldUtil.getAsProperties(editedForm);
		FacilioModule formModule = ModuleFactory.getFormModule();

		if (editedForm.getStateFlowId() != -99) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(formModule.getTableName())
					.select(FieldFactory.getFormFields())
					.andCondition(CriteriaAPI.getIdCondition(editedForm.getId(), formModule));
			Map<String, Object> map = builder.fetchFirst();
			Long stateFlowId = (Long) map.get("stateFlowId");
			if (stateFlowId != null && stateFlowId > 0) {
				boolean stateFlowUsedInAnyForm = FormsAPI.isStateFlowUsedInAnyForm(stateFlowId, editedForm.getId());
				if (!stateFlowUsedInAnyForm) {
					StateFlowRulesAPI.updateFormLevel(stateFlowId, false);
				}
			}
		}
		
		GenericUpdateRecordBuilder formUpdateBuilder = new GenericUpdateRecordBuilder()
				.table(formModule.getTableName())
				.fields(FieldFactory.getFormFields())
				.andCondition(CriteriaAPI.getIdCondition(editedForm.getId(), formModule));
		
		formUpdateBuilder.update(props);

		if (editedForm.getStateFlowId() > 0) {
			StateFlowRulesAPI.updateFormLevel(editedForm.getStateFlowId(), true);
		}
		
		if (CollectionUtils.isNotEmpty(editedForm.getSections())) {
			FormsAPI.initFormFields(editedForm);
		}
		
		return false;
	}

}
