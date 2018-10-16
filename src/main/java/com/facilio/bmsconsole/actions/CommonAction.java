package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.util.ExportUtil;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.opensymphony.xwork2.ActionSupport;

public class CommonAction extends ActionSupport {
	
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
	
	private Object result;
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public String executeWorkflow() throws Exception {
		if (workflow.getWorkflowString() == null) {
			workflow.setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(workflow));
		}
		result = WorkflowUtil.getWorkflowExpressionResult(workflow.getWorkflowString(), null);
		return SUCCESS;
	}
	
	public String exportModule() throws Exception {
		fileUrl = ExportUtil.exportModule(FileFormat.getFileFormat(type), moduleName, viewName);
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
}
