package com.facilio.bmsconsole.jobs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.util.AssetAnomalyUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class V1AnomalyDetectorJob extends FacilioJob {
	private static long THIRTY_MINUTES_IN_MILLISEC = 30 * 60 * 1000L;
	public static final long MAIN_METER_ID=-1;
	private static final Logger logger = Logger.getLogger(V1AnomalyDetectorJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		try {
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.ANOMALY_DETECTOR)) {
				logger.log(Level.INFO, "Feature Bit is not enabled");
				return;
			} else {
				logger.log(Level.INFO, "Feature Bit is enabled");
			}

			Integer anomalyPeriodicity = Integer.parseInt(AwsUtil.getAnomalyPeriodicity());
			// get the list of all sub meters

			long correction = 0;

			if (AwsUtil.isDevelopment()) { // for dev testing purpose time is moved back
				correction = System.currentTimeMillis() - 1529948963000L;
			}

			long endTime = System.currentTimeMillis() - correction;
			long startTime = endTime - (2 * anomalyPeriodicity * 60 * 1000L);

			// Collect all asset configuration details
			Map<Long, AnomalyAssetConfigurationContext> meterConfigurations = null;
			long orgID = jc.getOrgId();
			String moduleName = "dummyModuleName";
			try {
				meterConfigurations = AssetAnomalyUtil.getAllAssetConfigWithDefaults(moduleName, jc.getOrgId());
				logger.log(Level.INFO, "meters configured  = " + (meterConfigurations.size()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Collect all configuration of energy meters
			List<EnergyMeterContext> energyContextList = AssetAnomalyUtil
					.getAllConfiguredEnergyMeters(meterConfigurations); // (meterConfigurations)(meterConfigurations);
			

			Map<Long, EnergyMeterContext> idToMeterMapping = new LinkedHashMap<>();
			
			for(int i=0; i < energyContextList.size(); i++) {
				idToMeterMapping.put(energyContextList.get(i).getId(), energyContextList.get(i));
			}
			
			List<AssetTreeContext> watchMeterContextList = AssetAnomalyUtil
					.getAllEnergyMetersInAssetTree(orgID);
			List<EnergyMeterContext> filteredMeterList = new ArrayList<>(); 
			
			for(AssetTreeContext assetTreeContext: watchMeterContextList) {
				EnergyMeterContext ctxt = idToMeterMapping.get(assetTreeContext.getChildAsset());
				if(ctxt != null) {
					filteredMeterList.add(ctxt);
				}
			}
			Set<Long> siteIdList = filteredMeterList.stream().map(s -> s.getSiteId()).collect(Collectors.toSet());
			
			// Collect the list of energy meters
			for (Long siteId : siteIdList) {
				List<EnergyMeterContext> subEnergyMeterContextList = filteredMeterList.stream()
						.filter(s -> s.getSiteId() == siteId).collect(Collectors.toList());

				doEnergyMeterAnomalyDetection(subEnergyMeterContextList, meterConfigurations, startTime, endTime,
						siteId, orgID);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	class AnomalyDetails {
		public long getAnomalyIDs() {
			return anomalyIDs;
		}
		public void setAnomalyIDs(long anomalyIDs) {
			this.anomalyIDs = anomalyIDs;
		}
		public double getTemperature() {
			return temperature;
		}
		public void setTemperature(double temperature) {
			this.temperature = temperature;
		}
		public long getTtime() {
			return ttime;
		}
		public void setTtime(long ttime) {
			this.ttime = ttime;
		}
		public short getAnomalyStatus() {
			return anomalyStatus;
		}
		public void setAnomalyStatus(short anomalyStatus) {
			this.anomalyStatus = anomalyStatus;
		}
		public double getMinKWH() {
			return minKWH;
		}
		public void setMinKWH(double minKWH) {
			this.minKWH = minKWH;
		}
		public double getMaxKWH() {
			return maxKWH;
		}
		public void setMaxKWH(double maxKWH) {
			this.maxKWH = maxKWH;
		}
		public double getActualKWH() {
			return actualKWH;
		}
		public void setActualKWH(double actualKWH) {
			this.actualKWH = actualKWH;
		}
		
		long anomalyIDs;
		double temperature;
		long ttime;
		short anomalyStatus;
		double minKWH, maxKWH, actualKWH;
	}

	// internal class f
	class AnomalyList {
		AnomalyDetails[] anomalyDetails;
		public void setAnomalyIDs(AnomalyDetails[] anomalyDetails) {
			this.anomalyDetails = anomalyDetails;
		}
		public AnomalyDetails[] getAnomalyDetails() {
			return anomalyDetails;
		}
	}

	private void doEnergyMeterAnomalyDetection(List<EnergyMeterContext> subEnergyMeterContextList,
			Map<Long, AnomalyAssetConfigurationContext> meterConfigurations, long startTime, long endTime, Long siteId,
			long orgID) {
		String moduleName = "dummyModuleName";
		
		new ObjectMapper();
		Map<Long, List<TemperatureContext>> siteIdToWeatherMapping = new HashMap<>();
		Map<EnergyMeterContext, AnomalyDetails[]> masterAnomalyMap = new LinkedHashMap<>(); 
		try {
			for (EnergyMeterContext energyMeterContext : subEnergyMeterContextList) {
				List<AnalyticsAnomalyContext> meterReadings = AssetAnomalyUtil.getAllEnergyReadings(startTime,
						endTime, energyMeterContext.getId(), orgID);
				List<AnalyticsAnomalyContext> validAnomalyContext = new ArrayList<>();

				if (meterReadings == null || meterReadings.size() == 0) {
					logger.log(Level.SEVERE, "NOT received readings for ID " + energyMeterContext.getId()
							+ " startTime = " + startTime + " endTime = " + endTime);
					continue;
				}
				List<TemperatureContext> siteTemperatureReadings = null;
				if (siteIdToWeatherMapping.containsKey(siteId)) {
					siteTemperatureReadings = siteIdToWeatherMapping.get(siteId);
				} else {
					siteTemperatureReadings = AssetAnomalyUtil
							.getWeatherReadingsForOneSite((startTime - THIRTY_MINUTES_IN_MILLISEC), endTime, siteId);
					siteIdToWeatherMapping.put(siteId, siteTemperatureReadings);
				}

				if (siteTemperatureReadings == null || siteTemperatureReadings.size() == 0) {
					logger.log(Level.SEVERE, "NOT received weatherData for ID " + energyMeterContext.getId()
							+ " startTime = " + startTime + " endTime = " + endTime);
					continue;
				}

				AnomalyAssetConfigurationContext configContext = AssetAnomalyUtil
						.getAssetConfig(energyMeterContext.getId(), meterConfigurations);
				String result = doPostAnomalyDetectAPI(configContext, meterReadings, siteTemperatureReadings,
						energyMeterContext.getId(), orgID);
				logger.log(Level.INFO, " result is " + result);

				AnomalyList anomalyList = new GsonBuilder().create().fromJson(result,AnomalyList.class);
				logger.log(Level.INFO, " anomaly result is " + anomalyList.getAnomalyDetails().length);
				
				if((anomalyList != null) && (anomalyList.getAnomalyDetails().length > 0)) {
					masterAnomalyMap.put(energyMeterContext, anomalyList.getAnomalyDetails());
				}
				
				int waitTimeInSecs = Integer.parseInt(AwsUtil.getAnomalyDetectWaitTimeInSeconds());
				
				if(waitTimeInSecs > 0) {
						Thread.sleep(1000 * waitTimeInSecs);
				}
			} // end of for
			if (masterAnomalyMap.size() > 0) {
				doRootCause(orgID, startTime, endTime);

				for (EnergyMeterContext energyMeterContext : masterAnomalyMap.keySet()) {
					for (AnomalyDetails detail : masterAnomalyMap.get(energyMeterContext)) {
						triggerAlarm(detail, energyMeterContext);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static List<Long> getKeyFromValue(Map<Long, Long> hm, Long value) {
	    ArrayList<Long> result = new ArrayList<>();
		for (Long o : hm.keySet()) {
	      if (hm.get(o).equals(value)) {
	        result.add(o);
	      }
	    }
		
	    return (result.size() == 0 ? null : result);
	 }
	
	public void doRootCause(long orgID, long startTime, long endTime) throws Exception {
		//Get Tree
		Map<Long, Long> parentChildTree=AssetAnomalyUtil.getAssetTree(orgID);
		
		// get main tree
		List<Long> mainMeterList = getKeyFromValue(parentChildTree, MAIN_METER_ID);
		
		if(mainMeterList == null || mainMeterList.size() == 0) {
			return;
		}
		
		Long mainMeter = mainMeterList.get(0);
		rootCauseAnalysisApi(mainMeter, orgID, startTime ,endTime,AccountUtil.getCurrentAccount().getTimeZone());
		
		// get level 1 
		List<Long> levelOneNodes=getKeyFromValue(parentChildTree, mainMeter);
		for(Long meterIds: levelOneNodes) {
			rootCauseAnalysisApi(meterIds, orgID, startTime ,endTime,AccountUtil.getCurrentAccount().getTimeZone());
		}
	}
	
	String doPostAnomalyDetectAPI(AnomalyAssetConfigurationContext configContext,
			List<AnalyticsAnomalyContext> meterReadings, 
			List<TemperatureContext> siteTemperatureReadings, 
			long meterID, long orgId)
			
					throws IOException {
		CheckAnomalyModelPostData postData = new CheckAnomalyModelPostData();

		String postURL = AwsUtil.getAnomalyCheckServiceURL() + "/checkGam";
		postData.meterID = meterID;
		postData.orgId=orgId;
		postData.dimension1 = configContext.getDimension1Buckets();
		postData.dimension1Value = configContext.getDimension1Value();
		postData.temperatureData = siteTemperatureReadings;
		postData.energyData = meterReadings;
		postData.timezone = AccountUtil.getCurrentAccount().getTimeZone();
		postData.meterInterval = configContext.getMeterInterval();
		postData.adjustmentPercentage = configContext.getAdjustmentPercentage();
		postData.tableValue = configContext.getTableValue();
		postData.orderRange = configContext.getOrderRange();
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(postData);
		logger.log(Level.INFO, " anomaly detect post: " + jsonInString);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		String result = AwsUtil.doHttpPost(postURL, headers, null, jsonInString);
		return result;
	}
	void rootCauseAnalysisApi(long meterid,long orgid,long ttimestart,long ttimeend,String timezone) throws IOException
	{
		AnomalyData anomalydata = new AnomalyData();
		String postURL = AwsUtil.getAnomalyCheckServiceURL() + "/ratioCheck";
		anomalydata.meterId = meterid;
		anomalydata.orgId = orgid;
		anomalydata.ttimeStart = ttimestart;
		anomalydata.ttimeEnd= ttimeend;
		anomalydata.timeZone = timezone;
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(anomalydata);
		logger.log(Level.INFO, " anomaly root cause post: " + jsonInString);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
        AwsUtil.doHttpPost(postURL, headers, null, jsonInString);
	    logger.log(Level.INFO,"after root cause api call");
		
	}
	class AnomalyData
	{
		long meterId,orgId,ttimeStart,ttimeEnd;
		String timeZone;
		public long getMeterId() {
			return meterId;
		}
		public void setMeterId(long meterId) {
			this.meterId = meterId;
		}
		public long getOrgId() {
			return orgId;
		}
		public void setOrgId(long orgId) {
			this.orgId = orgId;
		}
		public long getTtimeStart() {
			return ttimeStart;
		}
		public void setTtimeStart(long ttimeStart) {
			this.ttimeStart = ttimeStart;
		}
		public long getTtimeEnd() {
			return ttimeEnd;
		}
		public void setTtimeEnd(long ttimeEnd) {
			this.ttimeEnd = ttimeEnd;
		}
		public String getTimeZone() {
			return timeZone;
		}
		public void setTimeZone(String timeZone) {
			this.timeZone = timeZone;
		}
	}

	@SuppressWarnings("unchecked")
	private void triggerAlarm(AnomalyDetails detail, EnergyMeterContext context) throws Exception {
		double minKWH=detail.getMinKWH();
		double maxKWH=detail.getMaxKWH();
		double actualKWH=detail.getActualKWH();
		double temperature=detail.getTemperature();
		double ttime = detail.getTtime();
		long meterId = context.getId();
		long rowID = detail.getAnomalyIDs();
		DecimalFormat df_one_decimal = new DecimalFormat(".#");

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField consumptionField = modBean.getField("totalEnergyConsumptionDelta",
				FacilioConstants.ContextNames.ENERGY_DATA_READING);

			AssetContext asset = AssetsAPI.getAssetInfo(meterId);
			String assetName = asset.getName();

			String message = "Anomaly Detected. Energy meter reading " + df_one_decimal.format(actualKWH) + " is outside the range [" + df_one_decimal.format(minKWH) + ':' + df_one_decimal.format(maxKWH) + "]";
			 
			JSONObject obj = new JSONObject();
			obj.put("message", message);
			obj.put("source", assetName);
			obj.put("condition", "Anomaly Detected in Energy Consumption");
			obj.put("resourceId", meterId);
			obj.put("severity", "Minor");
			obj.put("timestamp", ttime);
			obj.put("consumption", actualKWH);

			obj.put("sourceType", SourceType.ANOMALY_ALARM.getIntVal());
			obj.put("readingFieldId", consumptionField.getFieldId());
			obj.put("readingDataId", rowID);
			obj.put("startTime", ttime);
			obj.put("readingMessage", message);

			FacilioContext addEventContext = new FacilioContext();
			addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
			Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
			getAddEventChain.execute(addEventContext);
	}

	class CheckAnomalyModelPostData {
		public long getMeterID() {
			return meterID;
		}

		public void setMeterID(long meterID) {
			this.meterID = meterID;
		}

		public String getDimension1() {
			return dimension1;
		}

		public void setDimension1(String dimension1) {
			this.dimension1 = dimension1;
		}

		public String getDimension1Value() {
			return dimension1Value;
		}

		public void setDimension1Value(String dimension1Value) {
			this.dimension1Value = dimension1Value;
		}

		public List<TemperatureContext> getTemperatureData() {
			return temperatureData;
		}

		public void setTemperatureData(List<TemperatureContext> temperatureData) {
			this.temperatureData = temperatureData;
		}

		public List<AnalyticsAnomalyContext> getEnergyData() {
			return energyData;
		}

		public void setEnergyData(List<AnalyticsAnomalyContext> energyData) {
			this.energyData = energyData;
		}

		public String getTimezone() {
			return timezone;
		}

		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}

		public int getMeterInterval() {
			return meterInterval;
		}

		public void setMeterInterval(int meterInterval) {
			this.meterInterval = meterInterval;
		}

		public double getAdjustmentPercentage() {
			return adjustmentPercentage;
		}

		public void setAdjustmentPercentage(double adjustmentPercentage) {
			this.adjustmentPercentage = adjustmentPercentage;
		}

		public double getTableValue() {
			return tableValue;
		}

		public void setTableValue(double tableValue) {
			this.tableValue = tableValue;
		}

		public int getOrderRange() {
			return orderRange;
		}

		public void setOrderRange(int orderRange) {
			this.orderRange = orderRange;
		}
		public long getOrgId() {
			return orgId;
		}

		public void setOrgId(long orgId) {
			this.orgId = orgId;
		}

		long meterID;
		long orgId;
		

		String dimension1;
		String dimension1Value;
		List<TemperatureContext> temperatureData;
		List<AnalyticsAnomalyContext> energyData;
		String timezone;
		int meterInterval;
		double adjustmentPercentage;
		double tableValue;
		int orderRange;
	}
}