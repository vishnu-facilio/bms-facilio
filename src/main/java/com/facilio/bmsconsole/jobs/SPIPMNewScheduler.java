package com.facilio.bmsconsole.jobs;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
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
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class SPIPMNewScheduler extends FacilioJob {

	private static final Logger LOGGER = LogManager.getLogger(SPIPMNewScheduler.class.getName());

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
				List<Long> longs = Arrays.asList(15609L,
						15611L,
						15638L,
						15639L,
						15644L,
						15651L,
						15821L,
						15822L,
						15823L,
						15824L,
						15828L,
						15833L,
						15834L,
						15835L,
						15836L,
						15837L,
						15986L,
						15987L,
						15991L,
						16004L,
						16006L,
						16011L,
						16012L,
						16015L,
						16016L,
						16018L,
						16019L,
						16091L,
						16184L,
						16186L,
						16187L,
						16188L,
						16192L,
						16193L,
						882655L,
						882660L,
						882661L,
						882662L,
						882663L,
						882664L,
						882665L,
						882666L,
						882671L,
						882672L,
						882673L,
						882674L,
						882675L,
						882676L,
						882677L,
						882678L,
						890130L,
						890131L,
						890134L,
						890172L,
						890173L,
						890207L,
						890260L,
						890264L,
						895363L,
						895371L,
						895373L,
						895389L,
						895390L,
						895391L,
						895392L,
						895393L,
						895394L,
						895395L,
						895398L,
						895400L,
						895401L,
						895402L,
						895403L,
						895404L,
						895405L,
						895406L,
						895407L,
						895408L,
						895414L,
						895427L,
						896334L,
						896363L,
						896369L,
						896372L,
						896373L,
						896381L,
						896394L,
						896395L,
						896396L,
						896397L,
						896398L,
						896399L,
						896400L,
						896401L,
						896402L,
						896403L,
						896404L,
						896405L,
						896410L,
						896411L,
						896446L,
						896458L,
						896468L,
						896512L,
						896513L,
						896514L,
						896515L,
						896516L,
						896517L,
						896518L,
						896519L,
						896520L,
						896521L,
						896522L,
						896523L,
						896524L,
						896527L,
						896528L,
						896536L,
						896555L,
						896556L,
						897544L,
						897591L,
						897592L,
						897593L,
						897594L,
						897604L,
						897605L,
						897608L,
						897706L,
						897707L,
						897737L,
						897782L,
						897783L,
						897784L,
						897785L,
						897786L,
						897787L,
						897790L,
						897791L,
						897793L,
						897794L,
						897795L,
						897796L,
						897797L,
						897800L,
						897801L,
						897802L,
						897803L,
						897896L,
						904892L,
						904924L,
						904944L,
						904945L,
						904946L,
						904947L,
						904948L,
						904949L,
						904950L,
						904951L,
						904952L,
						904953L,
						904954L,
						904955L,
						904965L,
						904968L,
						904969L,
						907051L,
						907052L,
						907326L,
						907330L,
						907341L,
						907342L,
						907343L,
						907344L,
						907345L,
						907347L,
						907348L,
						907349L,
						907355L,
						907356L,
						907377L,
						907378L,
						907407L,
						907408L,
						907409L,
						907410L,
						907411L,
						907413L,
						907414L,
						907415L,
						907416L,
						907417L,
						907418L,
						908812L,
						908813L,
						908814L,
						908843L,
						908847L,
						908851L,
						908896L,
						908907L,
						908908L,
						908909L,
						908910L,
						908911L,
						908912L,
						908913L,
						908914L,
						908915L,
						908916L,
						908917L,
						908918L,
						908922L,
						908923L,
						908925L,
						908926L,
						908927L,
						909213L,
						909214L,
						909215L,
						909216L,
						909217L,
						909218L,
						909219L,
						909220L,
						929056L,
						1104523L);
				Set<Long> s = new HashSet<>(longs);
				for(PreventiveMaintenance pm : pmList) {
					if (!s.contains(pm.getId())) {
						continue;
					}
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

//				for(PreventiveMaintenance pm : pmList) {
//					PreventiveMaintenanceAPI.incrementGenerationTime(pm.getId(), getEndTime(pm));
//				}
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

					bulkWorkOrderContexts.addAll(generateBulkWoContext(pm, context, workorderTemplate, triggers));
				}
			}
		}
		else {
			long templateId = pm.getTemplateId();
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			bulkWorkOrderContexts.addAll(generateBulkWoContext(pm, context, workorderTemplate, pm.getTriggers()));
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

	private long getMax(long pmId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
		selectRecordsBuilder.select(fields)
				.module(module)
				.beanClass(WorkOrderContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), 1+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), 1572546600000L+"", NumberOperators.GREATER_THAN))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), pmId+"", NumberOperators.EQUALS))
				.orderBy("WorkOrders.CREATED_TIME ASC")
				.limit(1);

		List<WorkOrderContext> workOrderContexts = selectRecordsBuilder.get();

		if (CollectionUtils.isEmpty(workOrderContexts)) {
			return  -1L;
		}

		long createdTime = workOrderContexts.get(0).getCreatedTime();
		return createdTime;
	}

	private List<BulkWorkOrderContext> generateBulkWoContext(PreventiveMaintenance pm, FacilioContext context, WorkorderTemplate workorderTemplate, List<PMTriggerContext> triggers) throws Exception {
		List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
		long range = getMax(pm.getId()) - 300L;
		long endTime = 1573064940L;
		if (range != -1) {
			endTime = range / 1000;
		}
		long maxTime = 1572546600L;
		context.put("spi-mig", true);
		for (PMTriggerContext trigger: triggers) {
			if (trigger.getSchedule() != null) {
				BulkWorkOrderContext bulkWoContextsFromTrigger = PreventiveMaintenanceAPI.createBulkWoContextsFromPM(context, pm, trigger, maxTime, endTime, workorderTemplate);
				bulkWorkOrderContexts.add(bulkWoContextsFromTrigger);
			}
		}
		return bulkWorkOrderContexts;
	}

	private long getEndTime(PreventiveMaintenance pm) {
		return pm.getWoGeneratedUpto() + (24 * 60 * 60 * 8);
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
