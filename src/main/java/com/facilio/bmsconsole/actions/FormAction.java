package com.facilio.bmsconsole.actions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.formFactory.FacilioFormChainFactory;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.forms.FacilioForm.FormSourceType;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;

import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FormAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String formNames;
	public void setFormNames(String formNames) {
		this.formNames = formNames;
	}
	
	public String getFormNames() {
		return this.formNames;
	}
	
	private List<FacilioForm> forms;
	
	public void setForms(List<FacilioForm> forms) {
		this.forms = forms;
	}
	
	public List<FacilioForm> getForms() {
		return this.forms;
	}
	

	public String fetchFormMeta() throws Exception {
		Context context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.FORM_NAME, this.getFormNames());
		context.put(FacilioConstants.ContextNames.FORM_ID, this.getFormId());
		context.put(ContextNames.APP_LINKNAME, this.getAppLinkName());

		FacilioChain c = FacilioChainFactory.getFormMetaChain();
		c.execute(context);
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		this.setForms(Collections.singletonList(form));
		
		return SUCCESS;
	}
	
	public String v2fetchFormMeta() throws Exception {
		fetchFormMeta();
		setResult(FacilioConstants.ContextNames.FORMS, forms);
		return SUCCESS;
	}
	
	private String moduleName;
	
	public void setModuleName(String name) {
		this.moduleName = name;
	}
	
	public String getModuleName() {
		return this.moduleName;
	}
	
	private Boolean fetchExtendedModuleForms;

	public Boolean getFetchExtendedModuleForms() {
		return fetchExtendedModuleForms;
	}

	public void setFetchExtendedModuleForms(Boolean fetchExtendedModuleForms) {
		this.fetchExtendedModuleForms = fetchExtendedModuleForms;
	}
	
	private Boolean fetchDisabledForms;

	public Boolean getFetchDisabledForms() {
		return fetchDisabledForms;
	}

	public void setFetchDisabledForms(Boolean fetchDisabledForms) {
		this.fetchDisabledForms = fetchDisabledForms;
	}
	
	private Boolean fetchFormRuleFields;

	public Boolean getFetchFormRuleFields() {
		return fetchFormRuleFields;
	}

	public void setFetchFormRuleFields(Boolean fetchFormRuleFields) {
		this.fetchFormRuleFields = fetchFormRuleFields;
	}

	private long formId = -1;
	private long parentFormId = -1;
	private long appId = -1;
	private String appLinkName;
    public String getAppLinkName() {
		return appLinkName;
	}

	public void setAppLinkName(String appLinkName) {
		this.appLinkName = appLinkName;
	}

	private long formFieldId;

	public long getParentFormId() {
		return parentFormId;
	}

	public void setParentFormId(long parentFormId) {
		this.parentFormId = parentFormId;
	}
	
	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public long getFormFieldId() {
		return formFieldId;
	}

	public void setFormFieldId(long formFieldId) {
		this.formFieldId = formFieldId;
	}

	private List<FormField> fields;
	
	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}
	
	public List<FormField> getFields() {
		return this.fields;
	}
	
	public String fetchFormFields() throws Exception {
		Context context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.getModuleName());
		FacilioChain c = FacilioChainFactory.getFormFieldsChain();
		c.execute(context);
		
		setFields((List<FormField>) context.get(FacilioConstants.ContextNames.FORM_FIELDS));
		return SUCCESS;
	}
	
	private FacilioForm form;
	
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	
	public FacilioForm getForm() {
		return this.form;
	}
	
	private FormField formField;
	public FormField getFormField() {
		return formField;
	}
	public void setFormField(FormField formField) {
		this.formField = formField;
	}

	public Boolean getSkipTemplatePermission() {
		return skipTemplatePermission;
	}

	public void setSkipTemplatePermission(Boolean skipTemplatePermission) {
		this.skipTemplatePermission = skipTemplatePermission;
	}

	public FacilioField getFacilioField() {
		return facilioField;
	}
	public void setFacilioField(FacilioField facilioField) {
		this.facilioField = facilioField;
	}
	public void setFieldJson(JSONObject fieldJson) throws Exception {
		this.fieldJson = fieldJson;
		setFacilioField(FieldUtil.parseFieldJson(this.fieldJson));
	}

	public long getFormSectionId() {
		return formSectionId;
	}

	public void setFormSectionId(long formSectionId) {
		this.formSectionId = formSectionId;
	}

	public long getPreviousFormFieldId() {
		return previousFormFieldId;
	}
	public void setPreviousFormFieldId(long previousFormFieldId) {
		this.previousFormFieldId = previousFormFieldId;
	}

	public long getNextFormFieldId() {
		return nextFormFieldId;
	}

	public void setNextFormFieldId(long nextFormFieldId) {
		this.nextFormFieldId = nextFormFieldId;
	}

	public FacilioForm getSubForm() {
		return subForm;
	}

	public void setSubForm(FacilioForm subForm) {
		this.subForm = subForm;
	}

	private FacilioForm subForm;

	private long formSectionId;
	private long previousFormFieldId;
	private long nextFormFieldId;
	private JSONObject fieldJson;
	private long nextFormSectionId;
	private long nextFormId;

	public long getNextFormId() {
		return nextFormId;
	}

	public void setNextFormId(long nextFormId) {
		this.nextFormId = nextFormId;
	}

	public long getPreviousFormId() {
		return previousFormId;
	}

	public void setPreviousFormId(long previousFormId) {
		this.previousFormId = previousFormId;
	}

	private long previousFormId;
	public long getNextFormSectionId() {
		return nextFormSectionId;
	}

	public void setNextFormSectionId(long nextFormSectionId) {
		this.nextFormSectionId = nextFormSectionId;
	}

	public long getPreviousFormSectionId() {
		return previousFormSectionId;
	}

	public void setPreviousFormSectionId(long previousFormSectionId) {
		this.previousFormSectionId = previousFormSectionId;
	}

	private long previousFormSectionId;
	private FacilioField facilioField;

	public String getSubModuleName() {
		return subModuleName;
	}

	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}

	@Getter @Setter
	private String extendModuleName;

	@Getter @Setter
	private String lookupFieldName;
	private String subModuleName;

	private Boolean skipTemplatePermission = false;

	private Boolean forCreate = false;

	public Boolean getForCreate() {
		return forCreate;
	}

	public void setForCreate(Boolean forCreate) {
		this.forCreate = forCreate;
	}

	public String loadApprovalFormFields() throws Exception {
		List<FacilioField> allFields = new ArrayList();
		List<FacilioField> fields = new ArrayList();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");		
		allFields.addAll(modBean.getAllFields("workorder"));
		for(FacilioField fieldObject:allFields) {
			if(FieldFactory.Fields.APPROVAL_FORM_FIELDS.contains(fieldObject.getName()) || !fieldObject.isDefault()) {
				fields.add(fieldObject);
			}
		}
		setResult(FacilioConstants.ContextNames.FORM_FIELDS, FormFactory.getMetaFormFieldApprovals(allFields));
		setResult(FacilioConstants.ContextNames.FIELD_NAME_LIST, fields);
		return SUCCESS;
	}
	
	public String updateForm() throws Exception {

		FacilioChain chain = TransactionChainFactory.getUpdateFormChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.FORM, this.getForm());

		chain.execute();
		setResult("result", "success");

		Map<String, Object> props = (Map<String, Object>) context.get(ContextNames.FORM_OBJECT);
