package com.facilio.bmsconsoleV3.context;

import java.util.List;

import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;

public class V3ServiceContext extends V3Context {

    private static final Long serialVersionUID = 1L;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String description;
    private Double duration;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    private ServiceContext.ServiceStatus status;

    public ServiceContext.ServiceStatus getStatusEnum() {
        return status;
    }

    public Integer getStatus() {
        if (status != null) {
            return status.getValue();
        }
        return null;
    }

    public void setStatus(ServiceContext.ServiceStatus status) {
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = ServiceContext.ServiceStatus.valueOf(status);
    }

    public static enum ServiceStatus {
        ACTIVE(),
        INACTIVE(),
        ;

        public int getValue() {
            return ordinal() + 1;
        }

        public static V3ServiceContext.ServiceStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    private List<V3ServiceVendorContext> serviceVendors;

    public List<V3ServiceVendorContext> getServiceVendors() {
        return serviceVendors;
    }

    public void setServiceVendors(List<V3ServiceVendorContext> serviceVendors) {
        this.serviceVendors = serviceVendors;
    }

    private Double sellingPrice;

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    private V3ServiceContext.PaymentType paymentType;

    public Integer getPaymentType() {
        if (paymentType != null) {
            return paymentType.getIndex();
        }
        return null;
    }

    public void setPaymentType(Integer paymentType) {
        if (paymentType != null) {
            this.paymentType = V3ServiceContext.PaymentType.valueOf(paymentType);
        }
    }

    public V3ServiceContext.PaymentType getPaymentTypeEnum() {
        return paymentType;
    }
    //  public void setPaymentType(V3ServiceContext.PaymentType paymentType) {
    //    this.paymentType = paymentType;
    // }

    public static enum PaymentType implements FacilioIntEnum {
        FIXED("Fixed"),
        DURATION_BASED("Duration Based");

        private String name;

        PaymentType(String name) {
            this.name = name;
        }

        public static V3ServiceContext.PaymentType valueOf(Integer value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    private Double buyingPrice;

    public Double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(Double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }
}
