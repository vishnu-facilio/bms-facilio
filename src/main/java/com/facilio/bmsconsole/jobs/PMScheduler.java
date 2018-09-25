package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMScheduler extends FacilioJob {

	private Logger log = LogManager.getLogger(PMScheduler.class.getName());

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			FacilioModule pmTriggerModule = ModuleFactory.getPMTriggersModule();
			FacilioModule pmModule = ModuleFactory.getPreventiveMaintenancetModule();
			List<FacilioField> fields = FieldFactory.getPMTriggerFields();
			List<FacilioField> pmFields = FieldFactory.getPreventiveMaintenanceFields();
			Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(pmFields);
			fields.addAll(pmFields);
			
			GenericSelectRecordBuilder pmTriggerBuilder = new GenericSelectRecordBuilder()
																.select(fields)
																.table(pmTriggerModule.getTableName())
																.innerJoin(pmModule.getTableName())
																.on(pmTriggerModule.getTableName()+".PM_ID = "+pmModule.getTableName()+".ID")
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmTriggerModule))
																.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("triggerType"), String.valueOf(TriggerType.ONLY_SCHEDULE_TRIGGER.getVal()), NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition(pmFieldsMap.get("status"), String.valueOf(true), BooleanOperators.IS))
																;
			
			List<Map<String, Object>> triggerProps = pmTriggerBuilder.get();
			if(triggerProps != null && !triggerProps.isEmpty()) {
				List<PMTriggerContext> triggers = new ArrayList<>();
				Map<Long, PreventiveMaintenance> pms = new HashMap<>();
				StringBuilder ids = new StringBuilder();
				triggerProps.forEach(triggerProp -> {
					PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(triggerProp, PMTriggerContext.class);
					triggers.add(trigger);
					ids.append(", ")
						.append(trigger.getId());
					
					if(pms.get(trigger.getPmId()) == null) {
						PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(triggerProp, PreventiveMaintenance.class);
						pm.setId(trigger.getPmId());
						pms.put(pm.getId(), pm);
					}
				});
				Map<Long, Long> maxNextExecutionTimesMap = getMaxExecutionTimes(ids.substring(", ".length()));
				
				long endTime = DateTimeUtil.getDayStartTime(PreventiveMaintenanceAPI.PM_CALCULATION_DAYS+1, true) - 1;
				for(PMTriggerContext trigger : triggers) {
					if(trigger.getSchedule().getFrequencyTypeEnum() != FrequencyType.DO_NOT_REPEAT) {
						Long maxTime = maxNextExecutionTimesMap.get(trigger.getId());
						if(maxTime < endTime) {
							PreventiveMaintenanceAPI.createPMJobs(pms.get(trigger.getPmId()), trigger, maxTime, endTime);
						}
					}
				};
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
														
	}
	
	private Map<Long, Long> getMaxExecutionTimes(String triggerIds) throws Exception {
		FacilioModule module = ModuleFactory.getPMJobsModule();
		Map<String, FacilioField> pmFields = FieldFactory.getAsMap(FieldFactory.getPMJobFields());
		FacilioField maxField = FieldFactory.getField("maxExecutionTime", "MAX(NEXT_EXECUTION_TIME)", FieldType.NUMBER);
		FacilioField triggerIdField = pmFields.get("pmTriggerId");
		List<FacilioField> fields = new ArrayList<>();
		fields.add(maxField);
		fields.add(triggerIdField);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(triggerIdField, triggerIds, NumberOperators.EQUALS))
														.groupBy(triggerIdField.getName())
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		return props.stream().collect(Collectors.toMap(prop -> (Long) prop.get(triggerIdField.getName()), prop -> (Long) prop.get(maxField.getName())));
	}
}
