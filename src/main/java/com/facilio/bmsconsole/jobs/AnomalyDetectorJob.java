package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.facilio.bmsconsole.context.ReadingContext;
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
	private static long FIFTEEN_MINUTES_IN_MILLISEC = 15 * 60 * 60 * 1000;

	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		try {
		    //TO DO  .. Feature bit check
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_ANOMALY_DETECTOR))
			{
				return;
			}
		
			// get the list of all sub meters
			List<EnergyMeterContext> allEnergyMeters=DeviceAPI.getAllEnergyMeters();
			
			long now = System.currentTimeMillis();
			long endTime = now - FIFTEEN_MINUTES_IN_MILLISEC; 
			long startTime = endTime - SEVEN_DAYS_IN_MILLISEC;
			for(EnergyMeterContext energyMeter: allEnergyMeters) {
				doEnergyMeterAnomalyDetection(energyMeter, startTime, endTime);
			}
		}
		catch (Exception e) {
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
		String url=AwsUtil.getConfig("dataScienceUrl");
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			List<AnalyticsAnomalyContext> meterReadings = AnomalySchedulerUtil.getAllReadings(moduleName,startTime, endTime, energyMeterContext.getId(), energyMeterContext.getOrgId());

			if(meterReadings.size() == 0) {
				logger.log(Level.SEVERE, "NOT received readings for ID " + energyMeterContext.getId() + " startTime = " + startTime + " endTime = " + endTime);
				return;
			}
				
			String jsonInString = mapper.writeValueAsString(meterReadings);
			String result=AwsUtil.doHttpPost(url, null, null, jsonInString);
			logger.log(Level.INFO, "ID=" + energyMeterContext.getId() + " startTime=" + startTime + " endTime=" + endTime + " anomaly=" + (result == null ? "NA" : result));
			
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
			
			if(impactedIDs.contains(anomalyObject.getId()) && (anomalyObject.getTtime() >= endTimeOfWindow - 2 * FIFTEEN_MINUTES_IN_MILLISEC))  {
				AnomalyIDInsertRow newAnomalyId = new AnomalyIDInsertRow(anomalyObject.getId(), anomalyObject.getOrgId(), 
								anomalyObject.getModuleId(), anomalyObject.getMeterId(), anomalyObject.getTtime(), anomalyObject.getEnergyDelta(), currentTimeInMillisec);
				
				props.add(FieldUtil.getAsProperties(newAnomalyId));
				impactedIDs.remove(anomalyObject.getId());
				impactedContexts.add(anomalyObject); 
			}
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
			obj.put("severity", "Minor");
			obj.put("time", context.getTtime());
			obj.put("consumption", context.getEnergyDelta());

			FacilioContext addEventContext = new FacilioContext();
			addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, obj);
			Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
			getAddEventChain.execute(addEventContext);
		}
	}
}