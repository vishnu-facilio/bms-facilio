package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.opensymphony.xwork2.ActionSupport;

public class TenantAction extends ActionSupport {
	
	private TenantContext tenant;
	public TenantContext getTenant() {
		return tenant;
	}
	public void setTenant(TenantContext tenant) {
		this.tenant = tenant;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String result;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	private List<TenantContext> tenants;
	public List<TenantContext> getTenants() {
		return tenants;
	}
	public void setTenants(List<TenantContext> tenants) {
		this.tenants = tenants;
	}
	
	public String addTenant() throws Exception {
		TenantsAPI.addTenant(tenant);
		return SUCCESS;
	}
	
	public String updateTenant() throws Exception {
		TenantsAPI.updateTenant(tenant);
		result = SUCCESS;
		return SUCCESS;
	}
	
	public String fetchTenant() throws Exception {
		tenant = TenantsAPI.getTenant(id);
		return SUCCESS;
	}
	
	public String deleteTenant() throws Exception {
		TenantsAPI.deleteTenant(id);
		result = SUCCESS;
		return SUCCESS;
	}
	
	public String fetchAllTenants() throws Exception {
		tenants = TenantsAPI.getAllTenants();
		return SUCCESS;
	}
	
}
