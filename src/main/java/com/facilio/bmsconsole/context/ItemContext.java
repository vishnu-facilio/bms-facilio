package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
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
	public int getCostType() {
		if (costType != null) {
			return costType.getIndex();
		}
		return -1;
	}
	public void setCostType(int costType) {
		this.costType = CostType.valueOf(costType);
	}
	public CostType getCostTypeEnum() {
		return costType;
	}
	public void setCostType(CostType costType) {
		this.costType = costType;
	}

	public static enum CostType implements FacilioIntEnum {
		FIFO, LIFO;

		@Override
		public Integer getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
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

	private double quantity = -1;

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = 0;
		if (quantity > 0) {
			this.quantity = Math.round(quantity*1000.0)/1000.0;
		}
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

	private long lastPurchasedDate = -1;

	public long getLastPurchasedDate() {
		return lastPurchasedDate;
	}

	public void setLastPurchasedDate(long lastPurchasedDate) {
		this.lastPurchasedDate = lastPurchasedDate;
	}

	private double lastPurchasedPrice = -1;

	public double getLastPurchasedPrice() {
		return lastPurchasedPrice;
	}

	public void setLastPurchasedPrice(double lastPurchasedPrice) {
		this.lastPurchasedPrice = lastPurchasedPrice;
	}
	
	private double minimumQuantity = -1;
	public double getMinimumQuantity() {
		return minimumQuantity;
	}
	public void setMinimumQuantity(double minimumQuantity) {
		this.minimumQuantity = minimumQuantity;
	}
	
	public Boolean isUnderstocked;

	public Boolean getIsUnderstocked() {
		return isUnderstocked;
	}

	public void setIsUnderstocked(Boolean understocked) {
		this.isUnderstocked = understocked;
	}

	public boolean isUnderstocked() {
		if (isUnderstocked != null) {
			return isUnderstocked.booleanValue();
		}
		return false;
	}


}
