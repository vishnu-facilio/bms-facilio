package com.facilio.workflows.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.workflows.functions.FacilioFunctionNameSpace;
import com.facilio.workflows.functions.FacilioWorkflowFunctionInterface;
import com.facilio.workflows.util.WorkflowUtil;

public class WorkflowAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static org.apache.log4j.Logger log = LogManager.getLogger(WorkflowUtil.class.getName());
	
	List<FacilioFunctionNameSpace> namespaces;
	
	public List<FacilioFunctionNameSpace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<FacilioFunctionNameSpace> namespaces) {
		this.namespaces = namespaces;
	}
	
	public int nameSpaceValue;
	
	public int getNameSpaceValue() {
		return nameSpaceValue;
	}

	public void setNameSpaceValue(int nameSpaceValue) {
		this.nameSpaceValue = nameSpaceValue;
	}
	
	List<FacilioWorkflowFunctionInterface> functions;
	
	public List<FacilioWorkflowFunctionInterface> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FacilioWorkflowFunctionInterface> functions) {
		this.functions = functions;
	}
	

	public String getAllFunctions() {
		
		try {

			functions = WorkflowUtil.getFacilioFunctions(FacilioFunctionNameSpace.getFacilioDefaultFunction(nameSpaceValue).getName());
			
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e);
			return ERROR;
		}
	}

	public String getAllNameSpace() {
		
		try {
			namespaces = new ArrayList(FacilioFunctionNameSpace.getNamespaceMap().values());
			
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e);
			return ERROR;
		}
	}
	
}
