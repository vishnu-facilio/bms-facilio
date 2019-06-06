package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class VendorContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	private String name, email, phone, website, description;
	private long ttime, modifiedTime;

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public long getTtime() {
		return ttime;
	}

	public void setTtime(long ttime) {
		this.ttime = ttime;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

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

	private LocationContext address;

	public LocationContext getAddress() {
		return address;
	}

	public void setAddress(LocationContext address) {
		this.address = address;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	private long getLocationId;

	public long getGetLocationId() {
		if (address != null) {
			return address.getId();
		}
		return -1;
	}
}
