package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

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
	
    private long formId;
    private long formFieldId;
    
	
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

	public String loadApprovalFormFields() throws Exception {
		List<FacilioField> allFields = new ArrayList();
		List<FacilioField> fields = new ArrayList();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");		
		allFields.addAll(modBean.getAllFields("workorder"));
		for(FacilioField fieldObject:allFields) {
			if(FieldFactory.Fields.approvalFormFields.contains(fieldObject.getName()) || !fieldObject.isDefault()) {
				fields.add(fieldObject);
			}
		}
		setResult(FacilioConstants.ContextNames.FORM_FIELDS, FormFactory.getMetaFormFieldApprovals(allFields));
		setResult(FacilioConstants.ContextNames.FIELD_NAME_LIST, fields);
		return SUCCESS;
	}
	
	public String updateForm() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM, this.getForm());
		
		FacilioChain c = TransactionChainFactory.getUpdateFormChain();
		c.execute(context);
		
		setResult("result", "success");
		
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
	
	public String updateFormFields() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM, this.getForm());
		
		FacilioChain c = TransactionChainFactory.getUpdateFormFieldsChain();
		c.execute(context);
		
		setFormId(getForm().getId());
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
	
	public String getServicePortalForms() throws Exception{
		
		setModuleName(ContextNames.WORK_ORDER);
		setFormTypeEnum(FormType.PORTAL);
		
		return formList();
	}
	
	public String formList() throws Exception {
		Context context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		if (formType != null && CollectionUtils.isEmpty(formTypes)) {
			formTypes = Collections.singletonList(formType.getIntVal());
		}
		context.put(FacilioConstants.ContextNames.FORM_TYPE, formTypes);
		
		ReadOnlyChainFactory.getFormList().execute(context);
		setResult("forms",context.get(ContextNames.FORMS));
		setResult(FacilioConstants.ContextNames.STATE_FLOW_LIST, context.get(FacilioConstants.ContextNames.STATE_FLOW_LIST));
		
		return SUCCESS;
	}
	
	public String formUnusedFieldList() throws Exception {
		Map<String, List<FormField>> fields = FormsAPI.getFormUnusedFields(moduleName, formId);
		setResult("fields", fields);
		
		return SUCCESS;
	}
	
	public String formFieldList() throws Exception {
		List<FormField> fields = FormsAPI.getAllFormFields(moduleName, formType);
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

	public String addSubForm() throws Exception {
		if (form == null) {
			throw new IllegalArgumentException("Form cannot be empty");
		}

		form.setHideInList(true);
		form.setFormType(FormType.WEB);
		addForm();
		return SUCCESS;
	}
	
	public String addForm() throws Exception {
		Context context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FORM, form);
		
		TransactionChainFactory.getAddFormCommand().execute(context);
		
		setFormName(form.getName());
//		formDetails();
		
		return SUCCESS;
	}
	
	private FormType formType;
	public FormType getFormTypeEnum() {
		return formType;
	}
	public void setFormType(int type) {
		this.formType = FormType.getFormType(type);
	}
	public void setFormTypeEnum(FormType type) {
		this.formType = type;
	}
	
	private List<Integer> formTypes;
	public List<Integer> getFormTypes() {
		return formTypes;
	}
	public void setFormTypes(List<Integer> formTypes) {
		this.formTypes = formTypes;
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
	
	public String formDetails() throws Exception {
		Context context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.FORM_NAME, formName);
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FORM_TYPE, formType);
		FacilioChain c = FacilioChainFactory.getFormMetaChain();
		c.execute(context);
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		setResult(ContextNames.FORM, form);
		
		return SUCCESS;
	}
	
	private FormSection formSection;
	public FormSection getFormSection() {
		return formSection;
	}
	public void setFormSection(FormSection formSection) {
		this.formSection = formSection;
	}

	public String updateSection() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM_SECTION, formSection);
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		
		FacilioChain c = TransactionChainFactory.getUpdateFormSectionChain();
		c.execute(context);
		
		setResult("result", "success");
		
		return SUCCESS;
	}
	
	public String deleteForm() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		
		TransactionChainFactory.getDeleteFormChain().execute(context);
		setResult(ContextNames.ROWS_UPDATED, context.get(ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
}
