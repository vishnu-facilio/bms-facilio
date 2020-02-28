package com.facilio.bmsconsole.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

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
		try{
			org.json.simple.JSONObject props=BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			String ratioCheckFileName = props.get("ratioCheckFileName").toString();
			String checkGamFileName = props.get("checkGamFileName").toString();
			
			String home = System.getProperty("user.home");
			File ratioCheckFile=new File(home+"/javeed/"+ratioCheckFileName);
			File checkGamFile=new File(home+"/javeed/"+checkGamFileName);
			
			List<File> files = new ArrayList<File>(Arrays.asList(ratioCheckFile,checkGamFile));
			for(File file:files){
				String result = FileUtils.readFileToString(file, "UTF-8");
				JSONArray arr = new JSONArray(result);
				for(int i=0;i<arr.length();i++){

					JSONObject resultObj = (JSONObject) arr.get(i);
					Long mlId= Long.parseLong(resultObj.get("ml_id").toString());
					Context context = new FacilioContext();
					MLContext mlContext=MLUtil.getMLContext(mlId).get(0);
					mlContext.setResult(resultObj.toString());
					context.put(FacilioConstants.ContextNames.ML, mlContext);
					FacilioChain c = FacilioChainFactory.addHistoricMLReadingsChain();
					c.execute(context);
				}
			}
			LOGGER.info("AddHistoricalMLReadingsJob completed");
		}catch(Exception e){
			LOGGER.fatal("Error in AddHistoricalMLReadingsJob"+e);
			throw e;
		}
		
	}

}
