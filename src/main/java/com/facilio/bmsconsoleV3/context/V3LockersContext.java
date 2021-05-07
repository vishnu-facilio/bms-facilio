package com.facilio.bmsconsoleV3.context;

public class V3LockersContext  extends V3SpaceContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private V3EmployeeContext employee;
	
    public V3EmployeeContext getEmployee() {
		return employee;
	}
	public void setEmployee(V3EmployeeContext employee) {
		this.employee = employee;
	}

}