package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class WorkorderCostContext extends ModuleBaseWithCustomFields {

	private static final long serialVersionUID = 1L;
	private long parentId, ttime, modifiedTime;
	private double cost=-1;

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
	
	
//	private String costName;
	public String getCostName() {
		if (costType != null) {
			return costType.getDisplayName();
		}
		return null;
	}
	private String name;
	public String getName() {
		if (costType != null && costType.getValue()!=5) {
			return costType.getDisplayName();
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public static enum CostType {
		items(1, "Items"),
		tools(2,"Tools"),
		labour(3,"Labour"),
		service(4,"Service"),
		custom(5,"Custom");
		
		int costId;
		String displayName;
		
		private CostType(int costId, String displayName) {
			this.costId = costId;
			this.displayName = displayName;
			
		}
		public int getCostId() {
			return costId;
		}
		public void setCostOd(int costId) {
			this.costId = costId;
		}
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
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
	
	private long quantity;
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

}
