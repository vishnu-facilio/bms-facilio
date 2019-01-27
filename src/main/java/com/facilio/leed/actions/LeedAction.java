package com.facilio.leed.actions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.constants.LeedConstants;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.context.ConsumptionInfoContext;
import com.facilio.leed.context.FuelContext;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.context.LeedEnergyMeterContext;
import com.facilio.leed.util.LeedAPI;
import com.facilio.leed.util.LeedIntegrator;
import com.opensymphony.xwork2.ActionSupport;

public class LeedAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private static Logger log = LogManager.getLogger(LeedAction.class.getName());


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
	
	public void setLeedList(List<LeedConfigurationContext> leedList)
	{
		this.leedList = leedList;
	}
	
	public List<LeedConfigurationContext> getLeedList()
	{
		return this.leedList;
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
	
//	public String leedList() throws Exception
//	{
//		if(LeedAPI.checkIfLoginPresent(OrgInfo.getCurrentOrgInfo().getOrgid()))
//		{
//			setIsLoginRequired(true);
//		}
//		else
//		{
//			setIsLoginRequired(false);
//			setLeedList(LeedAPI.getLeedConfigurationList(OrgInfo.getCurrentOrgInfo().getOrgid()));
//		}
//		return SUCCESS;
//	}
	
	@SuppressWarnings("unchecked")
	public String leedList() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(LeedConstants.ContextNames.ORGID, AccountUtil.getCurrentOrg().getOrgId());
		
		Chain fetchLeedListChain = LeedConstants.FetchLeedListChain();
		fetchLeedListChain.execute(context);
		
		setIsLoginRequired((boolean)context.get(LeedConstants.ContextNames.LOGINREQ));
		setLeedList((List<LeedConfigurationContext>)context.get(LeedConstants.ContextNames.LEEDLIST));
		
		return SUCCESS;
	}
	
//	public String arcLoginAction() throws Exception
//	{
//		ArcContext credentials = new ArcContext();
//		credentials.setUserName(getArcUserName());
//		credentials.setPassword(getArcPassword());
//		credentials.setArcProtocol("https");
//		credentials.setArcHost("api.usgbc.org");
//		credentials.setArcPort("443");
//		credentials.setSubscriptionKey("ffa4212a87b748bb8b3623f3d97ae285");
//		LeedIntegrator leedInt = new LeedIntegrator();
//		credentials = leedInt.LoginArcServer(credentials);
//		LeedAPI.AddArcCredential(credentials);
//		importLeedList();
//		return SUCCESS;
//	}
	
	public String arcLoginAction() throws Exception
	{
		ArcContext credentials = new ArcContext();
		credentials.setUserName(getArcUserName());
		credentials.setPassword(getArcPassword());
		credentials.setArcProtocol("https");
		credentials.setArcHost("api.usgbc.org");
		credentials.setArcPort("443");
		credentials.setSubscriptionKey("ffa4212a87b748bb8b3623f3d97ae285");
		FacilioContext context = new FacilioContext();
		context.put(LeedConstants.ContextNames.ARCCONTEXT, credentials);
		
		Chain arcloginChain = LeedConstants.ArcLoginChain();
		arcloginChain.execute(context);
		
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
	
	private String meterType;
	
	public void setMeterType(String meterType)
	{
		this.meterType =  meterType;
	}
	
	public String getMeterType()
	{
		return this.meterType;
	}
	
	public String meterList() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(LeedConstants.ContextNames.BUILDINGID, getBuildingId());
		context.put(LeedConstants.ContextNames.METERTYPE, getMeterType());
		Chain meterListChain = LeedConstants.MeterListChain();
		meterListChain.execute(context);
		List<LeedEnergyMeterContext> meterList  = (List<LeedEnergyMeterContext>)context.get(LeedConstants.ContextNames.METERLIST);
		setMeterList(meterList);
		long leedId = (long)context.get(LeedConstants.ContextNames.LEEDID);
		getConsumptionData(leedId,meterList);
		return SUCCESS;
	}
	
	public String getConsumptionData(long leedId, List<LeedEnergyMeterContext> meterList) throws Exception
	{
		ArcContext arccontext = LeedAPI.getArcContext();
		LeedIntegrator integ = new LeedIntegrator(arccontext);
		syncConsumptionDataWithArc(integ,leedId,meterList);
		return SUCCESS;
	}
