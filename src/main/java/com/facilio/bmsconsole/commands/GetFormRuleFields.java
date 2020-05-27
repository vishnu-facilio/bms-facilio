package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetFormRuleFields extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Long formId = (Long) context.getOrDefault(FacilioConstants.ContextNames.FORM_ID, -1l);
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		Boolean fetchFormRuleFields = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS);
		if (fetchFormRuleFields != null && fetchFormRuleFields && formId > 0) {
			List<FacilioField> fields = FieldFactory.getFormRuleFields();
			
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(ModuleFactory.getFormRuleModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), formId+"", NumberOperators.EQUALS))
					;
			
			List<Map<String, Object>> props = selectBuilder.get();
			List<Long> ruleAssociatedFieldIds = new ArrayList<>();
			if (props != null && !props.isEmpty()) {
				for(Map<String, Object> prop :props) {
					if (!ruleAssociatedFieldIds.contains((Long)prop.get("fieldId"))) {
						ruleAssociatedFieldIds.add((Long)prop.get("fieldId"));
					}
				}
			}
			if (ruleAssociatedFieldIds.size() > 0 && form != null) {
				form.setRuleFieldIds(ruleAssociatedFieldIds);
			}
		}
		return false;
	}	

}
