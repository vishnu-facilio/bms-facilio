package com.facilio.workflows.action;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Priority;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class WorkflowAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String nameSpace;
	String functionName;

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	private static org.apache.log4j.Logger log = LogManager.getLogger(WorkflowUtil.class.getName());
	
	List<FacilioSystemFunctionNameSpace> namespaces;
	int defaultWorkflowId = -1;
	List<Object> paramList;
	public int nameSpaceValue;
	List<FacilioWorkflowFunctionInterface> functions;
	
	List<Long> ids;
	
	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public List<Object> getParamList() {
		return paramList;
	}

	public void setParamList(List<Object> paramList) {
		this.paramList = paramList;
	}

	public int getDefaultWorkflowId() {
		return defaultWorkflowId;
	}

	public void setDefaultWorkflowId(int defaultWorkflowId) {
		this.defaultWorkflowId = defaultWorkflowId;
	}

	public List<FacilioSystemFunctionNameSpace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<FacilioSystemFunctionNameSpace> namespaces) {
		this.namespaces = namespaces;
	}
	
	public int getNameSpaceValue() {
		return nameSpaceValue;
	}

	public void setNameSpaceValue(int nameSpaceValue) {
		this.nameSpaceValue = nameSpaceValue;
	}
	
	public List<FacilioWorkflowFunctionInterface> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FacilioWorkflowFunctionInterface> functions) {
		this.functions = functions;
	}

	public String getAllFunctions() {
		
		try {

			//functions = WorkflowUtil.getFacilioFunctions(FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceValue).getName());
			
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e);
			return ERROR;
		}
	}

	public String getAllNameSpace() {
		
		try {
			namespaces = new ArrayList(FacilioSystemFunctionNameSpace.getNamespaceMap().values());
			
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e);
			return ERROR;
		}
	}
	
	// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  V2 APIS $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	private WorkflowNamespaceContext namespace;
	private WorkflowUserFunctionContext userFunction;
	private ScheduledWorkflowContext scheduledWorkflow;
	
	public ScheduledWorkflowContext getScheduledWorkflow() {
		return scheduledWorkflow;
	}

	public void setScheduledWorkflow(ScheduledWorkflowContext scheduledWorkflow) {
		this.scheduledWorkflow = scheduledWorkflow;
	}

	public WorkflowNamespaceContext getNamespace() {
		return namespace;
	}

	public void setNamespace(WorkflowNamespaceContext namespace) {
		this.namespace = namespace;
	}

	public WorkflowUserFunctionContext getUserFunction() {
		return userFunction;
	}

	public void setUserFunction(WorkflowUserFunctionContext userFunction) {
		this.userFunction = userFunction;
	}

	public String getWorkflowXmlFromObject() throws Exception {
		setData("workflowString", WorkflowUtil.getXmlStringFromWorkflow(workflow));
		return SUCCESS;
	}

	public String runWorkflow() throws Exception {
		FacilioContext context = new FacilioContext();
		try {
			if(workflow == null) {
				if(nameSpace != null && functionName != null) {
					workflow = UserFunctionAPI.getWorkflowFunction(nameSpace, functionName);
				}
				else if (workflowId != -1) {
					workflow = WorkflowUtil.getWorkflowContext(workflowId);
				}
			}
			context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			context.put(WorkflowV2Util.WORKFLOW_PARAMS, paramList);
			
			FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
			
			chain.execute(context);
		}
	    catch(Exception e) {
	    	log.log(Priority.ERROR, e);
	    }
	    setData(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
	    setData(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR));
		return SUCCESS;
	}
	
	public String addWorkflow() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowChain();
			addWorkflowChain.execute(context);
		setData(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		setData(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR));
		return SUCCESS;
	}
	public String updateWorkflow() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		FacilioChain addWorkflowChain =  TransactionChainFactory.getUpdateWorkflowChain();
		addWorkflowChain.execute(context);
		setData(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		setData(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR));
		return SUCCESS;
	}
	public String deleteWorkflow() throws Exception {
		
		workflow = Constants.getScriptBean().deleteFunction(workflow);
		
		setData(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		return SUCCESS;
		
	}
	
	public String addNameSpace() throws Exception {
		namespace = Constants.getScriptBean().addNameSpace(namespace);
		setData(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		return SUCCESS;
	}
	public String updateNameSpace() throws Exception {
		namespace = Constants.getScriptBean().updateNameSpace(namespace);
		setData(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		return SUCCESS;
	}
	
	public String deleteNameSpace() throws Exception {
		namespace = Constants.getScriptBean().deleteNameSpace(namespace);
		setData(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		return SUCCESS;
	}
	
	public String addUserFunction() throws Exception {
		
		WorkflowNamespaceContext nameSpace = UserFunctionAPI.getNameSpace(userFunction.getNameSpaceId());
		
		userFunction.setNameSpaceName(nameSpace.getName());
		
		FacilioContext context = Constants.getScriptBean().addFunction(userFunction);
		
		setData(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		setData(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR));
		return SUCCESS;
	}
	
	public String updateUserFunction() throws Exception {
		
		WorkflowNamespaceContext nameSpace = UserFunctionAPI.getNameSpace(userFunction.getNameSpaceId());
		
		userFunction.setNameSpaceName(nameSpace.getName());
		
		FacilioContext context = Constants.getScriptBean().updateFunction(userFunction);
		
		setData(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		setData(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR));
		return SUCCESS;
	}
	public String deleteUserFunction() throws Exception {
		
		userFunction = Constants.getScriptBean().deleteFunction(userFunction);
		
		setData(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		return SUCCESS;
	}
	
	public String getNameSpaceListWithFunctions() throws Exception {
		FacilioContext context = new FacilioContext();
		
		FacilioChain getNameSpaceChain =  ReadOnlyChainFactory.getAllWorkflowNameSpaceChain();
		getNameSpaceChain.execute(context);
		setData(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT_LIST, context.get(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT_LIST));
		return SUCCESS;
	}
	
	public String getFunctionMeta() throws Exception {
		
		setData(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXTS, UserFunctionAPI.getWorkflowFunction(ids));
		return SUCCESS;
	}

	public String getDefaultWorkflow() throws Exception {
		if(workflowId > -1) {
			workflow = WorkflowUtil.getWorkflowContext(workflowId);

			setData(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		}
		return SUCCESS;
	}
	public String getDefaultWorkflowResultV2() throws Exception {
		if(defaultWorkflowId > -1) {
			FacilioContext context = new FacilioContext();
			context.put(WorkflowV2Util.DEFAULT_WORKFLOW_ID, defaultWorkflowId);
			context.put(WorkflowV2Util.WORKFLOW_PARAMS, paramList);
			
			FacilioChain chain = TransactionChainFactory.getExecuteDefaultWorkflowChain();
			
			chain.execute(context);
			
			setData(WorkflowV2Util.WORKFLOW_CONTEXT, context.get(WorkflowV2Util.WORKFLOW_CONTEXT));
			setData(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR));
		}
		return SUCCESS;
	}
	
	public String addOrUpdateScheduledWorkflow() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT, scheduledWorkflow);
//		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, scheduledWorkflow.getWorkflowContext());
		FacilioChain addWorkflowChain =  TransactionChainFactory.getAddOrUpdateScheduledWorkflowChain();
		addWorkflowChain.execute(context);
		setData(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT, scheduledWorkflow);
		addAuditLogs(scheduledWorkflow);
//		setData(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, context.get(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR));
		return SUCCESS;
	}

	private void addAuditLogs(ScheduledWorkflowContext scheduledWorkflow){

		String type = scheduledWorkflow.getCreatedTime() ==  scheduledWorkflow.getModifiedTime() ? "created":"updated";
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Scheduler {%s} has been %s ", scheduledWorkflow.getName(),type),
				null,
				AuditLogHandler.RecordType.SETTING,
				"Scheduler",scheduledWorkflow.getId())
				.setActionType(scheduledWorkflow.getId() > 0 ? AuditLogHandler.ActionType.UPDATE : AuditLogHandler.ActionType.ADD)
				.setLinkConfig(((Function<Void, String>) o -> {
					JSONArray array = new JSONArray();
					JSONObject json = new JSONObject();
					json.put("id", scheduledWorkflow.getId());
					json.put("navigateTo", "Scheduler");
					array.add(json);
					return array.toJSONString();
				}).apply(null))
		);
	}

	public String deleteScheduledWorkflow() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT, scheduledWorkflow);
