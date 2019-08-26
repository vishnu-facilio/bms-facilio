package com.facilio.bmsconsole.jobs;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;

public class PMNewScheduler extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(PMNewScheduler.class.getName());

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
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
//																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(pmTriggerModule))
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
				List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
				if (pmList != null && !pmList.isEmpty()) {
					PreventiveMaintenanceAPI.logIf(92L,"No of pms " + pmList.size());
				}

				int i = 0;
				for(PreventiveMaintenance pm : pmList) {
					PreventiveMaintenanceAPI.logIf(92L,"pm: " + i + " Executing pm: "  + pm.getId());
					List<BulkWorkOrderContext> bulkWo = createPMJobs(pm, triggerMap, maxNextExecutionTimesMap, endTime);
					if (!bulkWo.isEmpty()) {
						bulkWorkOrderContexts.addAll(bulkWo);
					}
					i++;
				}

				if (!bulkWorkOrderContexts.isEmpty()) {
					FacilioContext context = new FacilioContext();
					BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext(bulkWorkOrderContexts);
					PreventiveMaintenanceAPI.logIf(92L,"No  of work orders to save " + bulkWorkOrderContext.getWorkOrderContexts().size());
					context.put(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT, bulkWorkOrderContext);
					context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);

					Chain addWOChain = TransactionChainFactory.getTempAddPreOpenedWorkOrderChain();
					addWOChain.execute(context);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception occurred in PM Scheduler Job ID - "+jc.getJobId(), e);
			CommonCommandUtil.emailException("PMScheduler", "Exception occurred in PM Scheduler Job - "+jc.getJobId(), e);
		}
	}
		
	private List<BulkWorkOrderContext>  createPMJobs(PreventiveMaintenance pm, Map<Long,PMTriggerContext> triggerMap, Map<Long, Long> maxNextExecutionTimesMap, long endTime) throws Exception {
	    List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RESOURCE_MAP, new HashMap<Long, ResourceContext>());
		context.put(FacilioConstants.ContextNames.STATUS_MAP, new HashMap<FacilioStatus.StatusType, FacilioStatus>());
		if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
			long templateId = pm.getTemplateId();
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			if (workorderTemplate != null) {
				Long baseSpaceId = pm.getBaseSpaceId();
				if (baseSpaceId == null || baseSpaceId < 0) {
					baseSpaceId = pm.getSiteId();
				}
				//TODO
				List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());
				//TODO
				Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
				//TODO
				List<ResourceContext> resourceObjs = ResourceAPI.getResources(resourceIds, false); // ?

				Map<Long, ResourceContext> resourceMap = new HashMap<>();
				if(resourceObjs != null && !resourceObjs.isEmpty()) {
					for (ResourceContext resource : resourceObjs) {
						resourceMap.put(resource.getId(), resource);
					}
				}

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
						if (currentResourcePlanner.getAssignedToId() != null && currentResourcePlanner.getAssignedToId() > 0 ) {
							workorderTemplate.setAssignedToId(currentResourcePlanner.getAssignedToId());
						}
						currentResourcePlanner.setTriggerContexts(trigs);
						triggers = trigs;
					}

					if(triggers == null) {
						triggers = new ArrayList<>();
						triggers.add(PreventiveMaintenanceAPI.getDefaultTrigger(pm.getTriggers()));
					}

					if (resourceMap.get(resourceId) != null) {
						workorderTemplate.setResourceId(resourceId);
						workorderTemplate.setResource(resourceMap.get(resourceId));
					} else {
						LOGGER.error("work order not generated PMID: " + pm.getId() + "ResourceId: " + resourceId);
						CommonCommandUtil.emailAlert("work order not generated", "PMID: " + pm.getId() + "ResourceId: " + resourceId);
					}

					bulkWorkOrderContexts.addAll(generateBulkWoContext(pm, maxNextExecutionTimesMap, endTime, context, workorderTemplate, triggers));
				}
			}
		}
		else {
			long templateId = pm.getTemplateId();
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			bulkWorkOrderContexts.addAll(generateBulkWoContext(pm, maxNextExecutionTimesMap, endTime, context, workorderTemplate, pm.getTriggers()));
		}

		return bulkWorkOrderContexts;
	}

	private List<BulkWorkOrderContext> generateBulkWoContext(PreventiveMaintenance pm, Map<Long, Long> maxNextExecutionTimesMap, long endTime, FacilioContext context, WorkorderTemplate workorderTemplate, List<PMTriggerContext> triggers) throws Exception {
		List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
		for (PMTriggerContext trigger: triggers) {
			if (trigger.getSchedule() != null) {
				Long maxTime = maxNextExecutionTimesMap.get(trigger.getId());
				if (maxTime != null) {
					if ((maxTime/1000)  < endTime) {
						BulkWorkOrderContext bulkWoContextsFromTrigger = PreventiveMaintenanceAPI.createBulkWoContextsFromPM(context, pm, trigger, maxTime / 1000, workorderTemplate);
						bulkWorkOrderContexts.add(bulkWoContextsFromTrigger);
					}
				} else {
					long startTime = trigger.getStartTime();
					BulkWorkOrderContext bulkWoContextsFromTrigger = PreventiveMaintenanceAPI.createBulkWoContextsFromPM(context, pm, trigger, startTime / 1000, workorderTemplate);
					bulkWorkOrderContexts.add(bulkWoContextsFromTrigger);
				}
			}
		}
		return bulkWorkOrderContexts;
	}

	private List<PreventiveMaintenance> groupPmAndTriggers(Map<Long, PreventiveMaintenance> pms, List<PMTriggerContext> triggers) throws Exception {
		List<PreventiveMaintenance> pmList = new ArrayList<>();
		if(pms != null) {
			for( Map.Entry<Long, PreventiveMaintenance> map : pms.entrySet()) {
				
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
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> woFields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> woFieldMap = FieldFactory.getAsMap(woFields);
		FacilioField maxField = FieldFactory.getField("maxScheduledStart", "MAX(SCHEDULED_START)", FieldType.NUMBER);
		FacilioField triggerIdField = woFieldMap.get("trigger");
		List<FacilioField> fields = new ArrayList<>();
		fields.add(maxField);
		fields.add(triggerIdField);
		FacilioField statusField = woFieldMap.get("status");
		FacilioField jobStatusField = woFieldMap.get("jobStatus");
		FacilioStatus status = TicketAPI.getStatus("preopen");
		FacilioModule ticketModule = ModuleFactory.getTicketsModule();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.innerJoin(ticketModule.getTableName())
														.on(ticketModule.getTableName()+ ".ID=" + module.getTableName() + ".ID")
														.andCondition(CriteriaAPI.getCondition(triggerIdField, triggerIds, NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(statusField, String.valueOf(status.getId()), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(jobStatusField, String.valueOf(PMJobsContext.PMJobsStatus.ACTIVE.getValue()), NumberOperators.EQUALS))
														.groupBy(triggerIdField.getCompleteColumnName())
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		return props.stream().collect(Collectors.toMap(prop -> (Long) prop.get(triggerIdField.getName()), prop -> (Long) prop.get(maxField.getName())));
	}
}
