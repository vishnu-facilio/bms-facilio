package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

public class DeleteFormCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
		boolean fetchOnlySubformRules = (boolean) context.getOrDefault(FormRuleAPI.FETCH_ONLY_SUB_FORM_RULES,false);

		if(fetchOnlySubformRules){
			validateSubFormRule(formId);
		}
		FacilioForm facilioForm = FormsAPI.getFormFromDB(formId,false);
		context.put(FacilioConstants.ContextNames.FORM,facilioForm);
		int count = FormsAPI.deleteForms(Collections.singletonList(formId));
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		
		return false;
	}


	private static void validateSubFormRule(long subFormId) throws Exception{


		Map<String, FacilioField> formRuleFieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(formRuleFieldMap.get("subFormId"), ""+subFormId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(formRuleFieldMap.get("type"), ""+FormRuleContext.FormRuleType.FROM_RULE.getIntVal(), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		FacilioUtil.throwIllegalArgumentException(CollectionUtils.isNotEmpty(props), "Sub form used in sub form rules.");

	}

}
