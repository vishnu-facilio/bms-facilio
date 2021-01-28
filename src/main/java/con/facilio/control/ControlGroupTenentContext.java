package con.facilio.control;

import com.facilio.bmsconsole.tenant.TenantContext;

public class ControlGroupTenentContext extends ControlGroupContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	ControlGroupContext parentGroup;
	TenantContext tenant;
	public ControlGroupContext getParentGroup() {
		return parentGroup;
	}
	public void setParentGroup(ControlGroupContext parentGroup) {
		this.parentGroup = parentGroup;
	}
	public TenantContext getTenant() {
		return tenant;
	}
	public void setTenant(TenantContext tenant) {
		this.tenant = tenant;
	}
}
