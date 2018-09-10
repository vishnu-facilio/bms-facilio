package com.facilio.bmsconsole.jobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.context.AnalyticsAnomalyConfigContext;
import com.facilio.bmsconsole.context.AnalyticsAnomalyContext;
import com.facilio.bmsconsole.context.TemperatureContext;
import com.facilio.bmsconsole.util.AnomalySchedulerUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.fs.S3FileStore;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RefreshAnomalyModelJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(RefreshAnomalyModelJob.class.getName());

	@Override
	public void execute(JobContext jc) {
		try {
			// TO DO .. Feature bit check
			if (!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_ANOMALY_DETECTOR)) {
				logger.log(Level.INFO, "RefreshAnomalyJob: Feature BITS is not enabled");
				return;
			}else {
				logger.log(Level.INFO, "RefreshAnomalyJob: Feature BITS is enabled");
			}

			logger.log(Level.INFO, "RefreshAnomalyJob: Getting energy meters ");

			List<AnalyticsAnomalyConfigContext> meterConfigurations=null;
			String moduleName="dummyModuleName";
			try {
				meterConfigurations = AnomalySchedulerUtil.getAllAssetConfigs(moduleName,  jc.getOrgId());
				logger.log(Level.INFO, "meters configured  = " + (meterConfigurations.size()));
			}catch (Exception e) {
				e.printStackTrace();
			}

			long midnightTimeInMillisec = DateTimeUtil.getDayStartTime();
			
			for(AnalyticsAnomalyConfigContext meterContext: meterConfigurations) {
				long startTime = 0;
				
				if (!meterContext.getStartDateMode()) {
					startTime = midnightTimeInMillisec - (meterContext.getHistoryDays() * 24 * 60 * 60 * 1000L);
				}else {
					startTime = DateTimeUtil.getDayStartTime(meterContext.getStartDate(), "yyyy-MM-dd");
				}
				
				logger.log(Level.INFO, "RefreshAnomalyJob " + meterContext.getMeterId() + " start: " + startTime +  "end:" + midnightTimeInMillisec);
				buildEnergyAnomalyModel(meterContext, startTime, midnightTimeInMillisec);
				logger.log(Level.INFO, "RefreshAnomalyJob over ");
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "RefreshAnomalyJob: Exception " + e.getMessage());
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void writeEnergyReadingFile(String moduleName, long meterID, long orgID, long startTime,
			long endTime, String energyReadingFileName) throws Exception {
		logger.log(Level.INFO, " inside writeEnergyReadingFile");
		List<AnalyticsAnomalyContext> meterReadings = AnomalySchedulerUtil.getAllEnergyReadings(startTime,
				endTime,  meterID, orgID);

		if (meterReadings.size() == 0) {
			logger.log(Level.INFO, "NOT received readings for ID " +  meterID + " startTime = "
					+ startTime + " endTime = " + endTime);
			return;
		}else {
			logger.log(Level.INFO, " received " + meterReadings.size() + " readings for ID " +  meterID + " startTime = "
					+ startTime + " endTime = " + endTime);
		}

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

	private void writeWeatherReadingFile(String moduleName, long orgID, long startTime,
			long endTime, String weatherReadingFileName) throws Exception {
		logger.log(Level.INFO, " inside writeWeatherReadingFile");
		List<TemperatureContext> temperatureContext = AnomalySchedulerUtil.getAllTemperatureReadings(moduleName,
				startTime, endTime, orgID);

		File temperatureFile = new File(weatherReadingFileName);
		BufferedWriter temperatureWriter = new BufferedWriter(new FileWriter(temperatureFile));

		String temperatureInfo = "ID,TTIME,TEMPERATURE\n";

		for (TemperatureContext reading : temperatureContext) {

			String line = reading.toString() + "\n"; 
			//logger.log(Level.INFO, line);
			temperatureInfo += line;
		}

		temperatureWriter.write(temperatureInfo);
		temperatureWriter.close();
	}

	private void buildEnergyAnomalyModel(AnalyticsAnomalyConfigContext meterContext, long startTime, long endTime) {
		String moduleName = "dummyModuleName";
		ObjectMapper mapper = new ObjectMapper();
		String postURL = AwsUtil.getConfig("anomalyCheckServiceURL") + "/refreshAnomalyModel";

		logger.log(Level.INFO, " RefreshAnomalyModel : " + startTime + "  "  + endTime);
		
		try {
			String meterName = meterContext.getMeterId() + "";
			String tempDir = AwsUtil.getConfig("anomalyTempDir");
			
			long currentTimeInSecs=System.currentTimeMillis() / 1000;
			
			String energyBaseFileName = meterName + "_" + currentTimeInSecs + "_meter.txt";
			String weatherBaseFileName = meterName + "_" + currentTimeInSecs +  "_weather.txt";
			
			String energyFileName = tempDir +  File.separator + energyBaseFileName;
			String weatherFileName = tempDir +  File.separator + weatherBaseFileName; 
			
			long meterID =meterContext.getMeterId();
			long organisationID=meterContext.getOrgId();
					
			writeEnergyReadingFile(moduleName, meterID, organisationID, startTime, endTime, energyFileName);
			writeWeatherReadingFile(moduleName, organisationID, startTime, endTime, weatherFileName);
	
			logger.log(Level.INFO, " RefreshAnomalyModel :  files written ");
			
			
			String bucket = AwsUtil.getConfig("anomalyBucket");
			String filePath = AwsUtil.getConfig("anomalyBucketDir");
	
			String meterFileUrl="";
			String weatherFileUrl="";
			if ("development".equalsIgnoreCase(AwsUtil.getConfig("environment"))) {
				weatherBaseFileName = "1256_1530861012_weather.txt";
				energyBaseFileName = "1256_1530861012_meter.txt";
				meterFileUrl = "http://localhost:8000/" + energyBaseFileName;
				weatherFileUrl = "http://localhost:8000/" + weatherBaseFileName;
			}else {
				logger.log(Level.INFO, " RefreshAnomalyModel :  files written ");
				meterFileUrl = S3FileStore.getURL(bucket, filePath + File.separator + energyBaseFileName , new File(energyFileName));
				weatherFileUrl = S3FileStore.getURL(bucket, filePath + File.separator + weatherBaseFileName, new File(weatherFileName));
				logger.log(Level.INFO, " RefreshAnomalyModel :  files written " + meterFileUrl + " " + weatherFileUrl);
			}
			
			BuildAnomalyModelPostData postData=new BuildAnomalyModelPostData();
			postData.timezone = AccountUtil.getCurrentOrg().getTimezone();
			postData.readingFile=meterFileUrl;
			postData.temperatureFile=weatherFileUrl;
			postData.organizationID=AccountUtil.getCurrentOrg().getId();
			postData.meterID = meterContext.getMeterId();
			postData.constant1 = meterContext.getConstant1();
			postData.constant2 = meterContext.getConstant2();
			postData.maxDistance = meterContext.getMaxDistance();
			postData.dimension1 = meterContext.getDimension1Buckets();
			postData.dimension2 = meterContext.getDimension2Buckets();
			postData.dimension1Value =  meterContext.getDimension1Value();
			postData.dimension2Value =  meterContext.getDimension2Value();
			postData.xAxisDimension = meterContext.getxAxisDimension();
			postData.yAxisDimension = meterContext.getyAxisDimension();
			postData.outlierDistance =meterContext.getOutlierDistance();
			
			String jsonInString = mapper.writeValueAsString(postData);
			logger.log(Level.INFO, "RefreshAnomalyJob:  post body is " + jsonInString);
			
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type","application/json");
			String result=AwsUtil.doHttpPost(postURL, headers, null, jsonInString);
			logger.log(Level.INFO, " result is " + result);
		} catch (Exception e) {
			logger.log(Level.INFO, "RefreshAnomalyJob: Exception " + e.getMessage());
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	class BuildAnomalyModelPostData {
		public String getTimezone() {
			return timezone;
		}
		public String getReadingFile() {
			return readingFile;
		}
		public String getTemperatureFile() {
			return temperatureFile;
		}
		public Long getOrganizationID() {
			return organizationID;
		}
		public Long getMeterID() {
			return meterID;
		}
		
		String timezone;
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
		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}
		public void setReadingFile(String readingFile) {
			this.readingFile = readingFile;
		}
		public void setTemperatureFile(String temperatureFile) {
			this.temperatureFile = temperatureFile;
		}
		public void setOrganizationID(Long organizationID) {
			this.organizationID = organizationID;
		}
		public void setMeterID(Long meterID) {
			this.meterID = meterID;
		}
		public double getOutlierDistance() {
			return outlierDistance;
		}
		public void setOutlierDistance(double outlierDistance) {
			this.outlierDistance = outlierDistance;
		}

		String readingFile;
		String temperatureFile;
		Long organizationID;
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
		double outlierDistance;
		List<AnalyticsAnomalyContext> energyData;
		List<TemperatureContext> temperatureData;
	}
}