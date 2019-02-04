package com.facilio.bmsconsole.reports;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.ReportFolderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.google.gson.Gson;

public class ReportsUtil 
{
	private static Logger logger = Logger.getLogger("ReportsUtil");
	private static org.apache.log4j.Logger log = LogManager.getLogger(ReportsUtil.class.getName());

	public static final double conversionMultiplier=3.412;
	public static double getVariance(Double currentVal, Double previousVal)
	{
		if(currentVal==null || currentVal==0 || previousVal==null || previousVal==0)
		{
			return 0;
		}
		double variance =(currentVal - previousVal)/previousVal;
		variance=variance*100;
		return roundOff(variance, 2);
	}
	
	private static double unitCost=0.41;
	
	public static double getUnitCost(long orgid)
	{
		try {
			Map<String, Object> unitCostMap = CommonCommandUtil.getOrgInfo(orgid, "unitCost");
			if (unitCostMap != null && unitCostMap.get("value") != null && !"".equals(unitCostMap.get("value").toString())) {
				return Double.parseDouble(unitCostMap.get("value").toString().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (orgid == 60) {
			return 5;
		}
		return unitCost;
	}

	
	public static String[] energyUnitConverter(Double value)
	{
		long longVal=value.longValue();
		long length=(long)Math.log10(longVal)+1;
		String units="kWh";
		if(length>4)
		{
			//converting kilo to mega
			value=value/1000;
			units="MWh";
		}
		String[] result= new String[2];
		result[0] =""+roundOff(value, 2);
		result[1] =units;
		return result;
	}
	
	public static double roundOff(double value, int decimalDigits)
	{
		double multiplier =Math.pow(10, decimalDigits);;
		return Math.round(value*multiplier)/ multiplier ;
	}
	
	public static String[] getCost(Double kWh)
	{
		if(kWh==null || kWh==0)
		{
			return null;
		}
		//later we need to calculate based on slab..
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
	
		
		return costConverter(kWh*getUnitCost(orgId));
	}
	
	
	public static List<Integer> getDateList(String dateString) {
		
		String[] value= dateString.split("-");
		int length=value.length;
		int day=01;int month=01; int year=2018;
		try {
		//if the input format is dd-mm-yyyy the day will be the first value
		//if the input format is mm-yyyy the day will be the same as month to avoid ArrayIndexOB exception.
		day=Integer.parseInt(value[0]);
		month=Integer.parseInt(value[length-2]);
		year=Integer.parseInt(value[length-1]);
		}
		catch(Exception e) {
			//if the input format is invalid.. setting the default value to 1-1-2018
			log.info("Exception occurred ", e);
		}
		List<Integer> dateList=new ArrayList<Integer>();
		dateList.add(day);
		dateList.add(month);
		dateList.add(year);
		return dateList;
	}
	
	public static Long[] getTimeInterval(String duration)
	{
		//defaulting to "today"
		long startTime=DateTimeUtil.getDayStartTime();
		long endTime=DateTimeUtil.getCurrenTime();

		if (duration.equalsIgnoreCase("yesterday"))
		{
			startTime=DateTimeUtil.getDayStartTime(-1);
			endTime=DateTimeUtil.getDayStartTime() -1;
		}
		else if (duration.equalsIgnoreCase("week"))
		{
			startTime=DateTimeUtil.getWeekStartTime();	
		}
		else if (duration.equalsIgnoreCase("lastWeek"))
		{
			startTime=DateTimeUtil.getWeekStartTime(-1);	
			endTime=DateTimeUtil.getWeekStartTime() -1;	
		}
		else if (duration.equalsIgnoreCase("month"))
		{
			startTime=DateTimeUtil.getMonthStartTime();
		}
		else if (duration.equalsIgnoreCase("lastMonth"))
		{
			startTime=DateTimeUtil.getMonthStartTime(-1);
			endTime=DateTimeUtil.getMonthStartTime()-1;
		}
		else if (duration.equalsIgnoreCase("year"))
		{
			startTime=DateTimeUtil.getYearStartTime();
		}
		else if (duration.equalsIgnoreCase("lastYear"))
		{
			startTime=DateTimeUtil.getYearStartTime(-1);
			endTime=DateTimeUtil.getYearStartTime()-1;
		}
		Long[] timeIntervals= new Long[2];
		timeIntervals[0]=startTime;
		timeIntervals[1]=endTime;
		return timeIntervals;
	}
	
	public static String[] costConverter(Double value)
	{

		long longVal=value.longValue();
		long length=(long)Math.log10(longVal)+1;
		int divider=1;
		String units="";
		if(length>6)
		{
			divider=1000000;
			units=" M";
		}
		else if(length>4)
		{
			divider=1000;
			units=" K";
		}
		double finalValue=value/divider;
		String[] result= new String[2];
		result[0] =""+roundOff(finalValue, 2);
		result[1] =units;
		return result;
	}
	
	
	public static FacilioField getEnergyField() {
		FacilioField energyFld = new FacilioField();
		energyFld.setName("CONSUMPTION");
		energyFld.setColumnName("ROUND(SUM(TOTAL_ENERGY_CONSUMPTION_DELTA),2)");
		energyFld.setDataType(FieldType.DECIMAL);
		return energyFld;
	}

	public static FacilioField getField(String name, String colName, FieldType type) {
		FacilioField energyFld = new FacilioField();
		energyFld.setName(name);
		energyFld.setColumnName(colName);
		energyFld.setDataType(type);
		return energyFld;
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> valueSort(Map<K, V> map, boolean descending) {
		   
		Stream<Entry<K,V>> stream= map.entrySet().stream();
		
		if(descending){
			
			stream =stream.sorted(Map.Entry.comparingByValue(Collections.reverseOrder()));
		}
		else {
			stream=stream.sorted(Map.Entry.comparingByValue());	
		}
		
		return stream.collect(Collectors.toMap(
                Map.Entry::getKey, 
                Map.Entry::getValue, 
                (e1, e2) -> e1, 
                LinkedHashMap::new
              ));
	}
	
	public static String removeLastChar(StringBuilder builder, String deleteChar)
	{
		int index=builder.lastIndexOf(deleteChar);
		if(index==-1)
		{
			return builder.toString();
		}
		return builder.deleteCharAt(index).toString();
	}
	
	
	public static JSONObject getBuildingData(BuildingContext building)
	{
		return getBuildingData(building,true);
	}
	
	
	@SuppressWarnings("unchecked")
	public static JSONObject getBuildingData(BuildingContext building, boolean fetchLocation)
	{
		JSONObject buildingData= new JSONObject();
		buildingData.put("name", building.getName());
		buildingData.put("id", building.getId());
		buildingData.put("area", building.getGrossFloorArea());
		buildingData.put("floors", building.getNoOfFloors());
		buildingData.put("siteId", building.getSiteId());
		
		try {
			if(building.getPhotoId() <= 0) {
				
				List<PhotosContext> photos = SpaceAPI.getBaseSpacePhotos(building.getId());
				
				if(photos != null && !photos.isEmpty()) {
					building.setPhotoId(photos.get(0).getPhotoId());
				}
			}
			
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		
		buildingData.put("photoid", building.getPhotoId());
		if(!fetchLocation)
		{
			return buildingData;
		}
		
		try{
			String avatarUrl=building.getAvatarUrl();
			buildingData.put("avatar",avatarUrl);
			LocationContext location=building.getLocation();
			if(location!=null)
			{
				location=SpaceAPI.getLocationSpace(building.getLocation().getId());
				buildingData.put("city", location.getCity());
				buildingData.put("street",location.getStreet());
				buildingData.put("latitude",location.getLat());
				buildingData.put("longitude",location.getLng());
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		return buildingData;
	}
	
	
	@SuppressWarnings("unchecked")
	public static JSONObject getEnergyData(Double kwh,int days)
	{
		JSONObject data = new JSONObject();
		if(kwh==null || kwh==0)
		{
			return data;
		}
		String[] consumptionArray=ReportsUtil.energyUnitConverter(kwh);
		data.put("consumption",consumptionArray[0]);
		data.put("units",consumptionArray[1]);
		data.put("currency","AED");
		data.put("days", days);
		
		String [] costArray=ReportsUtil.getCost(kwh);
		data.put("cost", costArray[0]);
		data.put("costUnits", costArray[1]);
		return data;
	}
	
	
	@SuppressWarnings("unchecked")
	public static JSONObject getBuildingMap() 
	{
		
		try {
			List<BuildingContext> buildings=SpaceAPI.getAllBuildings();
			JSONObject result = new JSONObject();
			for(BuildingContext building:buildings) {
				long spaceId=building.getId();
				String name=building.getName();
				result.put(spaceId,name);
			}
			return result;
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	public static Map<Long, Long> getBuildingVsMeter(List<EnergyMeterContext> energyMeters)
	{
	
		Map <Long, Long> buildingVsMeter= new HashMap<Long,Long>();
		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long buildingId =emc.getPurposeSpace().getId();
			//under the assumption only one main meter for a building.. 
			//when there are more than 1 main meter, there should be one virtual meter which will be the main meter..
			buildingVsMeter.put(buildingId,meterId);
		}
		return buildingVsMeter;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject getPurposeMapping() 
	{
		JSONObject result = new JSONObject();
		try {
		List<EnergyMeterPurposeContext> purposeList= DeviceAPI.getAllPurposes();
		for(EnergyMeterPurposeContext emc:purposeList) {
			result.put(emc.getId(),emc.getName());
		}
		return result;
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	public static Map<Long, Long> getMeterVsPurpose(List<EnergyMeterContext> energyMeters)
	{
		Map <Long,Long> meterVsPurpose= new HashMap<Long,Long>();

		for(EnergyMeterContext emc:energyMeters) {

			long meterId=emc.getId();
			long purposeId =emc.getPurpose().getId();
			meterVsPurpose.put(meterId,purposeId);
		}
		return meterVsPurpose;
	}
	
	public static double getEUI(Double currentKwh, Double buildingArea) throws Exception {

		if(currentKwh==null || currentKwh==0 || buildingArea==null || buildingArea==0)
		{
			return 0;
		}
		
		Unit displayUnit = UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getId(), Metric.AREA);
		buildingArea = UnitsUtil.convert(buildingArea, Unit.SQUARE_FOOT, displayUnit);
		
		double eui= currentKwh/buildingArea;
		return roundOff(eui, 2);
	}
	
	public static List<Map<String, Object>> fetchMeterData(String deviceList,long startTime, long endTime)
	{
		return fetchMeterData(deviceList,startTime,endTime,false);
	}
	
	public static List<Map<String, Object>> fetchMeterData(String deviceList,long startTime, long endTime, boolean org)
	{
		return fetchMeterData(deviceList,startTime,endTime, org,false);
	}
	public static List<Map<String, Object>> fetchMeterData(String deviceList,long startTime, long endTime, boolean org,boolean rollUp)
	{
		List<Map<String, Object>> result=null;
		
		if (deviceList.trim().isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		
		FacilioField energyFld = ReportsUtil.getEnergyField();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(energyFld);
		StringBuilder groupBy=new StringBuilder();
		if(org)
		{
			FacilioField meterFld = ReportsUtil.getField("Meter_ID","PARENT_METER_ID",FieldType.NUMBER);
			fields.add(meterFld);
			groupBy.append(meterFld.getCompleteColumnName());
			if(rollUp)
			{
				groupBy.append(" WITH ROLLUP");
			}
		}
		
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Energy_Data")
                .andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("TTIME between ? AND ?",startTime,endTime)
				.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", deviceList, NumberOperators.EQUALS))
				.groupBy(groupBy.toString());
		try {
			result = builder.get();
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return result;	
	}
	
	public static Map<Long,Double> getMeterVsConsumption(List<Map<String, Object>> result)
	{
		return getMapping(result,"Meter_ID","CONSUMPTION");
	}
	
	public static Map<Long,Double> getMapping(List<Map<String, Object>> result,String key,String value)
	{
		Map<Long,Double> keyVsValue = new HashMap<Long,Double>();
		for(Map<String,Object> rowData: result)
		{
			Long meterId=(Long)	rowData.get(key);
			Double consumption=(Double) rowData.get(value);
			keyVsValue.put(meterId, consumption);
		}
		return keyVsValue;
	}
	
	public static Map<Long,List<Double>> getMeterVsConsumption(List<Map<String, Object>> result, double totalKwh)
	{
		Map<Long,List<Double>> meterConsumption = new HashMap<Long,List<Double>>();
		for(Map<String,Object> rowData: result)
		{
			Long meterId=(Long)	rowData.get("Meter_ID");
			Double consumption=(Double) rowData.get("CONSUMPTION");
			Double percentage=getPercentage(consumption,totalKwh);
			List<Double> vals= new ArrayList<Double>();
			vals.add(roundOff(consumption, 2));
			vals.add(roundOff(percentage,2));
			meterConsumption.put(meterId, vals);
		}
		return meterConsumption;
	}
	
	public static double getPercentage(double numerator, double denominator)
	{
		double returnVal=numerator/denominator;
		return returnVal*100;
	}
	
	public static FacilioModule getReportModule(ReportContext reportContext) throws Exception {
		ReportFolderContext reportFolder = DashboardUtil.getReportFolderContext(reportContext.getParentFolderId());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		return modBean.getModule(reportFolder.getModuleId());
	}
	
	// Temporary
	public static String getReportClientUrl(String moduleName, Long reportId, FileFormat fileFormat) {
		StringBuilder url = new StringBuilder(AwsUtil.getConfig("clientapp.url")).append("/app/");
		if (moduleName.equals(FacilioConstants.ContextNames.WORK_ORDER)) {
			url.append("wo");
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.ALARM)) {
			url.append("fa");
		}
		else if (moduleName.equals(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
			url.append("em");
		}
		url.append("/reports/view/").append(reportId);
		if(fileFormat == FileFormat.IMAGE) {
			url.append("/show");
		}
		return url.toString();
	}
	
	public static String getAnalyticsClientUrl(Map<String, Object> config, FileFormat fileFormat) {
		StringBuilder url = new StringBuilder((String) config.get("path"));
		if(fileFormat == FileFormat.IMAGE) {
			url.append("/show");
		}
		String json = new Gson().toJson(config);
		json = encodeURIComponent(json);
		url.append("?filters=").append(json);
		return url.toString();
	}
	
	public static String encodeURIComponent(String url) {
	    String result = null;
	    try
	    {
	      result = URLEncoder.encode(url, "UTF-8")
	                         .replaceAll("\\+", "%20")
	                         .replaceAll("\\%21", "!")
	                         .replaceAll("\\%27", "'")
	                         .replaceAll("\\%28", "(")
	                         .replaceAll("\\%29", ")")
	                         .replaceAll("\\%7E", "~");
	    }
	    catch (UnsupportedEncodingException e) {
	      result = url;
	    }

	    return result;
	  }
}
