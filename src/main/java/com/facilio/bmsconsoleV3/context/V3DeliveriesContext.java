package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;

public class V3DeliveriesContext extends V3Context{

    private static final long serialVersionUID = 1L;

    private V3EmployeeContext employee;

    public V3EmployeeContext getEmployee() {
		return employee;
	}
	public void setEmployee(V3EmployeeContext employee) {
		this.employee = employee;
	}
    
	private V3DepartmentContext department;
    
    public V3DepartmentContext getDepartment() {
		return department;
	}
	public void setDepartment(V3DepartmentContext department) {
		this.department = department;
	}
	
	private Long receivedTime;
	
	public Long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Long receivedTime) {
        this.receivedTime = receivedTime;
    }
    
    private Long deliveredTime;
	
	public Long getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(Long deliveredTime) {
        this.deliveredTime = deliveredTime;
    }
}
