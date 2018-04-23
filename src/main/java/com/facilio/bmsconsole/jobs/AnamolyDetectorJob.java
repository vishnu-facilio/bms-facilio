package com.facilio.bmsconsole.jobs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AnalyticsAnamolyContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AnamolySchedulerUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.WeatherUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

public class AnamolyDetectorJob extends FacilioJob {
	private static long ONE_DAY = 1 * 24 * 60 * 60 * 1000L;
	private static long SEVEN_DAYS = 7 * 24 * 60 * 60 * 1000L;
	private static long  FIFTEEN_MINUTES_IN_MILLISEC =  (15)  * 60 * 60 * 1000;

	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		try {
		    //TO DO  .. Feature bit check
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_ANAMOLY_DETECTOR))
			{
				return;
			}
			
			// get the list of all sub meters
			List<EnergyMeterContext> allEnergyMeters=DeviceAPI.getAllEnergyMeters();
			
			// get the list of all meters
			List<ReadingContext> readings= new ArrayList<ReadingContext>();
			
			long now = System.currentTimeMillis();
			long endTime = now - FIFTEEN_MINUTES_IN_MILLISEC; 
			long startTime = endTime - SEVEN_DAYS;
			for(EnergyMeterContext energyMeter: allEnergyMeters) {
				doEnergyMeterAnamolyDetection(energyMeter, startTime, endTime);
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	class AnamolyList {
		Long[] anamolyIDs;
		
		public void setAnamolyIDs(Long[] anamolyIDs) {
			this.anamolyIDs = anamolyIDs;
		}
		
		public Long[] getAnamolyIDs() {
			return anamolyIDs;
		}
	}
	
	private void doEnergyMeterAnamolyDetection(EnergyMeterContext  energyMeterContext, long startTime, long endTime) {
		String moduleName="dummyModuleName";
		String url=AwsUtil.getConfig("dataScienceUrl");
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			List<AnalyticsAnamolyContext> meterReadings = AnamolySchedulerUtil.getAllReadings(moduleName,startTime, endTime, energyMeterContext.getId(), energyMeterContext.getOrgId());

			if(meterReadings.size() > 0) {
				logger.log(Level.INFO, "received readings for ID " + energyMeterContext.getId() + " startTime = " + startTime + " endTime = " + endTime);
			}else {
				logger.log(Level.INFO, "NOT received readings for ID " + energyMeterContext.getId() + " startTime = " + startTime + " endTime = " + endTime);
			}
				
			String jsonInString = mapper.writeValueAsString(meterReadings);
			String result=AwsUtil.doHttpPost(url, null, null, jsonInString);
			
			AnamolyList anamolyList = new GsonBuilder().create().fromJson(result, AnamolyList.class);
			logger.log(Level.INFO, "received result " + result);
			
			String idList = Arrays.toString(anamolyList.getAnamolyIDs());
			idList = "(" + idList.substring(1, idList.length() - 1) + ")";
			
			LinkedHashSet<Long> anamolyIDs=new LinkedHashSet<>(Arrays.asList(anamolyList.getAnamolyIDs()));
			LinkedHashSet<Long> existingAnamolyIds=AnamolySchedulerUtil.getExistingAnamolyIDs(moduleName, idList);
			
			LinkedHashSet<Long> insertIDs= (LinkedHashSet)anamolyIDs.clone();	
			insertIDs.removeAll(existingAnamolyIds);
		
			long currentTimeInMillisec=DateTimeUtil.getCurrenTime();
			
			if(!insertIDs.isEmpty()) {
				insertAnamolyIDs(insertIDs, meterReadings, currentTimeInMillisec, endTime);
			}else {
				logger.info("No IDs found as anamoly");
				
			}
		}catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	class AnamolyIDInsertRow {
		private long id;
		private long orgId;
		private long moduleId;
		private long meterId;
		private long ttime;
		private double energyDelta;
		private long createdTime;
				
		public AnamolyIDInsertRow(long id, long orgId, long moduleId, long meterId ,long ttime, double energyDelta, long createdTime) {
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
	
	private void insertAnamolyIDs(LinkedHashSet<Long> insertIDs, List<AnalyticsAnamolyContext> anamolyContext, long currentTimeInMillisec, long endTimeOfWindow) throws Exception {
		LinkedHashSet<Long> impactedIDs =(LinkedHashSet<Long>) insertIDs.clone();
		List<Map<String, Object>> props = new ArrayList<>();
	
		Iterator<AnalyticsAnamolyContext> iterator = anamolyContext.iterator();
		while (iterator.hasNext() && (!impactedIDs.isEmpty())) {
			AnalyticsAnamolyContext anamolyObject = iterator.next();
			
			if(impactedIDs.contains(anamolyObject.getId())  && (anamolyObject.getTtime() >  endTimeOfWindow - ONE_DAY))  {
				AnamolyIDInsertRow newAnamolyId = new AnamolyIDInsertRow(anamolyObject.getId(), anamolyObject.getOrgId(), 
								anamolyObject.getModuleId(), anamolyObject.getMeterId(), anamolyObject.getTtime(), anamolyObject.getEnergyDelta(), currentTimeInMillisec);
				
				props.add(FieldUtil.getAsProperties(newAnamolyId));
				impactedIDs.remove(anamolyObject.getId());
			}
		}
		
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getAnalyticsAnamolyIDListModule().getTableName())
				.fields(FieldFactory.getAnamolyIDInsertFields())
				.addRecords(props);
		insertRecordBuilder.save();
	}
	
	private Set<Long> convertStringToAnamolyIDs(String idList) {
		Long splitLongs[] = Arrays.asList(idList.split(",")).stream().map(Long::valueOf).toArray(Long[]::new);
		return new HashSet<Long>(Arrays.asList(splitLongs));
	}
}