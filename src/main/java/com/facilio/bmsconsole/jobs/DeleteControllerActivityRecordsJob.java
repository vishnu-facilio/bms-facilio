package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteControllerActivityRecordsJob extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(DeleteControllerActivityRecordsJob.class.getName());
	
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		try {
			List<Long> ids = getControllerActivityIds(jc);
			deleteControllerActivityRecords(ids);
		}
		catch (Exception e) {
			LOGGER.error("Error occurred during deletion of Controller activity");
			CommonCommandUtil.emailException("DeleteControllerActivityJob", "Error occurred during deletion of Controller activity", e);
		}
	}
	
	private List<Long> getControllerActivityIds (JobContext jc) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityModule();
		List<FacilioField> fields = FieldFactory.getControllerActivityFields();
		FacilioField createdTimeField = FieldFactory.getAsMap(fields).get("createdTime");
		
		long lastweek = DateTimeUtil.getDateTime(jc.getExecutionTime(), true).minusDays(7).toInstant().toEpochMilli();
		
		List<Map<String, Object>> props = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(createdTimeField, String.valueOf(lastweek), DateOperators.IS_BEFORE))
														.get()
														;
		
		if (props != null && !props.isEmpty()) {
			return props.stream().map(prop -> (Long) prop.get("id")).collect(Collectors.toList());
		}
		return null;
	}
	
	private void deleteControllerActivityRecords(List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getControllerActivityRecordsModule();
		new GenericDeleteRecordBuilder()
			.table(module.getTableName())
			.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
			.andCondition(CriteriaAPI.getIdCondition(ids, module))
			.delete()
			;
	}

}
