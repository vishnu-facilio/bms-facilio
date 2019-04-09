package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.GatePassType;

public class GatePassContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private long issuedTime = -1;

	public long getIssuedTime() {
		return issuedTime;
	}

	public void setIssuedTime(long issuedTime) {
		this.issuedTime = issuedTime;
	}

	private long returnTime = -1;

	public long getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(long returnTime) {
		this.returnTime = returnTime;
	}

	private String issuedTo;

	public String getIssuedTo() {
		return issuedTo;
	}

	public void setIssuedTo(String issuedTo) {
		this.issuedTo = issuedTo;
	}

	private User issuedBy;

	public User getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(User issuedBy) {
		this.issuedBy = issuedBy;
	}
	
	private Boolean isReturnable;
	public Boolean getIsReturnable() {
		return isReturnable;
	}
	public void setIsReturnable(Boolean isReturnable) {
		this.isReturnable = isReturnable;
	}
	
	public boolean isReturnable() {
		if (isReturnable != null) {
			return isReturnable.booleanValue();
		}
		return false;
	}
	
	private String vehicleNo;
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	private GatePassType gatePassType;
	public GatePassType getGatePassTypeEnum() {
		return gatePassType;
	}
	public int getGatePassType() {
		if (gatePassType != null) {
			return gatePassType.getValue();
		}
		return -1;
	}
	public void setGatePassType(GatePassType gatePassType) {
		this.gatePassType = gatePassType;
	}
	public void setGatePassType(int gatePassType) {
		this.gatePassType = GatePassType.valueOf(gatePassType);
	}
	
	private List<GatePassLineItemsContext> lineItems;
	public List<GatePassLineItemsContext> getLineItems() {
		return lineItems;
	}
	public void setLineItems(List<GatePassLineItemsContext> lineItems) {
		this.lineItems = lineItems;
	}
}
