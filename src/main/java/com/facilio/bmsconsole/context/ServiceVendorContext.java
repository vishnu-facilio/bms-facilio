package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class ServiceVendorContext extends ModuleBaseWithCustomFields {

	private static final long serialVersionUID = 1L;

	private long serviceId;

	public long getServiceId() {
		return serviceId;
	}

	public void setServiceId(long serviceId) {
		this.serviceId = serviceId;
	}

	private VendorContext vendor;

	public VendorContext getVendor() {
		return vendor;
	}

	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}

	private Unit orderingUnit;

	public Unit getOrderingUnitEnum() {
		return orderingUnit;
	}

	public void setOrderingUnit(Unit orderingUnit) {
		this.orderingUnit = orderingUnit;
	}

	public int getOrderingUnit() {
		if (orderingUnit != null) {
			return orderingUnit.getUnitId();
		}
		return -1;
	}

	public void setOrderingUnit(int orderingUnit) {
		this.orderingUnit = Unit.valueOf(orderingUnit);
	}

	private double lastPrice = -1;

	public double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}

	private long lastOrderedDate = -1;



	public long getLastOrderedDate() {
		return lastOrderedDate;
	}

	public void setLastOrderedDate(long lastOrderedDate) {
		this.lastOrderedDate = lastOrderedDate;
	}

	
	
}
