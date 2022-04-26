package com.facilio.bmsconsole.tenant;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class TenantSpaceContext extends ModuleBaseWithCustomFields {
	
	private static final long serialVersionUID = 1L;
	
//	private SpaceContext space;
//    public SpaceContext getSpace() {
//        return space;
//    }
//    public void setSpace(SpaceContext space) {
//        this.space = space;
//    }
    
    private TenantContext tenant;
    public TenantContext getTenant() {
       return tenant;
    }
	public void setTenant(TenantContext tenant) {
       this.tenant = tenant;
    }
	
	private BaseSpaceContext space;
    public BaseSpaceContext getSpace() {
		return space;
	}
	public void setSpace(BaseSpaceContext space) {
		this.space = space;
	}

	private long tenantId;
	public long getTenantId() {
		return tenantId;
	}
	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}

	private Boolean currentlyOccupied;
	public Boolean getCurrentlyOccupied() {
		return currentlyOccupied;
	}
	public void setCurrentlyOccupied(Boolean currentlyOccupied) {
		this.currentlyOccupied = currentlyOccupied;
	}

	private Long associatedTime;
	public Long getAssociatedTime() {
		return associatedTime;
	}
	public void setAssociatedTime(Long associatedTime) {
		this.associatedTime = associatedTime;
	}

	private Long disassociatedTime;
	public Long getDisassociatedTime() {
		return disassociatedTime;
	}
	public void setDisassociatedTime(Long disassociatedTime) {
		this.disassociatedTime = disassociatedTime;
	}
}
