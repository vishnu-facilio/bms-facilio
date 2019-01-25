package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.bmsconsole.util.FreeMarkerAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
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
		setResult("workflowResult", WorkflowUtil.getWorkflowExpressionResult(workflow.getWorkflowString(), null));
		return SUCCESS;
	}
	public String mailExportModule () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, FileFormat.getFileFormat(type));
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.SUB_VIEW, viewName);
		context.put(FacilioConstants.ContextNames.FILTERS, getFilters());
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		Chain mailModuleChain = FacilioChainFactory.sendModuleMailChain();
		mailModuleChain.execute(context);
		setResult(FacilioConstants.ContextNames.WORK_ORDER, moduleName);
		return SUCCESS;
	}
	public String exportModule() throws Exception {
		fileUrl = ExportUtil.exportModule(FileFormat.getFileFormat(type), moduleName, viewName, filters);
		return SUCCESS;
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
		if (workflow.getWorkflowString() == null) {
			workflow.setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(workflow));
		}
		Map<String, Object> parameters = new HashMap<String,Object>();
		parameters.put("org", AccountUtil.getCurrentOrg());
		if (this.parameters != null) {
			parameters.putAll(this.parameters);
		}
		Map<String, Object> params = WorkflowUtil.getExpressionResultMap(workflow.getWorkflowString(), parameters);
		
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
	
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
}
