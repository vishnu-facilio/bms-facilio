package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.v3.context.V3Context;

public class V3TenantSpaceContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private V3TenantContext tenant;
    public V3TenantContext getTenant() {
        return tenant;
    }
    public void setTenant(V3TenantContext tenant) {
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
