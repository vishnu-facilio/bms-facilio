package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class WorkorderCostContext extends ModuleBaseWithCustomFields {

	private static final long serialVersionUID = 1L;
	private long parentId, ttime, modifiedTime;
	private double cost;

	public long getTtime() {
		return ttime;
	}

	public void setTtime(long ttime) {
		this.ttime = ttime;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
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
		parts, technician;

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

}
