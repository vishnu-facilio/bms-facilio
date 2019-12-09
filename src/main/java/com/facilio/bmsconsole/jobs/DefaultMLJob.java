package com.facilio.bmsconsole.jobs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DefaultMLJob extends FacilioJob 
{
	private static final Logger LOGGER = Logger.getLogger(DefaultMLJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception 
	{	
		try{
			LOGGER.info("Inside DefaultMLJob, JOB ID :"+jc.getJobId());
			List<MLContext> mlContextList = MLUtil.getMlContext(jc);
			List<MLContext> filteredmlContextList = mlContextList.stream().filter(ml->ml!=null).collect(Collectors.toList());
			
			for(MLContext mlContext:filteredmlContextList)
			{
				LOGGER.info("mlContext"+mlContext.getId());
				mlContext.setPredictionTime(jc.getExecutionTime());
				FacilioChain chain = FacilioChainFactory.getMLModelBuildingChain();
				FacilioContext context = chain.getContext();
				
				try{
					JSONObject reportProps= getJobProps("MLReportJob");
					if(reportProps.containsKey("fileId") && Long.parseLong(reportProps.get("fileId").toString()) > 0){
						Long fileId = Long.parseLong(reportProps.get("fileId").toString());
					    context.put("fileId",  fileId);
					    addContent(fileId, "Starting job : JOBID : "+jc.getJobId()+" JOBNAME : "+jc.getJobName()+" MLID : "+mlContext.getId()+" MODELPATH : "+mlContext.getModelPath()+" EXECUTION_TIME : "+jc.getExecutionTime());
					}
				}catch(Exception e){
					LOGGER.fatal("Error while writing job start in mlJobReport"+e);
				}
				
				context.put(FacilioConstants.ContextNames.ML, mlContext);
				chain.execute();
				
				try{
					if(context.containsKey("fileId")){
						Long fileId = Long.parseLong(context.get("fileId").toString());
						addContent(fileId, "Ending job : JOBID : "+jc.getJobId()+" JOBNAME : "+jc.getJobName()+" EXECUTION_TIME : "+jc.getExecutionTime());
					}
				}catch(Exception e){
					LOGGER.fatal("Error while ending job start in mlJobReport"+e);
				}
				
			}
			LOGGER.info("Finished DefaultMLJob, JOB ID :"+jc.getJobId());
		}catch(Exception e){
			LOGGER.fatal("Error in DefaultMLJob"+e);
		}
	}
	public static void addContent(Long fileId,String contentToAdd) throws Exception{
		FileStore fs = FacilioFactory.getFileStore();
		InputStream inputStream = fs.readFile(fileId);
		LOGGER.info("File readed");
		InputStreamReader isReader = new InputStreamReader(inputStream);
	    BufferedReader reader = new BufferedReader(isReader);
	    StringBuffer sb = new StringBuffer();
	    String str;
	    while((str = reader.readLine())!= null){
	       sb.append(str);
	    }
	    LOGGER.info("Sb before adding"+sb.toString());
	    sb.append(contentToAdd);
	    LOGGER.info("Sb after adding"+sb.toString());
		fs.addFile("ML_JOB_REPORT", sb.toString(), "application/text");
		LOGGER.info("File replaced successfully");
	}

	public static JSONObject getJobProps (String jobName) throws Exception {
		FacilioModule module = ModuleFactory.getCommonJobPropsModule();
		List<FacilioField> fields = FieldFactory.getCommonJobPropsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField jobNameField = fieldMap.get("jobName");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(jobNameField, jobName, StringOperators.IS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			String jsonStr = (String) props.get(0).get("props");
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(jsonStr);
		}
		return null;
	}

}
