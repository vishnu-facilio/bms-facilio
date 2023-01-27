package com.facilio.bmsconsole.util;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.db.criteria.operators.CommonOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.FormSiteRelationContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormField.Required;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
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
import com.facilio.fw.FacilioException;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

public class FormsAPI {

	private static final Logger LOGGER = LogManager.getLogger(FormsAPI.class.getName());

	public static Map<String, Collection<FacilioForm>> getMobileForms() throws Exception {
		Map<String, Collection<FacilioForm>> forms = new HashMap<> ();
		List<FacilioForm> formList = new ArrayList<>();
		formList.add(FormFactory.getMobileWorkOrderForm());
		forms.put(ContextNames.WORK_ORDER, formList);
		
		formList = new ArrayList<>();
		formList.add(FormFactory.getMobileAssetForm());
		forms.put(ContextNames.ASSET, formList);
		
		formList = new ArrayList<>();
		formList.add(FormFactory.getMobileAssetForm());
		forms.put(ContextNames.APPROVAL, formList);
		
		for(Map.Entry<String, Collection<FacilioForm>> entry :forms.entrySet()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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
		
		return forms;
	}

	private static Criteria getFormTypeCriteria(String appLinkName) throws Exception {
		ApplicationContext app = appLinkName != null ?  ApplicationApi.getApplicationForLinkName(appLinkName) : AccountUtil.getCurrentApp();
		if (app == null) {
			app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
		}
		
		Criteria formTypeCriteria = new Criteria();
		Condition formTypeCondition = new Condition();
		formTypeCondition.setColumnName("APP_ID");
		formTypeCondition.setFieldName("appId");
		formTypeCondition.setOperator(NumberOperators.EQUALS);
		formTypeCondition.setValue(String.valueOf(app.getId()));
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
		return getDBFormList(null, criteria, null, true, false,true, -1l);
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
		FacilioModule module = modBean.getModule(modid);
		form.setModule(module);
		fieldSelectBuilder
				.andCondition(CriteriaAPI.getCondition("FORMID", "formId", String.valueOf(form.getId()), NumberOperators.EQUALS))
				.orderBy("SEQUENCE_NUMBER, SPAN");
		
		List<Map<String, Object>> fieldprops = fieldSelectBuilder.get();
		List<FormField> fields = new ArrayList<>();
		for (Map<String, Object> p: fieldprops) {
			FormField f = FieldUtil.getAsBeanFromMap(p, FormField.class);
			FacilioField field = null;
			if (f.getFieldId() != -1) {
				field =  modBean.getField(f.getFieldId());
				if (f.getName() == null) {
					f.setName(field.getName());
				}
			}
			/***
			 * Temp handling to set name for form fields if fieldId is empty 
			 * Should introduce name column in Form Fields 
			 */
			else if (f.getDisplayTypeEnum() == FieldDisplayType.TICKETNOTES){
				// Shaan will change this temp handling suggested by him
				if (f.getName() != null && StringUtils.equals("quotenotes", f.getName())) {
					f.setName(f.getName());
				} else {
					f.setName("comment");
				}
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
//				f.setField(FieldFactory.getSiteField(module));
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
			handleFormField(f, module.getName(), field);
			fields.add(f);
			if (f.getSectionId() != -1) {
				sectionMap.get(f.getSectionId()).addField(f);
			}
		}
		form.setFields(fields);
	}
	
	private static void handleFormField(FormField formField, String moduleName, FacilioField field ) throws Exception {
		if (formField.getDisplayTypeEnum() == FieldDisplayType.ATTACHMENT) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioModule> subModules = modBean.getSubModules(moduleName, ModuleType.ATTACHMENTS);
			formField.setLookupModuleName(subModules.get(0).getName());
		}
		else if (field != null) {
			formField.setField(field);
			if (field instanceof BaseLookupField) {
				formField.setLookupModuleName(((BaseLookupField)field).getLookupModule().getName());
			}
		}
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

						fillSubFormLookupField(section, subForm, form.getModuleId());
					}
				}
			}
			form.setSections(sections);
		}
	}

	private static void fillSubFormLookupField(FormSection section, FacilioForm subForm, long parentModuleId) throws Exception {
		try {
			long lookupFieldId = section.getLookupFieldId();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (lookupFieldId == -1 && subForm != null) {
				FacilioModule module = subForm.getModule();
				if (module == null) {
					module = modBean.getModule(subForm.getModuleId());
				}

				// taking first lookup field, assuming that each module will have only one lookup field from a module
				if (module != null) {
					List<FacilioField> allFields = modBean.getAllFields(module.getName());
					Optional<FacilioField> first = allFields.stream().filter(f -> f instanceof LookupField &&
							((LookupField) f).getLookupModule() != null &&
							((LookupField) f).getLookupModule().getModuleId() == parentModuleId).findFirst();
					if (first.isPresent()) {
						section.setLookupFieldId(first.get().getFieldId());

						FacilioModule formSectionModule = ModuleFactory.getFormSectionModule();
						GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
								.table(formSectionModule.getTableName())
								.fields(Collections.singletonList(FieldFactory.getField("lookupFieldId", "LOOKUP_FIELDID", formSectionModule, FieldType.NUMBER)))
								.andCondition(CriteriaAPI.getIdCondition(section.getId(), formSectionModule));
						Map map = new HashMap();
						map.put("lookupFieldId", section.getLookupFieldId());
						builder.update(map);
					}
				}
			}

			if (section.getLookupFieldId() > 0) {
				FacilioField field = modBean.getField(section.getLookupFieldId(), subForm.getModuleId());
				if (field != null) {
					section.setLookupFieldName(field.getName());
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Error in getting lookupFieldId in subform", ex);
		}
	}

	public static long createForm(FacilioForm form, FacilioModule module) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getId();
		form.setOrgId(orgId);
		form.setModule(module);
		if (form.getShowInWeb() == null) {
			form.setShowInWeb(true);
		}
		if (form.getAppId() < 0) {
			ApplicationContext app = (form.getAppLinkName() != null) ? ApplicationApi.getApplicationForLinkName(form.getAppLinkName()) : ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			form.setAppId(app.getId());
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
		
		addFormFields(formId, fields, false);
	}
	
	public static void addFormFields(long formId, List<FormField> fields, boolean seqInitialized) throws Exception {
		if (fields != null) {
			long orgId = AccountUtil.getCurrentOrg().getId();
			int i = 1;
			List<Map<String, Object>> fieldProps = new ArrayList<>();
			for (FormField f: fields) {
				f.setId(-1);
				f.setFormId(formId);
				f.setOrgId(orgId);
				if (!seqInitialized) {
					f.setSequenceNumber(i++);
				}
				if (f.getSpan() == -1) {
					f.setSpan(1);
				}
				if(f.getFieldId() != -1) {
					f.setName(null);
				}
				else if(f.getName() == null) {	// Notes field
					f.setName(f.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+","")+f.getSequenceNumber());
				}
				Map<String, Object> prop = FieldUtil.getAsProperties(f);
				if (prop.get("required") == null) {
					prop.put("required", false);
				}
				fieldProps.add(prop);
			}
			
			FacilioModule formFieldModule = ModuleFactory.getFormFieldsModule();
			GenericInsertRecordBuilder fieldInsertBuilder = new GenericInsertRecordBuilder()
					.table(formFieldModule.getTableName())
					.fields(FieldFactory.getFormFieldsFields())
					.addRecords(fieldProps);
			
			fieldInsertBuilder.save();
			
			int count=0;
			for(Map<String, Object> fieldProp : fieldProps) {
				fields.get(count++).setId((long)fieldProp.get("id"));
			}
		}
	}
	
	private static void addFormSections (long formId, List<FormSection> sections, boolean... seqInitialized) throws Exception {
		if (sections != null) {
			long orgId = AccountUtil.getCurrentOrg().getId();
			int i = 1;
			List<Map<String, Object>> props = new ArrayList<>();
			Map<Long, FormSection> sectionMap = new HashMap<>();
			for (FormSection f: sections) {
				f.setId(-1);
				f.setFormId(formId);
				f.setOrgId(orgId);
				if (seqInitialized.length == 0 || !seqInitialized[0]) {
					f.setSequenceNumber(i++);
				}
				sectionMap.put(f.getSequenceNumber(), f);
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
				if(section.getSectionTypeEnum() != FormSection.SectionType.SUB_FORM){
					section.getFields().forEach(field -> field.setSectionId(id));
					fields.addAll(section.getFields());
				}
			}
		}
	}
	
	public static List<FormField> getFormFieldsFromSections(List<FormSection> sections) {
		return sections.stream().map(section -> section.getFields() != null ? section.getFields() : new ArrayList<FormField>()).flatMap(List::stream).collect(Collectors.toList());
	}
	
	private static int deleteFormFields(List<Long> fieldIds) throws Exception {
		
		FacilioModule module = ModuleFactory.getFormFieldsModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(fieldIds, module))
														;
		return deleteBuilder.delete();
	}
	
	private static int deleteFormSections(List<Long> sectionIds) throws Exception {
		deleteSubforms(sectionIds);
		FacilioModule module = ModuleFactory.getFormSectionModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(sectionIds, module))
														;
		return deleteBuilder.delete();
	}

	private static void deleteSubforms(List<Long> sectionIds) throws Exception{
		FacilioModule module = ModuleFactory.getFormSectionModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getFormSectionFields())
				.andCondition(CriteriaAPI.getIdCondition(sectionIds, module))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getField("subFormId", "SUB_FORM_ID", module, FieldType.NUMBER), CommonOperators.IS_NOT_EMPTY));
		List<Map<String, Object>> props = selectBuilder.get();

		if(!CollectionUtils.isEmpty(props)) {
			for(Map<String,Object> prop : props) {
				long subformId = (long) prop.get("subFormId");
				FacilioChain deleteSubFormChain = TransactionChainFactory.getDeleteFormChain();
				Context context = deleteSubFormChain.getContext();
				context.put(FacilioConstants.ContextNames.FORM_ID, subformId);
				deleteSubFormChain.execute();
			}
		}
	}
	
	// To update fields based on formfieldid...if fieldNamesToUpdate is not given, then the all the values for the record should be set
	public static void updateFormFields(List<FormField> formFields, List<String> fieldNamesToUpdate) throws Exception {
		FacilioModule module = ModuleFactory.getFormFieldsModule();
		List<FacilioField> updateFields = FieldFactory.getFormFieldsFields();
		updateFormRecords(formFields, fieldNamesToUpdate, module, updateFields, FormField.class);
	}
	public static void updateFormSections(List<FormSection> formSections, List<String> fieldNamesToUpdate) throws Exception {
		FacilioModule module = ModuleFactory.getFormSectionModule();
		List<FacilioField> updateFields = FieldFactory.getFormSectionFields();
		updateFormRecords(formSections, fieldNamesToUpdate, module, updateFields, FormSection.class);
	}
	
	
	// To update fields based on id...if fieldNamesToUpdate is not given, then the all the values for the record should be set
	public static <E> void updateFormRecords(List<E> records, List<String> fieldNamesToUpdate, FacilioModule module, List<FacilioField> updateFields, Class<E> classObj) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(updateFields);

		if (fieldNamesToUpdate != null) {
			updateFields = new ArrayList<>();
			for(String fieldName: fieldNamesToUpdate) {
				updateFields.add(fieldMap.get(fieldName));
			}
		}

		List<Map<String, Object>> props = FieldUtil.getAsMapList(records, classObj);
		List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList = new ArrayList<>();
		for(Map<String, Object> field: props) {
			GenericUpdateRecordBuilder.BatchUpdateByIdContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();

			for(FacilioField updateField: updateFields) {
				String name = updateField.getName();
				updateVal.addUpdateValue(name,  field.get(name));
			}

			updateVal.setWhereId((long) field.get("id"));
			batchUpdateList.add(updateVal);
		}

		new GenericUpdateRecordBuilder()
		.table(module.getTableName())
		.fields(updateFields)
		.batchUpdateById(batchUpdateList)
		;
	}
	
	public static void initFormFields(FacilioForm form) throws Exception {
		List<FormSection> sectionsToUpdate = new ArrayList<>();
		List<FormSection> sectionsToAdd = new ArrayList<>();
		
		List<FormField> fieldsToUpdate = new ArrayList<>();
		List<FormField> fieldsToAdd = new ArrayList<>();
		
		
		FacilioForm dbForm = getFormFromDB(form.getId());
		List<FormField> dbFields = getFormFieldsFromSections(dbForm.getSections());
		
		List<Long> dbFieldIds = dbFields.stream().map(field -> field.getId()).collect(Collectors.toList());
		List<Long> dbSectionIds =  dbForm.getSections().stream().map(field -> field.getId()).collect(Collectors.toList());
		
		int sectionSeq = 1;
		int fieldSeq = 1;
		for(FormSection section: form.getSections()) {
			section.setSequenceNumber(sectionSeq++);
			if (section.getId() == -1) {
				sectionsToAdd.add(section);
			}
			else {
				dbSectionIds.remove(section.getId());
				sectionsToUpdate.add(section);
			}
			
			
			for(FormField field: section.getFields()) {
				field.setSequenceNumber(fieldSeq++);
				field.setSectionId(section.getId());
				if (field.getId() == -1) {
					fieldsToAdd.add(field);
				}
				else {
					dbFieldIds.remove(field.getId());
					field.setFormId(form.getId());
					if (field.getFieldId() != -1) {
						field.setName(null);
					}
					else if (field.getDisplayTypeEnum() == FieldDisplayType.NOTES) {
						field.setName(field.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+","")+fieldSeq);
					}
					fieldsToUpdate.add(field);
				}
			}
		}
		
		
		
		if (!dbSectionIds.isEmpty()) {
			deleteFormSections(dbSectionIds);
		}
		if (!sectionsToUpdate.isEmpty()) {
			updateFormSections(sectionsToUpdate, null);
		}
		if (!sectionsToAdd.isEmpty()) {
			addFormSections(form.getId(), sectionsToAdd, true);
		}
		
		if (!dbFieldIds.isEmpty()) {
			
//			checkFieldUsageInRule(dbFieldIds);
			
			deleteFormFields(dbFieldIds);
		}
		if (!fieldsToUpdate.isEmpty()) {
			updateFormFields(fieldsToUpdate, null);
		}
		if (!fieldsToAdd.isEmpty()) {
			addFormFields(form.getId(), fieldsToAdd, true);
		}
	}
	
	private static void checkFieldUsageInRule(List<Long> dbFieldIds) throws Exception {
		// TODO Auto-generated method stub
		if(FormRuleAPI.isFieldExistAsRuleTriggerField(dbFieldIds)) {
			throw new FacilioException("Field is used in one or more rule.");
		}
		if(FormRuleAPI.isFieldExistInRuleAction(dbFieldIds)) {
			throw new FacilioException("Field is used in one or more rule.");
		}
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
			for(FacilioForm facilioForm : forms){
				List<FormField> formFields = getFormFieldsFromSections(facilioForm.getSections());
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				Map<String, FacilioField> facilioFieldMap = FieldFactory.getAsMap(modBean.getAllFields(facilioForm.getModule().getName()));
				FacilioModule module = modBean.getModule(facilioForm.getModule().getName());
				for(FormField formField:formFields){
					if(formField.getField()==null){
						FacilioField field = facilioFieldMap.get(formField.getName());
						if(field!=null ) {
							formField.setField(field);
							formField.setFieldId(field.getFieldId());
						} else if (Objects.equals(formField.getName(), "siteId")) {
							field = FieldFactory.getSiteIdField(module);
							formField.setField(field);
							formField.setFieldId(field.getFieldId());
						}
					}
				}

			}
			return forms.get(0);
		}
		return null;
	}

	public static FacilioForm getFormFromDB(long id, boolean fetchFields) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getFormModule()));
		List<FacilioForm> dbFormList = getDBFormList(null, criteria, null, fetchFields, false, true, -1l);
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
				if(form.getIsSystemForm()!=null && form.getIsSystemForm()){
					throw new IllegalArgumentException("Default forms cannot be deleted");
				}
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
		if (facilioField instanceof BaseLookupField) {
			formField.setLookupModuleName(((BaseLookupField)facilioField).getLookupModule().getName());
		}
		return formField;
	}
	
	public static FormField getFormFieldFromId(Long id) throws Exception {
		GenericSelectRecordBuilder fieldSelectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getFormFieldsModule().getTableName())
				.select(FieldFactory.getFormFieldsFields())
				.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getFormFieldsModule()));
		
		List<Map<String, Object>> props = fieldSelectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			FormField field = FieldUtil.getAsBeanFromMap(props.get(0), FormField.class);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (field.getFieldId() > 0) {
				FacilioField dbField = modBean.getField(field.getFieldId());
				field.setField(dbField);
				field.setName(dbField.getName());
				field.setFieldId(dbField.getFieldId());
			}
			return field;
		}
		return null;
	}
	
	public static Map<String, FacilioForm> getFormsAsMap (String moduleName,Boolean fetchExtendedModuleForms,Boolean fetchDisabledForms, long appId) throws Exception {
		List<FacilioForm> forms = getDBFormList(moduleName, fetchExtendedModuleForms, fetchDisabledForms, appId);
		Map<String, FacilioForm> formMap = new LinkedHashMap<>();
		if (forms != null && !forms.isEmpty()) {
			for(FacilioForm form: forms) {
				formMap.put(form.getName(), form);
			}
			return formMap;
		}
		return null;
	}
	
	public static List<FacilioForm> getDBFormList(String moduleName, Boolean fetchExtendedModuleForms, Boolean fetchDisabledForms, long appId) throws Exception{
		return getDBFormList(moduleName, null, null, false, fetchExtendedModuleForms, fetchDisabledForms, appId);
	}
	
	public static List<FacilioForm> getDBFormList(String moduleName, Criteria criteria, Map<String, Object> selectParams, boolean fetchFields) throws Exception{
		return getDBFormList(moduleName, criteria, selectParams, fetchFields, false, true, -1l);
	}

	public static List<FacilioForm> getDBFormList(String moduleName, Criteria criteria, Map<String, Object> selectParams, boolean fetchFields, boolean fetchExtendedModuleForms) throws Exception{
		return getDBFormList(moduleName, criteria, selectParams, fetchFields, fetchExtendedModuleForms, true, -1l);
	}
	
	public static List<FacilioForm> getDBFormList(String moduleName, Criteria criteria, Map<String, Object> selectParams, boolean fetchFields, Boolean fetchExtendedModuleForms,Boolean fetchDisabledForms, long appId) throws Exception{
		
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

		if (criteria != null) {
			formListBuilder.andCriteria(criteria);
		}
		
		ApplicationContext app = null;
		if (appId > 0) {
			app = ApplicationApi.getApplicationForId(appId);
			List<Long> appIds = Collections.singletonList(app.getId());
			formListBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), StringUtils.join(appIds, ","), NumberOperators.EQUALS));
		}
	
		if (MapUtils.isNotEmpty(selectParams)) {
			if (selectParams.containsKey(Builder.ORDER_BY)) {
				String orderBy = (String) selectParams.get(Builder.ORDER_BY);
				String orderByType = (String) selectParams.get(Builder.ORDER_BY_TYPE);
				formListBuilder.orderBy(fieldMap.get(orderBy).getCompleteColumnName() + (orderByType != null ? " " + orderByType : " asc"));
			}
			if(selectParams.containsKey(ContextNames.HIDE_IN_LIST)){
				formListBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("hideInList"), String.valueOf(selectParams.get(ContextNames.HIDE_IN_LIST)), BooleanOperators.IS));
			}
			if (selectParams.containsKey(Builder.LIMIT)) {
				formListBuilder.limit((int) selectParams.get(Builder.LIMIT));
			}
		}
		else {
			formListBuilder.orderBy("ID asc");
		}
		
		List<FacilioForm> forms = FieldUtil.getAsBeanListFromMapList(formListBuilder.get(), FacilioForm.class);
		if ( CollectionUtils.isNotEmpty(forms)) {
			forms = getAsFormSiteRelationListMap(forms, spaces);
			if (app == null) {
				// Assuming only forms for a particular app will be fetched in a list
				long formAppId = forms.get(0).getAppId();
				if (formAppId > 0) {
					app = ApplicationApi.getApplicationForId(formAppId);
				}
			}
			String appName = app != null ? app.getLinkName() : ApplicationLinkNames.FACILIO_MAIN_APP;
			for (FacilioForm form: forms) {
				form.setAppLinkName(appName);
				
				if (fetchFields) {
					setFormSections(form);
					setFormFields(form);
				}
			}
		}
		return forms;
			
	}

	public static long getFormsCount(String moduleName, Boolean fetchExtendedModuleForms, Boolean fetchDisabledForms, long appId) throws Exception{
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
				.select(FieldFactory.getCountField())
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

		ApplicationContext app = null;
		if (appId > 0) {
			app = ApplicationApi.getApplicationForId(appId);
			List<Long> appIds = Collections.singletonList(app.getId());
			formListBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), StringUtils.join(appIds, ","), NumberOperators.EQUALS));
		}

		Map<String, Object> modulesMap = formListBuilder.fetchFirst();
		long count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

		return count;
	}
	
	private static List<FacilioForm> getAsFormSiteRelationListMap(List<FacilioForm> forms, List<Long> spaces) throws Exception {
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
		// TODO Auto-generated method stub
		return forms;
	}

	private static void addUnusedSystemFields(FacilioForm form, List<FormField> defaultFields, ModuleBean modBean, String moduleName) throws Exception {
		String appLinkName = form.getAppLinkName();
		if (isPortalApp(appLinkName)) {
			addUnusedPortalSystemFields(form, defaultFields);
		}
		else {
			addUnusedWebSystemFields(form, defaultFields);
		}
		
		for (FormField f: defaultFields) {
			if (f.getField() == null) {
				FacilioField field = modBean.getField(f.getName(), moduleName);
				if (field != null) {
					f.setFieldId(field.getFieldId());
					f.setField(field);
				}
			}
		}
	}
	
	private static void addUnusedWebSystemFields(FacilioForm form, List<FormField> defaultFields) throws Exception {
		List<FormField> fields = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (form.getModule().isCustom()) {
			addUnusedCustomModuleSystemFields(form, fields);
		}
		 if (form.getModule().getName() != null) {
			switch (form.getModule().getName()) {

			case ContextNames.WORK_ORDER:
				FormField serviceRequest = new FormField("serviceRequest", FieldDisplayType.LOOKUP_SIMPLE, "Service Request", Required.OPTIONAL, 14, 1);
				serviceRequest.setHideField(true);
				fields.add(serviceRequest);
				fields.add(new FormField("subject", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
				fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 2, 1));
				fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 3, 1));
				fields.add(new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.OPTIONAL, "ticketcategory", 4, 2));
				fields.add(new FormField("type",FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", Required.OPTIONAL, "tickettype", 4, 3));
				fields.add(new FormField("priority", FieldDisplayType.LOOKUP_SIMPLE, "Priority", Required.OPTIONAL, "ticketpriority", 5, 1));
				fields.add(new FormField("resource", FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", Required.OPTIONAL, 6, 1));
				fields.add(new FormField("assignment", FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", Required.OPTIONAL, 7, 1));
				fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 8, 1));
				fields.add(new FormField("parentWO", FieldDisplayType.LOOKUP_SIMPLE, "Parent WorkOrder", Required.OPTIONAL, 9, 1));
				fields.add(new FormField("sendForApproval", FieldDisplayType.DECISION_BOX, "Send For Approval", Required.OPTIONAL, 10, 1));
				fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, 11, 1));
				fields.add(new FormField("tasks", FieldDisplayType.TASKS, "TASKS", Required.OPTIONAL, 13, 1));
				fields.addAll(FormFactory.getRequesterFormFields(false, false));
				fields.add(new FormField("dueDate", FieldDisplayType.DATETIME, "Due Date", Required.OPTIONAL, 1, 1));
				fields.add(new FormField("isSignatureRequired", FieldDisplayType.DECISION_BOX, "Is Signature Required ", Required.OPTIONAL, 1, 1));
				fields.add(new FormField("responseDueDate", FieldDisplayType.DATETIME, "Response Due Date", Required.OPTIONAL, 1, 1));
				// scheduled duration specific
				fields.add(new FormField("scheduledStart", FieldDisplayType.DATETIME, "Scheduled Start", Required.OPTIONAL, 1, 1));
				fields.add(new FormField("estimatedEnd", FieldDisplayType.DATETIME, "Estimated End", Required.OPTIONAL, 1, 1));
				if (AccountUtil.isFeatureEnabled(FeatureLicense.TENANTS)) {
					fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL,"tenant", 1, 1));
				}
				if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CLIENT)) {
					fields.add(new FormField("client", FieldDisplayType.LOOKUP_SIMPLE, "Client", Required.OPTIONAL, "client", 1, 1));
				}
				break;
			case ContextNames.WorkPermit.WORKPERMIT:
				fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Subject", Required.REQUIRED, 1, 1));
				fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
				fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 2));
				fields.add(new FormField("space", FieldDisplayType.SPACECHOOSER, "Location", Required.OPTIONAL, "basespace", 3, 3));
				fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, "vendors", 4, 2));
				fields.add(new FormField("people", FieldDisplayType.LOOKUP_SIMPLE, "Contact", Required.OPTIONAL, "people", 4, 3));
				fields.add(new FormField("ticket", FieldDisplayType.LOOKUP_SIMPLE, "Work Order", Required.OPTIONAL, "ticket", 6, 1));
				fields.add(new FormField("workPermitType", FieldDisplayType.LOOKUP_SIMPLE, "Permit Type", Required.OPTIONAL, "workPermitType", 7, 1));
				fields.add(new FormField("checklist", FieldDisplayType.PERMIT_CHECKLIST, "Checklist", Required.OPTIONAL, 8, 1));
				fields.add(new FormField("expectedStartTime", FieldDisplayType.DATETIME, "Valid From", Required.OPTIONAL, 1, 1));
				fields.add(new FormField("expectedEndTime", FieldDisplayType.DATETIME, "Valid Till", Required.OPTIONAL, 1, 1));
				break;
			case ContextNames.ASSET:
				fields.add(new FormField("name", FieldDisplayType.TEXTBOX, "Name", Required.REQUIRED, "name", 1, 1));
				fields.add(new FormField("description", FieldDisplayType.TEXTAREA, "Description", Required.OPTIONAL, 2, 1));
				fields.add(new FormField("siteId", FieldDisplayType.LOOKUP_SIMPLE, "Site", Required.REQUIRED, "site", 3, 2));
				FormField categoryField = new FormField("category", FieldDisplayType.LOOKUP_SIMPLE, "Category", Required.REQUIRED, "assetcategory", 4, 2);
				categoryField.setIsDisabled(true);
				fields.add(categoryField);
				fields.add(new FormField("department", FieldDisplayType.LOOKUP_SIMPLE, "Department", Required.OPTIONAL,"assetdepartment", 4, 3));
				fields.add(new FormField("space", FieldDisplayType.SPACECHOOSER, "Asset Location", Required.OPTIONAL, 5, 2));
				fields.add(new FormField("type", FieldDisplayType.LOOKUP_SIMPLE, "Type", Required.OPTIONAL,"assettype", 5, 3));
				fields.add(new FormField("manufacturer", FieldDisplayType.TEXTBOX, "Manufacturer", Required.OPTIONAL, 6, 2));
				fields.add(new FormField("supplier", FieldDisplayType.TEXTBOX, "Supplier", Required.OPTIONAL, 6, 3));
				fields.add(new FormField("model", FieldDisplayType.TEXTBOX, "Model", Required.OPTIONAL, 7, 2));
				fields.add(new FormField("serialNumber", FieldDisplayType.TEXTBOX, "Serial Number", Required.OPTIONAL, 7, 3));
				fields.add(new FormField("tagNumber", FieldDisplayType.TEXTBOX, "Tag", Required.OPTIONAL, 8, 2));
				fields.add(new FormField("partNumber", FieldDisplayType.TEXTBOX, "Part No.", Required.OPTIONAL, 8, 3));
				fields.add(new FormField("purchasedDate", FieldDisplayType.DATETIME, "Purchased Date", Required.OPTIONAL, 9, 2));
				fields.add(new FormField("retireDate", FieldDisplayType.DATETIME, "Retire Date", Required.OPTIONAL, 9, 3));
				fields.add(new FormField("unitPrice", FieldDisplayType.NUMBER, "Unit Price", Required.OPTIONAL, 10, 2));
				fields.add(new FormField("warrantyExpiryDate", FieldDisplayType.DATETIME, "Warranty Expiry Date", Required.OPTIONAL, 10, 3));
				fields.add(new FormField("qrVal", FieldDisplayType.TEXTBOX, "QR Value", Required.OPTIONAL, 11, 2));
				fields.add(new FormField("rotatingItem", FieldDisplayType.LOOKUP_SIMPLE, "Rotating Item",Required.OPTIONAL, "item", 12,2));
				fields.add(new FormField("rotatingTool", FieldDisplayType.LOOKUP_SIMPLE, "Rotating Tool",Required.OPTIONAL, "tool", 12,3));
				fields.add(new FormField("geoLocationEnabled", FieldDisplayType.DECISION_BOX, "Is Movable",Required.OPTIONAL, 13,2));
				fields.add(new FormField("moveApprovalNeeded", FieldDisplayType.DECISION_BOX, "Is Move Approval Needed",Required.OPTIONAL, 13,2));
				fields.add(new FormField("boundaryRadius", FieldDisplayType.NUMBER, "Boundary Radius", Required.OPTIONAL, 14, 2));
				fields.add(new FormField("failureClass", FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
				fields.add(new FormField("salvageAmount", FieldDisplayType.NUMBER, "Salvage Amount", Required.OPTIONAL, 1, 1));
				break;
			case ContextNames.INVITE_VISITOR:
				fields.add(new FormField("isVip", FieldDisplayType.DECISION_BOX, "VIP", Required.OPTIONAL, 1, 1));
				fields.add(new FormField( "visitorPhone", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Phone", FormField.Required.REQUIRED, 1, 1));
				fields.add(new FormField( "visitorName", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Name", FormField.Required.REQUIRED, 1, 1));
				fields.add(new FormField( "visitorEmail", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Email", FormField.Required.REQUIRED, 1, 1));
				fields.add(new FormField( "visitedSpace", FacilioField.FieldDisplayType.SPACECHOOSER, "Visited Space", FormField.Required.OPTIONAL, 1, 1));
				break;
			case ContextNames.VISITOR_LOG:
				fields.add(new FormField( "avatar", FacilioField.FieldDisplayType.IMAGE, "Avatar", FormField.Required.OPTIONAL, 1, 1));
				fields.add(new FormField( "visitorPhone", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Phone", FormField.Required.REQUIRED, 1, 1));
				fields.add(new FormField( "visitorName", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Name", FormField.Required.REQUIRED, 1, 1));
				fields.add(new FormField( "visitorEmail", FacilioField.FieldDisplayType.TEXTBOX, "Visitor Email", FormField.Required.REQUIRED, 1, 1));
				fields.add(new FormField( "visitedSpace", FacilioField.FieldDisplayType.SPACECHOOSER, "Visited Space", FormField.Required.OPTIONAL, 1, 1));
				fields.add(new FormField( "host", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Host", FormField.Required.OPTIONAL,1, 1));
				fields.add(new FormField( "nda", FacilioField.FieldDisplayType.IMAGE, "NDA", FormField.Required.OPTIONAL,1, 1));
				fields.add(new FormField( "isVip", FieldDisplayType.DECISION_BOX, "VIP", Required.OPTIONAL, 1, 1));
				fields.add(new FormField( "isDenied", FieldDisplayType.DECISION_BOX, "Is Denied", Required.OPTIONAL, 1, 1));
				fields.add(new FormField( "visitCustomResponse", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Custom Response", FormField.Required.OPTIONAL,"visitcustomresponse", 1, 1,false));
				break;
			case ContextNames.INSURANCE:
				fields.add(new FormField("insuranceType", FieldDisplayType.SELECTBOX, "Insurance Type", FormField.Required.OPTIONAL, 1, 1));
				fields.add(new FormField("companyName", FieldDisplayType.TEXTBOX, "Company Name", Required.REQUIRED, 1, 1));
				fields.add(new FormField("validFrom", FieldDisplayType.DATE, "Valid From", Required.OPTIONAL, 2, 1));
				fields.add(new FormField("validTill", FieldDisplayType.DATE, "Valid Till", Required.OPTIONAL, 3, 1));
				fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.REQUIRED,"vendors", 3, 2));
				fields.add(new FormField("insurance", FieldDisplayType.FILE, "Insurance", Required.OPTIONAL, 1, 1));
				break;
			case ContextNames.SPACE:
				fields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building Associated", FormField.Required.REQUIRED,"building", 5, 1,true));
				fields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor Associated", FormField.Required.REQUIRED,"floor", 5, 1,true));
				fields.add(FormFactory.getSpaceAssociatedField());
				fields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
				fields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
				fields.add(new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1));
				fields.add(new FormField("area", FacilioField.FieldDisplayType.DECIMAL, "Area", FormField.Required.OPTIONAL, 4, 1));
				fields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site Associated", FormField.Required.REQUIRED,"site", 5, 1,true));
				fields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Maximum Occupancy Count", FormField.Required.OPTIONAL, 6, 1));
				fields.add(new FormField("location", FieldDisplayType.GEO_LOCATION, "Location", Required.OPTIONAL, 7, 1));
				fields.add(new FormField("failureClass", FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));
				fields.add(new FormField("amenities",FieldDisplayType.MULTI_LOOKUP_SIMPLE,"Amenities",FormField.Required.OPTIONAL,"amenity",9,2));
				break;
				
			case ContextNames.SITE:
			case ContextNames.BUILDING:
			case ContextNames.FLOOR:
				// Add modules here, if not all fields needs to be shown and only form factory fields are needed
				break;
				
				// Add fields here if it has to be shown in unused list and not there in the default form
				
				default:
					List<FacilioField> allFields = modBean.getAllFields(form.getModule().getName());
					allFields = allFields.stream().filter(f -> f.isDefault() && !FieldUtil.isSystemUpdatedField(f.getName())).collect(Collectors.toList());
					List<FacilioModule> subModules = modBean.getSubModules(form.getModule().getName(), ModuleType.ATTACHMENTS);
					if(CollectionUtils.isNotEmpty(subModules)){
					fields.add(new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "attachment", 8, 1));
					}
					fields.addAll(getFormFieldsFromFacilioFields(allFields, 1));
			}
		}

		addToDefaultFields(new ArrayList<>(form.getFields()), defaultFields, fields);

		
	}
	
	private static void addUnusedPortalSystemFields(FacilioForm form, List<FormField> defaultFields) throws Exception{
		List<FormField> fields = new ArrayList<>();
		if (form.getModule().isCustom()) {
			addUnusedCustomModuleSystemFields(form, fields);
		}
		else if (form.getModule().getName() != null) {
			switch (form.getModule().getName()) {
				case ContextNames.WORK_ORDER:
					fields.addAll(FormFactory.getWoClassifierFields());
					fields.add(FormFactory.getWoResourceField());
					fields.add(new FormField("vendor", FieldDisplayType.LOOKUP_SIMPLE, "Vendor", Required.OPTIONAL, 10, 1));
					if (AccountUtil.isFeatureEnabled(FeatureLicense.TENANTS)) {
						fields.add(new FormField("tenant", FieldDisplayType.LOOKUP_SIMPLE, "Tenant", Required.OPTIONAL, "tenant", 1, 1));
					}
					break;
				// DO NOT add any more modules here
			}
		}
		
		addToDefaultFields(form.getFields(), defaultFields, fields);
	}

	private static void addUnusedCustomModuleSystemFields(FacilioForm form, List<FormField> fields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField photoField = modBean.getField("photo", form.getModule().getName());
		if(photoField != null) {
			fields.add(getFormFieldFromFacilioField(photoField, 1));
		}
		FacilioField nameField = modBean.getField("name", form.getModule().getName());
		fields.add(getFormFieldFromFacilioField(nameField, 1));
		FacilioField failureClassField = modBean.getField("failureclass", form.getModule().getName());
		fields.add(getFormFieldFromFacilioField(failureClassField, 1));
		fields.add(FormFactory.getSiteField());
		FormField attField = new FormField("attachedFiles", FieldDisplayType.ATTACHMENT, "Attachments", Required.OPTIONAL, "cmdattachments", 1, 1);
		fields.add(attField);
	}
	
	private static void addToDefaultFields(List<FormField> existingFields, List<FormField> defaultFields, List<FormField> newFields) {
		if (!newFields.isEmpty()) {
			existingFields.addAll(defaultFields);
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

//		Map<String, FormField> formFieldMap = fields.stream().collect(Collectors.toMap(FormField::getName, Function.identity()));
		Map<String, FormField> formFieldMap = new HashMap<>();
		for(FormField formField : fields){
			if (!formFieldMap.containsKey(formField.getName())){
				formFieldMap.put(formField.getName(),formField);
			}
			else{
				LOGGER.info("formField "+ formField.getName()+ " duplicate for form id : "+ formId);
				if(formField.getFieldId() != -1){
					formFieldMap.put(formField.getName(),formField);
				}
			}
		}
		List<FormField> systemFields = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (form.getModule().isCustom()) {
			addUnusedSystemFields(form, systemFields, modBean, moduleName);
		}
		else {
			// Temp handling
			if (!moduleName.equals(ContextNames.WORK_ORDER)) {
				form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			else if (form.getAppLinkName() == null && form.getAppId() > 0) {
				form.setAppLinkName(ApplicationApi.getApplicationForId(form.getAppId()).getLinkName());
			}
			
			FacilioForm defaultForm = getDefaultForm(moduleName, form.getAppLinkName(), true);
			if (defaultForm != null && CollectionUtils.isNotEmpty(defaultForm.getFields())) {
				
				setUnusedSystemFields(systemFields, defaultForm, formFieldMap);
				addUnusedSystemFields(form, systemFields, modBean, moduleName);
				
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
	
	private static List<FormField> setUnusedSystemFields(List<FormField> systemFields, FacilioForm defaultForm, Map<String, FormField> formFieldMap) throws Exception {
		List<FormField> fields = new ArrayList<>(defaultForm.getFields()); // TODO get fields from all sections
		FacilioModule extendedModule = defaultForm.getModule().getExtendModule();
		if (extendedModule != null) {
			FacilioForm extendedForm = getDefaultForm(extendedModule.getName(), defaultForm.getAppLinkName(), true);
			if (extendedForm != null) {
				fields.addAll(extendedForm.getFields());
			}
		}
		for (FormField f: fields) {
			if (!formFieldMap.containsKey(f.getName()) && (f.getField() == null || f.getField().isDefault())) {
				if(f.getName() == null && (f.getDisplayTypeEnum() == FieldDisplayType.WOASSETSPACECHOOSER || f.getDisplayTypeEnum() == FieldDisplayType.TEAMSTAFFASSIGNMENT)){
					f.setName(f.getDisplayTypeEnum() == FieldDisplayType.WOASSETSPACECHOOSER ? ContextNames.RESOURCE:ContextNames.ASSIGNMENT);
				}
				FormField formField = FieldUtil.cloneBean(f, FormField.class);
				formField.setId(-1);
				formField.setFormId(-1);
				formField.setSectionId(-1);
				systemFields.add(formField);
			}
		}
		
		return systemFields;
	}
	
	public static List<FormField> getAllFormFields(String moduleName, String appLinkName) throws Exception {
		if (appLinkName == null) {
			ApplicationContext app = (AccountUtil.getCurrentApp() != null ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP));
			appLinkName = app.getLinkName();
		}
		FacilioForm form = new FacilioForm();
		form.setAppLinkName(appLinkName);
		List<FormField> allFields = new ArrayList<>();
		FacilioForm defaultForm = getDefaultForm(moduleName, appLinkName, true);
		List<Long> defaultCustomFields = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (defaultForm != null) {
			List<FormField> defaultFields = new ArrayList<>(defaultForm.getFields());
			if (defaultFields == null) {
				defaultFields = getFormFieldsFromSections(form.getSections());
			}
			if (moduleName.equals(ContextNames.WORK_ORDER)) {
				defaultFields.add(new FormField("comment", FieldDisplayType.TICKETNOTES, "Comment", Required.OPTIONAL, "ticketnotes", defaultFields.size()+1, 1));
			}
			if (moduleName.equals(ContextNames.SERVICE_REQUEST)) {
				defaultFields.add(new FormField("comment", FieldDisplayType.TICKETNOTES, "Comment", Required.OPTIONAL, "servicerequestsnotes", defaultFields.size()+1, 1));
			}
			if (defaultForm.getId() == -1) {
				setFieldDetails(modBean, defaultFields, moduleName);
			}
			else {
				defaultCustomFields = defaultFields.stream().filter(field -> field.getField() != null && !field.getField().isDefault())
						.map(field -> field.getFieldId()).collect(Collectors.toList());
			}
			allFields.addAll(defaultFields);
			addUnusedWebSystemFields(defaultForm, allFields);
		}
		
		List<FacilioField> customFields = modBean.getAllCustomFields(moduleName);
		if (CollectionUtils.isNotEmpty(customFields)) {
			for(FacilioField field: customFields) {
				if (!defaultCustomFields.contains(field.getFieldId())) {
					FormField formField = getFormFieldFromFacilioField(field, 1);
					allFields.add(formField);
				}
			}
		}
		return allFields;
	}

	public static void setFieldDetails(ModuleBean modBean, List<FormField> fields, String moduleName) throws Exception {
		for (int i = 0; i < fields.size(); i++) {
			FormField f = fields.get(i);
			if(f.getName() == null && (f.getDisplayTypeEnum() == FieldDisplayType.WOASSETSPACECHOOSER || f.getDisplayTypeEnum() == FieldDisplayType.TEAMSTAFFASSIGNMENT)){
				f.setName(f.getDisplayTypeEnum() == FieldDisplayType.WOASSETSPACECHOOSER ? ContextNames.RESOURCE:ContextNames.ASSIGNMENT);
			}
			FormField mutatedField = FieldUtil.cloneBean(f, FormField.class);
			FacilioField field = modBean.getField(mutatedField.getName(), moduleName);
			if (field != null) {
				mutatedField.setFieldId(field.getFieldId());
			}
			handleFormField(mutatedField, moduleName, field);
			fields.set(i, mutatedField);
		}
	}
	
	// From factory first. If not (for custom module), then will check in db
	public static FacilioForm getDefaultForm(String moduleName, String appLinkName, Boolean...onlyFields) throws Exception {

		Map<String, Object> params = new HashMap<>();
		params.put(Builder.ORDER_BY, "id");
		params.put(Builder.LIMIT, 1);
		params.put(ContextNames.HIDE_IN_LIST,false);
		ApplicationContext application = ApplicationApi.getApplicationForLinkName(appLinkName);
		List<FacilioForm> forms = getDBFormList(moduleName, null, params, true, false, false,application.getId());
		if (CollectionUtils.isNotEmpty(forms)) {
			return forms.get(0);
		}else if(!appLinkName.equals(ApplicationLinkNames.FACILIO_MAIN_APP)){
			ApplicationContext applicationContext = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			List<FacilioForm> mainAppForms = getDBFormList(moduleName, null, params, true, false, false,applicationContext.getId());
			if(CollectionUtils.isNotEmpty(mainAppForms)){
				return mainAppForms.get(0);
			}
		}

		FacilioForm defaultForm = FormFactory.getDefaultForm(moduleName,appLinkName,onlyFields);
		if (defaultForm == null ) {
			String linkName = isPortalApp(appLinkName) ? ApplicationLinkNames.OCCUPANT_PORTAL_APP : ApplicationLinkNames.FACILIO_MAIN_APP;
			defaultForm = FormFactory.getDefaultForm(moduleName, linkName, onlyFields);
			if (defaultForm != null) {
				defaultForm.setAppLinkName(appLinkName);
			}
		}
		if(defaultForm!=null){
			LOGGER.info("formFactoryTracking formName : "+ defaultForm.getName() +"for module : "+ moduleName +" DB first modified");
		}
		return defaultForm;

	}
	
	public static boolean isPortalApp(String appLinkName) {
		return appLinkName != null && (appLinkName.equals(ApplicationLinkNames.OCCUPANT_PORTAL_APP) 
				|| appLinkName.equals(ApplicationLinkNames.TENANT_PORTAL_APP ) || appLinkName.equals(ApplicationLinkNames.VENDOR_PORTAL_APP));
	}

	// From DB first. If not, will check in factory
	public static FacilioForm getDefaultFormFromDBOrFactory(FacilioModule module, String appLinkName, Boolean...onlyFields) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (module.getName().equals("ticket")) {
			module = modBean.getModule("workorder");
		}
		String formName = FormFactory.getDefaultFormName(module.getName(), appLinkName);
		FacilioForm form = FormsAPI.getFormFromDB(formName, module);
		
		if (form == null) {
			FacilioForm defaultForm = FormFactory.getDefaultForm(module.getName(), appLinkName, onlyFields);
			if (defaultForm != null) {
				form = new FacilioForm(defaultForm);
				form.setSections(defaultForm.getSections());
				LOGGER.info("formFactoryTracking formName : "+ defaultForm.getName() +"for module : "+ module.getName() + " while get form with module name and link name");
			}
		}
		return form;
	}
	
	
	/*** Default  Val Placeholder handling ****/
	
	public static final String CURRENT_DATE_REGEX = "\\$\\{(CURRENT_DATE)([\\+-]?)([0-9]*)\\}";
	public static final String CURRENT_TIME_REGEX = "\\$\\{(CURRENT_TIME)([\\+-]?)([0-9]*)\\}";
	
	private static boolean isDatePlaceHolder(String placeholder) {
		Pattern pattern = Pattern.compile(CURRENT_DATE_REGEX);
		Matcher matcher = pattern.matcher(placeholder);
		return matcher.find();
	}
	
	private static boolean isTimePlaceHolder(String placeholder) {
		Pattern pattern = Pattern.compile(CURRENT_TIME_REGEX);
		Matcher matcher = pattern.matcher(placeholder);
		return matcher.find();
	}
	
	private static long getResolvedDate(String placeholder) {
		long time =  DateTimeUtil.getDayStartTime();
		return getCalculatedTime(CURRENT_DATE_REGEX, placeholder, time, true);
	}
	
	private static long getResolvedTime(String placeholder) {
		long time =  DateTimeUtil.getCurrenTime();
		return getCalculatedTime(CURRENT_TIME_REGEX, placeholder, time, false);
	}
	
	private static long getCalculatedTime(String regex, String placeholder, long time, boolean isDay) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(placeholder);
		while(matcher.find()) {
			if (matcher.group(2) != null) {
				String operator = matcher.group(2);
				Integer offset = FacilioUtil.parseInt(matcher.group(3));
				if (isDay) {
					time = operator.equals("-") ? DateTimeUtil.minusDays(time, offset) : DateTimeUtil.addDays(time, offset);
				}
				else {
					time = operator.equals("-") ? DateTimeUtil.minusSeconds(time, offset) : DateTimeUtil.addSeconds(time, offset);
				}
			}
		}
		return time;
	}
	
	public static Object resolveDefaultValPlaceholder(String placeholder) throws Exception {
		
		long ouid = AccountUtil.getCurrentUser().getOuid();
		switch(placeholder) {
			case FacilioConstants.Criteria.LOGGED_IN_USER:
				return ouid;
				
			case FacilioConstants.Criteria.LOGGED_IN_USER_TENANT:
				TenantContext tenant = PeopleAPI.getTenantForUser(ouid);
				if (tenant != null) {
					return tenant.getId();
				}
				break;
			case FacilioConstants.Criteria.LOGGED_IN_USER_VENDOR:
				VendorContext vendor = PeopleAPI.getVendorForUser(ouid);
				if (vendor != null) {
					return vendor.getId();
				}
				break;
			case FacilioConstants.Criteria.CURRENT_TIME:
				return DateTimeUtil.getCurrenTime();
				
			case FacilioConstants.Criteria.CURRENT_DATE:
				return DateTimeUtil.getDayStartTime();
			case FacilioConstants.Criteria.LOGGED_IN_PEOPLE:
				long peopleId = PeopleAPI.getPeopleIdForUser(ouid);
				if (peopleId > 0) {
					return peopleId;
				}
				break;
			default:
				return replacePlaceholders(placeholder);
				
		}
		return null;
	}
	
	private static Object replacePlaceholders(String placeholder) {
		if (isDatePlaceHolder(placeholder)) {
			return getResolvedDate(placeholder);
		}
		else if (isTimePlaceHolder(placeholder)) {
			return getResolvedTime(placeholder);
		}
		return null;
	}
	
	/******* Default Val End ********/
	
	public static FacilioForm fetchForm(long formId, String moduleName) throws Exception {
		return fetchForm(formId, moduleName, false);
	}
	
	public static FacilioForm fetchForm(long formId, String moduleName, boolean fetchRuleFields) throws Exception {
		FacilioChain chain = FacilioChainFactory.getFormMetaChain();
		Context formContext = chain.getContext();

		formContext.put(FacilioConstants.ContextNames.FORM_ID, formId);
		formContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		formContext.put(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS, fetchRuleFields);
		
		chain.execute();

		return (FacilioForm) formContext.get(FacilioConstants.ContextNames.FORM);
	}

	public static void updateFormName(long formId, String formName) throws Exception {
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFormModule().getTableName())
				.fields(Collections.singletonList(FieldFactory.getNameField(ModuleFactory.getFormModule())))
				.andCondition(CriteriaAPI.getIdCondition(formId, ModuleFactory.getFormModule()));
		Map<String, Object> map = new HashMap<>();
		map.put("name", formName);
		builder.update(map);
	}

    public static String generateSubFormLinkName(FacilioForm form) {
        String generatedName = null;
        if (form.getName() == null) {
            generatedName = form.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
        } else {
            generatedName = form.getName().toLowerCase().replaceAll("[^a-zA-Z0-9_]+", "");
        }
        return ((!generatedName.startsWith(FacilioConstants.LinkNamePrefix.SUB_FORM_PREFIX)) ? (FacilioConstants.LinkNamePrefix.SUB_FORM_PREFIX + generatedName) : generatedName);
    }

    public static String getDefaultFormName(String moduleName, String appLinkName) {
        String name = "default_" + moduleName + "_" + (FormsAPI.isPortalApp(appLinkName) ? "portal" : "web");
        if (moduleName.equals("space")) { // Special temp handling for space forms.
            name += "_site";
        }
        return name;
    }

    public static FacilioForm getDefaultFormFromDB(String moduleName,String appLinkName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		long moduleId = module.getModuleId();
		long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);

		if (moduleId == -1) {
			return null;
		}
		GenericSelectRecordBuilder selectPrimaryFormBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormFields())
				.table(ModuleFactory.getFormModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PRIMARY_FORM", "primaryForm", String.valueOf(true), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));

		FacilioForm defaultForm = FieldUtil.getAsBeanFromMap(selectPrimaryFormBuilder.fetchFirst(), FacilioForm.class);
		if (defaultForm == null) {
			return null;
		}
		return getDefaultFormFromDB(defaultForm);
	}

	public static FacilioForm getDefaultFormFromDB (FacilioForm defaultForm) throws Exception {

		if(defaultForm.getSections()!=null){
			return defaultForm;
		}
		long defaultFormId = defaultForm.getId();

		GenericSelectRecordBuilder selectPrimaryFormSectionBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormSectionFields())
				.table(ModuleFactory.getFormSectionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("FORMID", "formId", String.valueOf(defaultFormId), NumberOperators.EQUALS));

		List<Map<String, Object>> defaultFormSections = selectPrimaryFormSectionBuilder.get();
		List<FormSection> sections = new ArrayList<>();
		for (Map<String, Object> section : defaultFormSections) {

			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFieldFields());

			List<FacilioField> fields = new ArrayList<FacilioField>(FieldFactory.getFormFieldsFields());
			fields.removeIf(field -> field.getName().equals("name"));
			fields.add(fieldMap.get("name"));

			GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getFieldsModule().getTableName())
					.select(fields).leftJoin(ModuleFactory.getFormFieldsModule().getTableName())
					.on(ModuleFactory.getFormFieldsModule().getTableName() + ".FIELDID=" + ModuleFactory.getFieldsModule().getTableName() + ".FIELDID")
					.andCondition(CriteriaAPI.getCondition("SECTIONID", "sectionId", String.valueOf(section.get("id")), NumberOperators.EQUALS))
					.orderBy("sequenceNumber");

			List<Map<String, Object>> defaultFormFields = genericSelectRecordBuilder.get();
			List<FormField> formFields = FieldUtil.getAsBeanListFromMapList(defaultFormFields, FormField.class);

			GenericSelectRecordBuilder formFieldsFieldIdNullBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getFormFieldsModule().getTableName())
					.select(FieldFactory.getFormFieldsFields())
					.andCondition(CriteriaAPI.getCondition("SECTIONID", "sectionId", String.valueOf(section.get("id")), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition("FIELDID","fieldId","NULL",CommonOperators.IS_EMPTY))
					.orderBy("sequenceNumber");

			List<Map<String, Object>> defaultFieldIdNullFormFields = formFieldsFieldIdNullBuilder.get();
			List<FormField> FieldIdNullFormFields = FieldUtil.getAsBeanListFromMapList(defaultFieldIdNullFormFields, FormField.class);
			formFields.addAll(FieldIdNullFormFields);

			int i = 1;
			FormSection defaultFormSection = new FormSection((String) section.get("name"), i++, formFields, section.get("showLabel") != null && (boolean) section.get("showLabel"));
			sections.add(defaultFormSection);
		}
		defaultForm.setSections(sections);
		return defaultForm;
	}
	public static FacilioForm getSpecificFormOfTheModuleFromDB (String formName,String appLinkName) throws Exception {

		long appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);

		GenericSelectRecordBuilder selectSpecificFormBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormFields())
				.table(ModuleFactory.getFormModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("NAME", "name", formName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));

		FacilioForm defaultForm = FieldUtil.getAsBeanFromMap(selectSpecificFormBuilder.fetchFirst(), FacilioForm.class);
		return getDefaultFormFromDB(defaultForm);
	}

	public static Map<String, FacilioForm> getFormsFromDB(String moduleName, List<String> appLinkNames) throws Exception{

		Map<String, FacilioForm> forms = new HashMap<>();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		long moduleId = module.getModuleId();

		GenericSelectRecordBuilder selectDbFormBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormFields())
				.table(ModuleFactory.getFormModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));

		List<Map<String, Object>> dbFormList = selectDbFormBuilder.get();
		if(dbFormList.isEmpty()){
			return new HashMap<>();
		}
		List<FacilioForm> formsList = FieldUtil.getAsBeanListFromMapList(dbFormList, FacilioForm.class);
		if(appLinkNames.isEmpty()){
			return formsList.stream().collect(Collectors.toMap(FacilioForm::getName,Function.identity()));
		}
		for(FacilioForm form:formsList){
			if(form.getSections()==null) {
				getDefaultFormFromDB(form);
			}
			if(form.getAppId()== -1){
				continue;
			}
			ApplicationContext app = ApplicationApi.getApplicationForId(form.getAppId());
			String linkName = app != null ? app.getLinkName() : null;
			form.setAppLinkName(linkName);
			if(appLinkNames.contains(form.getAppLinkName())){
				forms.put(form.getName(),form);
			}
		}
		return forms;
	}
	public static FacilioForm getFormsFromDB(String moduleName,String formName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<String> appLinkName = new ArrayList<>();
		Map<String, FacilioForm> formsMap = getFormsFromDB(moduleName, appLinkName);
		if(formsMap.isEmpty()){
			return null;
		}
		return formsMap.get(formName);
	}

	public static void insertFormField(String moduleName,String formName,String fieldName,int sequenceNumber,long sectionId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long moduleId = modBean.getModule(moduleName).getModuleId();

		Map<String,FacilioField> formFieldMap = FieldFactory.getAsMap(FieldFactory.getFormFields());

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(formFieldMap.get("id")))
				.table(ModuleFactory.getFormModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULEID","moduleId", String.valueOf(moduleId),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("NAME","name", formName,StringOperators.IS));

		Map<String, Object> formMap = selectRecordBuilder.fetchFirst();

		long formId = (long) formMap.get("id");
		FacilioField facilioField = modBean.getField(fieldName, moduleName);

		FormField formField = getFormFieldFromFacilioField(facilioField,sequenceNumber);
		formField.setField(facilioField);

		formField.setSectionId(sectionId);
		formField.setFormId(formId);

		Map<String, Object> props = FieldUtil.getAsProperties(formField);

		Map<String,FacilioField> formFieldFieldMap = FieldFactory.getAsMap(FieldFactory.getFormFieldsFields());

		GenericSelectRecordBuilder formFieldRecordBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormFieldsFields())
				.table(ModuleFactory.getFormFieldsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("FORMID","formId", String.valueOf(formId),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("SEQUENCE_NUMBER","sequenceNumber", String.valueOf(sequenceNumber),NumberOperators.GREATER_THAN_EQUAL));

		List<Map<String, Object>> formFieldFieldList = formFieldRecordBuilder.get();

		List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();

		for(Map<String,Object> formFieldFieldsMap: formFieldFieldList) {
			GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
			updateVal.addUpdateValue("sequenceNumber",(int)formFieldFieldsMap.get("sequenceNumber") + 1);
			updateVal.addWhereValue("id", formFieldFieldsMap.get("id"));
			batchUpdateList.add(updateVal);
		}

		FacilioField fieldIdField = formFieldFieldMap.get("id");
		List<FacilioField> whereFields = new ArrayList<>();
		whereFields.add(fieldIdField);

		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFormFieldsModule().getTableName())
				.fields(Collections.singletonList(formFieldFieldMap.get("sequenceNumber")));

		updateRecordBuilder.batchUpdate(whereFields, batchUpdateList);

		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormFieldsModule().getTableName())
				.fields(FieldFactory.getFormFieldsFields());

		long formFieldId = insertRecordBuilder.insert(props);


	}

}
