package com.facilio.bmsconsole.jobs;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.HistoricalJobContext;
import com.facilio.bmsconsole.context.HistoricalJobMLContext;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class SplitHistoricalJob extends FacilioJob 
{
	private static final Logger LOGGER = Logger.getLogger(SplitHistoricalJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception 
	{	
		try{
			
			LOGGER.info("Inside SplitHistoricalJob, JOB ID :"+jc.getJobId());
			
			JSONObject props= new JSONObject(BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName()).toString());
			JSONArray mlArray = new JSONArray(props.get("ml").toString());
			
			for(int i=0;i<mlArray.length();i++){
				
				JSONObject mljson = new JSONObject(mlArray.get(i).toString());
				long mlid = Long.parseLong(mljson.get("mlId").toString());
				long startTime = Long.parseLong(mljson.get("startTime").toString());
				List<MLContext> mlContextList = MLUtil.getMLContext(mlid);
				List<MLContext> filteredmlContextList = mlContextList.stream().filter(ml->ml!=null).collect(Collectors.toList());
				
				for(MLContext mlContext:filteredmlContextList)
				{
					mlContext.setHistoric(true);
					mlContext.setExecutionEndTime(startTime);
					mlContext.setPredictionTime(startTime);
					mlContext.setPredictionTime(jc.getExecutionTime());
					
					FacilioChain chain = FacilioChainFactory.getMLModelBuildingChain();
					FacilioContext context = chain.getContext();
					context.put(FacilioConstants.ContextNames.ML, mlContext);
					chain.execute();
				}
			}
			
			JSONObject jobJson = new JSONObject(props.get("parentJob").toString());
			HistoricalJobContext job = new HistoricalJobContext();
			job.setEndTime(Long.parseLong(jobJson.get("endTime").toString()));
			job.setJobId(Long.parseLong(jobJson.get("jobId").toString()));
			job.setStartTime(Long.parseLong(jobJson.get("startTime").toString()));
			job.setSubIntervalTime(Long.parseLong(jobJson.get("subIntervalTime").toString()));
			job.setJobName(jobJson.get("jobName").toString());
			job.setCheckGamParent(new JSONArray(jobJson.getString("checkGamParent")));
			job.setCheckGamModuleid(jobJson.getLong("checkGamModuleid"));
			job.setCheckratioModuleid1(jobJson.getLong("checkratioModuleid1"));
			
			Queue<HistoricalJobMLContext> mlList = new LinkedList<HistoricalJobMLContext>();
			JSONArray ml = new JSONArray(jobJson.get("mlList").toString());
			for(int i=0;i<ml.length();i++){
				JSONObject mlJson =  new JSONObject(ml.get(i).toString());
				HistoricalJobMLContext ml1 = new HistoricalJobMLContext();
				ml1.setMlId(mlJson.getLong("mlId"));
				ml1.setExecutionInterval(mlJson.getLong("executionInterval"));
				mlList.add(ml1);
			}
			job.setMlList(mlList);
			
			long endTime = Long.parseLong(props.get("endTime").toString());
			
			if(endTime < job.getEndTime()){
				
				reschedule(endTime,job,jobJson);
				initiatingRescheduledJob(job);
				
			}else{
				
				finishJob(job);
				
			}
			
			updateExecutionEndTimeInProps(job.getJobId(),job.getJobName(),endTime);
			
			LOGGER.info("Finished SplitHistoricalJob, JOB ID :"+jc.getJobId());
		}catch(Exception e){
			LOGGER.fatal("Error in SplitHistoricalJob"+e);
			throw e;
		}
	}

	private void initiatingRescheduledJob(HistoricalJobContext job) throws Exception{
		org.json.simple.JSONObject props = new org.json.simple.JSONObject();
		BmsJobUtil.deleteJobWithProps(job.getJobId(), "InitiateSplitJob");
		BmsJobUtil.scheduleOneTimeJobWithProps(job.getJobId(), "InitiateSplitJob",System.currentTimeMillis()/1000 , "priority",props);
	}

	private void finishJob(HistoricalJobContext job) throws Exception{
		if(job.getCheckGamModuleid()>0){
			
			org.json.simple.JSONObject props = new org.json.simple.JSONObject();
			props.put("startTime", job.getStartTime());
			props.put("endTime", job.getEndTime());
			props.put("interval",3600000);
			props.put("checkGamParent",job.getCheckGamParent().toString());
			props.put("checkGamModuleid",job.getCheckGamModuleid());
			props.put("checkratioModuleid1",job.getCheckratioModuleid1());
			
			BmsJobUtil.scheduleOneTimeJobWithProps(job.getJobId(), "AnomalyEventJob",( System.currentTimeMillis() + 60000 )/1000 , "priority", props);
		}
	}
	
	private void reschedule(long endtime,HistoricalJobContext job,JSONObject jobJson) throws Exception{
		long startTime = endtime;
		long endTime = startTime + job.getSubIntervalTime();
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
		props.put("parentJob",jobJson.toString());
		
		BmsJobUtil.deleteJobWithProps(job.getJobId(), "SplitHistoricalJob");
    	BmsJobUtil.scheduleOneTimeJobWithProps(job.getJobId(), "SplitHistoricalJob", System.currentTimeMillis()/1000 , "priority", props);
		
	}
	
	private void updateExecutionEndTimeInProps(long jobId,String jobName,long endTime) throws Exception {
		JSONObject props=new JSONObject(BmsJobUtil.getJobProps(jobId, jobName).toString());
		props.put("executionEndTime", endTime);
		Map<String,Object> prop=new HashMap<String, Object>();
		prop.put("props", props.toString());
		
		updateCommonJob(jobId, jobName, prop);
	}

	public static void updateCommonJob(long jobid,String jobname,Map<String, Object> prop) throws SQLException 
	{
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getCommonJobPropsModule().getTableName())
				.fields(FieldFactory.getCommonJobPropsFields())
				.andCondition(CriteriaAPI.getCondition("JOBID", "jobid", ""+jobid, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("JOBNAME", "jobname", "'"+jobname+"'", NumberOperators.EQUALS));
		updateBuilder.update(prop);
	}

}
