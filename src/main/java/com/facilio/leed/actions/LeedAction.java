package com.facilio.leed.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.leed.context.ConsumptionInfoContext;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.context.LeedEnergyMeterContext;
import com.facilio.leed.util.LeedAPI;
import com.opensymphony.xwork2.ActionSupport;

public class LeedAction extends ActionSupport {


	private LeedConfigurationContext leedConfigurationContext;
	
	public LeedConfigurationContext getLeedConfigurationContext() 
	{
		return leedConfigurationContext;
	}
	public void setLeedConfigurationContext(LeedConfigurationContext leedConfigurationContext) 
	{
		this.leedConfigurationContext = leedConfigurationContext;
	}
	
	private LeedEnergyMeterContext leedEnergyMeterContext;
	public LeedEnergyMeterContext getLeedEnergyMeterContext() 
	{
		return leedEnergyMeterContext;
	}
	public void setLeedEnergyMeterContext(LeedEnergyMeterContext leedEnergyMeterContext) 
	{
		this.leedEnergyMeterContext = leedEnergyMeterContext;
	}
		
	public String execute() throws Exception
	{
		return null;
	}
	
	private List<LeedConfigurationContext> leedList;
	
	public String leedList() throws Exception
	{
		setLeedList(LeedAPI.getLeedConfigurationList(OrgInfo.getCurrentOrgInfo().getOrgid()));
		return SUCCESS;
	}
	
	public void setLeedList(List<LeedConfigurationContext> leedList)
	{
		this.leedList = leedList;
	}
	
	public List<LeedConfigurationContext> getLeedList()
	{
		return this.leedList;
	}
	
	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewLeedListLayout();
	}

	private long buildingId;
	
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
	}
	
	
	public String meterList() throws Exception
	{
		setMeterList(LeedAPI.fetchMeterListForBuilding(getBuildingId()));
		return SUCCESS;
	}
	
	private List<LeedEnergyMeterContext> meterList;
	
	public void setMeterList(List<LeedEnergyMeterContext> meterList)
	{
		this.meterList = meterList;
	}
	
	public List<LeedEnergyMeterContext> getMeterList()
	{
		return this.meterList;
	}
	
	public String importLeedList() throws Exception
	{
		FacilioContext context = new FacilioContext();
		Chain FetchAssetsFromArcChain = FacilioChainFactory.FetchAssetsFromArcChain();
		FetchAssetsFromArcChain.execute(context);
		leedList();
		return SUCCESS;
	}
	
	public String addLeedConfiguration() throws Exception
	{
		LeedConfigurationContext context = new LeedConfigurationContext();
		context.setId(1);
		context.setLeedId(20000);
		context.setBuildingStatus("Registered");
		context.setLeedScore(55);
		context.setEnergyScore(33);
		context.setWaterScore(15);
		context.setWasteScore(8);
		LeedAPI.addLeedConfiguration(context);
		return SUCCESS;
	}
	public String leedDetails() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BUILDINGID, getBuildingId());
		Chain LeedDetailsPageChain = FacilioChainFactory.LeedDetailsPageChain();
		LeedDetailsPageChain.execute(context);
		LeedConfigurationContext leedConfigurationContext = (LeedConfigurationContext)context.get(FacilioConstants.ContextNames.LEED);
		setLeedConfigurationContext(leedConfigurationContext);
		meterList();
		return SUCCESS;
	}
	
	private long deviceId;
	
	
	public long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}

	private String meterName;
	
	public String getMeterName() {
		return meterName;
	}
	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}
	
	private JSONObject consumptionData;
	
	public JSONObject getConsumptionData()
	{
		return this.consumptionData;
	}
	
	public void setConsumptionData(JSONObject consumptionData)
	{
		this.consumptionData = consumptionData;
	}
	
/*	public String detailspage() throws Exception
	{
		setBuildingId(1);
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.ID, getBuildingId());
		
		Chain getBuildingChain = FacilioChainFactory.getBuildingUtilityProviderDetailsChain();
		
		getBuildingChain.execute(context);
		
		//setBuilding((BuildingContext) context.get(FacilioConstants.ContextNames.BUILDING));
		
		System.out.println(">>>>>>>>>> Comes Here >>>>>>>>>>>");
		
		return SUCCESS;
	}*/
	
	private long leedID;
	
	public long getLeedID()
	{
		return leedID;
	}
	
	public void setLeedID(long leedID)
	{
		this.leedID = leedID;
	}
	
	private String meterID;
	
	public String getMeterID()
	{
		return meterID;
	}
	
	public void setMeterID(String meterID)
	{
		this.meterID = meterID;
	}
	
	private List<ConsumptionInfoContext> energyData;
	
	public List<ConsumptionInfoContext> getEnergyData() 
	{
		return energyData;
	}
	
	public void setEnergyData(List<ConsumptionInfoContext> energyData) 
	{
		this.energyData = energyData;
	}
	
	
	public String addMeter() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BUILDINGID, getBuildingId());
		context.put(FacilioConstants.ContextNames.LEEDID, getLeedID());
		context.put(FacilioConstants.ContextNames.METERNAME,getMeterName());
		Chain addEnergyMeterChain = FacilioChainFactory.AddEnergyMeterChain();
		addEnergyMeterChain.execute(context);
		return SUCCESS;
	}
	
	public String addConsumptionData() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.LEEDID, getLeedID());
		context.put(FacilioConstants.ContextNames.DEVICEID, getDeviceId());
		context.put(FacilioConstants.ContextNames.COMSUMPTIONDATA_LIST, getEnergyData());
		Chain addConsumptionDataChain = FacilioChainFactory.addConsumptionDataChain();
		addConsumptionDataChain.execute(context);
		return SUCCESS;	
	}
	
	public String fetchConsumptionData() throws Exception
	{
		JSONObject consumptionData =  LeedAPI.getConsumptionData(getDeviceId());
		setConsumptionData(consumptionData);		
		return SUCCESS;
	}
	
	/*	private BuildingContext buildingContext;
	public BuildingContext getBuildingContext() 
	{
		return buildingContext;
	}
	public void setBuildingContext(BuildingContext buildingContext) 
	{
		this.buildingContext = buildingContext;
	}

	private long buildingId;
	public long getBuildingId() 
	{
		return buildingId;
	}
	public void setBuildingId(long buildingId) 
	{
		this.buildingId = buildingId;
	}
	*/
}
