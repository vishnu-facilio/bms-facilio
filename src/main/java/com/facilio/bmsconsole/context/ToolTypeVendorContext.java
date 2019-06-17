package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class ToolTypeVendorContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;

	private ToolTypesContext toolType;

	public ToolTypesContext getToolType() {
		return toolType;
	}

	public void setToolType(ToolTypesContext toolType) {
		this.toolType = toolType;
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

	private double price = -1;

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	private long dateOrdered = -1;

	public long getDateOrdered() {
		return dateOrdered;
	}

	public void setDateOrdered(long dateOrdered) {
		this.dateOrdered = dateOrdered;
	}

	public ToolTypeVendorContext(ToolTypesContext toolType, VendorContext vendor, double price, long dateOrdered) {
		super();
		this.toolType = toolType;
		this.vendor = vendor;
		this.price = price;
		this.dateOrdered = dateOrdered;
	}
	public ToolTypeVendorContext() {
		// TODO Auto-generated constructor stub
	}

}
