package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ContractsPaymentScheduleContext extends ModuleBaseWithCustomFields {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long contractId;
	private long paymentInterval;
	private long scheduleDay;
	private int scheduleMonth;
	private long scheduleTime;
	public long getContractId() {
		return contractId;
	}
	public void setContractId(long contractId) {
		this.contractId = contractId;
	}
	
	public long getPaymentInterval() {
		return paymentInterval;
	}
	public void setPaymentInterval(long paymentInterval) {
		this.paymentInterval = paymentInterval;
	}
	public long getScheduleDay() {
		return scheduleDay;
	}
	public void setScheduleDay(long scheduleDay) {
		this.scheduleDay = scheduleDay;
	}
	public int getScheduleMonth() {
		return scheduleMonth;
	}
	public void setScheduleMonth(int scheduleMonth) {
		this.scheduleMonth = scheduleMonth;
	}
	public long getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(long scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	
	private FrequencyType frequencyType;
	public FrequencyType getFrequencyTypeEnum() {
		return frequencyType;
	}
	public int getFrequencyType() {
		if (frequencyType != null) {
			return frequencyType.getValue();
		}
		return -1;
	}
	public void setFrequencyType(int frequencyType) {
		this.frequencyType = FrequencyType.valueOf(frequencyType);
	}
	public void setFrequencyType(FrequencyType frequencyType) {
		this.frequencyType = frequencyType;
	}
	
	public static enum FrequencyType {
		DAILY(),
		WEEKLY(),
		MONTHLY(),
		YEARLY()
		
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static FrequencyType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
		

}
