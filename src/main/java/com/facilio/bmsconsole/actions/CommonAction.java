package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.FreeMarkerAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.modules.FieldUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class CommonAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	
	public String executeWorkflow() throws Exception {
		if (workflow.getWorkflowString() == null) {
			workflow.setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(workflow)); 
		}
		setResult("workflowResult", WorkflowUtil.getWorkflowExpressionResult(workflow, null));
		return SUCCESS;
	}
	public String mailExportModule () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, FileFormat.getFileFormat(type));
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.SUB_VIEW, viewName);
		context.put(FacilioConstants.ContextNames.FILTERS, getFilters());
		context.put(FacilioConstants.ContextNames.IS_S3_VALUE, true);
		context.put(FacilioConstants.ContextNames.SPECIAL_FIELDS, false);
		context.put(FacilioConstants.ContextNames.VIEW_LIMIT, null);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		FacilioChain mailModuleChain = TransactionChainFactory.sendModuleMailChain();
		mailModuleChain.execute(context);
		setResult(FacilioConstants.ContextNames.WORK_ORDER, moduleName);
		return SUCCESS;
	}
	public String exportModule() throws Exception {
		
		FacilioChain exportModule = TransactionChainFactory.getExportModuleChain();
		FacilioContext context= exportModule.getContext();
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, FileFormat.getFileFormat(type));
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.SUB_VIEW, viewName);
		context.put(FacilioConstants.ContextNames.FILTERS, getFilters());
		context.put(FacilioConstants.ContextNames.IS_S3_VALUE, false);
		context.put(FacilioConstants.ContextNames.SPECIAL_FIELDS, specialFields);
		context.put(FacilioConstants.ContextNames.VIEW_LIMIT, null);
		
		if (getClientCriteria() != null) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getClientCriteria());
			Criteria newCriteria = FieldUtil.getAsBeanFromJson(json, Criteria.class);
			context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, newCriteria);
		}
		
		exportModule.execute();
		
		fileUrl = (String) context.get(FacilioConstants.ContextNames.FILE_URL);
		return SUCCESS;
	}
	
	private String clientCriteria;
	
	public String getClientCriteria() {
		return clientCriteria;
	}

	public void setClientCriteria(String clientCriteria) {
		this.clientCriteria = clientCriteria;
	}
	
	private String ftl;
	public String getFtl() {
		return ftl;
	}
	public void setFtl(String ftl) {
		this.ftl = ftl;
	}
	
	private Map<String, Object> parameters;
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public String parseFtl() throws Exception {
		if (!workflow.isV2Script() && workflow.getWorkflowString() == null) {
			workflow.setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(workflow));
		}
		Map<String, Object> parameters = new HashMap<String,Object>();
		parameters.put("org", AccountUtil.getCurrentOrg());
		if (this.parameters != null) {
			parameters.putAll(this.parameters);
		}
		
		Map<String, Object> params;
		if (workflow.isV2Script()) {
			params = (Map<String, Object>) WorkflowUtil.getWorkflowExpressionResult(workflow, parameters);
		}
		else {
			params = WorkflowUtil.getExpressionResultMap(workflow, parameters);
		}
		
		setResult("parsedFtl", FreeMarkerAPI.processTemplate(ftl, params));
		setResult("workflowResultMap", params);
		return SUCCESS;
	}
	
	private int type=1;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	private String fileUrl;
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String url) {
		this.fileUrl = url;
	}
	
	private String filters;

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getFilters() {
		return this.filters;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private String viewName;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	private boolean specialFields;
	public boolean getSpecialFields() {
		return specialFields;
	}
	public void setSpecialFields(boolean specialFields) {
		this.specialFields = specialFields;
	}
	

	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
}
