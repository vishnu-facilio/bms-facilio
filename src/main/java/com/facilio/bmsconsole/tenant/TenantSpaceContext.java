package com.facilio.bmsconsole.tenant;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class TenantSpaceContext extends ModuleBaseWithCustomFields {
	
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
    public long getSpace() {
		return space;
	}
	public void setSpace(long space) {
		this.space = space;
	}
	public void setTenant(TenantContext tenant) {
       this.tenant = tenant;
    }
    
    private long tenantId;
	public long getTenantId() {
		return tenantId;
	}
	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}
	
	private long space;

}
