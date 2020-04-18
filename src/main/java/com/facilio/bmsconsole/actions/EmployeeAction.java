package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class EmployeeAction extends FacilioAction{

private static final long serialVersionUID = 1L;
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private Boolean fetchCount;
	public Boolean getFetchCount() {
		if (fetchCount == null) {
			return false;
		}
		return fetchCount;
	}
	public void setFetchCount(Boolean fetchCount) {
		this.fetchCount = fetchCount;
	}

	private EmployeeContext employee;
	private List<EmployeeContext> employees;
	
	
	public EmployeeContext getEmployee() {
		return employee;
	}
	public void setEmployee(EmployeeContext employee) {
		this.employee = employee;
	}
	public List<EmployeeContext> getEmployees() {
		return employees;
	}
	public void setEmployees(List<EmployeeContext> employees) {
		this.employees = employees;
	}
	private List<Long> employeeIds;
	
	public List<Long> getEmployeeIds() {
		return employeeIds;
	}
	public void setEmployeeIds(List<Long> employeeIds) {
		this.employeeIds = employeeIds;
	}

	private long recordId = -1;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	private boolean includeParentFilter;

	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}

	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}

	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long appId;
	
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	
	private long occupantPortalAppId;
	public long getOccupantPortalAppId() {
		return occupantPortalAppId;
	}
	public void setOccupantPortalAppId(long occupantPortalAppId) {
		this.occupantPortalAppId = occupantPortalAppId;
	}
	
	public String addEmployees() throws Exception {
		
		if(!CollectionUtils.isEmpty(employees)) {
			FacilioChain c = TransactionChainFactory.addEmployeeChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, employees);
			c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			c.getContext().put(FacilioConstants.ContextNames.APP_ID, getAppId());
			c.getContext().put(FacilioConstants.ContextNames.SERVICE_PORTAL_APP_ID, getOccupantPortalAppId());
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			c.execute();
			setResult(FacilioConstants.ContextNames.EMPLOYEES, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}
	
	public String updateEmployees() throws Exception {
		
		if(!CollectionUtils.isEmpty(employees)) {
			FacilioChain c = TransactionChainFactory.updateEmployeeChain();
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.EDIT);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, employees);
			c.getContext().put(FacilioConstants.ContextNames.APP_ID, getAppId());
			c.getContext().put(FacilioConstants.ContextNames.SERVICE_PORTAL_APP_ID, getOccupantPortalAppId());
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			c.execute();
			setResult(FacilioConstants.ContextNames.EMPLOYEES, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
	}

	public String deleteEmployees() throws Exception {
		
		if(!CollectionUtils.isEmpty(employeeIds)) {
			FacilioChain c = FacilioChainFactory.deleteEmployeeChain();
			
			c.getContext().put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, employeeIds);
			c.execute();
			setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, c.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));
		}
		return SUCCESS;
	}
	
	public String employeeList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getEmployeeListChain();
		
		chain.getContext().put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
		chain.getContext().put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, "employee");
 		
		chain.getContext().put(FacilioConstants.ContextNames.SORTING_QUERY, "Employee.ID asc");
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		chain.getContext().put(FacilioConstants.ContextNames.FILTERS, json);
	 		chain.getContext().put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "employee.name");
 			searchObj.put("query", getSearch());
 			chain.getContext().put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		if(!getFetchCount()) {
			JSONObject pagination = new JSONObject();
			pagination.put("page", getPage());
			pagination.put("perPage", getPerPage());
			if (getPerPage() < 0) {
				pagination.put("perPage", 5000);
			}
			chain.getContext().put(FacilioConstants.ContextNames.PAGINATION, pagination);
		}
 	 	
 	 	
 	 	chain.execute();
		if (getFetchCount()) {
			setResult(FacilioConstants.ContextNames.RECORD_COUNT,chain.getContext().get(FacilioConstants.ContextNames.RECORD_COUNT));
		}
		else {
			List<EmployeeContext> employeeList = (List<EmployeeContext>) chain.getContext().get(FacilioConstants.ContextNames.RECORD_LIST);
			setResult(FacilioConstants.ContextNames.EMPLOYEES, employeeList);
		}
		
		return SUCCESS;
	}
	
	public String getEmployeeDetails() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getEmployeeDetailsChain();
		chain.getContext().put(FacilioConstants.ContextNames.ID, recordId);
		
		chain.execute();
		
		EmployeeContext employee = (EmployeeContext) chain.getContext().get(FacilioConstants.ContextNames.RECORD);
		setResult(FacilioConstants.ContextNames.EMPLOYEE, employee);
		
		return SUCCESS;
	}
	
	public String updateEmployeeAppAccess() throws Exception {
		
		if(!CollectionUtils.isEmpty(employees)) {
			FacilioChain c = TransactionChainFactory.updateEmployeeAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, employees);
			c.getContext().put(FacilioConstants.ContextNames.APP_ID, getAppId());
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			c.execute();
			setResult(FacilioConstants.ContextNames.EMPLOYEES, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
		  
	}	
	
	public String updateEmployeeOccupantPortalAccess() throws Exception {
		
		if(!CollectionUtils.isEmpty(employees)) {
			FacilioChain c = TransactionChainFactory.updateEmployeeAppAccessChain();
			c.getContext().put(FacilioConstants.ContextNames.SERVICE_PORTAL_APP_ID, getOccupantPortalAppId());
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
			
			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, employees);
			c.execute();
			setResult(FacilioConstants.ContextNames.EMPLOYEES, c.getContext().get(FacilioConstants.ContextNames.RECORD_LIST));
		}
		return SUCCESS;
		  
	}	

}
