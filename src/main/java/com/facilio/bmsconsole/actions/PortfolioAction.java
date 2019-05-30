package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.opensymphony.xwork2.ActionSupport;

public class PortfolioAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(PortfolioAction.class.getName());
	
	@SuppressWarnings("unchecked")
	public String getBuildingMap() throws Exception
	{
		JSONObject result = new JSONObject();
		List<EnergyMeterContext> energyMeters= DeviceAPI.getAllMainEnergyMeters();
		Map <Long, Long> buildingVsMeter= ReportsUtil.getBuildingVsMeter(energyMeters);
		String buildingList=StringUtils.join(buildingVsMeter.keySet(),",");
		List<BuildingContext> buildings=SpaceAPI.getBuildingSpace(buildingList);
		
		for(BuildingContext building:buildings) {

			long buildingId=building.getId();
			String name =building.getName();
			result.put(buildingId,name);
		}
		setReportData(result);
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	
	JSONArray chillerPlantsJson;
	
	public JSONArray getChillerPlantsJson() {
		return chillerPlantsJson;
	}

	public void setChillerPlantsJson(JSONArray chillerPlantsJson) {
		this.chillerPlantsJson = chillerPlantsJson;
	}

	public String getAllChiller() throws Exception {
		
		AssetCategoryContext chillerPlantCategory = AssetsAPI.getCategory("Chiller Plant");
		AssetCategoryContext chillerCategory = AssetsAPI.getCategory("Chiller");
		
		if(chillerPlantCategory != null && chillerCategory != null) {
			List<AssetContext> chillerPlants = AssetsAPI.getAssetListOfCategory(chillerPlantCategory.getId());
			List<AssetContext> chillers = AssetsAPI.getAssetListOfCategory(chillerCategory.getId());
			
			chillerPlantsJson = new JSONArray();
			
			if(chillerPlants != null && !chillerPlants.isEmpty()) {
				
				for(AssetContext chillerPlant :chillerPlants) {
					
					JSONObject plantJson = FieldUtil.getAsJSON(chillerPlant);
					
					JSONArray chillerArray = new JSONArray();
					
					if(chillers != null && !chillers.isEmpty()) {
						
						for(AssetContext chiller :chillers) {
							if(chiller.getParentAssetId() == chillerPlant.getId()) {
								JSONObject chillerJson = FieldUtil.getAsJSON(chiller);
								chillerArray.add(chillerJson);
							}
						}
					}
					
					plantJson.put("childrens", chillerArray);
					
					chillerPlantsJson.add(plantJson);
				}
			}
		}
		
		return SUCCESS;
	}
	
	public String getAllBuildingsWithRootMeter() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule energyMeterModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		FacilioModule energyMeterPurposeModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
		FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(energyMeterModule.getTableName())
		.innerJoin(energyMeterPurposeModule.getTableName()).on("Energy_Meter.PURPOSE_ID = Energy_Meter_Purpose.ID")
		.innerJoin(baseSpaceModule.getTableName()).on("Energy_Meter.PURPOSE_SPACE_ID = BaseSpace.ID")
		.innerJoin(resourceModule.getTableName()).on("BaseSpace.id = Resources.id")
		.innerJoin(resourceModule.getTableName() + " b").on("Energy_Meter.id = b.id");
		
		builder.andCustomWhere("Energy_Meter_Purpose.NAME = \"Main\"");
		builder.andCustomWhere("IS_ROOT = true");
		builder.andCustomWhere("BaseSpace.SPACE_TYPE = ?",BaseSpaceContext.SpaceType.BUILDING.getIntVal());
		builder.andCustomWhere("Resources.SYS_DELETED is null or Resources.SYS_DELETED = false");
		builder.andCustomWhere("Energy_Meter.ORGID = ?", AccountUtil.getCurrentOrg().getId());
		builder.andCustomWhere("b.SYS_DELETED is null or b.SYS_DELETED = false");
		
		builder.select(modBean.getAllFields(baseSpaceModule.getName()));
		
		
		List<Map<String, Object>> props = builder.get();
		
		JSONObject result = new JSONObject();
		result.put("buildingsWithRootMeter", props);
		
		setReportData(result);
		
		return SUCCESS;
	}
	
	public String getAllBuildingsWithRootMeterMeta() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule energyMeterModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		FacilioModule energyMeterPurposeModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
		FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		
		builder.table(energyMeterModule.getTableName())
		.innerJoin(energyMeterPurposeModule.getTableName()).on("Energy_Meter.PURPOSE_ID = Energy_Meter_Purpose.ID")
		.innerJoin(baseSpaceModule.getTableName()).on("Energy_Meter.PURPOSE_SPACE_ID = BaseSpace.ID")
		.innerJoin(resourceModule.getTableName()).on("BaseSpace.id = Resources.id")
		.innerJoin(resourceModule.getTableName() + " b").on("Energy_Meter.id = b.id");
		
		builder.andCustomWhere("Energy_Meter_Purpose.NAME = \"Main\"");
		builder.andCustomWhere("IS_ROOT = true");
		builder.andCustomWhere("BaseSpace.SPACE_TYPE = ?",BaseSpaceContext.SpaceType.BUILDING.getIntVal());
		builder.andCustomWhere("Resources.SYS_DELETED is null or Resources.SYS_DELETED = false");
		builder.andCustomWhere("Energy_Meter.ORGID = ?", AccountUtil.getCurrentOrg().getId());
		builder.andCustomWhere("b.SYS_DELETED is null or b.SYS_DELETED = false");
		
		List<FacilioField> selectfields = new ArrayList<>();
		
		selectfields.addAll(modBean.getAllFields(baseSpaceModule.getName()));
		
		FacilioField meterIdField = FieldFactory.getIdField(energyMeterModule).clone();
		meterIdField.setName("meterId");
		
		selectfields.add(meterIdField);
		
		builder.select(selectfields);
		
		List<Map<String, Object>> props = builder.get();
		
		List<Long> mainMeterList = new ArrayList<Long>();
		for(Map<String, Object> prop :props) {
			mainMeterList.add((Long)prop.get("meterId"));
		}
		
		FacilioModule energyDataModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_DATA_READING);
		Map<String, FacilioField> energyDataFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_DATA_READING));
		
		FacilioField energyField = energyDataFieldMap.get("totalEnergyConsumptionDelta").clone();
		
		selectfields = new ArrayList<>();
		selectfields.add(energyDataFieldMap.get("parentId"));
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select1 = new SelectRecordsBuilder<>();
		select1.select(selectfields);
		select1.aggregate(AggregateOperator.NumberAggregateOperator.SUM, energyField);
		select1.module(energyDataModule)
		.andCondition(CriteriaAPI.getCondition(energyDataFieldMap.get("parentId"), mainMeterList, NumberOperators.EQUALS))
		.andCondition(CriteriaAPI.getCondition(energyDataFieldMap.get("ttime"), DateOperators.CURRENT_MONTH_UPTO_NOW))
		.groupBy("PARENT_METER_ID");
		
		List<Map<String, Object>> propsThisMonth = select1.getAsProps();
		
		Map<Long,Double> thisMeterVsConsumption=ReportsUtil.getMapping(propsThisMonth,"parentId","totalEnergyConsumptionDelta");
		
		long prevMonthStartTime= DateTimeUtil.getMonthStartTime(-1);
		long currentMonthStartTime=DateTimeUtil.getMonthStartTime();
		long currentEndTime=DateTimeUtil.getCurrenTime();
		long previousEndTime=prevMonthStartTime+(currentEndTime-currentMonthStartTime);
		
		select1 = new SelectRecordsBuilder<>();
		select1.select(selectfields);
		select1.aggregate(AggregateOperator.NumberAggregateOperator.SUM, energyField);
		select1.module(energyDataModule)
		.andCondition(CriteriaAPI.getCondition(energyDataFieldMap.get("parentId"), mainMeterList, NumberOperators.EQUALS))
		.andCondition(CriteriaAPI.getCondition(energyDataFieldMap.get("ttime"),prevMonthStartTime+","+previousEndTime ,DateOperators.BETWEEN))
		.groupBy("PARENT_METER_ID");
		
		List<Map<String, Object>> propsLastMonth = select1.getAsProps();
		
		Map<Long,Double> prevMeterVsConsumption=ReportsUtil.getMapping(propsLastMonth,"parentId","totalEnergyConsumptionDelta");
		
		for(Map<String, Object> prop : props) {
			
			Long buildingId = (Long)prop.get("building");
			
			ResourceContext resourceContext = ResourceAPI.getResource(buildingId);
			Long meterId = (Long) prop.get("meterId");
			Double thisMonthKwh = (Double) thisMeterVsConsumption.get(meterId);
			Double lastMonthKwh = (Double) prevMeterVsConsumption.get(meterId);
			
			prop.put("thisMonthKwh", thisMonthKwh);
			prop.put("lastMonthKwh", lastMonthKwh);
			prop.put("avatar", resourceContext.getAvatarUrl());
			
			if(thisMonthKwh != null && thisMonthKwh > 0 && lastMonthKwh != null && lastMonthKwh > 0) {
				double variance= ReportsUtil.getVariance(thisMonthKwh, lastMonthKwh);
				prop.put("variance", variance);
			}
		}
		JSONObject result = new JSONObject();
		result.put("buildingsWithRootMeterMeta", props);
		
		setReportData(result);
			
		return SUCCESS;
	}
	
	public String getAllBuildings() throws Exception 
	{
		JSONObject result = new JSONObject();
		long sitesCount = SpaceAPI.getSitesCount();
		result.put("sitesCount", sitesCount);
		
		List<Long> siteIds = null;
		Map <Long, Long> buildingVsMeter = DeviceAPI.getMainEnergyMeterForAllBuildings(siteIds);
		String deviceList=StringUtils.join(buildingVsMeter.values(),",");
		String buildingList=StringUtils.join(buildingVsMeter.keySet(),",");
		List<BuildingContext> buildings;
		if (!buildingList.isEmpty()) {
			buildings=SpaceAPI.getBuildingSpace(buildingList);
		} else {
			buildings = new ArrayList<>();
		}
		result.put("buildingCount", buildings.size());
		
		long prevStartTime= DateTimeUtil.getMonthStartTime(-1);
		long currentStartTime=DateTimeUtil.getMonthStartTime();
		long endTime=DateTimeUtil.getCurrenTime();
		long previousEndTime=prevStartTime+(endTime-currentStartTime);
		
		List<Map<String, Object>> prevResult = null;
		prevResult = ReportsUtil.fetchMeterData(deviceList,prevStartTime,previousEndTime-1,true);
		
		Map<Long,Double> prevMeterVsConsumption=ReportsUtil.getMeterVsConsumption(prevResult);
		//going for two queries.. so that it will be easy while going for separate queries in case of caching url..
		List<Map<String, Object>> currentResult= ReportsUtil.fetchMeterData(deviceList,currentStartTime,endTime,true);
		LOGGER.severe("currentResult---- "+currentResult);
		Map<Long,Double> currentMeterVsConsumption=ReportsUtil.getMeterVsConsumption(currentResult);
		
		int lastMonthDays= DateTimeUtil.getDaysBetween(prevStartTime,currentStartTime);//sending this month startTime as to time is excluded
		int thisMonthDays=DateTimeUtil.getDaysBetween(currentStartTime,endTime)+1; //adding 1 as it to time is excluded..
		JSONArray buildingArray= new JSONArray ();
		
		for(BuildingContext building:buildings) {
			
			JSONObject buildingData=ReportsUtil.getBuildingData(building);
			long buildingId=building.getId();
			long meterId=buildingVsMeter.get(buildingId);
			Double lastMonthKwh=prevMeterVsConsumption.get(meterId);
			Double thisMonthKwh=currentMeterVsConsumption.get(meterId);
			JSONObject lastMonthData = ReportsUtil.getEnergyData(lastMonthKwh,lastMonthDays);
			buildingData.put("previousVal", lastMonthData);
			LOGGER.severe("thisMonthKwh1 ---- "+thisMonthKwh);
			JSONObject thisMonthData = ReportsUtil.getEnergyData(thisMonthKwh,thisMonthDays);
			LOGGER.severe("thisMonthKwh1 ---- "+thisMonthData);
			buildingData.put("currentVal", thisMonthData);
			double variance= ReportsUtil.getVariance(thisMonthKwh, lastMonthKwh);
			buildingData.put("variance", variance);
			
			List<EnergyMeterContext> mainMeter = DashboardUtil.getMainEnergyMeter(building.getId()+"");
			if(mainMeter != null && !mainMeter.isEmpty()) {
				buildingData.put("rootMeter", mainMeter.get(0));
			}
			List<EnergyMeterContext> meters = DeviceAPI.getRootServiceMeters(building.getId()+"");
			if(meters != null && !meters.isEmpty()) {
				buildingData.put("rootServiceMeters", meters);
			}
			buildingArray.add(buildingData);
		}
		result.put("buildingDetails", buildingArray);
		setReportData(result);
		return SUCCESS;
	}
	
	
		//period - month, year, week, day, yesterday, lastWeek lastMonth or lastYear
		@SuppressWarnings("unchecked")
		public String getConsumption() throws Exception
		{
			JSONObject result = new JSONObject();
			List<EnergyMeterContext> energyMeters= DeviceAPI.getAllMainEnergyMeters();
			Map <Long, Long> buildingVsMeter= ReportsUtil.getBuildingVsMeter(energyMeters);
			String deviceList=StringUtils.join(buildingVsMeter.values(),",");
			
			Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
			long startTime=timeInterval[0];
			long endTime=timeInterval[1];
			
			List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true);
			Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
			
			for(Map.Entry<Long,Long> entry:buildingVsMeter.entrySet()) {
				
				long buildingId=entry.getKey();
				long meterId=entry.getValue();
				Double consumption=meterVsConsumption.get(meterId);
				JSONObject consumptionData = ReportsUtil.getEnergyData(consumption,-1);
				result.put(buildingId,consumptionData);
			}
			setReportData(result);
			return SUCCESS;
		}
		Long meterid;
		
	
	//period -year & lastYear -EUI ranking
	@SuppressWarnings("unchecked")
	public String getBuildingRankings() throws Exception
	{
		JSONObject result = new JSONObject();
		List<EnergyMeterContext> energyMeters= DeviceAPI.getAllMainEnergyMeters();
		Map <Long, Long> buildingVsMeter= ReportsUtil.getBuildingVsMeter(energyMeters);
		String deviceList=StringUtils.join(buildingVsMeter.values(),",");
		String buildingList=StringUtils.join(buildingVsMeter.keySet(),",");
		
		Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
		long startTime=timeInterval[0];
		long endTime=timeInterval[1];
		
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		Map<Long,Double> buildingVsArea= ReportsUtil.getMapping(SpaceAPI.getBuildingArea(buildingList), "ID", "AREA");
		
		Map<Long,Double> euiMap = new LinkedHashMap<Long,Double>();
		
		for(Map.Entry<Long,Long> entry:buildingVsMeter.entrySet()) {
			
			long buildingId=entry.getKey();
			long meterId=entry.getValue();
			Double currentKwh= meterVsConsumption.get(meterId);
			Double buildingArea=buildingVsArea.get(buildingId);
			double eui=ReportsUtil.getEUI(currentKwh,buildingArea);
			if(eui!=0)
			{
				euiMap.put(buildingId, eui);
			}
		}
		if(!euiMap.isEmpty())
		{
			result.put("buildingDetails",ReportsUtil.valueSort(euiMap,false));
			result.put("units", "kBTU/sq.ft");
		}
		setReportData(result);
		return SUCCESS;
	}
	
	
	//period - year/day/week/month
	// use this for Building Consumption Stats & Consumption vs Buildings
	@SuppressWarnings("unchecked")
	public String getConsumptionDetails() throws Exception
	{
		JSONObject result = new JSONObject();
		List<EnergyMeterContext> energyMeters= DeviceAPI.getAllMainEnergyMeters();
		Map <Long, Long> buildingVsMeter= ReportsUtil.getBuildingVsMeter(energyMeters);
		String buildingList=StringUtils.join(buildingVsMeter.keySet(),",");
		List<BuildingContext> buildings=SpaceAPI.getBuildingSpace(buildingList);
		
		String deviceList= StringUtils.join(buildingVsMeter.values(),",");
		Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
		long startTime=timeInterval[0];
		long endTime=timeInterval[1];
		
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		JSONArray buildingArray= new JSONArray ();
		
		for(BuildingContext building:buildings) {
			
			long buildingId=building.getId();
			JSONObject buildingData=ReportsUtil.getBuildingData(building,false);
			long meterId=buildingVsMeter.get(buildingId);
			Double currentKwh=meterVsConsumption.get(meterId);
			JSONObject currentData = new JSONObject();
			currentData.put("consumption",currentKwh);
			String [] costArray=ReportsUtil.getCost(currentKwh);
			if(costArray!=null)
			{
				currentData.put("cost", costArray[0]);
				currentData.put("costUnits", costArray[1]);
			}
			buildingData.put("currentVal", currentData);
			buildingArray.add(buildingData);
		}
		result.put("units","kWh");
		result.put("currency","$");
		result.put("buildingDetails", buildingArray);
		
		setReportData(result);
		return SUCCESS;
	}
	
	// period -day/week/month/year
	@SuppressWarnings("unchecked")
	public String getAllServiceConsumption() throws Exception
	{
		JSONObject result = new JSONObject();
		
		List<EnergyMeterContext> energyMeters= DeviceAPI.getAllServiceMeters();
		StringBuilder deviceBuilder = new StringBuilder();
		HashMap <Long, ArrayList<Long>> purposeVsMeter= new HashMap<Long,ArrayList<Long>>();
		
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long purposeId =emc.getPurpose().getId();
			ArrayList<Long> meterList=purposeVsMeter.get(purposeId);
			if(meterList==null)
			{
				meterList=new ArrayList<Long>();
				purposeVsMeter.put(purposeId, meterList);
			}
			meterList.add(meterId);
			deviceBuilder.append(meterId);
			deviceBuilder.append(",");
		}
		String deviceList= ReportsUtil.removeLastChar(deviceBuilder, ",");
		Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
		long startTime=timeInterval[0];
		long endTime=timeInterval[1];

		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true);
		Map<Long,Double> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData);
		Map<Long,Double> purposeVsConsumption= new HashMap<Long,Double>();
		
		for (Map.Entry<Long,ArrayList<Long>> entry:purposeVsMeter.entrySet())
		{
			Long purposeId=entry.getKey();
			ArrayList<Long> meterList=entry.getValue();
			Double totalKwh=new Double(0);
			for(Long meter:meterList)
			{
				Double value=meterVsConsumption.get(meter);
				if(value!=null)
				{
					totalKwh+=value;
				}
			}
			purposeVsConsumption.put(purposeId, ReportsUtil.roundOff(totalKwh,2));
		}
		result.put("consumption", ReportsUtil.valueSort(purposeVsConsumption, true));
		result.put("units", "kWh");
		setReportData(result);
		return SUCCESS;
	}

	// period -day/week/month/year
	// purpose - id for purposes like LIFT, AHU, Lighting etc..
	@SuppressWarnings("unchecked")
	public String getServiceConsumption() throws Exception
	{
		JSONObject result = new JSONObject();
		List<EnergyMeterContext> energyMeters =DeviceAPI.getEnergyMetersOfPurpose(""+getPurpose(),true);
		Map <Long, Long> buildingVsMeter= ReportsUtil.getBuildingVsMeter(energyMeters);
		String deviceList=StringUtils.join(buildingVsMeter.values(),",");
		
		Long[] timeInterval=ReportsUtil.getTimeInterval(getPeriod());
		long startTime=timeInterval[0];
		long endTime=timeInterval[1];
		
		List<Map<String, Object>> resultData= ReportsUtil.fetchMeterData(deviceList,startTime,endTime,true,true);
		double totalKwh=0;
		if(resultData!=null & !resultData.isEmpty()) {
			Map<String,Object> currentTotal=resultData.remove(resultData.size()-1);
			totalKwh = (double)currentTotal.get("CONSUMPTION");
		}
		
		Map<Long,List<Double>> meterVsConsumption=ReportsUtil.getMeterVsConsumption(resultData,totalKwh);
		for(Map.Entry<Long, Long> entry:buildingVsMeter.entrySet()) {
			
			long meterId=entry.getValue();
			long buildingId=entry.getKey();
			List<Double> consumption=meterVsConsumption.get(meterId);
			if(consumption!=null)
			{
				result.put(buildingId,consumption);
			}
		}
		result.put("units", "kWh");
		result.put("totalKwh", totalKwh);
		setReportData(result);
		return SUCCESS;
	}

	private long purpose=-1;
	public long getPurpose() 
	{
		return purpose;
	}
	
	public void setPurpose(long purpose) 
	{
		this.purpose = purpose;
	}
	
	private String period="day";
	
	public void setPeriod(String period)
	{
		this.period=period;
	}
	
	public String getPeriod()
	{
		return this.period;
	}
	
	private JSONObject reportData = null;
	
	public JSONObject getReportData() {
		return reportData;
	}
	public void setReportData(JSONObject reportData) {
		this.reportData = reportData;		
	}
	
}