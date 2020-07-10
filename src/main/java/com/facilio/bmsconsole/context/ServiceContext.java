package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

import javax.xml.ws.Service;

public class ServiceContext extends ModuleBaseWithCustomFields{

	private static final long serialVersionUID = 1L;

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	private double duration;
	
   public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}

private ServiceStatus status;
	
   public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(ServiceStatus status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = ServiceStatus.valueOf(status);
	}
	
	
	public static enum ServiceStatus {
		ACTIVE(),
		INACTIVE(),
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static ServiceStatus valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private List<ServiceVendorContext> serviceVendors;
	public List<ServiceVendorContext> getServiceVendors() {
		return serviceVendors;
	}
	public void setServiceVendors(List<ServiceVendorContext> serviceVendors) {
		this.serviceVendors = serviceVendors;
	}

	private double sellingPrice;

	public double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	private ServiceContext.PaymentType paymentType;
	public int getPaymentType() {
		if (paymentType != null) {
			return paymentType.getIndex();
		}
		return -1;
	}
	public void setPaymentType(int paymentType) {
		this.paymentType = ServiceContext.PaymentType.valueOf(paymentType);
	}
	public ServiceContext.PaymentType getPaymentTypeEnum() {
		return paymentType;
	}
	public void setPaymentType(ServiceContext.PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public static enum PaymentType implements FacilioEnum {
		SINGLE_PAYMENT, HOURLY_PAYMENT;

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static ServiceContext.PaymentType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	private double buyingPrice;

	public double getBuyingPrice() {
		return buyingPrice;
	}

	public void setBuyingPrice(double buyingPrice) {
		this.buyingPrice = buyingPrice;
	}
}
