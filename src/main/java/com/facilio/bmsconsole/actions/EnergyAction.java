package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.HistoricalLoggersWrapper;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.opensymphony.xwork2.ActionSupport;

import nl.basjes.shaded.org.springframework.util.StringUtils;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnergyAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String addEnergyMeter() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
		context.put(FacilioConstants.ContextNames.RECORD, energyMeter);
		//energyMeter.setName("test1");
		Chain addAssetChain = FacilioChainFactory.getAddEnergyMeterChain();
		addAssetChain.execute(context);
		setId(energyMeter.getId());
		
		return SUCCESS;
	}
	
	public String updateEnergyMeter() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		context.put(FacilioConstants.ContextNames.RECORD, energyMeter);
		
		Chain updateChain = FacilioChainFactory.updateEnergyMeterChain();
		updateChain.execute(context);
		
		return SUCCESS;
	}
	
	public String insertVirtualMeterReadings() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.STARTTIME, startTime);
		context.put(FacilioConstants.ContextNames.ENDTIME, endTime);
		context.put(FacilioConstants.ContextNames.INTERVAL, interval);
		context.put(FacilioConstants.ContextNames.VM_LIST, vmList);
		
		Chain insertVMChain = TransactionChainFactory.getAddHistoricalVMCalculationChain();
		insertVMChain.execute(context);
		
		return SUCCESS;
	}

	public String getvmHistoricalLogger() throws Exception {
		
		List<HistoricalLoggerContext> allHistoricalLoggers = HistoricalLoggerUtil.getAllHistoricalLogger();
		
		Map<Long,List<HistoricalLoggerContext>> wrapper = new HashMap<Long, List<HistoricalLoggerContext>>();
		Map<Long,HistoricalLoggerContext> historicalLoggerParentIdMap = new HashMap<Long, HistoricalLoggerContext>();
		
		List<HistoricalLoggersWrapper> resolvedWrapperList = new ArrayList<HistoricalLoggersWrapper>();
		List<HistoricalLoggersWrapper> wrapperList = new ArrayList<HistoricalLoggersWrapper>();
		
		if(allHistoricalLoggers != null && !allHistoricalLoggers.isEmpty())
		{
			for(HistoricalLoggerContext historicalLogger: allHistoricalLoggers)
			{
				if(historicalLogger.getloggerGroupId() == null && historicalLogger.getDependentId() == null)
				{
					wrapper.put(historicalLogger.getId(), new ArrayList<HistoricalLoggerContext>()) ;
					historicalLoggerParentIdMap.put(historicalLogger.getId(), historicalLogger);
				}
				else if(historicalLogger.getloggerGroupId() > 0) {
					
					List<HistoricalLoggerContext> childHistoricalLoggerContexts = wrapper.get(historicalLogger.getloggerGroupId());
					childHistoricalLoggerContexts = childHistoricalLoggerContexts == null 
							? new ArrayList<HistoricalLoggerContext>() : childHistoricalLoggerContexts;
							
					childHistoricalLoggerContexts.add(historicalLogger);
					wrapper.put(historicalLogger.getloggerGroupId(), childHistoricalLoggerContexts) ;
				}
			}	
			
			for(Long parentId : historicalLoggerParentIdMap.keySet()) {
				HistoricalLoggersWrapper logWrapper = new HistoricalLoggersWrapper();
				if(historicalLoggerParentIdMap.get(parentId).getStatus() == 1)
				{
					logWrapper.setHistoricalLoggerParentMeter(historicalLoggerParentIdMap.get(parentId));
					logWrapper.setHistoricalLoggerChildMeters(wrapper.get(parentId));
					wrapperList.add(logWrapper);
				}
				else
				{
					logWrapper.setHistoricalLoggerParentMeter(historicalLoggerParentIdMap.get(parentId));
					logWrapper.setHistoricalLoggerChildMeters(wrapper.get(parentId));
					resolvedWrapperList.add(logWrapper);
				}
			}	
			
			wrapperList.addAll(resolvedWrapperList);
			
		}
		setResult("wrapperList", wrapperList);
		return SUCCESS;		
	}

	
	public String addEnergyMeterPurpose() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
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
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
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
	
	private long startTime;
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	private long endTime;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	private int interval;
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	private List<Long> vmList;
	public List<Long> getVmList() {
		return vmList;
	}
	public void setVmList(List<Long> vmList) {
		this.vmList = vmList;
	}
	
	private boolean runParentMeter = false;
	public boolean getRunParentMeter() {
		return runParentMeter;
	}
	public void setRunParentMeter(boolean runParentMeter) {
		this.runParentMeter = runParentMeter;
	}
	
}
