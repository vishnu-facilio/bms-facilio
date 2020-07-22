package com.facilio.bmsconsole.jobs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.services.s3.model.S3Object;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class AddHistoricalMLReadingsJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(AddHistoricalMLReadingsJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception{
		LOGGER.info("AddHistoricalMLReadingsJob started");
		try(
				ByteArrayOutputStream result1 = new ByteArrayOutputStream();
				ByteArrayOutputStream result2 = new ByteArrayOutputStream();
				) {
			org.json.simple.JSONObject props = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			String ratioCheckFileName = props.get("ratioCheckFileName").toString();
			String checkGamFileName = props.get("checkGamFileName").toString();

//			String home = System.getProperty("user.home");
//			File ratioCheckFile=new File(home+"/javeed/"+ratioCheckFileName);
//			File checkGamFile=new File(home+"/javeed/"+checkGamFileName);


			String bucket = FacilioProperties.getAnomalyBucket();
			String rcfilePath = FacilioProperties.getAnomalyBucketDir() + File.separator + ratioCheckFileName;
			String cgfilePath = FacilioProperties.getAnomalyBucketDir() + File.separator + checkGamFileName;

			try (
				S3Object rc_so = AwsUtil.getAmazonS3Client().getObject(bucket, rcfilePath);
				InputStream rc_is = rc_so.getObjectContent();

				S3Object cg_so = AwsUtil.getAmazonS3Client().getObject(bucket, cgfilePath);
				InputStream cg_is = cg_so.getObjectContent();
			) {

				LOGGER.info("RatioCheck is :: " + rc_is);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = rc_is.read(buffer)) != -1) {
					result1.write(buffer, 0, length);
				}

				String rcStr = result1.toString(StandardCharsets.UTF_8.name());

				System.out.println("333 rc:: " + rcStr);
				LOGGER.info("RatioCheck String :: " + rcStr);

				LOGGER.info("CheckGam is :: " + cg_is);

				byte[] buffer1 = new byte[1024];
				int length1;
				while ((length1 = cg_is.read(buffer1)) != -1) {
					result2.write(buffer1, 0, length1);
				}

				String cgStr = result2.toString(StandardCharsets.UTF_8.name());

				System.out.println("333 cg:: " + cgStr);
				LOGGER.info("CheckGam String :: " + cgStr);

				List<String> str = new ArrayList<String>(Arrays.asList(rcStr, cgStr));
				for (String result : str) {
	//			List<File> files = new ArrayList<File>(Arrays.asList(ratioCheckFile,checkGamFile));
	//			for(File file:files){
	//				String result = FileUtils.readFileToString(file, "UTF-8");
					JSONArray arr = new JSONArray(result);
					for (int i = 0; i < arr.length(); i++) {

						JSONObject resultObj = (JSONObject) arr.get(i);
						Long mlId = Long.parseLong(resultObj.get("ml_id").toString());
						Context context = new FacilioContext();
						MLContext mlContext = MLUtil.getMLContext(mlId).get(0);
						mlContext.setResult(resultObj.toString());
						context.put(FacilioConstants.ContextNames.ML, mlContext);
						FacilioChain c = FacilioChainFactory.addHistoricMLReadingsChain();
						c.execute(context);
					}
				}
				LOGGER.info("AddHistoricalMLReadingsJob completed");
			}
		}catch(Exception e){
			LOGGER.fatal("Error in AddHistoricalMLReadingsJob"+e);
			throw e;
		}
		
	}

}
