package com.facilio.fsm.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class ServiceOrderCostContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private ServiceOrderContext serviceOrder;
    private Double cost;
    private Long quantity;
    private InventoryCostType inventoryCostType;
    private InventorySource inventorySource;

    public ServiceOrderContext getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrderContext serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Integer getInventoryCostType() {
        if (inventoryCostType != null) {
            return inventoryCostType.getIndex();
        }
        return -1;
    }

    public void setInventoryCostType(Integer inventoryCostType) {
        if (inventoryCostType != null) {
            this.inventoryCostType = InventoryCostType.valueOf(inventoryCostType);
        }
    }
    public InventoryCostType getInventoryCostTypeEnum() {
        return inventoryCostType;
    }

    public enum InventoryCostType implements FacilioIntEnum {
        ITEMS("Items"), TOOLS("Tools"),SERVICES("Services"),CUSTOM("Custom");

        private final String value;
        InventoryCostType(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static InventoryCostType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
    public Integer getInventorySource() {
        if (inventorySource != null) {
            return inventorySource.getIndex();
        }
        return -1;
    }

    public void setInventorySource(Integer inventorySource) {
        if (inventorySource != null) {
            this.inventorySource = InventorySource.valueOf(inventorySource);
        }
    }
    public InventorySource getInventorySourceEnum() {
        return inventorySource;
    }


    public enum InventorySource implements FacilioIntEnum {
        PLANS("Plans"), ACTUALS("Actuals");

        private final String value;
        InventorySource(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static InventorySource valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

}
