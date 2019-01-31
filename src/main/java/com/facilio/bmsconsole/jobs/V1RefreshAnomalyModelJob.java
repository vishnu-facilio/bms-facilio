package com.facilio.bmsconsole.jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.context.AnomalyAssetConfigurationContext;
import com.facilio.bmsconsole.context.AnalyticsAnomalyContext;
import com.facilio.bmsconsole.context.AnomalyAssetConfigurationContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.TemperatureContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AssetAnomalyUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.fs.S3FileStore;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class V1RefreshAnomalyModelJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(RefreshAnomalyModelJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		try {
			// TO DO .. Feature bit check
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_ANOMALY_DETECTOR)) {
				logger.log(Level.INFO, "RefreshAnomalyJob: Feature Bit is not enabled");
				return;
			} else {
				logger.log(Level.INFO, "RefreshAnomalyJob: Feature Bit is enabled");
			}

			logger.log(Level.INFO, "RefreshAnomalyJob: Getting energy meters ");

			// Collect all asset configuration details
			Map<Long, AnomalyAssetConfigurationContext> meterConfigurations = null;
			long orgID = jc.getOrgId();
			String moduleName = "dummyModuleName";
			try {
				meterConfigurations = AssetAnomalyUtil.getAllAssetConfigWithDefaults(moduleName, jc.getOrgId());
				logger.log(Level.INFO, "meter configuration count = " + (meterConfigurations.size()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Collect all configuration of energy meters
			List<EnergyMeterContext> energyContextList = AssetAnomalyUtil
					.getAllConfiguredEnergyMeters(meterConfigurations);
			
			Set<Long> siteIdList = energyContextList.stream().map(s -> s.getSiteId()).collect(Collectors.toSet());
			
			logger.log(Level.INFO, "number of energy meters = " + (energyContextList.size()));
			logger.log(Level.INFO, "number of sites = " + (siteIdList.size()));
			
			long endTime = DateTimeUtil.getDayStartTime(); // start of current day

			// Collect the list of energy meters
			for (Long siteId : siteIdList) {
				List<EnergyMeterContext> subEnergyMeterContextList = energyContextList.stream()
						.filter(s -> s.getSiteId() == siteId).collect(Collectors.toList());

				buildEnergyAnomalyModel(subEnergyMeterContextList, meterConfigurations, endTime, siteId, orgID);
			}

		} catch (Exception e) {
			logger.log(Level.INFO, "RefreshAnomalyJob: Exception " + e.getMessage());
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
		    
		 public void buildEnergyAnomalyModel(List<EnergyMeterContext> subEnergyMeterContextList,
				 Map<Long, AnomalyAssetConfigurationContext> meterConfigurations, long endTime,long siteId, long orgID) throws Exception {
			 
			 String bucket = AwsUtil.getConfig("anomalyBucket");
			 String filePath = AwsUtil.getConfig("anomalyBucketDir");
 			 String tempDir =  AwsUtil.getConfig("anomalyTempDir");
             Map<Long, String> siteIdToWeatherMapping = new HashMap<>();
 			
			 for (EnergyMeterContext eachEnergyMeterContext: subEnergyMeterContextList) {
		    		long meterID = eachEnergyMeterContext.getId();
		    		
		    		logger.log(Level.INFO, "processing " +  meterID);			
		    		AnomalyAssetConfigurationContext meterConfigContext =  AssetAnomalyUtil.getAssetConfig(meterID, meterConfigurations);
		    		long startTime = AssetAnomalyUtil.getStartTime(endTime, meterConfigContext);

		    		List<TemperatureContext> allTemperatureReadings=AssetAnomalyUtil.getWeatherReadingsForOneSite(startTime, endTime, siteId);
					
		    		if(allTemperatureReadings == null || allTemperatureReadings.size() == 0) {
		    			logger.log(Level.INFO, "NOT received readings for siteID " +  siteId);
	    				continue;
		    		}
		    		
		    		String s3WeatherFileUrl = null;
		    		String s3EnergyFileUrl = null;
		    		
		    		if(siteIdToWeatherMapping.containsKey(siteId)){
		    			s3WeatherFileUrl = siteIdToWeatherMapping.get(siteId);
		    		}
		    		else {
		    			String weatherBaseFileName = AssetAnomalyUtil.getWeatherFileName(meterID, siteId, orgID);
		    			String weatherAbsoluteFilePath = tempDir + "/" + weatherBaseFileName;
		    			writeWeatherReadingFile(weatherAbsoluteFilePath , allTemperatureReadings);

		    			if(AssetAnomalyUtil.isDevEnviroment()) {
		    				s3WeatherFileUrl = "http://localhost:8000/" + weatherBaseFileName;
		    			}else {
		    				s3WeatherFileUrl = S3FileStore.getURL(bucket, filePath + File.separator + weatherBaseFileName, new File(weatherAbsoluteFilePath));
		    			}
		    			
						siteIdToWeatherMapping.put(siteId, s3WeatherFileUrl);		
		    		}
		    		
		    		List<AnalyticsAnomalyContext > meterReadings = AssetAnomalyUtil.getAllEnergyReadings(startTime, endTime, meterID, orgID);
		    		if (meterReadings.size() == 0) {
		    				logger.log(Level.INFO, "NOT received readings for ID " +  meterID + " startTime = "
		    						+ startTime + " endTime = " + endTime);
		    				continue;
		    		}
		    		
		    		logger.log(Level.INFO, " received " + meterReadings.size() + " readings for ID " +  meterID + " startTime = "
		    						+ startTime + " endTime = " + endTime);

		    		String energyBaseFileName = AssetAnomalyUtil.getEnergyFileName(meterID,orgID);
	    			String energyAbsoluteFilePath = tempDir + "/" + energyBaseFileName;
	    			writeEnergyReadingFile(energyAbsoluteFilePath , meterReadings);

	    			if(AssetAnomalyUtil.isDevEnviroment()) {
	    				s3EnergyFileUrl = "http://localhost:8000/" + energyBaseFileName;
	    			}else {
	    				s3EnergyFileUrl = S3FileStore.getURL(bucket, filePath + File.separator + energyBaseFileName, new File(energyAbsoluteFilePath));
	    			}

	    			long createdTime = System.currentTimeMillis();
	    			String createdDate = DateTimeUtil.getFormattedTime(createdTime,"yyyy-MM-dd");
	    			
	    			//s3URLInsertRow(meterID,  orgID, createdDate, createdTime, 0L, s3EnergyFileUrl, 1);
	    			//s3URLInsertRow(meterID,  orgID, createdDate, createdTime, 1L, s3WeatherFileUrl, 1);

	    			doRefreshAnomalyModel(meterID, orgID, s3EnergyFileUrl, s3WeatherFileUrl,meterConfigContext);
	    			
	    			try {
	    				int waitTimeInSecs = Integer.parseInt(AwsUtil.getConfig("anomalyRefreshWaitTimeInSeconds"));
	    				
	    				Thread.sleep(1000 * waitTimeInSecs);
	    				if(waitTimeInSecs > 0) {
	    				}
	    			}catch (Exception e) {
	    				logger.log(Level.INFO, "RefreshAnomalyJob: Exception " + e.getMessage());
	    			}
			 }
	}

	private void doRefreshAnomalyModel(long meterID, long orgID, String s3EnergyFileUrl, String s3WeatherFileUrl, 
			AnomalyAssetConfigurationContext configContext) throws JsonProcessingException,IOException {
		BuildAnomalyModelPostData postData=new BuildAnomalyModelPostData();
		postData.timezone = AccountUtil.getCurrentOrg().getTimezone();
		postData.readingFile=s3EnergyFileUrl;
		postData.temperatureFile=s3WeatherFileUrl;
		postData.meterID = meterID;
		postData.dimension1 = configContext.getDimension1Buckets();
		postData.dimension1Value = configContext.getDimension1Value();
		postData.timezone = AccountUtil.getCurrentOrg().getTimezone();
		postData.meterInterval = configContext.getMeterInterval();
		postData.adjustmentPercentage = configContext.getAdjustmentPercentage();
		postData.tableValue = configContext.getTableValue();
		postData.orderRange = configContext.getOrderRange();
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(postData);
		logger.log(Level.INFO, "refresh anomaly post: " + jsonInString);
		
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type","application/json");
		String postURL = AwsUtil.getConfig("anomalyCheckServiceURL") + "/refreshGamModel";
		String result=AwsUtil.doHttpPost(postURL, headers, null, jsonInString);
		logger.log(Level.INFO, " result is " + result);
	}

	private void writeEnergyReadingFile(String energyReadingFileName, List<AnalyticsAnomalyContext> meterReadings) throws Exception {
		File outputFile = new File(energyReadingFileName);
		BufferedWriter meterReadingWriter = new BufferedWriter(new FileWriter(outputFile));

		String readingInfo = "ID,TTIME,TOTAL_ENERGY_CONSUMPTION_DELTA\n";
		for (AnalyticsAnomalyContext reading : meterReadings) {

			String line = reading.toString() + "\n";
			readingInfo += line;		
		}

		meterReadingWriter.write(readingInfo);
		meterReadingWriter.close();
	}

	private void writeWeatherReadingFile(String weatherReadingFileName,List<TemperatureContext> temperatureContext) throws Exception {
		logger.log(Level.INFO, " inside writeWeatherReadingFile");
		File temperatureFile = new File(weatherReadingFileName);
		BufferedWriter temperatureWriter = new BufferedWriter(new FileWriter(temperatureFile));

		String temperatureInfo = "ID,TTIME,TEMPERATURE\n";

		for (TemperatureContext reading : temperatureContext) {
			String line = reading.toString() + "\n"; 
			temperatureInfo += line;
		}

		temperatureWriter.write(temperatureInfo);
		temperatureWriter.close();
	}

	private void s3URLInsertRow(long meterId, long orgID, String createdDate, long createdTime, long fileType, String s3URL, int isValid)
			throws Exception {
        List<Map<String, Object>> props = new ArrayList<>();
        S3URLInsertRow s3URLrow= new S3URLInsertRow (meterId, orgID, createdDate, createdTime, fileType, s3URL, isValid);  	 
        props.add(FieldUtil.getAsProperties(s3URLrow));
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getAnalyticsAnomalyS3URLModule().getTableName())
                    .fields(FieldFactory.getAnomalyS3URLfileds()).addRecords(props);
        insertRecordBuilder.save();
	}

	class BuildAnomalyModelPostData {
		public String getReadingFile() {
			return readingFile;
		}
		public void setReadingFile(String readingFile) {
			this.readingFile = readingFile;
		}
		public String getTemperatureFile() {
			return temperatureFile;
		}
		public void setTemperatureFile(String temperatureFile) {
			this.temperatureFile = temperatureFile;
		}
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

		public long getOrganizationID() {
			return organizationID;
		}
		public void setOrganizationID(long organizationID) {
			this.organizationID = organizationID;
		}
		
		String readingFile;
		String temperatureFile;
		long meterID;
		long organizationID;
		String dimension1;
		String dimension1Value;
		String timezone;
		int meterInterval;
		double adjustmentPercentage;
		double tableValue;
		int orderRange;
	}
}