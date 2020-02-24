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

}
