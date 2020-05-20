package com.facilio.energystar.action;

import java.util.List;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.time.DateRange;

public class EnergyStarAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	long meterId;
	long propertyId;
	long startTime;
	long endTime;
	boolean createAccount;
	List<EnergyStarMeterDataContext> meterData;
	String fieldName;
	
	
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public List<EnergyStarMeterDataContext> getMeterData() {
		return meterData;
	}
	public long getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(long propertyId) {
		this.propertyId = propertyId;
	}
	public void setMeterData(List<EnergyStarMeterDataContext> meterData) {
		this.meterData = meterData;
	}

	public boolean isCreateAccount() {
		return createAccount;
	}

	public void setCreateAccount(boolean createAccount) {
		this.createAccount = createAccount;
	}

	public long getMeterId() {
		return meterId;
	}

	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	EnergyStarPropertyContext propertyContext;
	
	
	public EnergyStarPropertyContext getPropertyContext() {
		return propertyContext;
	}


	public void setPropertyContext(EnergyStarPropertyContext propertyContext) {
		this.propertyContext = propertyContext;
	}


	public String enable() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.enableEnergyStarChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.IS_CREATE_ACCOUNT, isCreateAccount());
		
		chain.execute();
		
		setResult(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT));
		
		return SUCCESS;
	}
	
	public String confirmAccountShare() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.confirmESAccountShareChain();
		
		FacilioContext context = chain.getContext();
		
		chain.execute();
		
		setResult(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT));
		
		return SUCCESS;
	}
	
	public String confirmPendingShares() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.confirmPendingSharesChain();
		
		FacilioContext context = chain.getContext();
		
		chain.execute();
		
		setResult(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT));
		
		return SUCCESS;
	}
	
	public String addEnergyStarProperty() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.addEnergyStarProperyChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, getPropertyContext());
		
		chain.execute();
		setResult(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
	public String updateEnergyStarProperty() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.updateEnergyStarPropertyChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, getPropertyContext());
		
		chain.execute();
		setResult(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
	public String deleteEnergyStarProperty() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.updateEnergyStarPropertyChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, getPropertyContext());
		
		chain.execute();
		setResult(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
	public String addUtilityData() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.addEnergyStarUtilityDataChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_METER_DATA_CONTEXTS, getMeterData());
		
		chain.execute();
		setResult(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
	public String pushHistoricalData() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.addPushESHistoricalDataJobChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_METER_ID, getMeterId());
		context.put(FacilioConstants.ContextNames.START_TIME, getStartTime());
		context.put(FacilioConstants.ContextNames.END_TIME, getEndTime());
		
		chain.execute();
		
		return SUCCESS;
	}
	
	public String fetchHistoricalData() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.addFetchESHistoricalDataJobChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_ID, getPropertyId());
		context.put(FacilioConstants.ContextNames.START_TIME, getStartTime());
		context.put(FacilioConstants.ContextNames.END_TIME, getEndTime());
		
		chain.execute();
		
		return SUCCESS;
	}
	
	public String fetchMainSummaryData() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getESfetchMainSummaryData();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_FIELD_NAME, getFieldName());
		
		chain.execute();
		
		setResult(EnergyStarUtil.ENERGY_STAR_PROPERTIES_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTIES_CONTEXT));
		
		return SUCCESS;
	}
	
	public String fetchPropertyEnergyData() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getESfetchPropertyEnergyData();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_ID, getPropertyId());
		
		chain.execute();
		
		setResult(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
	public String fetchPropertyMetricsData() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getESfetchPropertyMetricData();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_ID, getPropertyId());
		if(getStartTime() > 0 && getEndTime() > 0) {
			context.put(FacilioConstants.ContextNames.DATE_RANGE, new DateRange(getStartTime(),getEndTime()));
		}
		
		chain.execute();
		
		setResult(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
}
