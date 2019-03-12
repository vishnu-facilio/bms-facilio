package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class ToolContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private ToolTypesContext toolType;

	public ToolTypesContext getToolType() {
		return toolType;
	}

	public void setToolType(ToolTypesContext tool) {
		this.toolType = tool;
	}

	private StoreRoomContext storeRoom;

	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}

	public void setStoreRoom(StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
	}

	private ToolStatusContext status;

	public ToolStatusContext getStatus() {
		return status;
	}

	public void setStatus(ToolStatusContext status) {
		this.status = status;
	}

	private Unit issuingUnit;

	public Unit getIssuingUnitEnum() {
		return issuingUnit;
	}

	public void setIssuingUnit(Unit issuingUnit) {
		this.issuingUnit = issuingUnit;
	}

	public int getIssuingUnit() {
		if (issuingUnit != null) {
			return issuingUnit.getUnitId();
		}
		return -1;
	}

	public void setIssuingUnit(int issuingUnit) {
		this.issuingUnit = Unit.valueOf(issuingUnit);
	}

	private double quantity = -1;

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	private double rate = -1;

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	private double currentQuantity = -1;

	public double getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(double currentQuantity) {
		this.currentQuantity = currentQuantity;
	}
	
	private List<PurchasedToolContext> purchasedTools;
	public List<PurchasedToolContext> getPurchasedTools() {
		return purchasedTools;
	}
	public void setPurchasedTools(List<PurchasedToolContext> purchasedTools) {
		this.purchasedTools = purchasedTools;
	}
	
}
