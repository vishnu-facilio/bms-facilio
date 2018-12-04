package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormFactory;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioField.FieldDisplayType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class FormAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] formNames;
	public void setFormNames(String[] formNames) {
		this.formNames = formNames;
	}
	
	public String[] getFormNames() {
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
		
		context.put(FacilioConstants.ContextNames.FORM_NAMES, this.getFormNames());
		Chain c = FacilioChainFactory.getFormMetaChain();
		c.execute(context);
		
		List<FacilioForm> forms = (List<FacilioForm>) context.get(FacilioConstants.ContextNames.FORMS);
		this.setForms(forms);
		
		return SUCCESS;
	}
	
	private String moduleName;
	
	public void setModuleName(String name) {
		this.moduleName = name;
	}
	
	public String getModuleName() {
		return this.moduleName;
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
	
	private String res;
	
	public String getRes() {
		return this.res;
	}
	
	public void setResult(String result) {
		this.res = result;
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
		FacilioField comments = FieldFactory.getField("comment", "Comment" ,null , modBean.getModule("workorder"), FieldType.STRING);
		comments.setDisplayType(FieldDisplayType.TICKETNOTES);
		fields.add(comments);
		setResult(FacilioConstants.ContextNames.FORM_FIELDS, FormFactory.getMetaFormFieldApprovals(allFields));
		setResult(FacilioConstants.ContextNames.FIELD_NAME_LIST, fields);
		return SUCCESS;
	}
	
	public String editForm() throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FORM_NAMES, new String[]{this.getForm().getName()});
		context.put(FacilioConstants.ContextNames.EDITED_FORM, this.getForm());
		
		Chain c = FacilioChainFactory.editFormChain();
		c.execute(context);
		
		return SUCCESS;
	}
}
