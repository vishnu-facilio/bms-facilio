package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class InventoryVendorContext extends ModuleBaseWithCustomFields{
private static final long serialVersionUID = 1L;
	private String name,email, phone;
	private long contractExpiry;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public long getContractExpiry() {
		return contractExpiry;
	}
	public void setContractExpiry(long contractExpiry) {
		this.contractExpiry = contractExpiry;
	}
	
}
