package com.facilio.leed.context;

import com.facilio.bmsconsole.context.EnergyMeterContext;

public class LeedEnergyMeterContext extends EnergyMeterContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FuelContext fuelContext;
	private long meterId;
	private String serviceProvider;
	private String unit;
	private String contactPerson;
	private String contactEmail;
	
	public FuelContext getFuelContext() {
		return fuelContext;
	}
	public void setFuelContext(FuelContext fuelContext) {
		this.fuelContext = fuelContext;
	}
	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}
	public String getServiceProvider() {
		return serviceProvider;
	}
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	

}
