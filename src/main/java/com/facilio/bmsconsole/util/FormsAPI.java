package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormSiteRelationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.constants.FacilioConstants.Builder;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;

public class FormsAPI {
	
	public static Map<String, Collection<FacilioForm>> getAllForms(FormType formtype) throws Exception {
		Map<String, Collection<FacilioForm>> forms = new HashMap<> (FormFactory.getAllForms(formtype));
		if (forms != null && AccountUtil.getCurrentAccount().isFromIos()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(Map.Entry<String, Collection<FacilioForm>> entry :forms.entrySet()) {
				Iterator<FacilioForm> iterator = entry.getValue().iterator();
				List<FacilioForm> formsList = new ArrayList<>();
				while (iterator.hasNext()) {
					FacilioForm form = iterator.next();
					FacilioForm mutatedForm = new FacilioForm(form);
					formsList.add(mutatedForm);
					
					String moduleName = form.getModule().getName();
					int count = form.getFields().size();
					List<FacilioField> customFields = modBean.getAllCustomFields(moduleName);
					if (customFields != null && !customFields.isEmpty()) {
						List<FormField> fields = new ArrayList<>(form.getFields());
						mutatedForm.setFields(fields);
						for (FacilioField f: customFields) {
							count = count + 1;
							fields.add(FormsAPI.getFormFieldFromFacilioField(f, count));
						}
					}
				}
				entry.setValue(formsList);
			}
		}
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
	
	public static FacilioForm getFormFromDB(String formName, FacilioModule module) throws Exception {

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormFields());
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("name"), formName, StringOperators.IS));
		if (module != null) {
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(module.getModuleId()), StringOperators.IS));
		}
		
		List<FacilioForm> forms = getFormFromDB(criteria);
		if (forms == null || forms.isEmpty()) {
			return null;
		}
		return forms.get(0);
	}

	public static List<FacilioForm> getFormFromDB(Criteria criteria) throws Exception {
		return getDBFormList(null, (List<Integer>) null, criteria, null, true, false,true);
	}
	
	private static void setFormFields (FacilioForm form) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule fieldsModule = ModuleFactory.getFormFieldsModule();
		
		Map<Long,FormSection> sectionMap = null;
		if (form.getSections() != null) {
			sectionMap = form.getSections().stream().collect(Collectors.toMap(FormSection::getId, Function.identity()));
		}
		
		GenericSelectRecordBuilder fieldSelectBuilder = new GenericSelectRecordBuilder()
				.table(fieldsModule.getTableName())
				.select(FieldFactory.getFormFieldsFields());
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(fieldsModule));
		
		long modid = form.getModuleId();
		form.setModule(modBean.getModule(modid));
		fieldSelectBuilder
				.andCondition(CriteriaAPI.getCondition("FORMID", "formId", String.valueOf(form.getId()), NumberOperators.EQUALS))
				.orderBy("SEQUENCE_NUMBER, SPAN");
		
		List<Map<String, Object>> fieldprops = fieldSelectBuilder.get();
		List<FormField> fields = new ArrayList<>();
		for (Map<String, Object> p: fieldprops) {
			FormField f = FieldUtil.getAsBeanFromMap(p, FormField.class);
			if (f.getFieldId() != -1) {
				FacilioField field =  modBean.getField(f.getFieldId());
				f.setField(field);
				if (field instanceof LookupField) {
					FacilioModule lookupMod = ((LookupField) field).getLookupModule();
					if (lookupMod != null) {
						f.setLookupModuleName(lookupMod.getName());
					}
				}
				if (f.getName() == null) {
					f.setName(field.getName());
				}
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
			else if (f.getDisplayTypeEnum() == FieldDisplayType.TEXTBOX && f.getDisplayName().equals("Contact Name") ) {
				f.setName("contact_name");
			}
			else if (f.getDisplayTypeEnum() == FieldDisplayType.TEXTBOX && f.getDisplayName().equals("Contact Email") ) {
				f.setName("contact_email");
			}
			else if (f.getDisplayTypeEnum() == FieldDisplayType.LOGO ) {
				f.setName("logo");
			}
			else if (f.getDisplayTypeEnum() == FieldDisplayType.LOOKUP_SIMPLE && f.getDisplayName().equals("Site")) {
				f.setName("siteId");
				f.setLookupModuleName("site");
			}
			else if (f.getDisplayTypeEnum() == FieldDisplayType.TEAM) {
				f.setName("groups");
				f.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
				f.setLookupModuleName("groups");
			}
			else if (f.getDisplayTypeEnum() == FieldDisplayType.SPACEMULTICHOOSER) {
				f.setName("spaces");
			}
			else if (f.getDisplayTypeEnum() == FieldDisplayType.ASSETMULTICHOOSER) {
				f.setName("utilityMeters");
			}
			fields.add(f);
			if (f.getSectionId() != -1) {
				sectionMap.get(f.getSectionId()).addField(f);
			}
		}
		form.setFields(fields);
	}
	
	private static void setFormSections(FacilioForm form) throws Exception {
		FacilioModule sectionModule = ModuleFactory.getFormSectionModule();
		List<FacilioField> fields = FieldFactory.getFormSectionFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(sectionModule.getTableName())
				.select(fields)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(sectionModule))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(form.getId()), NumberOperators.EQUALS))
				.orderBy(fieldMap.get("sequenceNumber").getColumnName());
				;
				
		
		List<Map<String, Object>> props = builder.get();
		if (props != null && !props.isEmpty()) {
			List<FormSection> sections = FieldUtil.getAsBeanListFromMapList(props, FormSection.class);
			if (CollectionUtils.isNotEmpty(sections)) {
				for (FormSection section : sections) {
					if (section.getSectionTypeEnum() == FormSection.SectionType.SUB_FORM) {
						long subFormId = section.getSubFormId();
						FacilioForm subForm = getFormFromDB(subFormId);
						section.setSubForm(subForm);
					}
				}
			}
			form.setSections(sections);
		}
	}
	
	public static long createForm(FacilioForm form, FacilioModule module) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		form.setOrgId(orgId);
		form.setModule(module);
		if (form.getShowInWeb() == null) {
			form.setShowInWeb(true);
		}
		
		Map<String, Object> props = FieldUtil.getAsProperties(form);
		FacilioModule formModule = ModuleFactory.getFormModule();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(formModule.getTableName())
				.fields(FieldFactory.getFormFields());
		
		long id = insertBuilder.insert(props);
		form.setId(id);

		if (form.getStateFlowId() > 0) {
			StateFlowRulesAPI.updateFormLevel(form.getStateFlowId(), true);
		}
		
		addFormFields(id, form);
		
		return id;
	}

	public static boolean isStateFlowUsedInAnyForm(long stateFlowId, Long formId) throws Exception {
		return isStateFlowUsedInAnyForm(stateFlowId, Collections.singletonList(formId));
	}

	public static boolean isStateFlowUsedInAnyForm(long stateFlowId, List<Long> formIds) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getFormModule().getTableName())
				.select(FieldFactory.getFormFields())
				.andCondition(CriteriaAPI.getCondition("STATE_FLOW_ID", "stateFlowId", String.valueOf(stateFlowId), NumberOperators.EQUALS));
		if (CollectionUtils.isNotEmpty(formIds)) {
			builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(ModuleFactory.getFormModule()), StringUtils.join(formIds, ","), NumberOperators.NOT_EQUALS));
		}
		List<Map<String, Object>> maps = builder.get();
		return (CollectionUtils.isNotEmpty(maps));
	}
	
	public static void addFormFields (long formId, FacilioForm form) throws Exception {
		List<FormField> fields = null;
		if (form.getSections() == null && form.getFields() != null) {
			FormSection section = new FormSection("Default", 1, form.getFields(), false);
			form.setSections(Collections.singletonList(section));
		}
		if (CollectionUtils.isNotEmpty(form.getSections())) {
			addFormSections(formId, form.getSections());
			fields = getFormFieldsFromSections(form.getSections());
		}
		
		if (fields != null) {
			long orgId = AccountUtil.getCurrentOrg().getId();
			int i = 1;
			List<Map<String, Object>> fieldProps = new ArrayList<>();
			for (FormField f: fields) {
				f.setId(-1);
				f.setFormId(formId);
				f.setOrgId(orgId);
				f.setSequenceNumber(i);
				if (f.getSpan() == -1) {
					f.setSpan(1);
				}
				if(f.getFieldId() != -1) {
					f.setName(null);
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
		}
	}
	
	private static void addFormSections (long formId, List<FormSection> sections) throws Exception {
		if (sections != null) {
			long orgId = AccountUtil.getCurrentOrg().getId();
			int i = 1;
			List<Map<String, Object>> props = new ArrayList<>();
			Map<Long, FormSection> sectionMap = new HashMap<>();
			for (FormSection f: sections) {
				f.setId(-1);
				f.setFormId(formId);
				f.setOrgId(orgId);
				f.setSequenceNumber(i);
				sectionMap.put(Long.valueOf(i++), f);
				Map<String, Object> prop = FieldUtil.getAsProperties(f);
				props.add(prop);
			}
			
			FacilioModule module = ModuleFactory.getFormSectionModule();
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getFormSectionFields())
					.addRecords(props);
			
			builder.save();
			
			List<FormField> fields = new ArrayList<>();
			for(Map<String, Object> prop: props) {
				long id = (long) prop.get("id");
				long sequenceNumber = (long) prop.get("sequenceNumber");
				FormSection section = sectionMap.get(sequenceNumber);
				section.setId(id);
				section.getFields().forEach(field -> field.setSectionId(id));
				fields.addAll(section.getFields());
			}
		}
	}
	
	private static List<FormField> getFormFieldsFromSections(List<FormSection> sections) {
		return sections.stream().map(section -> section.getFields() != null ? section.getFields() : new ArrayList<FormField>()).flatMap(List::stream).collect(Collectors.toList());
	}
	
	public static int deleteFormFields(long formId) throws Exception {
		deleteFormSections(formId);
		
		FacilioModule module = ModuleFactory.getFormFieldsModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormFieldsFields());
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(formId), NumberOperators.EQUALS))
														;
		return deleteBuilder.delete();
	}
	
	private static int deleteFormSections(long formId) throws Exception {
		FacilioModule module = ModuleFactory.getFormSectionModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormSectionFields());
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), String.valueOf(formId), NumberOperators.EQUALS))
														;
		return deleteBuilder.delete();
	}
	
	// To update fields based on formfieldid...values for fieldNamesToUpdate should not be null
	public static void updateFormFields(List<FormField> formFields, List<String> fieldNamesToUpdate) throws Exception {
		FacilioModule module = ModuleFactory.getFormFieldsModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormFieldsFields());
		
		List<FacilioField> updateFields = new ArrayList<>();
		for(String fieldName: fieldNamesToUpdate) {
			updateFields.add(fieldMap.get(fieldName));
		}

		List<FacilioField> whereFields = new ArrayList<>();
		whereFields.add(fieldMap.get("resourceId"));
		whereFields.add(fieldMap.get("fieldId"));
		
		List<Map<String, Object>> props = FieldUtil.getAsMapList(formFields, FormField.class);
		
		List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList = props.stream().map(field -> {
			GenericUpdateRecordBuilder.BatchUpdateByIdContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
			
//			Map<String, Object> fieldMap = FieldUtil.getAsProperties(field);
//			List<Map<String, Object>> props = FieldUtil.getAsPropertiesList(formFields);
			for(String fieldName: fieldNamesToUpdate) {
//				Object value = PropertyUtils.getProperty(field, fieldName);
				updateVal.addUpdateValue(fieldName,  field.get(fieldName));
			}
			
			updateVal.setWhereId((long) field.get("id"));

			return updateVal;
		}).collect(Collectors.toList());

		new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(updateFields)
				.batchUpdateById(batchUpdateList)
				;
	}
	
	public static void initFormFields(FacilioForm form) throws Exception {
		FormsAPI.deleteFormFields(form.getId());
		FormsAPI.addFormFields(form.getId(), form);
	}
	
	public static List<FacilioForm> getFormsFromDB(Collection<Long> ids) throws Exception {
		Criteria formNameCriteria = new Criteria();
		formNameCriteria.addAndCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getFormModule()));
		List<FacilioForm> forms = getFormFromDB(formNameCriteria);
		return forms;
	}
	
	public static FacilioForm getFormFromDB(long id) throws Exception {
		List<FacilioForm> forms = getFormsFromDB(Arrays.asList(id));
		if (forms != null && !forms.isEmpty()) {
			return forms.get(0);
		}
		return null;
	}

	public static FacilioForm getFormFromDB(long id, boolean fetchFields) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getFormModule()));
		List<FacilioForm> dbFormList = getDBFormList(null, (List<Integer>) null, criteria, null, fetchFields, false, true);
		if (CollectionUtils.isNotEmpty(dbFormList)) {
			return dbFormList.get(0);
		}
		return null;
	}
		
	public static Map<Long, FacilioForm> getFormsAsMap(Collection<Long> ids) throws Exception {
		List<FacilioForm> forms = getFormsFromDB(ids);
		if (forms != null && !forms.isEmpty()) {
			Map<Long, FacilioForm> formMap = new HashMap<>();
			for (FacilioForm form: forms) {
				formMap.put(form.getId(), form);
			}
			return formMap;
		}
		return null;
	}
	
	public static int deleteForms (Collection<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getFormModule();

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getFormFields())
				.andCondition(CriteriaAPI.getIdCondition(ids, module));
		List<FacilioForm> forms = FieldUtil.getAsBeanListFromMapList(builder.get(), FacilioForm.class);
		if (CollectionUtils.isNotEmpty(forms)) {
			Set<Long> stateflowIds = new HashSet<>();
			List<Long> formIds = new ArrayList<>();
			for (FacilioForm form : forms) {
				stateflowIds.add(form.getStateFlowId());
				formIds.add(form.getId());
			}

			if (CollectionUtils.isNotEmpty(stateflowIds)) {
				for (Long stateFlowId : stateflowIds) {
					if (!isStateFlowUsedInAnyForm(stateFlowId, formIds)) {
						StateFlowRulesAPI.updateFormLevel(stateFlowId, false);
					}
				}
			}
		}

		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(ids, module))
														;
		return deleteBuilder.delete();
	}
	
	public static List<FormField> getFormFieldsFromFacilioFields (List<FacilioField> fields, int count) throws Exception {
		List<FormField> formFields = new ArrayList<FormField>();
		for(FacilioField field: fields) {
			FormField formField = getFormFieldFromFacilioField(field, ++count);
			formFields.add(formField);
		}
		return formFields;
	}
	
	public static FormField getFormFieldFromFacilioField(FacilioField facilioField, int count) {
		FormField formField = new FormField(facilioField.getName(), facilioField.getDisplayType(), facilioField.getDisplayName(), FormField.Required.OPTIONAL, count, 1);
		formField.setField(facilioField);
		formField.setFieldId(facilioField.getFieldId());
		if (facilioField instanceof LookupField) {
			formField.setLookupModuleName(((LookupField)facilioField).getLookupModule().getName());
		}
		return formField;
	}
	
	public static Map<String, FacilioForm> getFormsAsMap (String moduleName,List<Integer> formTypes,Boolean fetchExtendedModuleForms,Boolean fetchDisabledForms) throws Exception {
		List<FacilioForm> forms = getDBFormList(moduleName, formTypes, fetchExtendedModuleForms, fetchDisabledForms);
		Map<String, FacilioForm> formMap = new LinkedHashMap<>();
		if (forms != null && !forms.isEmpty()) {
			for(FacilioForm form: forms) {
				formMap.put(form.getName(), form);
			}
			return formMap;
		}
		return null;
	}
	
	public static List<FacilioForm> getDBFormList(String moduleName,List<Integer> formTypes, Boolean fetchExtendedModuleForms, Boolean fetchDisabledForms) throws Exception{
		return getDBFormList(moduleName, formTypes, null, null, false, fetchExtendedModuleForms, fetchDisabledForms);
	}
	
	public static List<FacilioForm> getDBFormList(String moduleName,FormType formType, Criteria criteria, Map<String, Object> selectParams, boolean fetchFields) throws Exception{
		List<Integer> formTypes = null;
		if (formType != null) {
			formTypes = Collections.singletonList(formType.getIntVal());
		}
		return getDBFormList(moduleName, formTypes, criteria, selectParams, fetchFields, false, true);
	}
	
	public static List<FacilioForm> getDBFormList(String moduleName,List<Integer> formTypes, Criteria criteria, Map<String, Object> selectParams, boolean fetchFields, Boolean fetchExtendedModuleForms,Boolean fetchDisabledForms) throws Exception{
		
		FacilioModule formModule = ModuleFactory.getFormModule();
		
		List<FacilioField> fields = FieldFactory.getFormFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Map<String, FacilioField> formSiteFieldMap = FieldFactory.getAsMap(FieldFactory.getFormSiteRelationFields());
		List<Long> spaces = new ArrayList<>();
		if (AccountUtil.getCurrentSiteId() > 0) {
			spaces.add(AccountUtil.getCurrentSiteId());

		}
		else if (AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getAccessibleSpace() != null) {
			spaces = AccountUtil.getCurrentUser().getAccessibleSpace();
		}

		 GenericSelectRecordBuilder forms1 = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getFormSiteRelationModule().getTableName())
					.select(Collections.singleton(formSiteFieldMap.get("formId")))
					.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getFormSiteRelationModule()));

		 if (spaces != null && spaces.size() > 0) {
			 StringJoiner spaceIds = new StringJoiner(",");
			 spaces.stream().forEach(f -> spaceIds.add(String.valueOf(f)));
			 forms1.andCondition(CriteriaAPI.getCondition(formSiteFieldMap.get("siteId"), spaceIds.toString(),NumberOperators.EQUALS));
		 }
		 GenericSelectRecordBuilder forms2 = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getFormSiteRelationModule().getTableName())
					.select(Collections.singleton(formSiteFieldMap.get("formId")))
					.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getFormSiteRelationModule()));


		 GenericSelectRecordBuilder formListBuilder = new GenericSelectRecordBuilder()
									.select(fields)
									.table(formModule.getTableName())
									.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), formModule));
		 


		 if (moduleName != null) {
				List<Long> moduleIds = new ArrayList<>();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				long moduleId=module.getModuleId();
				moduleIds.add(moduleId);
				if (module.getExtendModule() != null && fetchExtendedModuleForms != null && fetchExtendedModuleForms) {
					moduleIds.add(module.getExtendModule().getModuleId());
					StringJoiner ids = new StringJoiner(",");
					moduleIds.stream().forEach(f -> ids.add(String.valueOf(f)));
					formListBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), ids.toString(), NumberOperators.EQUALS));
				}
				else {
					formListBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS));
				}
			}
		if (spaces != null && spaces.size() > 0) {
			formListBuilder.andCustomWhere("(" + FieldFactory.getIdField().getColumnName() + " in (" + forms1.constructSelectStatement() + ")");
			formListBuilder.orCustomWhere(FieldFactory.getIdField().getColumnName() + " not in (" + forms2.constructSelectStatement() + "))");
		}
		if (fetchDisabledForms != null && fetchDisabledForms) {
		}
		else {
			formListBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("showInWeb"), "true", BooleanOperators.IS));
		}
		if (formTypes != null) {
			formListBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("formType"), StringUtils.join(formTypes, ","), NumberOperators.EQUALS));
		}
		if (criteria != null) {
			formListBuilder.andCriteria(criteria);
		}
		if (MapUtils.isNotEmpty(selectParams)) {
			if (selectParams.containsKey(Builder.ORDER_BY)) {
				String orderBy = (String) selectParams.get(Builder.ORDER_BY);
				String orderByType = (String) selectParams.get(Builder.ORDER_BY_TYPE);
				formListBuilder.orderBy(fieldMap.get(orderBy).getCompleteColumnName() + (orderByType != null ? " " + orderByType : " asc"));
			}
			if (selectParams.containsKey(Builder.LIMIT)) {
				formListBuilder.limit((int) selectParams.get(Builder.LIMIT));
			}
		}
		else {
			formListBuilder.orderBy("ID asc");
		}
		System.out.println("********"+ formListBuilder.constructSelectStatement());
		List<FacilioForm> forms = FieldUtil.getAsBeanListFromMapList(formListBuilder.get(), FacilioForm.class);
		
		forms = getAsFormSiteRelationListMap(forms, spaces);
		
		

		if (fetchFields && CollectionUtils.isNotEmpty(forms)) {
			for (FacilioForm form: forms) {
				setFormSections(form);
				setFormFields(form);
			}
		}
		return forms;
			
	}
	
	private static List<FacilioForm> getAsFormSiteRelationListMap(List<FacilioForm> forms, List<Long> spaces) throws Exception {
		if (forms != null && forms.size() > 0) {
			List<Long> formIds = forms.stream().map(FacilioForm::getId).collect(Collectors.toList());
			StringJoiner ids = new StringJoiner(",");
			formIds.stream().forEach(f -> ids.add(String.valueOf(f)));
			Map<String, FacilioField> formSiteFieldMap = FieldFactory.getAsMap(FieldFactory.getFormSiteRelationFields());
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getFormSiteRelationFields())
					.table(ModuleFactory.getFormSiteRelationModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(formSiteFieldMap.get("formId"), ids.toString(), NumberOperators.EQUALS));
			
			List<FormSiteRelationContext> props = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(), FormSiteRelationContext.class);
			Map<Long, List<Long>> formVsSite = new HashMap<>();
			if (props != null && props.size() > 0) {
			for (FormSiteRelationContext prop : props) {
				Long formId = (Long) prop.getFormId();
				Long siteId = (Long) prop.getSiteId();
				
				List<Long> siteIds = formVsSite.get(formId);
				if (siteIds == null) {
					siteIds = new ArrayList<>();
					formVsSite.put(formId, siteIds);
				}
				siteIds.add(siteId);
			}
			for (FacilioForm form: forms) {
				if (formVsSite.get(form.getId()) != null) {
					form.setSiteIds(formVsSite.get(form.getId()));
				}
			}
			}

		}
		// TODO Auto-generated method stub
		return forms;
	}

	private static void addUnusedSystemFields(FacilioForm form, List<FormField> defaultFields) throws Exception {
		if (form.getFormTypeEnum() == FormType.WEB) {
			addUnusedWebSystemFields(form, defaultFields);
		}
		else if (form.getFormTypeEnum() == FormType.PORTAL) {
			addUnusedPortalSystemFields(form, defaultFields);
		}
	}
	
	private static void addUnusedWebSystemFields(FacilioForm form, List<FormField> defaultFields) throws Exception {
		List<FormField> fields = new ArrayList<>();
		if (form.getModule().getName() != null) {
			switch (form.getModule().getName()) {
			case ContextNames.WORK_ORDER:
				fields.addAll(FormFactory.getRequesterFormFields(false, false));
				fields.add(new FormField("dueDate", FieldDisplayType.DATETIME, "Due Date", Required.OPTIONAL, 1, 1));
				fields.add(new FormField("isSignatureRequired", FieldDisplayType.DECISION_BOX, "Is Signature Required ", Required.OPTIONAL, 1, 1));
				fields.add(new FormField("responseDueDate", FieldDisplayType.DATETIME, "Response Due Date", Required.OPTIONAL, 1, 1));
				if (AccountUtil.isFeatureEnabled(FeatureLicense.TENANTS)) {
					fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL,"tenant", 1, 1));
				}
				break;
			case ContextNames.WORKPERMIT:
				fields.add(new FormField("expectedStartTime", FieldDisplayType.DATETIME, "Valid From", Required.OPTIONAL, 1, 1));
				fields.add(new FormField("expectedEndTime", FieldDisplayType.DATETIME, "Valid Till", Required.OPTIONAL, 1, 1));
				break;
			// Add fields here if it has to be shown in unused list and not there in the default form
		}
		}
		
		addToDefaultFields(form.getFields(), defaultFields, fields);

		if (form.getModule().isCustom()) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			boolean hasPhoto = false;
			for (FormField field : form.getFields()) {
				if (field.getField() != null) {
					if (field.getField().getName().equals("photo")) {
						hasPhoto = true;
						break;
					}
				}
			}
			if (!hasPhoto) {
				FacilioField photoField = modBean.getField("photo", form.getModule().getName());
				if (photoField != null) {
					defaultFields.add(getFormFieldFromFacilioField(photoField, 1));					
				}
			}
		}
	}
	
	private static void addUnusedPortalSystemFields(FacilioForm form, List<FormField> defaultFields) {
		List<FormField> fields = new ArrayList<>();
		switch (form.getModule().getName()) {
			case ContextNames.WORK_ORDER:
				fields.addAll(FormFactory.getWoClassifierFields());
				fields.add(FormFactory.getWoResourceField());
				break;
			// Add fields here if it has to be shown in unused list and not there in the default form
		}
		
		
		addToDefaultFields(form.getFields(), defaultFields, fields);
	}
	
	private static void addToDefaultFields(List<FormField> existingFields, List<FormField> defaultFields, List<FormField> newFields) {
		if (!newFields.isEmpty()) {
			List<String> fieldNames = existingFields.stream().map(FormField::getName).collect(Collectors.toList());
			defaultFields.addAll(newFields.stream().filter(field -> !fieldNames.contains(field.getName())).collect(Collectors.toList()));
		}
	}
	
	public static Map<String, List<FormField>> getFormUnusedFields(String moduleName, long formId) throws Exception {
		FacilioForm form = getFormFromDB(formId);
		List<FormField> fields = form.getFields();
		if (fields == null) {
			fields = getFormFieldsFromSections(form.getSections());
			form.setFields(fields);
		}
		Map<String, FormField> formFieldMap = fields.stream().collect(Collectors.toMap(FormField::getName, Function.identity()));
		
		
		List<FormField> systemFields = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (form.getModule().isCustom()) {
			addUnusedSystemFields(form, systemFields);
		}
		else {
			FacilioForm defaultForm = getDefaultForm(moduleName, form.getFormTypeEnum(), true);
			if (defaultForm != null && CollectionUtils.isNotEmpty(defaultForm.getFields())) {
				for (FormField f: defaultForm.getFields()) {	// TODO get fields from all sections
					if (!formFieldMap.containsKey(f.getName()) && (f.getField() == null || f.getField().isDefault())) {
						FormField formField = FieldUtil.cloneBean(f, FormField.class);
						systemFields.add(formField);
					}
				}
				addUnusedSystemFields(form, systemFields);
				for (FormField f: systemFields) {
					if (f.getField() == null) {
						FacilioField field = modBean.getField(f.getName(), moduleName);
						if (field != null) {
							f.setFieldId(field.getFieldId());
							f.setField(field);
						}
					}
				}
			}
		}
		
		List<FacilioField> customFields = modBean.getAllCustomFields(moduleName);
		
		List<FormField> customFormFields = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(customFields)) {
			customFields = customFields.stream().filter(field -> !formFieldMap.containsKey(field.getName())).collect(Collectors.toList());
			customFormFields = getFormFieldsFromFacilioFields(customFields, 0);
		}
		
		Map<String, List<FormField>> formMap = new HashMap<>();
		formMap.put("systemFields", systemFields);
		formMap.put("customFields", customFormFields);
		return formMap;
	}
	
	public static List<FormField> getAllFormFields(String moduleName, FormType formType) throws Exception {
		if (formType == null) {
			formType = FormType.WEB;
		}
		FacilioForm form = new FacilioForm();
		form.setFormType(formType);
		List<FormField> allFields = new ArrayList<>();
		FacilioForm defaultForm = FormFactory.getDefaultForm(moduleName, form, true);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (defaultForm != null) {
			List<FormField> defaultFields = new ArrayList<>(defaultForm.getFields());
			if (defaultFields == null) {
				defaultFields = getFormFieldsFromSections(form.getSections());
			}
			if (moduleName.equals(ContextNames.WORK_ORDER)) {
				defaultFields.add(new FormField("comment", FieldDisplayType.TICKETNOTES, "Comment", Required.OPTIONAL, "ticketnotes", defaultFields.size()+1, 1));
			}
			setFieldDetails(modBean, defaultFields, moduleName);
			allFields.addAll(defaultFields);
		}
		
		List<FacilioField> customFields = modBean.getAllCustomFields(moduleName);
		if (CollectionUtils.isNotEmpty(customFields)) {
			List<FormField> customFormFields = getFormFieldsFromFacilioFields(customFields, 0);
			allFields.addAll(customFormFields);
		}
		return allFields;
	}

	public static void setFieldDetails(ModuleBean modBean, List<FormField> fields, String moduleName) throws Exception {
		for (int i = 0; i < fields.size(); i++) {
			FormField f = fields.get(i);
			FormField mutatedField = FieldUtil.cloneBean(f, FormField.class);
			FacilioField field = modBean.getField(mutatedField.getName(), moduleName);
			if (field != null) {
				mutatedField.setField(field);
				mutatedField.setFieldId(field.getFieldId());
			}
			fields.set(i, mutatedField);
		}
	}
	
	// From factory first. If not (for custom module), then will check in db
	public static FacilioForm getDefaultForm(String moduleName, FormType formType, Boolean...onlyFields) throws Exception {
		FacilioForm defaultForm = FormFactory.getDefaultForm(moduleName, formType, onlyFields);
		if (defaultForm == null && formType != FormType.PORTAL) {
			defaultForm = FormFactory.getDefaultForm(moduleName, FormType.WEB, onlyFields);
		}
		if (defaultForm == null) {
			Map<String, Object> params = new HashMap<>();
			params.put(Builder.ORDER_BY, "id");
			params.put(Builder.LIMIT, 1);
			List<FacilioForm> forms = getDBFormList(moduleName, formType, null, params, true);
			if (CollectionUtils.isNotEmpty(forms)) {
				return forms.get(0);
			}
		}
		return defaultForm;

	}

	// From DB first. If not, will check in factory
	public static FacilioForm getDefaultFormFromDBOrFactory(FacilioModule module, FormType formType, Boolean...onlyFields) throws Exception {
		String formName = FormFactory.getDefaultFormName(module.getName(), formType);
		FacilioForm form = FormsAPI.getFormFromDB(formName, module);
		if (form == null) {
			form = new FacilioForm(FormFactory.getDefaultForm(module.getName(), formType, onlyFields));
		}
		return form;
	}
}
