package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
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
	private User requestedBy;
	
	
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
	public WorkType getWorkTypeEnum() {
		return workType;
	}
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	public static enum WorkType implements FacilioEnum {
		HOT_WORK_PERMIT("Hot Work Permit"), COLD_WORK_PERMIT("Cold Work Permit"), EXCAVATION_WORK_PERMIT("Excavation Work Permit"), CONFINED_SPACE_WORK_PERMIT("Confined Space Work Permit"), EARTHMOVING_EQUIPMENT_VEHICULAR_WORK_PERMIT("EarthMoving Equipment/Vehicular Work Permit");

		@Override
		public int getIndex() {
			return ordinal() + 1;
		}
		
		private String name;
		
		WorkType(String name) {
			
			this.name = name;
		}

		@Override
		public String getValue() {
			return name;
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
	
	private BusinessHoursContext recurringInfo;
	
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
	public BusinessHoursContext getRecurringInfo() {
		return recurringInfo;
	}
	public void setRecurringInfo(BusinessHoursContext recurringInfo) {
		this.recurringInfo = recurringInfo;
	}
	public long getRecurringInfoId() {
		return recurringInfoId;
	}
	public void setRecurringInfoId(long recurringInfoId) {
		this.recurringInfoId = recurringInfoId;
	}
	public User getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}
	
	private long expectedStartTime;
	private long expectedEndTime;
	public long getExpectedStartTime() {
		return expectedStartTime;
	}
	public void setExpectedStartTime(long expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}
	public long getExpectedEndTime() {
		return expectedEndTime;
	}
	public void setExpectedEndTime(long expectedEndTime) {
		this.expectedEndTime = expectedEndTime;
	}
	
	
	
	
}
