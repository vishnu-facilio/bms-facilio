package com.facilio.bmsconsole.context;

public class EmployeeContext extends PeopleContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean canBeAssigned;

	public Boolean getCanBeAssigned() {
		return canBeAssigned;
	}

	public void setCanBeAssigned(Boolean canBeAssigned) {
		this.canBeAssigned = canBeAssigned;
	}

	public boolean canBeAssigned() {
		if (canBeAssigned != null) {
			return canBeAssigned.booleanValue();
		}
		return false;
	}
	
	private Boolean occupantPortalAccess;

	public Boolean getOccupantPortalAccess() {
		return occupantPortalAccess;
	}
	
	public void setOccupantPortalAccess(Boolean occupantPortalAccess) {
		this.occupantPortalAccess = occupantPortalAccess;
	}

	public boolean isOccupantPortalAccess() {
		if (occupantPortalAccess != null) {
			return occupantPortalAccess.booleanValue();
		}
		return false;
	}
	
	private Boolean isLabour;

	public Boolean getIsLabour() {
		return isLabour;
	}

	public void setIsLabour(Boolean isLabour) {
		this.isLabour = isLabour;
	}

	public boolean isLabour() {
		if (isLabour != null) {
			return isLabour.booleanValue();
		}
		return false;
	}
	
	private Boolean employeePortalAccess;

	public Boolean getEmployeePortalAccess() {
		return employeePortalAccess;
	}
	
	public void setEmployeePortalAccess(Boolean employeePortalAccess) {
		this.employeePortalAccess = employeePortalAccess;
	}

	public boolean isEmployeePortalAccess() {
		if (employeePortalAccess != null) {
			return employeePortalAccess.booleanValue();
		}
		return false;
	}
	
	private Boolean appAccess;

	public Boolean getAppAccess() {
		return appAccess;
	}
	
	public void setAppAccess(Boolean appAccess) {
		this.appAccess = appAccess;
	}

	public boolean isAppAccess() {
		if (appAccess != null) {
			return appAccess.booleanValue();
		}
		return false;
	}
	
	
}