/*	public String meterList1() throws Exception
	{
		
		List<LeedEnergyMeterContext> meterList = LeedAPI.fetchMeterListForBuilding(getBuildingId(),getMeterType());
		if(meterList.isEmpty())
		{
			long leedId = LeedAPI.getLeedId(getBuildingId());
			ArcContext arccontext = LeedAPI.getArcContext();
			LeedIntegrator integ = new LeedIntegrator(arccontext);
			JSONObject meterJSON = integ.getMeters(leedId+"");
			JSONObject meterMsgJSON = (JSONObject)meterJSON.get("message");
			
			meterList = getLeedEnergyMeterList(meterMsgJSON);
			LeedAPI.addLeedEnergyMeters(meterList,getBuildingId());
			syncConsumptionDataWithArc(integ,leedId,meterList);
			meterList = LeedAPI.fetchMeterListForBuilding(getBuildingId(),getMeterType());
		}
		
		setMeterList(meterList);
	
		fetchConsumptionData();
		
		return SUCCESS;
	}*/
	
	public void syncConsumptionDataWithArc(LeedIntegrator integ, long leedId, List<LeedEnergyMeterContext> meterList) throws ClientProtocolException, IOException, ParseException, SQLException, RuntimeException
	{
		List<HashMap> dataMapList = new ArrayList();
		Iterator itr = meterList.iterator();
		while(itr.hasNext())
		{
			LeedEnergyMeterContext meter = (LeedEnergyMeterContext)itr.next();
			long meterId = meter.getMeterId();
			long id = meter.getId();
			JSONArray arr = integ.getConsumptionListAsArray(leedId+"", meterId+"");
			System.out.println(">>>arr :"+arr);
			Iterator jitr = arr.iterator();
			while(jitr.hasNext())
			{
				JSONObject msgobj = (JSONObject)jitr.next();
				JSONObject obj = (JSONObject)msgobj.get("message");
				JSONArray resArr = (JSONArray)obj.get("results");
				for(int i = 0; i < resArr.size(); i++)
				{
					JSONObject conObj = (JSONObject)resArr.get(i);
					long consumptionId = (long)conObj.get("id");
					String stDate = (String)conObj.get("start_date");
					String enDate = (String)conObj.get("end_date");
					double consumption = (double)conObj.get("reading");

					long stDateLong = DateTimeUtil.getTime(stDate);
					long enDateLong = DateTimeUtil.getTime(enDate);
					HashMap<String, Object> endTimeData = DateTimeUtil.getTimeData(enDateLong);
					
					HashMap dataMap = new HashMap();
					dataMap.put("id", id);
					dataMap.put("endTime", enDateLong);
					dataMap.put("consumption", consumption);
					dataMap.put("timeData", endTimeData);
					dataMap.put("consumptionId", consumptionId);
					dataMap.put("startTime", stDateLong);
					dataMapList.add(dataMap);
					
				}
			}
		}
		LeedAPI.addConsumptionData(dataMapList);
	}
	
	public List<LeedEnergyMeterContext> getLeedEnergyMeterList(JSONObject meterJSON)
	{
		List<LeedEnergyMeterContext> meterList = new ArrayList();
		List<String> reqArray = new ArrayList();
		reqArray.add("operating_hours");
		reqArray.add("gross_area");
		reqArray.add("occupancy");
		JSONArray meterArr = (JSONArray)meterJSON.get("results");
		Iterator itr = meterArr.iterator();
		while(itr.hasNext())
		{
			JSONObject mJSON = (JSONObject)itr.next();
			JSONObject fuel_type = (JSONObject)mJSON.get("fuel_type");
			String fuelKind = (String)fuel_type.get("kind");
			if(reqArray.contains(fuelKind))
			{
				continue;
			}
			long meterId = (long)mJSON.get("id");
			String meterName = (String)mJSON.get("name");
			mJSON.get("type");
			LeedEnergyMeterContext context = new LeedEnergyMeterContext();
			context.setName(meterName);
			context.setMeterId(meterId);
//			context.setFuelType(fuelType);
//			context.setTypeVal(fuelKind);
			meterList.add(context);
		}
		return meterList;
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
			log.info("Exception occurred ", e);
		}
		return SUCCESS;
	}
	
	private FacilioContext addEnergyMeter() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(LeedConstants.ContextNames.BUILDINGID, getBuildingId());
		context.put(LeedConstants.ContextNames.LEEDID, getLeedID());
		context.put(LeedConstants.ContextNames.METERNAME,getMeterName());
		context.put(LeedConstants.ContextNames.METERTYPE,getMeterType());
		
		String meterType = getMeterType();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		FuelContext fcontext = new FuelContext();
		LeedEnergyMeterContext leedEnergyMeterContext = new LeedEnergyMeterContext();
		if(meterType.equalsIgnoreCase("electricity"))
		{
			fcontext.setFuelId(16L);
			fcontext.setFuelType("AZNM");
			fcontext.setSubType("WECC Southwest");
			fcontext.setKind("electricity");
			fcontext.setResource("Non-Renewable");
			leedEnergyMeterContext.setServiceProvider("AZNM");
			leedEnergyMeterContext.setUnit("KWH");
		}
		else if(meterType.equalsIgnoreCase("water"))
		{
			fcontext.setFuelId(47L);
			fcontext.setFuelType("Municipality supplied potable water");
			fcontext.setSubType("");
			fcontext.setKind("water");
			fcontext.setResource("Non-Reclaimed");
			leedEnergyMeterContext.setServiceProvider("Municipality supplied potable water");
			leedEnergyMeterContext.setUnit("GAL");
		}
		
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getId());
		
		leedEnergyMeterContext.setFuelContext(fcontext);
		leedEnergyMeterContext.setOrgId(orgId);
		leedEnergyMeterContext.setContactPerson(superAdmin.getName());
		leedEnergyMeterContext.setContactEmail(superAdmin.getEmail());
		leedEnergyMeterContext.setName(getMeterName());
		
		context.put(LeedConstants.ContextNames.LEEDMETERCONTEXT, leedEnergyMeterContext);
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
		context.put(FacilioConstants.ContextNames.METERTYPE, getMeterType());
		Chain addConsumptionDataChain = FacilioChainFactory.addConsumptionDataChain();
		addConsumptionDataChain.execute(context);
		meterList();
		JSONArray consumptionArray =  LeedAPI.getConsumptionData((long)context.get(FacilioConstants.ContextNames.DEVICEID));
		setConsumptionArray(consumptionArray);	
		updateLeedConfiguration();
		//leedDetails();
		
		return SUCCESS;	
	}
	
	public String updateLeedConfiguration() throws Exception
	{
		long buildingId = getBuildingId();
		long leedId = LeedAPI.getLeedId(buildingId);
		long energyScore = 0;
		long waterScore = 0;
		long wasteScore = 0;
		long humanExperienceScore = 0;
		long transportScore = 0;
		long baseScore = 0;
		long totalScore = 0;		
		new HashMap();
		ArcContext arccontext = LeedAPI.getArcContext();
		LeedIntegrator integ = new LeedIntegrator(arccontext);
		JSONObject response = integ.getPerformanceScores(leedId+"");
		JSONObject scoMsg = (JSONObject)response.get("message");
		String errResult = (String)scoMsg.get("result");
		if(errResult != null && errResult.equalsIgnoreCase("No result found."))
		{
		}
		else
		{
		JSONObject scores = (JSONObject)scoMsg.get("scores");
		System.out.println("LeedAction.updateLeedConfiguration().scores :"+scores);
		
		if(scores.get("energy") != null)
		{
			energyScore = (long)scores.get("energy");
		}
		
		if(scores.get("water") != null)
		{
			waterScore = (long)scores.get("water");
		}
		
		if(scores.get("waste") != null)
		{
			wasteScore = (long)scores.get("waste");
		}
		
		if(scores.get("human_experience") != null)
		{
			humanExperienceScore = (long)scores.get("human_experience");
		}
		
		if(scores.get("transport") != null)
		{
			transportScore = (long)scores.get("transport");
		}
		
		if(scores.get("base") != null)
		{
			baseScore = (long)scores.get("base");
		}
		totalScore = energyScore+waterScore+wasteScore+humanExperienceScore+transportScore+baseScore;
		LeedAPI.updateLeedScores(leedId, totalScore, energyScore, waterScore, wasteScore, transportScore, humanExperienceScore);
		}		
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
