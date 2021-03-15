package com.facilio.bmsconsoleV3.context;

public class V3TenantContactContext extends V3PeopleContext {

    private static final long serialVersionUID = 1L;

    private Boolean isTenantPortalAccess;

    public Boolean getIsTenantPortalAccess() {
        return isTenantPortalAccess;
    }

    public void setIsTenantPortalAccess(Boolean tenantPortalAccess) {
        this.isTenantPortalAccess = tenantPortalAccess;
    }

    public Boolean isTenantPortalAccess() {
        if (isTenantPortalAccess != null) {
            return isTenantPortalAccess.booleanValue();
        }
        return false;
    }

    private V3TenantContext tenant;

    public V3TenantContext getTenant() {
        return tenant;
    }

    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    private Boolean isPrimaryContact;

    public Boolean getIsPrimaryContact() {
        return isPrimaryContact;
    }

    public void setIsPrimaryContact(Boolean isPrimaryContact) {
        this.isPrimaryContact = isPrimaryContact;
    }

    public Boolean isPrimaryContact() {
        if (isPrimaryContact != null) {
            return isPrimaryContact.booleanValue();
        }
        return false;
    }

    public Long getTenantId() {
        if (tenant == null) {
            return null;
        }
        return tenant.getId();
    }

    public void setTenantId(Long tenantId) {
        if (tenant == null) {
            tenant = new V3TenantContext();
        }
        tenant.setId(tenantId);
    }

}
