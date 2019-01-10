package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.accounts.util.AccountUtil;
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
import com.facilio.bmsconsole.modules.FacilioField.FieldDisplayType;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class FormsAPI {
	public static Map<String, Set<FacilioForm>> getAllForms(FormType formtype) throws Exception {
		Map<String, Set<FacilioForm>> forms = new HashMap<> (FormFactory.getAllForms(formtype));
		Map<String, Set<FacilioForm>> dbForms = getAllFormsFromDB(formtype);
		if (!dbForms.isEmpty()) {
			forms.putAll(dbForms);
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
	
		for (Map<String, Object> prop: props) {
			GenericSelectRecordBuilder fieldSelectBuilder = new GenericSelectRecordBuilder()
					.table(fieldsModule.getTableName())
					.select(FieldFactory.getFormFieldsFields())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(fieldsModule));
			
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
				/***
				 * Temp handling to set name for form fields if fieldId is empty 
				 * Should introduce name column in Form Fields 
				 */
				else if (f.getDisplayTypeEnum() == FieldDisplayType.TICKETNOTES){
					f.setName("comment");
				}
				else if (f.getDisplayTypeEnum() == FieldDisplayType.ATTACHMENT) {
					f.setName("attachedFiles");
				}
				else if (f.getDisplayTypeEnum() == FieldDisplayType.LOOKUP_SIMPLE && f.getDisplayName().equals("Site")) {
					f.setName("siteId");
					f.setLookupModuleName("site");
				}
				fields.add(f);
			}
			
			form.setFields(fields);
			forms.add(form);
		}
		
		return forms;
	}

	public static long createForm(FacilioForm editedForm, FacilioModule parent)
			throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		editedForm.setOrgId(orgId);
		editedForm.setModule(parent);
		Map<String, Object> props = FieldUtil.getAsProperties(editedForm);
		FacilioModule formModule = ModuleFactory.getFormModule();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(formModule.getTableName())
				.fields(FieldFactory.getFormFields());
		
		long id = insertBuilder.insert(props);
		
		List<Map<String, Object>> fieldProps = new ArrayList<>();
		
		int i = 1;
		
		for (FormField f: editedForm.getFields()) {
			f.setFormId(id);
			f.setOrgId(orgId);
			f.setSequenceNumber(i);
			if (f.getSpan() == -1) {
				f.setSpan(1);
			}
			Map<String, Object> prop = FieldUtil.getAsProperties(f);
			if (prop.get("required") == null) {
				prop.put("required", false);
			}
			fieldProps.add(prop);
			++i;
		}
		
		FacilioModule formFieldModule = ModuleFactory.getFormFieldsModule();
		GenericInsertRecordBuilder fieldInsertBuilder = new GenericInsertRecordBuilder()
				.table(formFieldModule.getTableName())
				.fields(FieldFactory.getFormFieldsFields())
				.addRecords(fieldProps);
		
		fieldInsertBuilder.save();
		editedForm.setId(id);
		return id;
	}
	
	public static List<FacilioForm> getFromsFromDB(Collection<Long> ids) throws Exception {
		Criteria formNameCriteria = new Criteria();
		formNameCriteria.addAndCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getFormModule()));
		List<FacilioForm> forms = getFormFromDB(formNameCriteria);
		return forms;
	}
	
	public static FacilioForm getFormFromDB(long id) throws Exception {
		List<FacilioForm> forms = getFromsFromDB(Arrays.asList(id));
		if (forms != null && !forms.isEmpty()) {
			return forms.get(0);
		}
		return null;
	}
	
	public static Map<Long, FacilioForm> getFormsAsMap(Collection<Long> ids) throws Exception {
		List<FacilioForm> forms = getFromsFromDB(ids);
		if (forms != null && !forms.isEmpty()) {
			Map<Long, FacilioForm> formMap = new HashMap<>();
			for (FacilioForm form: forms) {
				formMap.put(form.getId(), form);
			}
			return formMap;
		}
		return null;
	}
	
	public static int deleteForms (Collection<Long> ids) throws SQLException {
		FacilioModule module = ModuleFactory.getFormModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(ids, module))
														;
		return deleteBuilder.delete();
	}
	
	public static List<FormField> getFacilioFieldsFromFormFields (List<FacilioField> fields) throws SQLException {
		List<FormField> formFields = new ArrayList<FormField>();
		for(FacilioField field: fields) {
			FormField forms = new FormField();
			forms.setDisplayName(field.getDisplayName());
			forms.setName(field.getName());
			forms.setFieldId(field.getFieldId());
			forms.setDisplayType(field.getDisplayType());
			formFields.add(forms);
		}
		return formFields;
	}
}
