package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.WorkorderCostContext.CostType;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.unitconversion.Unit;

public class InventryContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private ItemsContext item;

	public ItemsContext getItem() {
		return item;
	}

	public void setItem(ItemsContext item) {
		this.item = item;
	}

	private StoreRoomContext storeRoom;

	public StoreRoomContext getStoreRoom() {
		return storeRoom;
	}

	public void setStoreRoom(StoreRoomContext storeRoom) {
		this.storeRoom = storeRoom;
	}

	private InventoryStatusContext status;

	public InventoryStatusContext getStatus() {
		return status;
	}

	public void setStatus(InventoryStatusContext status) {
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
		fifo, lifo;

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

	private long ttime;

	public long getTtime() {
		return ttime;
	}

	public void setTtime(long ttime) {
		this.ttime = ttime;
	}

	private long modifiedTime;

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
}