//		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, scheduledWorkflow.getWorkflowContext());
		FacilioChain addWorkflowChain =  TransactionChainFactory.getDeleteScheduledWorkflowChain();
		addWorkflowChain.execute(context);
		setData(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT, scheduledWorkflow);

		ScheduledWorkflowContext scheduledWorkflowContext = (ScheduledWorkflowContext) context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT);
		sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Scheduler %s has been deleted.",scheduledWorkflowContext.getName()),
				null,
				AuditLogHandler.RecordType.SETTING,
				"Scheduler",scheduledWorkflowContext.getId())
				.setActionType(AuditLogHandler.ActionType.DELETE)
		);

		return SUCCESS;
	}
	
	public String getScheduledWorkflowList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		FacilioChain getNameSpaceChain =  ReadOnlyChainFactory.getAllScheduledWorkflowChain();
		getNameSpaceChain.execute(context);
		setData(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT_LIST, context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT_LIST));
		return SUCCESS;
	}

	public String updateScheduledWorkflowStatus() throws Exception{
		FacilioChain chain = TransactionChainFactory.updateScheduledWorkflowStatus();

		FacilioContext context = chain.getContext();
		context.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT,scheduledWorkflow);

		chain.execute();
		setData(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT,context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT));

		return SUCCESS;
	}
}
