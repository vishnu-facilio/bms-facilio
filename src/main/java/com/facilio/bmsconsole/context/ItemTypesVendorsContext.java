package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class ItemTypesVendorsContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private ItemTypesContext itemType;

	public ItemTypesContext getItemType() {
		return itemType;
	}

	public void setItemType(ItemTypesContext item) {
		this.itemType = item;
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

	public ItemTypesVendorsContext(ItemTypesContext itemType, VendorContext vendor, double price, long dateOrdered) {
		super();
		this.itemType = itemType;
		this.vendor = vendor;
		this.price = price;
		this.dateOrdered = dateOrdered;
	}
	public ItemTypesVendorsContext() {
		// TODO Auto-generated constructor stub
	}

}
