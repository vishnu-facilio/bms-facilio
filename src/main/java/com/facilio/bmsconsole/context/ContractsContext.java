package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.tasker.ScheduleInfo;

public class ContractsContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContractsContext() {
		// TODO Auto-generated constructor stub
	}

	public ContractsContext(ContractsContext contract) {
		setContractType(contract.getContractType());
		setName(contract.getName());
		setDescription(contract.getDescription());
		setVendor(contract.getVendor());
		setFromDate(contract.getFromDate());
		setEndDate(contract.getFromDate());
		setRenewalDate(contract.getRenewalDate());
		setRevisionNumber(contract.getRevisionNumber() + 1);
		setTotalCost(contract.getTotalCost());
		setStatus(Status.WAITING_FOR_APPROVAL);
		setParentId(contract.getParentId());
		setTermsAssociated(contract.getTermsAssociated());
		setFrequencyType(getFrequencyType());
		setPaymentInterval(getPaymentInterval());
		setScheduleDay(getScheduleDay());
		setScheduleMonth(getScheduleMonth());
		setScheduleTime(getScheduleTime());
		setId(-1);
	}

	private String name;
	private String description;
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
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public long getFromDate() {
		return fromDate;
	}
	public void setFromDate(long fromDate) {
		this.fromDate = fromDate;
	}
	public long getEndDate() {
		return endDate;
	}
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	public long getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(long renewalDate) {
		this.renewalDate = renewalDate;
	}
	private VendorContext vendor;
	
	private Status status;
	public Status getStatusEnum() {
		return status;
	}
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	private double totalCost = -1;
	
	private long revisionNumber = -1;


	public long getRevisionNumber() {
		return revisionNumber;
	}
	public void setRevisionNumber(long revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	public static enum Status {
		WAITING_FOR_APPROVAL(),
		APPROVED(),
		CLOSED(),
		CANCELLED(),
		SUSPENDED(),
		PENDING_FOR_REVISION(),
		REVISED()
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private ContractType contractType;
	public ContractType getContractTypeEnum() {
		return contractType;
	}
	public int getContractType() {
		if (contractType != null) {
			return contractType.getValue();
		}
		return -1;
	}
	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}
	public void setContractType(int contractType) {
		this.contractType = ContractType.valueOf(contractType);
	}
	
	public static enum ContractType {
		PURCHASE(),
		LABOUR(),
		WARRANTY(),
		RENTAL_LEASE()
		
		;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static ContractType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private long fromDate = -1;
	private long endDate = -1;
	private long renewalDate = -1;
	
	private long parentId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private List<ContractAssociatedTermsContext> termsAssociated;

	public List<ContractAssociatedTermsContext> getTermsAssociated() {
		return termsAssociated;
	}

	public void setTermsAssociated(List<ContractAssociatedTermsContext> termsAssociated) {
		this.termsAssociated = termsAssociated;
	}
	
	private List<ContractAssociatedAssetsContext> associatedAssets;
	
	public List<ContractAssociatedAssetsContext> getAssociatedAssets() {
		return associatedAssets;
	}

	public void setAssociatedAssets(List<ContractAssociatedAssetsContext> associatedAssets) {
		this.associatedAssets = associatedAssets;
	}
	
	private int paymentInterval;
	private int scheduleDay;
	private int scheduleMonth;
	private long scheduleTime;
	public int getPaymentInterval() {
		return paymentInterval;
	}
	public void setPaymentInterval(int paymentInterval) {
		this.paymentInterval = paymentInterval;
	}
	public int getScheduleDay() {
		return scheduleDay;
	}
	public void setScheduleDay(int scheduleDay) {
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
	
	private long nextPaymentDate;
 
	public long getNextPaymentDate() {
		return nextPaymentDate;
	}

	public void setNextPaymentDate(long nextPaymentDate) {
		this.nextPaymentDate = nextPaymentDate;
	}

	public Long computeNextPaymentDate() {
		if(getFrequencyTypeEnum() != null && getFrequencyType() > 0) {
			ScheduleInfo info = new ScheduleInfo();
			info.setFrequency(getPaymentInterval());
			info.setMonthValue(getScheduleMonth());
			if(getFrequencyTypeEnum().getValue() == FrequencyType.MONTHLY.getValue()) {
				info.setValues(Collections.singletonList(getScheduleDay()));
				info.setFrequencyType(ScheduleInfo.FrequencyType.MONTHLY_DAY);
			}
			else if(getFrequencyTypeEnum().getValue() == FrequencyType.DAILY.getValue()) {
				info.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
			}
			else if(getFrequencyTypeEnum().getValue() == FrequencyType.YEARLY.getValue()) {
				info.setFrequencyType(ScheduleInfo.FrequencyType.YEARLY);
				info.setMonthValue(getScheduleMonth());
				info.setValues(Collections.singletonList(getScheduleDay()));
			}
			long nextPaymentDate = info.nextExecutionTime(System.currentTimeMillis());
			setNextPaymentDate(nextPaymentDate);
		}
		return nextPaymentDate;
		
	}
	
}
