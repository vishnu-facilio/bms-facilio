package com.facilio.bmsconsole.jobs;

import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		long fileId = -1;
		try{
			JSONObject reportProps= getJobProps("MLReportJob");

			if(reportProps.containsKey("fileId") && Long.parseLong(reportProps.get("fileId").toString()) > 0){
				fileId = Long.parseLong(reportProps.get("fileId").toString());
			}
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
					if(fileId > 0){
						context.put("fileId",  fileId);
					    addContent(fileId, "Starting job : JOBID : "+jc.getJobId()+"| JOBNAME : "+jc.getJobName()+"| MLID : "+mlContext.getId()+"| MODELPATH : "+mlContext.getModelPath()+"| EXECUTION_TIME : "+jc.getExecutionTime());
					}
				}catch(Exception e){
					LOGGER.fatal("Error while writing job start in mlJobReport"+e);
				}
				
				context.put(FacilioConstants.ContextNames.ML, mlContext);
				chain.execute();
				
				try{
					if(fileId > 0){
						addContent(fileId, "Ending job : JOBID : "+jc.getJobId()+"| JOBNAME : "+jc.getJobName()+"| MLID : "+mlContext.getId()+"| MODELPATH : "+mlContext.getModelPath()+"| EXECUTION_TIME : "+jc.getExecutionTime());
					}
				}catch(Exception e){
					LOGGER.fatal("Error while ending job start in mlJobReport"+e);
				}
				
			}
			LOGGER.info("Finished DefaultMLJob, JOB ID :"+jc.getJobId());
		}catch(Exception e){
			LOGGER.fatal("Error in DefaultMLJob"+e);
			try{
				if(fileId > 0){
					addContent(fileId, e.getMessage()+" in job : JOBID : "+jc.getJobId()+"| JOBNAME : "+jc.getJobName()+"| EXECUTION_TIME : "+jc.getExecutionTime()+"| cause : "+e.getCause()+"| message : "+e.getMessage());
				}
			}catch(Exception ex){
				LOGGER.fatal("Error while adding error mlJobReport"+ex);
			}
		}
	}
	public static void addContent(Long fileId,String contentToAdd) throws Exception{
		
		FileStore fs = FacilioFactory.getFileStore();
		try{
	    URL url = new URL(fs.getOrgiFileUrl(fileId));
	    LOGGER.info("Getting url for the file"+url);
	    URLConnection connection = url.openConnection();
	    LOGGER.info("Getting the connection"+connection);
	    String toAdd = URLEncoder.encode(contentToAdd);
	    LOGGER.info("Encoding the content"+toAdd);
	    PrintStream outStream = new PrintStream(connection.getOutputStream());
	    LOGGER.info("Creating Outstream"+outStream);
        outStream.println(toAdd);
        LOGGER.info("Added new line in Outstream");
        outStream.close();
        LOGGER.info("Outstream closed");
		}catch(Exception e){
			LOGGER.fatal("Error in addcontent in url"+e);
		}
        
        
        try{
        String filePath = fs.getFileInfo(fileId).getFilePath();
        LOGGER.info("Filepath from filestore"+filePath);
        Path path = Paths.get(filePath);
        LOGGER.info("path from filepath"+path);
        byte[] strToBytes = contentToAdd.getBytes();
        LOGGER.info("content to bytes"+strToBytes);
        Files.write(path, strToBytes);
        LOGGER.info("Writing in the file");
        }catch(Exception e){
        	LOGGER.fatal("Error in addcontent in path"+e);
        }
        
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
