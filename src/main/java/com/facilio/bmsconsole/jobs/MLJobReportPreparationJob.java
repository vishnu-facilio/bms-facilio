package com.facilio.bmsconsole.jobs;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class MLJobReportPreparationJob extends FacilioJob 
{
	private static final Logger LOGGER = Logger.getLogger(MLJobReportPreparationJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception 
	{	
		try{
			LOGGER.info("Inside MLJobReportPreparationJob");
			JSONObject props=BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
			FileStore fs = FacilioFactory.getFileStore();
			long fileId = Long.parseLong(props.get("fileId").toString());
			LOGGER.info("MLJobReport fileId : "+fileId);
			if(fileId > 0){
				
				FileInfo file = fs.getFileInfo(fileId);
				Map<String, String> files = new HashMap<>();
				String name = file.getFileName();
				String url = fs.getPrivateUrl(fileId);
				files.put(name, url);
				url = fs.getOrgiFileUrl(fileId);
				files.put(name, url);
				url = fs.getOrgiDownloadUrl(fileId);
				files.put(name, url);
				LOGGER.info("MLJobReport File : "+files);
//				sendMail(files);
				LOGGER.info("MLJobReport Mail Successfully sent");
				fs.deleteFile(fileId);
				LOGGER.info("MLJobReport Successfully deleted");
			}
			File file = File.createTempFile("ML_JOB_REPORT", ".txt");
			fileId = fs.addFile("ML_JOB_REPORT", file, "application/text");
			LOGGER.info("MLJobReport Successfully added");
			props.put("fileId", fileId);
			Map<String,Object> prop=new HashMap<String, Object>();
			prop.put("props", props.toString());
			updateCommonJob(jc.getJobId(),jc.getJobName(),prop);
			
			LOGGER.info("Finished MLJobReportPreparationJob");
		}catch(Exception e){
			LOGGER.fatal("Error in MLJobReportPreparationJob "+e);
			throw e;
		}
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
	
	private void sendMail(Map<String, String> files) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer("\n Report : \n");
		if (FacilioProperties.isProduction()) {

			sb.append("\nOrgId: ")
			.append(AccountUtil.getCurrentOrg().getOrgId())
			.append("\n\n-----------------\n\n");

			JSONObject mailJson = new JSONObject();
			mailJson.put("sender", "noreply@facilio.com");
			mailJson.put("to", "anupriya@facilio.com");
			mailJson.put("subject", "ML job report");
			mailJson.put("message", sb.toString());
			
			AwsUtil.sendEmail(mailJson,files);
		}
	}
}
