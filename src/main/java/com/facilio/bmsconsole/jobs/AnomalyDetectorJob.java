package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AnalyticsAnomalyContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.TemperatureContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AnomalySchedulerUtil;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

public class AnomalyDetectorJob extends FacilioJob {
	private static long SEVEN_DAYS_IN_MILLISEC = 7 * 24 * 60 * 60 * 1000L;
	private static long FIFTEEN_MINUTES_IN_MILLISEC = 15 * 60 * 1000L;

	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		try {
			// TO DO .. Feature bit check
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_ANOMALY_DETECTOR)) {
				logger.log(Level.INFO, "Feature BITS is not enabled");
				return;
			}

			String meters = AwsUtil.getConfig("anomalyMeters");
			Integer anomalyPeriodicity = Integer.parseInt(AwsUtil.getConfig("anomalyPeriodicity"));
			// get the list of all sub meters
			List<EnergyMeterContext> allEnergyMeters = DeviceAPI.getSpecificEnergyMeters(meters);

			long correction = 0;
			
			// Uncomment below code for DEV testing only
			//long correction = System.currentTimeMillis() - 1524720891689L;
			
			long endTime = System.currentTimeMillis() - correction;
			long startTime = endTime - (2 * anomalyPeriodicity *  60 * 1000L);

			logger.log(Level.INFO, "  " + startTime + " " + endTime + " " + anomalyPeriodicity);
			logger.log(Level.INFO, "selected Meters ");
			for (EnergyMeterContext energyMeter : allEnergyMeters) {
				
				logger.log(Level.INFO, "" + energyMeter.getId() + "  " + startTime + " " + endTime);
				doEnergyMeterAnomalyDetection(energyMeter, startTime, endTime);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	// internal class f
	class AnomalyList {
		Long[] anomalyIDs;
		
		public void setAnomalyIDs(Long[] anomalyIDs) {
			this.anomalyIDs = anomalyIDs;
		}
		
		public Long[] getAnomalyIDs() {
			return anomalyIDs;
		}
	}
	
	private void doEnergyMeterAnomalyDetection(EnergyMeterContext  energyMeterContext, long startTime, long endTime) {
		String moduleName="dummyModuleName";
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			List<AnalyticsAnomalyContext> meterReadings = AnomalySchedulerUtil.getAllReadings(moduleName,startTime, endTime, energyMeterContext.getId(), energyMeterContext.getOrgId());

			if(meterReadings.size() == 0) {
				logger.log(Level.SEVERE, "NOT received readings for ID " + energyMeterContext.getId() + " startTime = " + startTime + " endTime = " + endTime);
				return;
			}
				
			List<TemperatureContext> temperatureContext = AnomalySchedulerUtil.getAllTemperatureReadings(moduleName,
					startTime, endTime, energyMeterContext.getOrgId());

			CheckAnomalyModelPostData postData=new CheckAnomalyModelPostData();
			String postURL = AwsUtil.getConfig("anomalyCheckServiceURL") + "/checkAnomaly";

			postData.meterID = energyMeterContext.getId();
			postData.temperatureData=temperatureContext;
			postData.energyData=meterReadings;
			postData.timezone = AccountUtil.getCurrentOrg().getTimezone();
			String jsonInString = mapper.writeValueAsString(postData);
			logger.log(Level.INFO, " post body is " + jsonInString);
			
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type","application/json");
			String result=AwsUtil.doHttpPost(postURL, headers, null, jsonInString);
			logger.log(Level.INFO, " result is " + result);
			AnomalyList anomalyList = new GsonBuilder().create().fromJson(result, AnomalyList.class);

			if(anomalyList == null || anomalyList.getAnomalyIDs() == null || anomalyList.getAnomalyIDs().length == 0) {
				// No Anomaly is Detected by our algorithm
				return;
			}
			
			String idList = Arrays.toString(anomalyList.getAnomalyIDs());
			idList = "(" + idList.substring(1, idList.length() - 1) + ")";
			
			LinkedHashSet<Long> anomalyIDs=new LinkedHashSet<>(Arrays.asList(anomalyList.getAnomalyIDs()));
			LinkedHashSet<Long> existingAnomalyIds=AnomalySchedulerUtil.getExistingAnomalyIDs(moduleName, idList);
			
			anomalyIDs.removeAll(existingAnomalyIds);
		
			if(!anomalyIDs.isEmpty()) {
				insertAnomalyIDs(anomalyIDs, meterReadings, DateTimeUtil.getCurrenTime(), endTime);
			}else {
				// No anomaly detected
				//logger.info("No IDs found as anomaly");
			}
		}catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	class AnomalyIDInsertRow {
		private long id;
		private long orgId;
		private long moduleId;
		private long meterId;
		private long ttime;
		private double energyDelta;
		private long createdTime;
				
		public AnomalyIDInsertRow() {
		}
		
		public AnomalyIDInsertRow(long id, long orgId, long moduleId, long meterId ,long ttime, double energyDelta, long createdTime) {
			this.id=id;
			this.orgId=orgId;
			this.moduleId=moduleId;
			this.meterId=meterId;
			this.ttime = ttime;
			this.energyDelta=energyDelta;
			this.createdTime=createdTime;
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
	}
	
	private void insertAnomalyIDs(LinkedHashSet<Long> insertIDs, List<AnalyticsAnomalyContext> anomalyContext, long currentTimeInMillisec, long endTimeOfWindow) throws Exception {
		LinkedHashSet<Long> impactedIDs =(LinkedHashSet<Long>) insertIDs.clone();
		List<Map<String, Object>> props = new ArrayList<>();
		ArrayList<AnalyticsAnomalyContext> impactedContexts = new ArrayList<>();
	
		Iterator<AnalyticsAnomalyContext> iterator = anomalyContext.iterator();
		while (iterator.hasNext() && (!impactedIDs.isEmpty())) {
			AnalyticsAnomalyContext anomalyObject = iterator.next();
			
			//if(impactedIDs.contains(anomalyObject.getId()) && (anomalyObject.getTtime() >= endTimeOfWindow - 2 * FIFTEEN_MINUTES_IN_MILLISEC))  {
				
			AnomalyIDInsertRow newAnomalyId = new AnomalyIDInsertRow(anomalyObject.getId(), anomalyObject.getOrgId(), 
								anomalyObject.getModuleId(), anomalyObject.getMeterId(), anomalyObject.getTtime(), anomalyObject.getEnergyDelta(), currentTimeInMillisec);
				
				props.add(FieldUtil.getAsProperties(newAnomalyId));
				impactedIDs.remove(anomalyObject.getId());
				impactedContexts.add(anomalyObject); 
			//}
		}
		
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getAnalyticsAnomalyIDListModule().getTableName())
				.fields(FieldFactory.getAnomalyIDInsertFields())
				.addRecords(props);
		insertRecordBuilder.save();
		
		triggerAlarm(impactedContexts);
	}
	
	@SuppressWarnings("unchecked")
	private void triggerAlarm(List<AnalyticsAnomalyContext> impactedContexts) throws Exception {
		
		for(AnalyticsAnomalyContext context: impactedContexts)
		{
			long meterId = context.getMeterId();	
			AssetContext asset = AssetsAPI.getAssetInfo(meterId);
			String assetName = asset.getName();
		
			JSONObject obj = new JSONObject();
			obj.put("message", "Anomaly Detected");
			obj.put("source", assetName);
			obj.put("node", assetName);
			obj.put("resourceId", meterId);
			obj.put("severity", "Info");
			obj.put("time", context.getTtime());
			obj.put("consumption", context.getEnergyDelta());

			FacilioContext addEventContext = new FacilioContext();
			addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
			Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
			getAddEventChain.execute(addEventContext);
		}
	}
	
	class CheckAnomalyModelPostData {
		public Long getMeterID() {
			return meterID;
		}
		public String getTimezone() {
			return timezone;
		}
		public List<AnalyticsAnomalyContext> getEnergyData() {
			return energyData;
		}
		public List<TemperatureContext> getTemperatureData() {
			return temperatureData;
		}
		Long meterID;
		String timezone;
		List<AnalyticsAnomalyContext> energyData;
		List<TemperatureContext> temperatureData;
	}
}