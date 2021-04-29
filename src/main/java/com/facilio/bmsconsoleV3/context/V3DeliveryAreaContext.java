package com.facilio.bmsconsoleV3.context;


import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.v3.context.V3Context;

public class V3DeliveryAreaContext extends V3Context{
	
	private static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
	
	private Boolean isActive;
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	
	private BaseSpaceContext location;
	public BaseSpaceContext getLocation() {
        return location;
    }

    public void setLocation(BaseSpaceContext location) {
        this.location = location;
    }
}