//		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.FORM,FacilioConstants.AuditLogRecordTypes.UPDATED);

		return SUCCESS;
	}

	public String updateFormField() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM_FIELD, getFormField());
		FacilioChain c = TransactionChainFactory.getUpdateFormFieldChain();
		c.execute(context);
		
		setResult("result", "success");
		
		return SUCCESS;
	}
	
	public String updateFieldAndFormField() throws Exception {


		FacilioChain chain = TransactionChainFactory.getUpdateFormFieldAndModuleFieldChain();

		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.FORM_FIELD, formField);
		context.put(FacilioConstants.ContextNames.CHECK_FIELD_DISPLAY_NAME_DUPLICATION, true);

		chain.execute();

		Map<String, Object> props = (Map<String, Object>) context.get(FacilioConstants.ContextNames.FORM_FIELD);
		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.FORM_FIELD,FacilioConstants.AuditLogRecordTypes.UPDATED);

		setResult("result", "success");
		
		return SUCCESS;
	}
	
	public String updateFormFields() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM, this.getForm());
		
		FacilioChain c = TransactionChainFactory.getUpdateFormFieldsChain();
		c.execute(context);
		
		setFormId(getForm().getId());
		
		formSourceType = FormSourceType.FROM_BUILDER;
		formDetails();
		
		return SUCCESS;
	}

	public String updateFormListFields() throws Exception {
		FacilioChain c = TransactionChainFactory.getUpdateFormListFieldsChain();
		c.getContext().put(ContextNames.FORMS_LIST, this.getForms());
		c.execute();
		setResult(ContextNames.FORMS_RESPONSE_LIST, c.getContext().get(ContextNames.FORMS_RESPONSE_LIST));

		return SUCCESS;
	}

	public String formListFromDB() throws Exception {
		FacilioChain chain = FacilioFormChainFactory.getFormListFromDB();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		if (fetchExtendedModuleForms != null && fetchExtendedModuleForms) {
			context.put(FacilioConstants.ContextNames.FETCH_EXTENDED_MODULE_FORMS, true);
		}
		else {
			context.put(FacilioConstants.ContextNames.FETCH_EXTENDED_MODULE_FORMS, false);
		}
		if (fetchDisabledForms != null && fetchDisabledForms) {
			context.put(FacilioConstants.ContextNames.FETCH_DISABLED_FORMS, true);
		}
		context.put(FacilioConstants.ContextNames.APP_ID, appId);

		chain.execute();
		setResult("forms",context.get(FacilioConstants.ContextNames.FORMS));
		setResult(FacilioConstants.ContextNames.STATE_FLOW_LIST, context.get(FacilioConstants.ContextNames.STATE_FLOW_LIST));

		return SUCCESS;
	}
	public String formList() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getFormList();
		FacilioContext context = chain.getContext();

		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FETCH_EXTENDED_MODULE_FORMS, fetchExtendedModuleForms);
		context.put(FacilioConstants.ContextNames.FETCH_DISABLED_FORMS, fetchDisabledForms);
		context.put(FacilioConstants.ContextNames.APP_ID, appId);
		context.put(FacilioConstants.ContextNames.SKIP_TEMPLATE_PERMISSION, getSkipTemplatePermission());

		chain.execute();

		setResult(ContextNames.FORMS,context.get(ContextNames.FORMS));
		setResult(FacilioConstants.ContextNames.STATE_FLOW_LIST, context.get(FacilioConstants.ContextNames.STATE_FLOW_LIST));

		return SUCCESS;
	}
	
	public String formUnusedFieldList() throws Exception {
		Map<String, List<FormField>> fields = FormsAPI.getFormUnusedFields(moduleName, formId);
		setResult("fields", fields);
		
		return SUCCESS;
	}
	public String findFormFieldUsages() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getFormFieldUsageChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.FORM_FIELD_ID, this.formFieldId);
		chain.execute();

		setResult(FacilioConstants.FormContextNames.FORM_RULE_USAGE, context.get(FacilioConstants.FormContextNames.FORM_RULE_USAGE));
		return  SUCCESS;
	}

	public String fetchFormFromDB() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getFormFromDBChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.FORM_NAME, formName);
		context.put(ContextNames.MODULE_NAME, moduleName);
		chain.execute();

		setResult("form",context.get(ContextNames.FORM));
		return  SUCCESS;

	}
	public String formFieldList() throws Exception {
		List<FormField> fields = FormsAPI.getAllFormFields(moduleName, appLinkName);
		setResult("fields", fields);
		
		return SUCCESS;
	}

	public String subFormModules() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getSubFormModulesChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.MODULE_NAME, moduleName);

		chain.execute();

		setResult(FacilioConstants.ContextNames.MODULE_LIST, context.get(FacilioConstants.ContextNames.MODULE_LIST));
		return SUCCESS;
	}

	public String addDBForm() throws Exception {
		FacilioChain chain = FacilioFormChainFactory.getAddFormCommand();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FORM, form);

		chain.execute();

		setFormName(form.getName());

		formSourceType = FormSourceType.FROM_BUILDER;
		formDetails();

		return SUCCESS;
	}
	public String addForm() throws Exception {

		FacilioChain chain = TransactionChainFactory.getAddFormCommand();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FORM, form);
		chain.execute();
		
		setFormName(form.getName());
		
		formSourceType = FormSourceType.FROM_BUILDER;
		formDetails();

		Map<String, Object> props = (Map<String, Object>) context.get(ContextNames.FORM_OBJECT);
		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.FORM,FacilioConstants.AuditLogRecordTypes.CREATED);

		return SUCCESS;
	}
	
	


	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	private String formName;
	public String getFormName() {
		return formName;
	}
	public void setFormName(String formName) {
		this.formName = formName;
	}
	public String formDetailsFromDB() throws Exception {

		FacilioChain c = FacilioFormChainFactory.getFormMetaChain();
		FacilioContext context = c.getContext();

		context.put(FacilioConstants.ContextNames.FORM_NAME, formName);
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS, fetchFormRuleFields);
		context.put(FacilioConstants.ContextNames.FORM_SOURCE, formSourceType);
		context.put(ContextNames.FOR_CREATE, forCreate);

		c.execute();

		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		setResult(ContextNames.FORM, form);

		return SUCCESS;
	}
	public String formDetails() throws Exception {
		Context context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.FORM_NAME, formName);
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS, fetchFormRuleFields);
		context.put(FacilioConstants.ContextNames.FORM_SOURCE, formSourceType);
		context.put(ContextNames.FOR_CREATE, forCreate);
		
		FacilioChain c = FacilioChainFactory.getFormMetaChain();
		c.execute(context);
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		setResult(ContextNames.FORM, form);
		
		return SUCCESS;
	}

	public String subModuleFormDetails() throws Exception {

		FacilioChain c = FacilioChainFactory.getSubModuleFormDetailsChain();
		FacilioContext context = c.getContext();

		context.put(FacilioConstants.ContextNames.FORM_NAME, formName);
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(ContextNames.LOOKUP_FIELD_NAME, lookupFieldName);
		context.put(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS, fetchFormRuleFields);
		context.put(FacilioConstants.ContextNames.FORM_SOURCE, formSourceType);
		context.put(ContextNames.FOR_CREATE, forCreate);

		c.execute(context);

		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		setResult(ContextNames.FORM, form);

		return SUCCESS;
	}

	public String extendModuleFormDetails() throws Exception {

		FacilioChain c = FacilioChainFactory.getExtendModuleFormDetailsChain();
		FacilioContext context = c.getContext();

		context.put(FacilioConstants.ContextNames.FORM_NAME, formName);
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(ContextNames.EXTENDED_MODULE_NAME, extendModuleName);
		context.put(FacilioConstants.ContextNames.FETCH_FORM_RULE_FIELDS, fetchFormRuleFields);
		context.put(FacilioConstants.ContextNames.FORM_SOURCE, formSourceType);
		context.put(ContextNames.FOR_CREATE, forCreate);

		c.execute(context);

		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		setResult(ContextNames.FORM, form);

		return SUCCESS;
	}
	public void setFromBuilder(Boolean fromBuilder) {
		if (fromBuilder != null) {
			formSourceType = FormSourceType.FROM_BUILDER;
		}
	}
	
	private FormSourceType formSourceType;
	public FormSourceType getFormSourceType() {
		return formSourceType;
	}

	public void setFormSourceType(int type) {
		if (type != -1) {
			formSourceType = FormSourceType.valueOf(type);
		}
	}

	private FormSection formSection;
	public FormSection getFormSection() {
		return formSection;
	}
	public void setFormSection(FormSection formSection) {
		this.formSection = formSection;
	}

	public String updateSection() throws Exception {

		FacilioChain chain = TransactionChainFactory.getUpdateFormSectionChain();

		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.FORM_SECTION, formSection);
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);

		chain.execute(context);

		Map<String, Object> props = (Map<String, Object>) context.get(ContextNames.FORM_SECTION);
		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.FORM_SECTION,FacilioConstants.AuditLogRecordTypes.UPDATED);

		setResult("result", "success");
		
		return SUCCESS;
	}

	public String deleteFormSection() throws Exception{

		FacilioChain facilioChain = TransactionChainFactory.getDeleteFormSectionChain();

		FacilioContext context = facilioChain.getContext();
		context.put(FacilioConstants.FormContextNames.FORM_SECTION_ID,getFormSectionId());

		facilioChain.execute();
		return SUCCESS;
	}

	
	public String deleteForm() throws Exception {

		FacilioChain chain = TransactionChainFactory.getDeleteFormChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		chain.execute();

		setResult(ContextNames.ROWS_UPDATED, context.get(ContextNames.ROWS_UPDATED));

		FacilioForm facilioForm = (FacilioForm) context.get(ContextNames.FORM);
		Map<String,Object> props = FieldUtil.getAsProperties(facilioForm);
		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.FORM,FacilioConstants.AuditLogRecordTypes.DELETED);

		return SUCCESS;
	}
	
