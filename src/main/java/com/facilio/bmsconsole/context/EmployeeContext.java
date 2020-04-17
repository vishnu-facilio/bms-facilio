package com.facilio.bmsconsole.context;

public class EmployeeContext extends PeopleContext{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean isAssignable;

	public Boolean getIsAssignable() {
		return isAssignable;
	}

	public void setIsAssignable(Boolean canBeAssigned) {
		this.isAssignable = canBeAssigned;
	}

	public boolean canBeAssigned() {
		if (isAssignable != null) {
			return isAssignable.booleanValue();
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
	
	private Boolean isAppAccess;

	public Boolean getIsAppAccess() {
		return isAppAccess;
	}
	
	public void setIsAppAccess(Boolean appAccess) {
		this.isAppAccess = appAccess;
	}

	public boolean isAppAccess() {
		if (isAppAccess != null) {
			return isAppAccess.booleanValue();
		}
		return false;
	}
	
	
}
