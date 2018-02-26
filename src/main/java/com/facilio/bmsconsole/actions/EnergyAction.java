package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class EnergyAction extends ActionSupport {
	
	public String addEnergyMeter() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, energyMeter);
		
		context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
		context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, AssetsAPI.getCategory("Energy Meter").getId());
		//energyMeter.setName("test1");
		Chain addAssetChain = FacilioChainFactory.getAddEnergyMeterChain();
		addAssetChain.execute(context);
		setId(energyMeter.getId());
		
		return SUCCESS;
	}
	
	public String addEnergyMeterPurpose() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, energyMeterPurpose);
		//energyMeterPurpose.setName("new name 1");
		Chain addAssetChain = FacilioChainFactory.getAddEnergyMeterPurposeChain();
		addAssetChain.execute(context);
		setId(energyMeterPurpose.getId());
		
		return SUCCESS;
	}
	
public String addEnergyData() throws Exception { 
		
		FacilioContext context = new FacilioContext();
		
		System.out.println("EnergyDataProps payleoddd --- "+payload);
		
		Map<String, Object> EnergyDataProps = new HashMap<String, Object>();
		EnergyDataProps.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		EnergyDataProps.put("moduleId", 29);
		EnergyDataProps.put("ttime", System.currentTimeMillis());
		EnergyDataProps.put("parentId", payload.get("meterId"));
		EnergyDataProps.put("totalEnergyConsumption",Double.parseDouble(payload.get("totalEnergyConsumption").toString()));
		
		ReadingContext reading = FieldUtil.getAsBeanFromMap(EnergyDataProps, ReadingContext.class);
		
		ModuleBaseWithCustomFields moduleBaseWithCustomFields = new ModuleBaseWithCustomFields();
		
		moduleBaseWithCustomFields.addData(EnergyDataProps);
		
		
		context.put(FacilioConstants.ContextNames.RECORD, reading);
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "energydata");
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Energy_Data");
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.CREATE);
		Chain getAddEnergyDataChain = FacilioChainFactory.getAddEnergyDataChain();
		getAddEnergyDataChain.execute(context);
		
		return SUCCESS;
	}
	private JSONObject payload;
	public JSONObject getPayload() {
		return payload;
	}
	public void setPayload(JSONObject payload) {
		this.payload = payload;
	}

	
	public String getVirtualMeterChildren() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		
		Chain virtualMeterChain = FacilioChainFactory.getVirtualMeterChildrenChain();
		virtualMeterChain.execute(context);
		
		setEnergyMeters((List<EnergyMeterContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
		return SUCCESS;
	}
	
	public String getEnergyMeterList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		
		Chain getEnergyMeterListChain = FacilioChainFactory.getEnergyMeterListChain();
		getEnergyMeterListChain.execute(context);
		
		setEnergyMeters((List<EnergyMeterContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST));
		
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

	public EnergyMeterContext getEnergyMeter() {
		return energyMeter;
	}
	public void setEnergyMeter(EnergyMeterContext energyMeter) {
		this.energyMeter = energyMeter;
	}
	
	private List<EnergyMeterContext> energyMeters;
	public List<EnergyMeterContext> getEnergyMeters() {
		return energyMeters;
	}
	public void setEnergyMeters(List<EnergyMeterContext> energyMeters) {
		this.energyMeters = energyMeters;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
}
