package com.facilio.bmsconsole.jobs;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class InitiateSplitJob extends FacilioJob {
	private static final Logger LOGGER = Logger.getLogger(InitiateSplitJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception 
	{	
		try{
			Map<String, Object> props = FieldUtil.getAsProperties(jc);
			FacilioField isActiveField = FieldFactory.getField("active", "IS_ACTIVE", ModuleFactory.getJobsModule(), FieldType.BOOLEAN);
			FacilioField jobidField = FieldFactory.getField("jobId", "JOBID", ModuleFactory.getJobsModule(), FieldType.ID);
			FacilioField jobNameField = FieldFactory.getField("jobName", "JOBNAME", ModuleFactory.getJobsModule(), FieldType.STRING);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(ModuleFactory.getJobsModule().getTableName())
					.fields(Collections.singletonList(isActiveField))
					.andCondition(CriteriaAPI.getCondition(jobidField, String.valueOf(jc.getJobId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(jobNameField, "SplitHistoricalJob", StringOperators.IS));
			updateBuilder.update(props);
		}catch(Exception e){
			LOGGER.fatal("Error in Initiate Split Job"+e);
			throw e;
		}
	}	
}
