package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FetchFieldsTemplates extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		List<FacilioForm> forms = (List<FacilioForm>) context.get(FacilioConstants.ContextNames.FORMS);
		Map<Long, FacilioForm> formVsIdMap = new HashMap<Long, FacilioForm>();
		Map<Long, List<FacilioForm>> fieldIdVsFormdMap = new HashMap<Long, List<FacilioForm>>();
		List<Long> fieldIds = fields.stream().map(a -> a.getId()).collect(Collectors.toList());
		if (forms != null && forms.size() > 0) {
			for(FacilioForm form :forms) {
				formVsIdMap.put(form.getId(), form);
			}
		}
		FacilioModule module = ModuleFactory.getFormFieldsModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormFieldsFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS))
				;
		List<Map<String, Object>> props = selectBuilder.get();
		for(Map<String, Object> prop : props) {
			FormField formField = FieldUtil.getAsBeanFromMap(prop, FormField.class);
				Long fieldId = (Long) formField.getFieldId();
				Long formId = (Long) formField.getFormId();
				FacilioForm form = formVsIdMap.get(formId);
				List<FacilioForm> facilioForms = fieldIdVsFormdMap.get(fieldId);
				if (facilioForms == null) {
					facilioForms = new ArrayList<>();
					fieldIdVsFormdMap.put(fieldId, facilioForms);
				}
				facilioForms.add(form);
		}
		context.put(FacilioConstants.ContextNames.FORM_FIELD_MAP, fieldIdVsFormdMap);
		System.out.println("*******"+ formVsIdMap + props + fieldIdVsFormdMap );
		return false;
	}

	

}
