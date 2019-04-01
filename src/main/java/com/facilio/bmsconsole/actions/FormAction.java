package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FacilioForm.FormType;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;

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
		Chain c = FacilioChainFactory.getFormMetaChain();
		c.execute(context);
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		this.setForms(Collections.singletonList(form));	// TODO remove
		this.setForm(form);
		
		return SUCCESS;
	}
	
	public String v2fetchFormMeta() throws Exception {
		fetchFormMeta();
		setResult(FacilioConstants.ContextNames.FORMS, forms); // TODO remove
		setResult(FacilioConstants.ContextNames.FORM, form);
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
		Chain c = FacilioChainFactory.getFormFieldsChain();
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
	
	public String allFormFields() throws Exception {
		setResult(FacilioConstants.ContextNames.FORM_FIELDS,FormsAPI.getallFormFields(moduleName));
		return SUCCESS;
	}	
	
    /* public String FormIdFields() throws Exception {
		setResult(FacilioConstants.ContextNames.FORM_FIELDS,FormsAPI.getFormIdFields(formId));
		return SUCCESS;
	} */	
	
	public String updateForm() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM, this.getForm());
		
		Chain c = TransactionChainFactory.getUpdateFormChain();
		c.execute(context);
		
		setResult("result", "success");
		
		return SUCCESS;
	}
	
	public String updateFormField() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM_FIELD, getFormField());
		
		Chain c = TransactionChainFactory.getUpdateFormFieldChain();
		c.execute(context);
		
		setResult("result", "success");
		
		return SUCCESS;
	}
	
	public String updateFormFields() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM, this.getForm());
		
		Chain c = TransactionChainFactory.getUpdateFormFieldsChain();
		c.execute(context);
		
		setFormId(getForm().getId());
		fetchFormMeta();
		
		setResult(ContextNames.FORM, form);
		
		return SUCCESS;
	}
	
	public String getServicePortalForms() throws Exception{
		
		setModuleName(ContextNames.WORK_ORDER);
		setFormType(FormType.PORTAL);
		
		return formList();
	}
	
	public String formList() throws Exception {
		Context context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FORM_TYPE, formType);
		
		ReadOnlyChainFactory.getFormList().execute(context);
		setResult("forms",context.get(ContextNames.FORMS));
		
		return SUCCESS;
	}
	
	public String addForm() throws Exception {
		Context context=new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.FORM, form);
		
		TransactionChainFactory.getAddFormCommand().execute(context);
		
		setFormNames(form.getName());
		fetchFormMeta();
		
		setResult(ContextNames.FORM, form);
		return SUCCESS;
	}
	
	private FormType formType;
	public FormType getFormTypeEnum() {
		return formType;
	}
	public void setFormType(int type) {
		this.formType = FormType.getFormType(type);
	}
	public void setFormType(FormType type) {
		this.formType = type;
	}

	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}
}
