package com.facilio.bmsconsoleV3.context;

import com.facilio.v3.context.V3Context;

public class V3WorkorderCostContext  extends V3Context {

    private static final long serialVersionUID = 1L;

    private Long ttime, modifiedTime;
    private Double cost;
    private V3WorkOrderContext parentId;

    public Long getTtime() {
        return ttime;
    }

    public void setTtime(Long ttime) {
        this.ttime = ttime;
    }

    public Long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public V3WorkOrderContext getParentId() {
        return parentId;
    }


    public void setParentId(V3WorkOrderContext parentId) {
        this.parentId = parentId;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    private CostType costType;

    public Integer getCostType() {
        if (costType != null) {
            return costType.getValue();
        }
        return -1;
    }

    public void setCostType(Integer costType) {
        if (costType != null) {
            this.costType = CostType.valueOf(costType);
        }
    }

    public CostType getCostTypeEnum() {
        return costType;
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

        Integer costId;
        String displayName;

        private CostType(Integer costId, String displayName) {
            this.costId = costId;
            this.displayName = displayName;

        }
        public Integer getCostId() {
            return costId;
        }
        public void setCostOd(Integer costId) {
            this.costId = costId;
        }
        public String getDisplayName() {
            return displayName;
        }
        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
        public Integer getValue() {
            return ordinal() + 1;
        }

        public static CostType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private Long quantity;
    public Long getQuantity() {
        return quantity;
    }
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }



}
