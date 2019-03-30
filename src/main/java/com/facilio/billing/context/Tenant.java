package com.facilio.billing.context;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ZoneContext;

import java.util.List;

public class Tenant {
	
	public long id;	
	public String name;
	public long orgId;
	public String address;
	public String contactEmail;
	public String contactNumber;
	public List<ZoneContext> tenantZones;
	public long templateId;
	public ExcelTemplate billTemplate;
	public List<EnergyMeterContext> tenantMeters;
	
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public List<ZoneContext> getTenantZones() {
		return tenantZones;
	}
	public void setTenantZones(List<ZoneContext> tenantZones) {
		this.tenantZones = tenantZones;
	}
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	public ExcelTemplate getBillTemplate() {
		return billTemplate;
	}
	public void setBillTemplate(ExcelTemplate billTemplate) {
		this.billTemplate = billTemplate;
	}
	
	public List<EnergyMeterContext> getTenantMeters() {
		return tenantMeters;
	}
	public void setTenantMeters(List<EnergyMeterContext> tenantMeters) {
		this.tenantMeters = tenantMeters;
	}
}
