package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.workflow.WorkflowEventContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class EnergyAction extends ActionSupport {
	
	public String addEnergyMeter() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, WorkflowEventContext.EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, energyMeter);
		//energyMeter.setName("test1");
		Chain addAssetChain = FacilioChainFactory.getAddEnergyMeterChain();
		addAssetChain.execute(context);
		setEnergyMeterId(energyMeter.getId());
		
		return SUCCESS;
	}
	
	public String addEnergyMeterPurpose() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, WorkflowEventContext.EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, energyMeterPurpose);
		//energyMeterPurpose.setName("new name 1");
		Chain addAssetChain = FacilioChainFactory.getAddEnergyMeterPurposeChain();
		addAssetChain.execute(context);
		setEnergyMeterPurposeId(energyMeterPurpose.getId());
		
		return SUCCESS;
	}
	
	private EnergyMeterContext energyMeter;
	private EnergyMeterPurposeContext energyMeterPurpose;
	public EnergyMeterPurposeContext getEnergyMeterPurpose() {
		return energyMeterPurpose;
	}
	public void setEnergyMeterPurpose(EnergyMeterPurposeContext energyMeterPurpose) {
		this.energyMeterPurpose = energyMeterPurpose;
	}

	private long energyMeterId;
	private long energyMeterPurposeId;
	public long getEnergyMeterPurposeId() {
		return energyMeterPurposeId;
	}

	public void setEnergyMeterPurposeId(long energyMeterPurposeId) {
		this.energyMeterPurposeId = energyMeterPurposeId;
	}

	public EnergyMeterContext getEnergyMeter() {
		return energyMeter;
	}
	public void setEnergyMeter(EnergyMeterContext energyMeter) {
		this.energyMeter = energyMeter;
	}
	public long getEnergyMeterId() {
		return energyMeterId;
	}
	public void setEnergyMeterId(long energyMeterId) {
		this.energyMeterId = energyMeterId;
	}
}
