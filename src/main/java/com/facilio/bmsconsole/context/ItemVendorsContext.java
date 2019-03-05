package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class ItemVendorsContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private ItemsContext item;

	public ItemsContext getItem() {
		return item;
	}

	public void setItem(ItemsContext item) {
		this.item = item;
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

}
