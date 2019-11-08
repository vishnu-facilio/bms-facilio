package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class WorkPermitContext extends ModuleBaseWithCustomFields{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	private TicketContext ticket;
	private String name;
	private String description;
	
	
	private WorkType workType;
	public int getWorkType() {
		if (workType != null) {
			return workType.getIndex();
		}
		return -1;
	}
	public void setWorkType(int workType) {
		this.workType = WorkType.valueOf(workType);
	}
	public WorkType getInviteSourceEnum() {
		return workType;
	}
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public static enum WorkType implements FacilioEnum {
		TYPE1, TYPE2, TYPE3, TYPE4;

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static WorkType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private VendorContext vendor;
	
	private Boolean isRecurring;

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public boolean isRecurring() {
		if (isRecurring != null) {
			return isRecurring.booleanValue();
		}
		return false;
	}
	
	private BusinessHourContext recurringInfo;
	
	private long recurringInfoId;
	public TicketContext getTicket() {
		return ticket;
	}
	public void setTicket(TicketContext ticket) {
		this.ticket = ticket;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}
	public BusinessHourContext getRecurringInfo() {
		return recurringInfo;
	}
	public void setRecurringInfo(BusinessHourContext recurringInfo) {
		this.recurringInfo = recurringInfo;
	}
	public long getRecurringInfoId() {
		return recurringInfoId;
	}
	public void setRecurringInfoId(long recurringInfoId) {
		this.recurringInfoId = recurringInfoId;
	}
	
	
}
