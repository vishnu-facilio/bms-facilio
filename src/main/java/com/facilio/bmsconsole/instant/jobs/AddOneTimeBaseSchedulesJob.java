package com.facilio.bmsconsole.instant.jobs;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.InstantJob;

public class AddOneTimeBaseSchedulesJob extends InstantJob {

	private static final Logger LOGGER = Logger.getLogger(AddOneTimeBaseSchedulesJob.class.getName());

	@Override
	public void execute(FacilioContext context) throws Exception {
		
		try {
			if(context != null && !context.isEmpty()) {
				Long jobScheduleId = (Long) context.get(FacilioConstants.ContextNames.BASE_SCHEDULE_ID);
				Boolean isUpdate = (Boolean) context.getOrDefault("isUpdate", true);
			    Boolean saveAsV3 = (Boolean) context.getOrDefault("saveAsV3", false); 
				if(jobScheduleId != null) {
					BaseScheduleContext baseScheduleContext = getBaseScheduleContext(jobScheduleId);
				    if(baseScheduleContext != null) {
						List<Map<String, Object>> parentRecordProps = baseScheduleContext.fetchParent();
						List<? extends ModuleBaseWithCustomFields> childRecords = baseScheduleContext.getScheduleTypeEnum().getSchedulerTypeHandler().createRecords(baseScheduleContext, isUpdate, parentRecordProps, true);	
						if(childRecords != null && !childRecords.isEmpty()) {
							if(saveAsV3) {
								baseScheduleContext.saveAsV3Records(childRecords);
							}
							else {
								baseScheduleContext.saveRecords(childRecords);
							}
						}
				    }
				}
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe("Error occurred while AddOneTimeBaseSchedulesJob for basescheduleContext -- "+context+" Exception -- " + e);
			CommonCommandUtil.emailException("Exception while AddOneTimeBaseSchedulesJob", "For basescheduleContext -- "+context,e);
		}	
	}

	private BaseScheduleContext getBaseScheduleContext(long scheduleId) throws Exception {	   
	   GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getBaseSchedulerFields())
				.table(ModuleFactory.getBaseSchedulerModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(scheduleId, ModuleFactory.getBaseSchedulerModule()));
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			BaseScheduleContext baseScheduleContext = FieldUtil.getAsBeanFromMap(props.get(0), BaseScheduleContext.class);
			return baseScheduleContext;
		}
		return null;
	}
}
