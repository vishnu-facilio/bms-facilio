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
import com.facilio.bmsconsole.context.AnalyticsAnomalyContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.TemperatureContext;
import com.facilio.bmsconsole.util.AnomalySchedulerUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.fs.S3FileStore;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RefreshAnomalyModelJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());

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

			String meters = AwsUtil.getConfig("anomalyMeters");
			Integer anomalyDuration = Integer.parseInt(AwsUtil.getConfig("anomalyDuration"));

			
			// get the list of all sub meters
			List<EnergyMeterContext> allEnergyMeters = DeviceAPI.getSpecificEnergyMeters(meters);

			// long now = System.currentTimeMillis();
			long midnightTimeInMillisec = DateTimeUtil.getDayStartTime();
			long startTime = midnightTimeInMillisec - (anomalyDuration * 24 * 60 * 60 * 1000L);

			logger.log(Level.INFO, "selected Meters ");
			for (EnergyMeterContext energyMeter : allEnergyMeters) {

				// logger.log(Level.INFO, "" + energyMeter.getId());
				buildEnergyAnomalyModel(energyMeter, startTime, midnightTimeInMillisec);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void writeEnergyReadingFile(String moduleName, EnergyMeterContext energyMeterContext, long startTime,
			long endTime, String energyReadingFileName) throws Exception {
		List<AnalyticsAnomalyContext> meterReadings = AnomalySchedulerUtil.getAllReadings(moduleName, startTime,
				endTime, energyMeterContext.getId(), energyMeterContext.getOrgId());

		if (meterReadings.size() == 0) {
			logger.log(Level.SEVERE, "NOT received readings for ID " + energyMeterContext.getId() + " startTime = "
					+ startTime + " endTime = " + endTime);
			return;
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

	private void writeWeatherReadingFile(String moduleName, EnergyMeterContext energyMeterContext, long startTime,
			long endTime, String weatherReadingFileName) throws Exception {

		List<TemperatureContext> temperatureContext = AnomalySchedulerUtil.getAllTemperatureReadings(moduleName,
				startTime, endTime, energyMeterContext.getOrgId());

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

	private void buildEnergyAnomalyModel(EnergyMeterContext energyMeterContext, long startTime, long endTime) {
		String moduleName = "dummyModuleName";
		ObjectMapper mapper = new ObjectMapper();
		String postURL = AwsUtil.getConfig("anomalyCheckServiceURL") + "/refreshAnomalyModel";
		try {
			String meterName = energyMeterContext.getId() + "";
			String tempDir = AwsUtil.getConfig("anomalyTempDir");
			
			long currentTimeInSecs=System.currentTimeMillis() / 1000;
			
			String energyBaseFileName = meterName + "_" + currentTimeInSecs + "_meter.txt";
			String weatherBaseFileName = meterName + "_" + currentTimeInSecs +  "_weather.txt";
			
			String energyFileName = tempDir +  File.separator + energyBaseFileName;
			String weatherFileName = tempDir +  File.separator + weatherBaseFileName; 
			
			writeEnergyReadingFile(moduleName, energyMeterContext, startTime, endTime, energyFileName);
			writeWeatherReadingFile(moduleName, energyMeterContext, startTime, endTime, weatherFileName);
			
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
				meterFileUrl = S3FileStore.getURL(bucket, filePath + File.separator + energyBaseFileName , new File(energyFileName));
				weatherFileUrl = S3FileStore.getURL(bucket, filePath + File.separator + weatherBaseFileName, new File(weatherFileName));
			}
			
			BuildAnomalyModelPostData postData=new BuildAnomalyModelPostData();
			postData.timezone = AccountUtil.getCurrentOrg().getTimezone();
			postData.readingFile=meterFileUrl;
			postData.temperatureFile=weatherFileUrl;
			postData.organizationID=AccountUtil.getCurrentOrg().getId();
			postData.meterID = energyMeterContext.getId();
			
			String jsonInString = mapper.writeValueAsString(postData);
			logger.log(Level.INFO, " post body is " + jsonInString);
			
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type","application/json");
			String result=AwsUtil.doHttpPost(postURL, headers, null, jsonInString);
			logger.log(Level.INFO, " result is " + result);
		} catch (Exception e) {
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
		String readingFile;
		String temperatureFile;
		Long organizationID;
		Long meterID;
	}
}