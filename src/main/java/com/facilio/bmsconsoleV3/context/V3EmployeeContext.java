package com.facilio.bmsconsoleV3.context;

public class V3EmployeeContext extends V3PeopleContext{

    private static final long serialVersionUID = 1L;

    private Boolean isAssignable;

    public Boolean getIsAssignable() {
        return isAssignable;
    }

    public void setIsAssignable(Boolean canBeAssigned) {
        this.isAssignable = canBeAssigned;
    }

    public Boolean canBeAssigned() {
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

    public Boolean isLabour() {
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

    public Boolean isAppAccess() {
        if (isAppAccess != null) {
            return isAppAccess.booleanValue();
        }
        return false;
    }
    
    private V3DepartmentContext department;
    
    public V3DepartmentContext getDepartment() {
		return department;
	}
	public void setDepartment(V3DepartmentContext department) {
		this.department = department;
	}

    private V3SpaceContext space;
	
	public V3SpaceContext getSpace() {
		return space;
	}
	public void setSpace(V3SpaceContext space) {
		this.space = space;
	}

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    private String designation;
}
