package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetFormMetaCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String[] formNames = (String[]) context.get(FacilioConstants.ContextNames.FORM_NAMES);
		if (formNames != null && formNames.length > 0) {
			List<FacilioForm> forms = new ArrayList<>();
			for (String formName: formNames) {
				FacilioForm form = getFormFromDB(formName);
				if (form == null) {
					form = FormFactory.getForm(formName);
					if (form == null) {
						throw new IllegalArgumentException("Invalid Form " + formName);
					}
					List<FormField> fields = form.getFields();
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					String moduleName = form.getModule().getName();
					form.setModule(modBean.getModule(moduleName));
					
					for (FormField f : fields) {
						String fieldName = f.getName();
						FacilioField field = modBean.getField(fieldName, moduleName);
						if (field != null) {
							f.setField(field);
							f.setFieldId(field.getFieldId());
						}
					}
				}
				forms.add(form);
			}
			context.put(FacilioConstants.ContextNames.FORMS, forms);
		}
		return false;
	}

	private FacilioForm getFormFromDB(String formName) throws Exception {
		FacilioModule formModule = ModuleFactory.getFormModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(formModule.getTableName())
				.select(FieldFactory.getFormFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(formModule))
				.andCustomWhere("NAME = ?", formName);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props == null || props.isEmpty()) {
			return null;
		}
		
		FacilioForm form = FieldUtil.getAsBeanFromMap(props.get(0), FacilioForm.class);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long modid = form.getModuleId();
		form.setModule(modBean.getModule(modid));
		
		FacilioModule fieldsModule = ModuleFactory.getFormFieldsModule();
		selectBuilder = new GenericSelectRecordBuilder()
				.table(fieldsModule.getTableName())
				.select(FieldFactory.getFormFieldsFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(fieldsModule))
				.andCondition(CriteriaAPI.getCondition("FORMID", "formId", String.valueOf(props.get(0).get("id")), NumberOperators.EQUALS))
				.orderBy("SEQUENCE_NUMBER");
		
		props = selectBuilder.get();
		
		List<FormField> fields = new ArrayList<>();
		for (Map<String, Object> p: props) {
			FormField f = FieldUtil.getAsBeanFromMap(p, FormField.class);
			if (f.getFieldId() != -1) {
				FacilioField field =  modBean.getField(f.getFieldId());
				f.setField(field);
				f.setName(field.getName());
			}
			fields.add(f);
		}
		
		form.setFields(fields);
		return form;
	}

}
