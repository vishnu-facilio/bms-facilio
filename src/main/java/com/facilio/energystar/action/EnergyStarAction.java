package com.facilio.energystar.action;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.v3.V3Action;

public class EnergyStarAction extends V3Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	long meterId;
	long startTime;
	long endTime;
	
	
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
		
		chain.execute();
		
		FacilioContext context = chain.getContext();
		
		setData(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_CUSTOMER_CONTEXT));
		
		return SUCCESS;
	}
	
	public String addEnergyStarProperty() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.addEnergyStarProperyChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, getPropertyContext());
		
		chain.execute();
		setData(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
	public String updateEnergyStarProperty() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.updateEnergyStarPropertyChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, getPropertyContext());
		
		chain.execute();
		setData(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
	public String deleteEnergyStarProperty() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.updateEnergyStarPropertyChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, getPropertyContext());
		
		chain.execute();
		setData(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT, context.get(EnergyStarUtil.ENERGY_STAR_PROPERTY_CONTEXT));
		
		return SUCCESS;
	}
	
	public String pushHistoricalData() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.pushEnergyStarHistoricalChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(EnergyStarUtil.ENERGY_STAR_METER_ID, getMeterId());
		context.put(FacilioConstants.ContextNames.START_TIME, getStartTime());
		context.put(FacilioConstants.ContextNames.END_TIME, getEndTime());
		
		chain.execute();
		
		return SUCCESS;
	}
	
}
