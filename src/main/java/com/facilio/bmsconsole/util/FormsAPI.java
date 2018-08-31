package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class FormsAPI {
	public static Map<String, Set<FacilioForm>> getAllForms(FormType formtype) throws Exception {
		Map<String, Set<FacilioForm>> forms = getAllFormsFromDB(formtype);
		if (forms.isEmpty()) {
			return FormFactory.getAllForms(formtype);
		}
		return forms;
	}

	private static Map<String, Set<FacilioForm>> getAllFormsFromDB(FormType formtype) throws Exception {
		Criteria formTypeCriteria = getFormTypeCriteria(formtype);
		List<FacilioForm> forms = getFormFromDB(formTypeCriteria);
		
		if (forms == null || forms.isEmpty()) {
			return Collections.emptyMap();
		}
		
		Map<String, Set<FacilioForm>> moduleForms = new HashMap<>();
		
		for (FacilioForm form: forms) {
			String moduleName = form.getModule().getName();
			Set<FacilioForm> set = moduleForms.get(form.getModule().getName());
			if (set == null) {
				set = new HashSet<>();
				moduleForms.put(moduleName, set);
			}
			set.add(form);
		}
		return moduleForms;
	}

	private static Criteria getFormTypeCriteria(FormType formtype) {
		Criteria formTypeCriteria = new Criteria();
		Condition formTypeCondition = new Condition();
		formTypeCondition.setColumnName("FORM_TYPE");
		formTypeCondition.setFieldName("formType");
		formTypeCondition.setOperator(NumberOperators.EQUALS);
		formTypeCondition.setValue(String.valueOf(formtype.getIntVal()));
		formTypeCriteria.addAndCondition(formTypeCondition);
		return formTypeCriteria;
	}

	public static List<FacilioForm> getFormFromDB(Criteria criteria) throws Exception {
		FacilioModule formModule = ModuleFactory.getFormModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(formModule.getTableName())
				.select(FieldFactory.getFormFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(formModule));
		if (criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props == null || props.isEmpty()) {
			return null;
		}
		
		List<FacilioForm> forms = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule fieldsModule = ModuleFactory.getFormFieldsModule();
		GenericSelectRecordBuilder fieldSelectBuilder = new GenericSelectRecordBuilder()
				.table(fieldsModule.getTableName())
				.select(FieldFactory.getFormFieldsFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(fieldsModule));
	
		for (Map<String, Object> prop: props) {
			FacilioForm form = FieldUtil.getAsBeanFromMap(prop, FacilioForm.class);
			long modid = form.getModuleId();
			form.setModule(modBean.getModule(modid));
			fieldSelectBuilder
					.andCondition(CriteriaAPI.getCondition("FORMID", "formId", String.valueOf(prop.get("id")), NumberOperators.EQUALS))
					.orderBy("SEQUENCE_NUMBER");
			
			List<Map<String, Object>> fieldprops = fieldSelectBuilder.get();
			List<FormField> fields = new ArrayList<>();
			for (Map<String, Object> p: fieldprops) {
				FormField f = FieldUtil.getAsBeanFromMap(p, FormField.class);
				if (f.getFieldId() != -1) {
					FacilioField field =  modBean.getField(f.getFieldId());
					if (field instanceof LookupField) {
						FacilioModule lookupMod = ((LookupField) field).getLookupModule();
						if (lookupMod != null) {
							f.setLookupModuleName(lookupMod.getName());
						}
					}
					f.setName(field.getName());
				}
				fields.add(f);
			}
			
			form.setFields(fields);
			forms.add(form);
		}
		
		return forms;
	}
}
