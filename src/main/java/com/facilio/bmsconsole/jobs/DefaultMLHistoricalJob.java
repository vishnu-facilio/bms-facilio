package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.GenerateMLModelCommand;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;


//Get JSONObject Props from db
//Execute parentML loop Itr = mLid 
//Inside ParentML Execute childML loop Itr = ChildMlid

public class DefaultMLHistoricalJob extends FacilioJob
{
	private static final Logger LOGGER = Logger.getLogger(GenerateMLModelCommand.class.getName());

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
			long startTime = Long.parseLong(props.get("startTime").toString());
			long endTime = Long.parseLong(props.get("endTime").toString());
			long executionTime = Long.parseLong(props.get("executionTime").toString());
			
			
			String[] parentMlid=((String) props.get("parentMlid")).split(",");
			List<MLContext> parentMlList = getList(parentMlid);
			
			
			while(startTime < endTime)
			{
				executeParent(parentMlList,startTime);
				if(child != null && !child.isEmpty())
				{
					ChildLoop(childMlList,startTime,(startTime + executionTime),childExecutionTime);
				}
				startTime = startTime + executionTime;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LOGGER.error("Job execution failed for Job :"+jc.getJobId()+" : "+ jc.getJobName(),e);
		}
		
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
			executeLoop(childMlList,startTime);
			startTime = startTime + childExecutionTime;
		}	
	}
	
	public void executeLoop(List<MLContext> mlContextList, long executionEndTime) throws Exception 
	{
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
			
			Chain c = FacilioChainFactory.getMLModelBuildingChain();
			c.execute(context);
		}
	}
}