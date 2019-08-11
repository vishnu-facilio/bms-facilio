package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.HistoricalLoggerUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class EnergyAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(EnergyAction.class.getName());
	
	
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
		
		if(startTime >= endTime)
		{
			throw new Exception("Start time should be less than the Endtime");
		}
		context.put(FacilioConstants.ContextNames.STARTTIME, startTime);
		context.put(FacilioConstants.ContextNames.ENDTIME, endTime);
		context.put(FacilioConstants.ContextNames.VM_LIST, vmList);
		
		Chain insertVMChain = TransactionChainFactory.getAddHistoricalVMCalculationChain();
		insertVMChain.execute(context);
		
		return SUCCESS;
	}
	
	public String getVirtualMeterList() throws Exception{
		
		List<EnergyMeterContext> virtualMeters = HistoricalLoggerUtil.getVirtualMeterStatusInfo();
		
		setResult("virtualMeters", virtualMeters);
		return SUCCESS;
	}

	public String getvmParentHistoricalLogger() throws Exception {
		
		List<HistoricalLoggerContext> parentHistoricalLoggerList = HistoricalLoggerUtil.getParentHistoricalLogger();
		
		setResult("parentHistoricalLoggers", parentHistoricalLoggerList);
		return SUCCESS;		
	}
	
	private Long parentId;

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
	this.parentId = parentId;
	}

	public String getvmChildHistoricalLogger() throws Exception {
		List<HistoricalLoggerContext> childHistoricalLoggers = HistoricalLoggerUtil.getGroupedHistoricalLogger(getParentId());	
	
		Map<Long,HistoricalLoggerContext> childIdMap = new HashMap<Long, HistoricalLoggerContext>();
		Map<Integer,List<HistoricalLoggerContext>> hierarchyChildMap = new HashMap<Integer, List<HistoricalLoggerContext>>();
	
		if(childHistoricalLoggers != null && !childHistoricalLoggers.isEmpty())
		{
			for(HistoricalLoggerContext historicalLogger: childHistoricalLoggers)
			{
				childIdMap.put(historicalLogger.getId(), historicalLogger);
			}
			
			for(HistoricalLoggerContext historicalLogger: childHistoricalLoggers)
			{
				Integer hierarchy = getHierarchy(historicalLogger, 0, childIdMap);
				if(hierarchyChildMap.containsKey(hierarchy))
				{
					List<HistoricalLoggerContext> groupedChildList = hierarchyChildMap.get(hierarchy);
					groupedChildList.add(historicalLogger);
				}
				else
				{
					List<HistoricalLoggerContext> groupedChildList = new ArrayList<HistoricalLoggerContext>();
					groupedChildList.add(historicalLogger);
					hierarchyChildMap.put(hierarchy, groupedChildList);
				}
			}
		}
	
		setResult("childMeters", hierarchyChildMap);
		return SUCCESS;

	}
	
	public Integer getHierarchy(HistoricalLoggerContext historicalLogger, Integer hierarchy, Map<Long,HistoricalLoggerContext> childIdMap) throws Exception {
		
		hierarchy++;
		
		if(historicalLogger.getDependentId().equals(getParentId()))
		{
			return hierarchy;
		}

		return getHierarchy(childIdMap.get(historicalLogger.getDependentId()), hierarchy, childIdMap);
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
