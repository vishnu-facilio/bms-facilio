package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class BaseSchedulerSingleInstanceJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(BaseSchedulerSingleInstanceJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception {
		
		long baseSchedulerId = jc.getJobId();
		
		BaseScheduleContext baseScheduleContext = getBaseSchedules(baseSchedulerId);
		
		JSONObject jobProps = BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
	    Boolean isUpdate = (jobProps != null) ? (Boolean) jobProps.getOrDefault("isUpdate", false) : false;
	    Boolean saveAsV3 = (jobProps != null) ? (Boolean) jobProps.getOrDefault("saveAsV3", false) : false;
		
		try {
			List<Map<String, Object>> parentRecordProps = baseScheduleContext.fetchParent();
			List<? extends ModuleBaseWithCustomFields> childRecords = baseScheduleContext.getScheduleTypeEnum().getSchedulerTypeHandler().createRecords(baseScheduleContext, isUpdate, parentRecordProps, false);	
			if(childRecords != null && !childRecords.isEmpty()) {
				if(saveAsV3) {
					baseScheduleContext.saveAsV3Records(childRecords);
				}
				else {
					baseScheduleContext.saveRecords(childRecords);
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	
	private BaseScheduleContext getBaseSchedules(long baseSchedulerId) throws Exception {	   
		   GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getBaseSchedulerFields())
					.table(ModuleFactory.getBaseSchedulerModule().getTableName())
					.andCondition(CriteriaAPI.getIdCondition(baseSchedulerId, ModuleFactory.getBaseSchedulerModule()));
				
		   	Criteria subCriteria = new Criteria();
			subCriteria.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", "" + System.currentTimeMillis(), NumberOperators.GREATER_THAN));
			subCriteria.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", "-1", CommonOperators.IS_EMPTY));
			selectBuilder.andCriteria(subCriteria);
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {			
				BaseScheduleContext baseSchedules = FieldUtil.getAsBeanFromMap(props.get(0), BaseScheduleContext.class);	
				return baseSchedules;
			}
			return null;
		}
}
