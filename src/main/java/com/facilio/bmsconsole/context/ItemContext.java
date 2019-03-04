package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.context.WorkorderCostContext.CostType;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class ItemContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private ItemTypesContext itemType;

	public ItemTypesContext getItemType() {
		return itemType;
	}

	public void setItemType(ItemTypesContext item) {
		this.itemType = item;
	}

	private StoreRoomContext storeRoom;

	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}

	public void setStoreRoom(StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
	}

	private ItemStatusContext status;

	public ItemStatusContext getStatus() {
		return status;
	}

	public void setStatus(ItemStatusContext status) {
		this.status = status;
	}

	private CostType costType;
	public CostType getCostTypeEnum() {
		return costType;
	}

	public void setCostType(CostType costType) {
		this.costType = costType;
	}

	public int getCostType() {
		if (costType != null) {
			return costType.getValue();
		}
		return -1;
	}

	public void setCostType(int costType) {
		this.costType = CostType.valueOf(costType);
	}

	public static enum CostType {
		FIFO, LIFO;

		public int getValue() {
			return ordinal() + 1;
		}

		public static CostType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
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
	
	private double quantity=-1;
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	private List<PurchasedItemContext> purchasedItems;
	public List<PurchasedItemContext> getPurchasedItems() {
		return purchasedItems;
	}
	public void setPurchasedItems(List<PurchasedItemContext> inventoryCost) {
		this.purchasedItems = inventoryCost;
	}
	
	@Override
	public long getLocalId() {
		// TODO Auto-generated method stub
		return super.getLocalId();
	}
	@Override
	public void setLocalId(long localId) {
		// TODO Auto-generated method stub
		super.setLocalId(localId);
	}
	
}
