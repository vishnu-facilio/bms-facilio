package com.facilio.leed.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.context.ConsumptionInfoContext;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.context.LeedEnergyMeterContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;
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
	
	private boolean isLoginRequired;
	
	public boolean getIsLoginRequired()
	{
		return this.isLoginRequired;
	}
	
	public void setIsLoginRequired(boolean isLoginRequired)
	{
		this.isLoginRequired = isLoginRequired;
	}
	
	private List<LeedConfigurationContext> leedList;
	
	public String leedList() throws Exception
	{
		if(LeedAPI.checkIfLoginPresent(OrgInfo.getCurrentOrgInfo().getOrgid()))
		{
			setIsLoginRequired(true);
		}
		else
		{
			setIsLoginRequired(false);
			setLeedList(LeedAPI.getLeedConfigurationList(OrgInfo.getCurrentOrgInfo().getOrgid()));
		}
		return SUCCESS;
	}
	private String arcUserName;
	private String arcPassword;
	
	public void setArcUserName(String arcUserName)
	{
		this.arcUserName =  arcUserName;
	}
	
	public String getArcUserName()
	{
		return this.arcUserName;
	}
	
	public void setArcPassword(String arcPassword)
	{
		this.arcPassword =  arcPassword;
	}
	
	public String getArcPassword()
	{
		return this.arcPassword;
	}
	
	public String arcLoginAction() throws Exception
	{
		ArcContext credentials = new ArcContext();
		credentials.setUserName(getArcUserName());
		credentials.setPassword(getArcPassword());
		credentials.setArcProtocol("https");
		credentials.setArcHost("api.usgbc.org");
		credentials.setArcPort("443");
		credentials.setSubscriptionKey("ffa4212a87b748bb8b3623f3d97ae285");
		LeedIntegrator leedInt = new LeedIntegrator();
		credentials = leedInt.LoginArcServer(credentials);
		LeedAPI.AddArcCredential(credentials);
		importLeedList();
		return SUCCESS;
	}
	
	public String importLeedList() throws Exception
	{
		FacilioContext context = new FacilioContext();
		Chain FetchAssetsFromArcChain = FacilioChainFactory.FetchAssetsFromArcChain();
		FetchAssetsFromArcChain.execute(context);
		leedList();
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
	
	private JSONArray consumptionArray;
	
	public JSONArray getConsumptionArray()
	{
		return this.consumptionArray;
	}
	
	public void setConsumptionArray(JSONArray consumptionArray)
	{
		this.consumptionArray = consumptionArray;
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
	
	private List<ConsumptionInfoContext> consumptionJSONArray;
	
	public List<ConsumptionInfoContext> getConsumptionJSONArray() 
	{
		return consumptionJSONArray;
	}
	
	public void setConsumptionJSONArray(List<ConsumptionInfoContext> consumptionJSONArray) 
	{
		this.consumptionJSONArray = consumptionJSONArray;
	}
	
	public String addSample() throws Exception
	{
		long deviceId = 1;
		List<HashMap> dataMapList = new ArrayList();
		
		List<JSONObject> list = new ArrayList();
		
		JSONObject obj1 = new JSONObject();
		obj1.put("st", "2017-07-11T00:00:00Z");
		obj1.put("en", "2017-08-12T00:00:00Z");
		obj1.put("data", 12567.0);
		obj1.put("consumpId", 621049L);
		
		
		
		list.add(obj1);
				
		Iterator data = list.iterator();
		while(data.hasNext())
		{
			JSONObject obj = (JSONObject)data.next();
			String stDate = (String)obj.get("st");
			String enDate = (String)obj.get("en");
			double consumption = (double)obj.get("data");
			long consumptionId = (long)obj.get("consumpId");
			long stDateLong = DateTimeUtil.getTime(stDate,true);
			long enDateLong = DateTimeUtil.getTime(enDate,true);
			HashMap<String, Object> endTimeData = DateTimeUtil.getTimeData(enDateLong);
			
			HashMap dataMap = new HashMap();
			dataMap.put("deviceId", deviceId);
			dataMap.put("endTime", enDateLong);
			dataMap.put("consumption", consumption);
			dataMap.put("timeData", endTimeData);
			dataMap.put("consumptionId", consumptionId);
			dataMap.put("startTime", stDateLong);
			dataMapList.add(dataMap);
		}
		try{
		LeedAPI.addConsumptionData(dataMapList);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	private FacilioContext addEnergyMeter() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.BUILDINGID, getBuildingId());
		context.put(FacilioConstants.ContextNames.LEEDID, getLeedID());
		context.put(FacilioConstants.ContextNames.METERNAME,getMeterName());
		Chain addEnergyMeterChain = FacilioChainFactory.AddEnergyMeterChain();
		addEnergyMeterChain.execute(context);
		
		return context;
	}
	
	public String addMeter() throws Exception
	{
		addEnergyMeter();
		return SUCCESS;
	}
	
	public String AddMeterNConsumptionData() throws Exception
	{
		FacilioContext context = addEnergyMeter();
		context.put(FacilioConstants.ContextNames.COMSUMPTIONDATA_LIST, getConsumptionJSONArray());
		Chain addConsumptionDataChain = FacilioChainFactory.addConsumptionDataChain();
		addConsumptionDataChain.execute(context);
		meterList();
		JSONArray consumptionArray =  LeedAPI.getConsumptionData((long)context.get(FacilioConstants.ContextNames.DEVICEID));
		setConsumptionArray(consumptionArray);	
		return SUCCESS;	
	}
	
	
	
	public String AddConsumptionData() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.LEEDID, getLeedID());
		context.put(FacilioConstants.ContextNames.DEVICEID, getDeviceId());
		context.put(FacilioConstants.ContextNames.COMSUMPTIONDATA_LIST, getConsumptionJSONArray());
		Chain addConsumptionDataChain = FacilioChainFactory.addConsumptionDataChain();
		addConsumptionDataChain.execute(context);
		fetchConsumptionData();
		return SUCCESS;	
	}
	
	public String fetchConsumptionData() throws Exception
	{
		JSONArray consumptionArray =  LeedAPI.getConsumptionData(getDeviceId());
		setConsumptionArray(consumptionArray);		
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
