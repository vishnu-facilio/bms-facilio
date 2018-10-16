package com.facilio.bmsconsole.jobs;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AnalyticsAnomalyConfigContext;
import com.facilio.bmsconsole.context.AnalyticsAnomalyContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.TemperatureContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AnomalySchedulerUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

public class AnomalyDetectorJob extends FacilioJob {
	private static long THIRTY_MINUTES_IN_MILLISEC = 30 * 60 * 1000L;

	private static final Logger logger = Logger.getLogger(AnomalyDetectorJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		try {
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_ANOMALY_DETECTOR)) {
				logger.log(Level.INFO, "Feature Bit is not enabled");
				return;
			} else {
				logger.log(Level.INFO, "Feature Bit is enabled");
			}

			Integer anomalyPeriodicity = Integer.parseInt(AwsUtil.getConfig("anomalyPeriodicity"));
			// get the list of all sub meters

			long correction = 0;
			
			if( AwsUtil.getConfig("environment").equals("development")) { // for dev testing purpose time is moved back 
				correction = System.currentTimeMillis() - 1529948963000L;
			}
			
			long endTime = System.currentTimeMillis() - correction;
			long startTime = endTime - (2 * anomalyPeriodicity * 60 * 1000L);

			// Collect all asset configuration details
			Map<Long, AnalyticsAnomalyConfigContext> meterConfigurations = null;
			long orgID = jc.getOrgId();
			String moduleName = "dummyModuleName";
			try {
				meterConfigurations = AnomalySchedulerUtil.getAllAssetConfigWithDefaults(moduleName, jc.getOrgId());
				logger.log(Level.INFO, "meters configured  = " + (meterConfigurations.size()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Collect all configuration of energy meters
			List<EnergyMeterContext> energyContextList = AnomalySchedulerUtil
					.getAllConfiguredEnergyMeters(meterConfigurations);
			Set<Long> siteIdList = energyContextList.stream().map(s -> s.getSiteId()).collect(Collectors.toSet());

			// Collect the list of energy meters
			for (Long siteId : siteIdList) {
				List<EnergyMeterContext> subEnergyMeterContextList = energyContextList.stream()
						.filter(s -> s.getSiteId() == siteId).collect(Collectors.toList());

				doEnergyMeterAnomalyDetection(subEnergyMeterContextList, meterConfigurations, startTime, endTime,
						siteId, orgID);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	class AnomalyDetails {
		Long anomalyIDs;
		Double outlierDistance;

		public void setAnomalyIDs(Long anomalyIDs) {
			this.anomalyIDs = anomalyIDs;
		}

		public Long getAnomalyIDs() {
			return anomalyIDs;
		}

		public void setOutlierDistance(Double outlierDistance) {
			this.outlierDistance = outlierDistance;
		}

		public Double getOutlierDistance() {
			return outlierDistance;
		}
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

		public Long[] getAnomalyIDs() {
			Long anomalyIDs[] = Arrays.stream(anomalyDetails).map(s -> s.getAnomalyIDs()).toArray(Long[]::new);

			return anomalyIDs;
		}
	}

	private void doEnergyMeterAnomalyDetection(List<EnergyMeterContext> subEnergyMeterContextList,
			Map<Long, AnalyticsAnomalyConfigContext> meterConfigurations,
			long startTime, long endTime, Long siteId, long orgID) {
		String moduleName="dummyModuleName";
		new ObjectMapper();
        Map<Long, List<TemperatureContext>> siteIdToWeatherMapping = new HashMap<>();
        
    	try {
    		for(EnergyMeterContext energyMeterContext: subEnergyMeterContextList) {
				List<AnalyticsAnomalyContext> meterReadings = AnomalySchedulerUtil.getAllEnergyReadings(startTime, endTime, 
						energyMeterContext.getId(), orgID);
				List<AnalyticsAnomalyContext> validAnomalyContext=new ArrayList<>(); 
			
				if(meterReadings == null || meterReadings.size() == 0) {
					logger.log(Level.SEVERE, "NOT received readings for ID " + energyMeterContext.getId() + 
							" startTime = " + startTime + " endTime = " + endTime);
					continue;
				}
				List<TemperatureContext> siteTemperatureReadings=null;
				if(siteIdToWeatherMapping.containsKey(siteId)){
	    			siteTemperatureReadings = siteIdToWeatherMapping.get(siteId);
	    		}else {
	    			siteTemperatureReadings=AnomalySchedulerUtil.getWeatherReadingsForOneSite((startTime - THIRTY_MINUTES_IN_MILLISEC),
	    					endTime, siteId);
	    			siteIdToWeatherMapping.put(siteId, siteTemperatureReadings);
	    		}

	    		if(siteTemperatureReadings == null || siteTemperatureReadings.size() == 0) {
	    			logger.log(Level.SEVERE, "NOT received weatherData for ID " + energyMeterContext.getId() + 
	    					" startTime = " + startTime + " endTime = " + endTime);
	    			continue;
	    		}
	    		
	    		AnalyticsAnomalyConfigContext configContext = AnomalySchedulerUtil.getAssetConfig(energyMeterContext.getId(), meterConfigurations);
	    		String result= doPostAnomalyDetectAPI(configContext, meterReadings, siteTemperatureReadings, energyMeterContext.getId());
	    		logger.log(Level.INFO, " result is " + result);

				AnomalyList anomalyList = new GsonBuilder().create().fromJson(result, AnomalyList.class);
				if(anomalyList == null || anomalyList.getAnomalyDetails() == null || anomalyList.getAnomalyDetails().length == 0) {
					// No Anomaly is Detected by our algorithm
					continue;
				}
			
				String idList = Arrays.toString(anomalyList.getAnomalyIDs());
				idList = "(" + idList.substring(1, idList.length() - 1) + ")";
			
				LinkedHashSet<Long> anomalyIDs=new LinkedHashSet<>(Arrays.asList(anomalyList.getAnomalyIDs()));
				LinkedHashSet<Long> existingAnomalyIds=AnomalySchedulerUtil.getExistingAnomalyIDs(moduleName, idList);
				anomalyIDs.removeAll(existingAnomalyIds);
				HashMap<Long, AnomalyDetails> validAnomalyList = new LinkedHashMap<>();
				if(!anomalyIDs.isEmpty()) {
					for(AnomalyDetails anomalyInfo : anomalyList.getAnomalyDetails()) {
						if(anomalyIDs.contains(anomalyInfo.getAnomalyIDs())) {
							validAnomalyList.put(anomalyInfo.getAnomalyIDs(), anomalyInfo);
						}
					}
				}
				
				for(AnalyticsAnomalyContext context: meterReadings) {
					AnomalyDetails anomalyDetails = validAnomalyList.get(context.getId());
					if(anomalyDetails != null) {
						context.setOutlierDistance(anomalyDetails.getOutlierDistance());
						logger.log(Level.INFO, "outier distance = " + context.getOutlierDistance());
						validAnomalyContext.add(context);
					}
				}
				
				insertAnomalyIDs(anomalyIDs, validAnomalyContext, DateTimeUtil.getCurrenTime(), endTime);
				int waitTimeInSecs = Integer.parseInt(AwsUtil.getConfig("anomalyDetectWaitTimeInSeconds"));
				
				if(waitTimeInSecs > 0) {
						Thread.sleep(1000 * waitTimeInSecs);
				}
    		} // end of for
    	}catch(Exception e) {
    			logger.log(Level.SEVERE, e.getMessage(), e);
    	}
    }
    

	String doPostAnomalyDetectAPI(AnalyticsAnomalyConfigContext configContext,
			List<AnalyticsAnomalyContext> meterReadings, List<TemperatureContext> siteTemperatureReadings, long meterID)
			throws IOException {
		CheckAnomalyModelPostData postData = new CheckAnomalyModelPostData();

		String postURL = AwsUtil.getConfig("anomalyCheckServiceURL") + "/checkAnomaly";
		postData.meterID = meterID;
		postData.constant1 = configContext.getConstant1();
		postData.constant2 = configContext.getConstant2();
		postData.maxDistance = configContext.getMaxDistance();
		postData.dimension1 = configContext.getDimension1Buckets();
		postData.dimension2 = configContext.getDimension2Buckets();
		postData.dimension1Value = configContext.getDimension1Value();
		postData.dimension2Value = configContext.getDimension2Value();
		postData.xAxisDimension = configContext.getxAxisDimension();
		postData.yAxisDimension = configContext.getyAxisDimension();
		postData.outlierDistance =configContext.getOutlierDistance();
		postData.temperatureData = siteTemperatureReadings;
		postData.energyData = meterReadings;
		postData.timezone = AccountUtil.getCurrentOrg().getTimezone();
		postData.meterInterval = configContext.getMeterInterval();

		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(postData);
		logger.log(Level.INFO, " anomaly detect post: " + jsonInString);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		String result = AwsUtil.doHttpPost(postURL, headers, null, jsonInString);
		return result;
	}
	
	class AnomalyIDInsertRow {
		private long id;
		private long orgId;
		private long moduleId;
		private long meterId;
		private long ttime;
		private double energyDelta;
		private long createdTime;
		private double outlierDistance;
				
		public AnomalyIDInsertRow() {
		}
		
		public AnomalyIDInsertRow(long id, long orgId, long moduleId, long meterId ,long ttime, 
				double energyDelta, long createdTime, double outlierDistance) {
			this.id=id;
			this.orgId=orgId;
			this.moduleId=moduleId;
			this.meterId=meterId;
			this.ttime = ttime;
			this.energyDelta=energyDelta;
			this.createdTime=createdTime;
			this.outlierDistance=outlierDistance;
		}
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			
			this.id = id;
		}
		
		public long getOrgId() {
			return orgId;
		}
		
		public void setOrgId(long orgId) {
			this.orgId = orgId;
		}
		public long getModuleId() {
			return moduleId;
		}
		
		public void setModuleId(long moduleId) {
				this.moduleId = moduleId;
		}
		
		public long getMeterId() {
			return meterId;
		}
		public void setMeterId(long meterId) {
			this.meterId = meterId;
		}
		
		public long getTtime() {
			return ttime;
		}
		public void setTtime(long ttime) {
			this.ttime = ttime;
		}
		public long getCreatedTime() {
			return createdTime;
		}
		public void setCreatedTime(long createdTime) {
			this.createdTime = createdTime;
		}

		public double getEnergyDelta() {
			return energyDelta;
		}

		public void setEnergyDelta(double energyDelta) {
			this.energyDelta = energyDelta;
		}

		public double getOutlierDistance() {
			return outlierDistance;
		}

		public void setOutlierDistance(double outlierDistance) {
			this.outlierDistance = outlierDistance;
		}
	}

	private void insertAnomalyIDs(LinkedHashSet<Long> insertIDs, List<AnalyticsAnomalyContext> anomalyContext,
			long currentTimeInMillisec, long endTimeOfWindow) throws Exception {
		LinkedHashSet<Long> impactedIDs = (LinkedHashSet<Long>) insertIDs.clone();
		List<Map<String, Object>> props = new ArrayList<>();
		ArrayList<AnalyticsAnomalyContext> impactedContexts = new ArrayList<>();

		Iterator<AnalyticsAnomalyContext> iterator = anomalyContext.iterator();
		while (iterator.hasNext() && (!impactedIDs.isEmpty())) {
			AnalyticsAnomalyContext anomalyObject = iterator.next();

			if (impactedIDs.contains(anomalyObject.getId())) { // a valid anomaly data point

				AnomalyIDInsertRow newAnomalyId = new AnomalyIDInsertRow(anomalyObject.getId(),
						anomalyObject.getOrgId(), anomalyObject.getModuleId(), anomalyObject.getParentId(),
						anomalyObject.getTtime(), anomalyObject.getTotalEnergyConsumptionDelta(), currentTimeInMillisec,
						anomalyObject.getOutlierDistance());

				props.add(FieldUtil.getAsProperties(newAnomalyId));
				impactedIDs.remove(anomalyObject.getId());
				impactedContexts.add(anomalyObject);
			}
		}

		if (impactedContexts.size() > 0) {
			GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getAnalyticsAnomalyIDListModule().getTableName())
					.fields(FieldFactory.getAnomalyIDInsertFields()).addRecords(props);
			insertRecordBuilder.save();

			triggerAlarm(impactedContexts);
		}
	}

	@SuppressWarnings("unchecked")
	private void triggerAlarm(List<AnalyticsAnomalyContext> impactedContexts) throws Exception {
		DecimalFormat df_one_decimal = new DecimalFormat(".#");

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField consumptionField = modBean.getField("totalEnergyConsumptionDelta",
				FacilioConstants.ContextNames.ENERGY_DATA_READING);

		for (AnalyticsAnomalyContext context : impactedContexts) {
			long meterId = context.getParentId();
			AssetContext asset = AssetsAPI.getAssetInfo(meterId);
			String assetName = asset.getName();

			String message = "Anomaly Detected. Energy meter reading is "
			+ df_one_decimal.format(context.getOutlierDistance()) + "% more than the expected value";
			JSONObject obj = new JSONObject();
			obj.put("message", message);
			obj.put("source", assetName);
			obj.put("entity", assetName);
			obj.put("resourceId", meterId);
			obj.put("severity", "Minor");
			obj.put("timestamp", context.getTtime());
			obj.put("consumption", context.getTotalEnergyConsumptionDelta());

			obj.put("sourceType", SourceType.ANOMALY_ALARM.getIntVal());
			obj.put("readingFieldId", consumptionField.getFieldId());
			obj.put("readingDataId", context.getId());
			obj.put("startTime", context.getTtime());
			obj.put("readingMessage", message);

			FacilioContext addEventContext = new FacilioContext();
			addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
			Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
			getAddEventChain.execute(addEventContext);
		}
	}

	class CheckAnomalyModelPostData {
		public int getMeterInterval() {
			return meterInterval;
		}
		public void setMeterInterval(int meterInterval) {
			this.meterInterval = meterInterval;
		}

		public Long getMeterID() {
			return meterID;
		}
		public void setMeterID(Long meterID) {
			this.meterID = meterID;
		}
		public double getConstant1() {
			return constant1;
		}
		public void setConstant1(double constant1) {
			this.constant1 = constant1;
		}
		public double getConstant2() {
			return constant2;
		}
		public void setConstant2(double constant2) {
			this.constant2 = constant2;
		}
		public double getMaxDistance() {
			return maxDistance;
		}
		public void setMaxDistance(double maxDistance) {
			this.maxDistance = maxDistance;
		}
		public String getDimension1() {
			return dimension1;
		}
		public void setDimension1(String dimension1) {
			this.dimension1 = dimension1;
		}
		public String getDimension2() {
			return dimension2;
		}
		public void setDimension2(String dimension2) {
			this.dimension2 = dimension2;
		}
		public String getDimension1Value() {
			return dimension1Value;
		}
		public void setDimension1Value(String dimension1Value) {
			this.dimension1Value = dimension1Value;
		}
		public String getDimension2Value() {
			return dimension2Value;
		}
		public void setDimension2Value(String dimension2Value) {
			this.dimension2Value = dimension2Value;
		}
		public String getxAxisDimension() {
			return xAxisDimension;
		}
		public void setxAxisDimension(String xAxisDimension) {
			this.xAxisDimension = xAxisDimension;
		}
		public String getyAxisDimension() {
			return yAxisDimension;
		}
		public void setyAxisDimension(String yAxisDimension) {
			this.yAxisDimension = yAxisDimension;
		}
		public String getTimezone() {
			return timezone;
		}
		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}
		public double getOutlierDistance() {
			return outlierDistance;
		}
		public void setOutlierDistance(double outlierDistance) {
			this.outlierDistance = outlierDistance;
		}
		public List<AnalyticsAnomalyContext> getEnergyData() {
			return energyData;
		}
		public void setEnergyData(List<AnalyticsAnomalyContext> energyData) {
			this.energyData = energyData;
		}
		public List<TemperatureContext> getTemperatureData() {
			return temperatureData;
		}
		public void setTemperatureData(List<TemperatureContext> temperatureData) {
			this.temperatureData = temperatureData;
		}
		Long meterID;
		double constant1;
		double constant2;
		double maxDistance;

		String dimension1;
		String dimension2;
		String dimension1Value;
		String dimension2Value;
		String xAxisDimension;
		String yAxisDimension;
		
		String timezone;
		double outlierDistance;
		List<AnalyticsAnomalyContext> energyData;
		List<TemperatureContext> temperatureData;
		int meterInterval;
	}
}