public String getServicePortalForms() throws Exception{
		
		setModuleName(ContextNames.WORK_ORDER);
		
		return formList();
	}
	
	public String addSubForm() throws Exception {
		if (form == null) {
			throw new IllegalArgumentException("Form cannot be empty");
		}

		form.setHideInList(true);
		form.setAppLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);

		FacilioChain chain = TransactionChainFactory.getAddSubformChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FORM, form);
		context.put(ContextNames.PARENT_FORM_ID, parentFormId);
		chain.execute();

		setFormName(form.getName());
		formSourceType = FormSourceType.FROM_BUILDER;
		formDetails();

		return SUCCESS;
	}

	public String fetchFormFromId() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getFormFromDBChain();
		FacilioContext context = chain.getContext();
		context.put(ContextNames.FORM_ID, getFormId());
		chain.execute();

		setResult("form", context.get(ContextNames.FORM));

		return SUCCESS;
	}
	
	public String addOrUpdateFormSharingRoles() throws Exception{

		FacilioChain facilioChain = TransactionChainFactory.getAddOrUpdateFormSharingRolesChain();
		FacilioContext context = facilioChain.getContext();
		context.put(ContextNames.FORM,getForm());
		facilioChain.execute();

		setResult(ContextNames.FORM_SHARING, context.get(ContextNames.FORM_SHARING));

		return SUCCESS;
	}

	public String cloneFacilioForm() throws Exception {

		FacilioChain facilioChain = TransactionChainFactory.getCloneFormChain();
		FacilioContext context = facilioChain.getContext();
		context.put(ContextNames.FORM_ID,getFormId());
		context.put(ContextNames.APP_LINKNAME,getAppLinkName());
		facilioChain.execute();

		return SUCCESS;
	}
	public String getFormSharingRolesForForms() throws Exception{

		FacilioChain facilioChain = TransactionChainFactory.getFormSharingRolesChain();
		FacilioContext context = facilioChain.getContext();
		context.put(ContextNames.FORM_ID,getFormId());
		facilioChain.execute();
		setResult(ContextNames.FORM_SHARING, context.get(ContextNames.FORM_SHARING));

		return SUCCESS;
	}

	public String getSupportedFieldTypes() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.getSupportedFieldTypesChain();
		FacilioContext context = chain.getContext();
		chain.execute();

		setResult(FacilioConstants.ContextNames.FORM_FIELD_List, context.get(FacilioConstants.ContextNames.FORM_FIELD_List));
		return SUCCESS;
	}

	public String addCustomFormField() throws Exception {

		FacilioChain facilioChain = TransactionChainFactory.getAddCustomFormFieldChain();

		FacilioContext context = facilioChain.getContext();
		context.put(ContextNames.MODULE_NAME, getModuleName());
		context.put(ContextNames.FORM_FIELD, getFormField());
		context.put(FacilioConstants.FormContextNames.PREVIOUS_FORM_FIELD_ID, getPreviousFormFieldId());
		context.put(FacilioConstants.FormContextNames.NEXT_FORM_FIELD_ID, getNextFormFieldId());
		context.put(FacilioConstants.FormContextNames.FORM_SECTION_ID, getFormSectionId());
		context.put(ContextNames.FORM_ID, getFormId());

		facilioChain.execute();

		Map<String, Object> props = (Map<String, Object>) context.get(FacilioConstants.ContextNames.FORM_FIELD);
		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.FORM_FIELD,FacilioConstants.AuditLogRecordTypes.CREATED);

		return SUCCESS;
	}

	public String deleteFormField() throws Exception {

		FacilioChain facilioChain = TransactionChainFactory.getDeleteFormFieldChain();

		FacilioContext context = facilioChain.getContext();
		context.put(ContextNames.FORM_FIELD_ID, getFormFieldId());

		facilioChain.execute();

		Map<String, Object> fieldProps = (Map<String, Object>) context.get(FacilioConstants.ContextNames.FORM_FIELD);

		FormsAPI.addAuditLogs(fieldProps,FacilioConstants.AuditLogRecordTypes.FORM_FIELD,FacilioConstants.AuditLogRecordTypes.DELETED);
		setResult(FacilioConstants.FormContextNames.FORM_RULE_USAGE, context.get(FacilioConstants.FormContextNames.FORM_RULE_USAGE));
		return SUCCESS;
	}

	public String reorderFormFields() throws Exception {

		FacilioChain facilioChain = TransactionChainFactory.getReorderFieldsChain();

		FacilioContext context = facilioChain.getContext();
		context.put(FacilioConstants.FormContextNames.PREVIOUS_FORM_FIELD_ID, getPreviousFormFieldId());
		context.put(FacilioConstants.FormContextNames.NEXT_FORM_FIELD_ID, getNextFormFieldId());
		context.put(ContextNames.FORM_FIELD_ID, getFormFieldId());
		context.put(FacilioConstants.FormContextNames.FORM_SECTION_ID, getFormSectionId());

		facilioChain.execute();

		Map<String, Object> fieldProps = (Map<String, Object>) context.get(FacilioConstants.ContextNames.FORM_FIELD);
		FormsAPI.addAuditLogs(fieldProps,FacilioConstants.AuditLogRecordTypes.FORM_FIELD,FacilioConstants.AuditLogRecordTypes.UPDATED);

		return SUCCESS;
	}

	public String reorderFormSections() throws Exception {

		FacilioChain facilioChain = TransactionChainFactory.getReorderSectionsChain();

		FacilioContext context = facilioChain.getContext();
		context.put(FacilioConstants.FormContextNames.PREVIOUS_FORM_SECTION_ID, getPreviousFormSectionId());
		context.put(FacilioConstants.FormContextNames.NEXT_FORM_SECTION_ID, getNextFormSectionId());
		context.put(ContextNames.FORM_ID, getFormId());
		context.put(FacilioConstants.FormContextNames.FORM_SECTION_ID, getFormSectionId());

		facilioChain.execute();
		return SUCCESS;
	}

	public String addFormSection() throws Exception {

		FacilioChain facilioChain = TransactionChainFactory.getAddFormSectionChain();

		FacilioContext context = facilioChain.getContext();
		context.put(ContextNames.FORM_SECTION, getFormSection());
		context.put(ContextNames.PARENT_FORM_ID, getFormId());
		context.put(FacilioConstants.FormContextNames.PREVIOUS_FORM_SECTION_ID, getPreviousFormSectionId());
		context.put(FacilioConstants.FormContextNames.NEXT_FORM_SECTION_ID, getNextFormSectionId());

		facilioChain.execute();

		Map<String, Object> props = (Map<String, Object>) context.get(ContextNames.FORM_SECTION);
		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.FORM_SECTION,FacilioConstants.AuditLogRecordTypes.CREATED);

		return SUCCESS;
	}

	public String addSubFormWithSection() throws Exception {

		FacilioChain chain = TransactionChainFactory.getAddSubFormAndSectionChain();

		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getSubModuleName());
		context.put(FacilioConstants.ContextNames.FORM, getSubForm());
		context.put(ContextNames.PARENT_FORM_ID, getParentFormId());
		context.put(FacilioConstants.FormContextNames.PREVIOUS_FORM_SECTION_ID,getPreviousFormSectionId());
		context.put(FacilioConstants.FormContextNames.NEXT_FORM_SECTION_ID,getNextFormSectionId());

		chain.execute();
		FacilioForm facilioForm = (FacilioForm) context.get(ContextNames.FORM);
		Map<String, Object> props = FieldUtil.getAsProperties(facilioForm);
		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.SUB_FORM,FacilioConstants.AuditLogRecordTypes.CREATED);

		return SUCCESS;
	}

	public String reorderForms() throws Exception {

		FacilioChain chain = TransactionChainFactory.getReorderFormsChain();

		FacilioContext context = chain.getContext();
		context.put(ContextNames.FORM_ID,getFormId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
		context.put(FacilioConstants.FormContextNames.NEXT_FORM_ID, getNextFormId());
		context.put(FacilioConstants.FormContextNames.PREVIOUS_FORM_ID, getPreviousFormId());
		context.put(FacilioConstants.ContextNames.APP_ID, getAppId());

		chain.execute();

		Map<String, Object> props = (Map<String, Object>) context.get(ContextNames.FORM);
		FormsAPI.addAuditLogs(props,FacilioConstants.AuditLogRecordTypes.FORM,FacilioConstants.AuditLogRecordTypes.UPDATED);

		return SUCCESS;
	}
}
