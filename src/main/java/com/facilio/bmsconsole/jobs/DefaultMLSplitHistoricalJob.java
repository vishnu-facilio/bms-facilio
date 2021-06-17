package com.facilio.bmsconsole.jobs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
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



public class DefaultMLSplitHistoricalJob extends FacilioJob
{
	private static final Logger LOGGER = Logger.getLogger(DefaultMLSplitHistoricalJob.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobContext jc) 
	{
		try {
			JSONObject props=BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			JSONObject child=null;
			long childExecutionTime=0l;
			List<MLContext> childMlList=null;
			if(props.containsKey("child"))
			{
				child=(JSONObject) props.get("child");
				childExecutionTime = Long.parseLong(child.get("executionTime").toString());
				String[] childMlid = child.get("childMlid").toString().split(",");
				childMlList = getList(childMlid);
			}
			long jobStartTime = Long.parseLong(props.get("startTime").toString());
			long startTime = Long.parseLong(props.get("executionStartTime").toString());
			long endTime = Long.parseLong(props.get("endTime").toString());
			long executionTime = Long.parseLong(props.get("executionTime").toString());
			long loopTime=startTime+executionTime;
			
			String[] parentMlid=((String) props.get("parentMlid")).split(",");
			List<MLContext> parentMlList = getList(parentMlid);
			
			while((startTime < loopTime)&&(startTime<endTime))
			{
				LOGGER.info("Executing Job for MLID: "+jc.getJobId()+" and startTime:"+new Date(startTime)+" day count : "+(((startTime-jobStartTime)/executionTime)+1));
				try {
				executeParent(parentMlList,startTime);
				if(child != null && !child.isEmpty())
				{
					ChildLoop(childMlList,startTime,(startTime + executionTime),childExecutionTime);
				}
				}
				catch (Exception e)
				{
					LOGGER.info("Exception in Loop "+e);
				}
				startTime = startTime + executionTime;
			}			
			props.put("executionStartTime", startTime);
			
			Map<String,Object> prop=new HashMap<String, Object>();
			prop.put("props", props.toString());
			
			updateCommonJob(jc.getJobId(),jc.getJobName(),prop);
			
			if(startTime>=endTime) 
			{
				updateJobs(jc.getJobId(),jc.getJobName());
			}			
			
			LOGGER.info("finished HistoricalJob for "+(((startTime-jobStartTime)/executionTime)+1)+" day");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LOGGER.error("Job execution failed for Job :"+jc.getJobId()+" : "+ jc.getJobName(),e);
		}
		
	}	
	public static void updateJobs(long jbid,String jbname) throws SQLException 
	{
		Map<String,Object> prop=new HashMap<String, Object>();
		prop.put("active", false);
		prop.put("isPeriodic", false);
		prop.put("period", 0);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getJobsModule().getTableName())
				.fields(FieldFactory.getJobFields())
				.andCondition(CriteriaAPI.getCondition("JOBID", "jobid", ""+jbid, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("JOBNAME", "jobname", "'"+jbname+"'", NumberOperators.EQUALS));
		updateBuilder.update(prop);
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
	
	
	public List<MLContext> getList(String[] mlid) throws NumberFormatException, Exception
	{
		List<MLContext> mlContextList = new ArrayList<MLContext>();
		for(String MLID:mlid)
		{
			mlContextList.addAll(MLUtil.getMLContext(Long.parseLong(MLID)));
		}
		return mlContextList;
	}
	
	public void executeParent(List<MLContext> parentMlList, long executionEndTime) throws NumberFormatException, Exception
	{
		executeLoop(parentMlList,executionEndTime);
	}
	
	public void ChildLoop(List<MLContext> childMlList, long startTime, long endTime, long childExecutionTime) throws Exception 
	{
		while(startTime < endTime)
		{
			startTime = startTime + childExecutionTime;
			executeLoop(childMlList,startTime);
		}	
	}
	
	public void executeLoop(List<MLContext> mlContextList, long executionEndTime) throws Exception 
	{
		LOGGER.info("inside execute historical Job "+mlContextList.get(0).getId());
		for(MLContext mlContext:mlContextList)
		{
			if(mlContext == null)
			{
				continue;
			}
		
			mlContext.setHistoric(true);
			mlContext.setExecutionEndTime(executionEndTime);
			mlContext.setPredictionTime(executionEndTime);
			
			Context context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ML, mlContext);
			
			FacilioChain c = FacilioChainFactory.getMLModelBuildingChain();
			c.execute(context);
		}
	}
}