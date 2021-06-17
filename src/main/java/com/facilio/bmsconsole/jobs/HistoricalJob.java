package com.facilio.bmsconsole.jobs;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facilio.bmsconsole.context.HistoricalJobContext;
import com.facilio.bmsconsole.context.HistoricalJobMLContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class HistoricalJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(HistoricalJob.class.getName());

	HistoricalJobContext job = new HistoricalJobContext();
	@Override
	public void execute(JobContext jc) throws Exception 
	{
		try{
			LOGGER.info("Historical Job started.JOB ID : "+jc.getJobId());
			
			job.setJobId(jc.getJobId());
			job.setJobName(jc.getJobName());
			
			startJob(jc);
			execute();
			
			LOGGER.info("Historical Job Completed.JOB ID : "+jc.getJobId());
		}catch(Exception e){
			LOGGER.fatal(" Error in Historical Job"+e);
			throw e;
		}
	}

	private void startJob(JobContext jc) throws Exception {
		
		JSONObject props=new JSONObject(BmsJobUtil.getJobProps(job.getJobId(), job.getJobName()).toString());
		job.setStartTime(Long.parseLong(props.get("startTime").toString()));
		job.setEndTime(Long.parseLong(props.get("endTime").toString()));
		job.setSubIntervalTime(Long.parseLong(props.get("subIntervalTime").toString()));
		if(props.has("executionEndTime")){
			job.setExecutionEndTime(props.getLong("executionEndTime"));
		}
		
		JSONArray mlArray = new JSONArray(props.get("MLList").toString());
		Queue<HistoricalJobMLContext> mlList =  new LinkedList<>();
		
		for(int i=0;i<mlArray.length();i++)
		{
			JSONObject ml = (JSONObject) mlArray.get(i);
			long mlId = Long.parseLong(ml.get("mlId").toString());
			long executionInterval = Long.parseLong(ml.get("executionInterval").toString());
			HistoricalJobMLContext ml1 = new HistoricalJobMLContext();
			ml1.setMlId(mlId);
			ml1.setExecutionInterval(executionInterval);
			mlList.add(ml1);
		}
		
		job.setMlList(mlList);
		job.setCheckGamParent(new JSONArray(props.getString("checkGamParent")));
		job.setCheckGamModuleid(props.getLong("checkGamModuleid"));
		job.setCheckratioModuleid1(props.getLong("checkratioModuleid1"));
	}

	private void execute() throws Exception {
		long startTime = job.getExecutionEndTime() > 0 ? job.getExecutionEndTime() : job.getStartTime();
		long endTime = startTime + job.getSubIntervalTime();
		
		if(endTime <= job.getEndTime()){
			JSONArray mlArray = new JSONArray();
			for(HistoricalJobMLContext ml : job.getMlList()){
				long mlStartTime = startTime;
				while(mlStartTime < endTime){
					JSONObject mlJson = new JSONObject();
					mlJson.put("mlId", ml.getMlId());
					mlJson.put("startTime", mlStartTime);
					mlArray.put(mlJson);
					mlStartTime = mlStartTime + ml.getExecutionInterval();
				}
			}
			
			org.json.simple.JSONObject props = new org.json.simple.JSONObject();
			props.put("ml", mlArray);
			props.put("endTime", endTime);
			JSONObject jobJson = new JSONObject();
			jobJson.put("endTime", job.getEndTime());
			jobJson.put("jobId", job.getJobId());
			jobJson.put("startTime", job.getStartTime());
			jobJson.put("subIntervalTime", job.getSubIntervalTime());
			jobJson.put("jobName", job.getJobName());
			jobJson.put("checkGamParent", job.getCheckGamParent().toString());
			jobJson.put("checkGamModuleid", job.getCheckGamModuleid());
			jobJson.put("checkratioModuleid1", job.getCheckratioModuleid1());
			
			JSONArray mls = new JSONArray();
			for(HistoricalJobMLContext ml:job.getMlList()){
				JSONObject mljson = new JSONObject();
				mljson.put("mlId", ml.getMlId());
				mljson.put("executionInterval", ml.getExecutionInterval());
				mls.put(mljson);
			}
			
			jobJson.put("mlList", mls);
			props.put("parentJob",jobJson.toString());
			if(BmsJobUtil.getJobProps(job.getJobId(), "SplitHistoricalJob") != null){
				BmsJobUtil.deleteJobWithProps(job.getJobId(), "SplitHistoricalJob");
	        }
		
			BmsJobUtil.scheduleOneTimeJobWithProps(job.getJobId(), "SplitHistoricalJob", System.currentTimeMillis()/1000 , "priority", props);
	    }
	}
	

}
