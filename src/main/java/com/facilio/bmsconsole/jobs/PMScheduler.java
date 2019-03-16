package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.facilio.accounts.util.AccountUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
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
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMScheduler extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(PMScheduler.class.getName());

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			if (AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_SCHEDULED_WO)) {
				return;
			}
			FacilioModule pmTriggerModule = ModuleFactory.getPMTriggersModule();
			FacilioModule pmModule = ModuleFactory.getPreventiveMaintenanceModule();
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
			Map<Long,PMTriggerContext> triggerMap = new HashMap<>();
			
			if(triggerProps != null && !triggerProps.isEmpty()) {
				List<PMTriggerContext> triggers = new ArrayList<>();
				Map<Long, PreventiveMaintenance> pms = new HashMap<>();
				StringBuilder ids = new StringBuilder();
				triggerProps.forEach(triggerProp -> {
					PMTriggerContext trigger = FieldUtil.getAsBeanFromMap(triggerProp, PMTriggerContext.class);
					triggers.add(trigger);
					triggerMap.put(trigger.getId(), trigger);
					ids.append(", ")
						.append(trigger.getId());
					
					if(pms.get(trigger.getPmId()) == null) {
						PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(triggerProp, PreventiveMaintenance.class);
						pm.setId(trigger.getPmId());
						pms.put(pm.getId(), pm);
					}
				});
				
				List<PreventiveMaintenance> pmList = groupPmAndTriggers(pms,triggers);
				Map<Long, Long> maxNextExecutionTimesMap = getMaxExecutionTimes(ids.substring(", ".length()));		// gives max scheduled date 
				long endTime = DateTimeUtil.getDayStartTime(PreventiveMaintenanceAPI.PM_CALCULATION_DAYS+1, true) - 1;
				for(PreventiveMaintenance pm : pmList) {
					createPMJobs(pm, triggerMap, maxNextExecutionTimesMap, endTime);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception occurred in PM Scheduler Job ID - "+jc.getJobId(), e);
			CommonCommandUtil.emailException("PMScheduler", "Exception occurred in PM Scheduler Job - "+jc.getJobId(), e);
		}
	}
		
	private void createPMJobs(PreventiveMaintenance pm, Map<Long,PMTriggerContext> triggerMap, Map<Long, Long> maxNextExecutionTimesMap, long endTime) {
		try {
			if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
				Long baseSpaceId = pm.getBaseSpaceId();
				if (baseSpaceId == null || baseSpaceId < 0) {
					baseSpaceId = pm.getSiteId();
				}
				List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());
				Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
				for(Long resourceId :resourceIds) {
					List<PMTriggerContext> triggers = null;
					if(pmResourcePlanner.get(resourceId) != null) {
						PMResourcePlannerContext currentResourcePlanner = pmResourcePlanner.get(resourceId);
						List<PMTriggerContext> trigs = new ArrayList<>();
						if (currentResourcePlanner.getTriggerContexts() != null) {
							for (PMTriggerContext t: currentResourcePlanner.getTriggerContexts()) {
								if (triggerMap.get(t.getId()) != null) {
									trigs.add(triggerMap.get(t.getId()));
								}
							}
						}
						currentResourcePlanner.setTriggerContexts(trigs);
						triggers = trigs;
					}
					
					if(triggers == null) {
						triggers = new ArrayList<>();
						triggers.add(PreventiveMaintenanceAPI.getDefaultTrigger(pm.getTriggers()));
					}

					for (PMTriggerContext trigger: triggers) {
						Long maxTime = maxNextExecutionTimesMap.get(trigger.getId());
						if (maxTime != null && maxTime < endTime) {
							PreventiveMaintenanceAPI.createPMJobs(pm, trigger, resourceId, maxTime, endTime, true);
						}
					}
				}
			}
			else {
				for(PMTriggerContext trigger : pm.getTriggers()) {
					if(trigger.getSchedule().getFrequencyTypeEnum() != FrequencyType.DO_NOT_REPEAT) {
						Long maxTime = maxNextExecutionTimesMap.get(trigger.getId());
						if(maxTime < endTime) {
							PreventiveMaintenanceAPI.createPMJobs(pm, trigger, maxTime, endTime);
						}
					}
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred during PM Job creation of PM : "+pm.getId());
			CommonCommandUtil.emailException("PMScheduler", "Error occurred during PM Job creation of PM : "+pm.getId(), e);
		}
	}
	
	private List<PreventiveMaintenance> groupPmAndTriggers(Map<Long, PreventiveMaintenance> pms, List<PMTriggerContext> triggers) throws Exception {
		List<PreventiveMaintenance> pmList = new ArrayList<>();
		if(pms != null) {
			for( Entry<Long, PreventiveMaintenance> map : pms.entrySet()) {
				
				PreventiveMaintenance pm = map.getValue();
				pm.setPmIncludeExcludeResourceContexts(TemplateAPI.getPMIncludeExcludeList(pm.getId(), null, null));
				
				for(PMTriggerContext trigger :triggers) {
					if(trigger.getPmId() == pm.getId()) {
						pm.addTriggers(trigger); 
					} 
				}
				
				pmList.add(pm);
			}
		}
		return pmList;
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
