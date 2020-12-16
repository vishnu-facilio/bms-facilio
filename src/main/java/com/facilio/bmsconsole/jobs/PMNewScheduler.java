package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.db.transaction.FacilioTransactionManager;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.PMJobPlanContext;
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
import com.facilio.chain.FacilioChain;
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
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMNewScheduler extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(PMNewScheduler.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
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
				List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
				if (pmList != null && !pmList.isEmpty()) {
					PreventiveMaintenanceAPI.logIf(92L,"No of pms " + pmList.size());
				}

				for(PreventiveMaintenance pm : pmList) {
					try{
						// PreventiveMaintenanceAPI.logIf(92L,"pm: " + i + " Executing pm: "  + pm.getId());
						List<BulkWorkOrderContext> bulkWo = createPMJobs(pm, triggerMap);
						if (!bulkWo.isEmpty()) {
							bulkWorkOrderContexts.addAll(bulkWo);
						}
					} catch (Exception e) {
						LOGGER.error("Exception occurred in PM Scheduler Job ID - "+jc.getJobId(), e);
						CommonCommandUtil.emailException("PMScheduler", "Exception occurred in generating Schedule - orgId: "+jc.getJobId() + " pmId " + pm.getId(), e);
					}
				}

				if (!bulkWorkOrderContexts.isEmpty()) {
					FacilioContext context = new FacilioContext();
					BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext(bulkWorkOrderContexts);
					PreventiveMaintenanceAPI.logIf(92L,"No  of work orders to save " + bulkWorkOrderContext.getWorkOrderContexts().size());
					context.put(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT, bulkWorkOrderContext);
					context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);

					FacilioChain addWOChain = TransactionChainFactory.getTempAddPreOpenedWorkOrderChain();
					addWOChain.execute(context);
				}

				for(PreventiveMaintenance pm : pmList) {
					PreventiveMaintenanceAPI.incrementGenerationTime(pm.getId(), getEndTime(pm));
				}
			}
		} catch (Exception e) {
			FacilioTransactionManager.INSTANCE.getTransactionManager().setRollbackOnly();
			LOGGER.error("Exception occurred in PM Scheduler Job ID - "+jc.getJobId(), e);
			CommonCommandUtil.emailException("PMScheduler", "Exception occurred in PM Scheduler Job - "+jc.getJobId(), e);
		}
	}
	
	private List<BulkWorkOrderContext>  createPMJobs(PreventiveMaintenance pm, Map<Long,PMTriggerContext> triggerMap) throws Exception {
	    List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RESOURCE_MAP, new HashMap<Long, ResourceContext>());
		context.put(FacilioConstants.ContextNames.STATUS_MAP, new HashMap<FacilioStatus.StatusType, FacilioStatus>());
		if (pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
			long templateId = pm.getTemplateId();
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			// PreventiveMaintenanceAPI.addJobPlanSectionsToWorkorderTemplate(pm, workorderTemplate); 								   un command here to start using job plans
			if (workorderTemplate != null) {
				;
				//TODO
				List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),PreventiveMaintenanceAPI.getPMSites(pm.getId()),pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), true);
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
					List<PMTriggerContext> triggers = getResourceTriggers(triggerMap, workorderTemplate, pmResourcePlanner, resourceId);
					if(triggers == null) {
						triggers = PreventiveMaintenanceAPI.getDefaultTrigger(pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers(), pm.getTriggers());
					}

					if (resourceMap.get(resourceId) != null) {
						workorderTemplate.setResourceId(resourceId);
						workorderTemplate.setResource(resourceMap.get(resourceId));
					} else {
						LOGGER.error("work order not generated PMID: " + pm.getId() + "ResourceId: " + resourceId);
						CommonCommandUtil.emailAlert("work order not generated", "PMID: " + pm.getId() + "ResourceId: " + resourceId);
					}
					List<BulkWorkOrderContext> bulkWorkOrderContextList = generateBulkWoContext(pm, context, workorderTemplate, triggers);
					if (!bulkWorkOrderContextList.isEmpty()) {
						bulkWorkOrderContexts.addAll(bulkWorkOrderContextList);
					}
				}
			}
		} else if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
			long templateId = pm.getTemplateId();
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			// PreventiveMaintenanceAPI.addJobPlanSectionsToWorkorderTemplate(pm, workorderTemplate); 								   un command here to start using job plans
			if (workorderTemplate != null) {
				Long baseSpaceId = pm.getBaseSpaceId();
				if (baseSpaceId == null || baseSpaceId < 0) {
					baseSpaceId = pm.getSiteId();
				}
				//TODO
				List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), false);
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
					List<PMTriggerContext> triggers = getResourceTriggers(triggerMap, workorderTemplate, pmResourcePlanner, resourceId);
					if(triggers == null) {
						triggers = PreventiveMaintenanceAPI.getDefaultTrigger(pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers(), pm.getTriggers());
					}

					if (resourceMap.get(resourceId) != null) {
						workorderTemplate.setResourceId(resourceId);
						workorderTemplate.setResource(resourceMap.get(resourceId));
					} else {
						LOGGER.error("work order not generated PMID: " + pm.getId() + "ResourceId: " + resourceId);
						CommonCommandUtil.emailAlert("work order not generated", "PMID: " + pm.getId() + "ResourceId: " + resourceId);
					}
					List<BulkWorkOrderContext> bulkWorkOrderContextList = generateBulkWoContext(pm, context, workorderTemplate, triggers);
					if (!bulkWorkOrderContextList.isEmpty()) {
						bulkWorkOrderContexts.addAll(bulkWorkOrderContextList);
					}
				}
			}
		}
		else {
			long templateId = pm.getTemplateId();
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			// PreventiveMaintenanceAPI.addJobPlanSectionsToWorkorderTemplate(pm, workorderTemplate);										un command here to start using job plans
			List<BulkWorkOrderContext> bulkWorkOrderContextList = generateBulkWoContext(pm, context, workorderTemplate, pm.getTriggers());
			if (!bulkWorkOrderContextList.isEmpty()) {
				bulkWorkOrderContexts.addAll(bulkWorkOrderContextList);
			}
		}
		return bulkWorkOrderContexts;
	}

	private List<PMTriggerContext> getResourceTriggers(Map<Long, PMTriggerContext> triggerMap, WorkorderTemplate workorderTemplate, Map<Long, PMResourcePlannerContext> pmResourcePlanner, Long resourceId) {
		if(pmResourcePlanner.get(resourceId) == null) {
			return null;
		}
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
		return trigs;
	}

	private List<BulkWorkOrderContext> generateBulkWoContext(PreventiveMaintenance pm, FacilioContext context, WorkorderTemplate workorderTemplate, List<PMTriggerContext> triggers) throws Exception {
		List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
		long endTime = getEndTime(pm);
		long minTime = pm.getWoGeneratedUpto();
		for (PMTriggerContext trigger: triggers) {
			if (trigger.getSchedule() != null) {
				long startTimeInSecond = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
				BulkWorkOrderContext bulkWoContextsFromTrigger = PreventiveMaintenanceAPI.createBulkWoContextsFromPM(context, pm, trigger, startTimeInSecond, endTime, minTime, workorderTemplate);
				bulkWorkOrderContexts.add(bulkWoContextsFromTrigger);
			}
		}
		return bulkWorkOrderContexts;
	}

	private long getEndTime(PreventiveMaintenance pm) {
		return pm.getWoGeneratedUpto() + (24 * 60 * 60);
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
