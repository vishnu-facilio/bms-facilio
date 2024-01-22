package com.facilio.plannedmaintenance;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMPlanner;
import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PMTriggerV2;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.fms.message.Message;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.plannedmaintenance.PlannedMaintenanceAPI.ScheduleOperation.EXTEND;
import static com.facilio.plannedmaintenance.PlannedMaintenanceAPI.ScheduleOperation.REINIT;

@Log4j
public class PlannedMaintenanceAPI {
    public enum ScheduleOperation implements FacilioStringEnum {
        EXTEND(ScheduleExecutor.class),
        REINIT (ScheduleExecutor.class),
        NIGHTLY (NightlyExecutor.class),
        EXTEND_RESOURCE_PLANNER(ScheduleResourcePlannerExecutor.class)
        ;
        
        private Class<? extends ExecutorBase> executorClass;
        
        private <E extends ExecutorBase> ScheduleOperation (Class<E> executorClass) {
            this.executorClass = executorClass;
        }
        
        public ExecutorBase getExecutorClass() throws Exception {
        	
        	return executorClass.getConstructor().newInstance();
        }
        
    }

    public static void extendPlanner(long plannerId, Duration duration) {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("plannerId", plannerId);
        message.put("operation", EXTEND.getValue());
        message.put("duration", duration.toString());

        Messenger.getMessenger().sendMessage(new Message()
                .setKey("pm_planner/" + plannerId + "/execute")
                .setOrgId(orgId)
                .setContent(message)
        );
    }
    
    public static void runNightlyPlanner(long plannerId) {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("plannerId", plannerId);
        message.put("operation", ScheduleOperation.NIGHTLY.getValue());

        Messenger.getMessenger().sendMessage(new Message()
                .setKey("pm_planner/" + plannerId + "/execute")
                .setOrgId(orgId)
                .setContent(message)
        );
    }

    public static void schedulePlanner(long plannerId) throws Exception {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        JSONObject message = new JSONObject();
        message.put("orgId", orgId);
        message.put("plannerId", plannerId);
        message.put("operation", REINIT.getValue());

        Messenger.getMessenger().sendMessage(new Message()
                .setKey("pm_planner/" + plannerId + "/execute")
                .setOrgId(orgId)
                .setContent(message)
        );
    }

    public static List<Long> getPlannerIds(List<Long> pmIds) throws Exception {
        List<PMPlanner> pmPlanners = getPmPlanners(pmIds);
        return pmPlanners.stream().map(PMPlanner::getId).collect(Collectors.toList());
    }
    
    public static List<PMResourcePlanner> getResourcePlanners(Long plannerId) throws Exception {
        
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
        FacilioModule pmPlannerModule = modBean.getModule("pmResourcePlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmResourcePlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);
        
        FacilioField plannerField = fieldMap.get("planner");

        SelectRecordsBuilder<PMResourcePlanner> records = new SelectRecordsBuilder<PMResourcePlanner>();
        records.select(pmPlannerFields)
                .module(pmPlannerModule)
                .beanClass(PMResourcePlanner.class)
                .andCondition(CriteriaAPI.getCondition(plannerField, plannerId+"", NumberOperators.EQUALS));
        return records.get();
    	
    }

    public static List<PMPlanner> getPmPlanners(List<Long> pmIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule("pmPlanner");
        List<FacilioField> pmPlannerFields = modBean.getAllFields("pmPlanner");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);
        FacilioField pmIdField = fieldMap.get("pmId");

        SelectRecordsBuilder<PMPlanner> records = new SelectRecordsBuilder<>();
        records.select(pmPlannerFields)
                .module(pmPlannerModule)
                .beanClass(PMPlanner.class)
                .andCondition(CriteriaAPI.getCondition(pmIdField, pmIds, NumberOperators.EQUALS));
        return records.get();
    }


    public static PMPlanner getPlanner(Long pmId, String plannerName) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_PLANNER);
        List<FacilioField> pmPlannerFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_PLANNER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmPlannerFields);
        SelectRecordsBuilder<PMPlanner> records = new SelectRecordsBuilder<PMPlanner>();
        records.select(pmPlannerFields)
                .module(pmPlannerModule)
                .beanClass(PMPlanner.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), String.valueOf(pmId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), plannerName, StringOperators.IS));
        return records.fetchFirst();
    }
    public static Map<Long, List<PMTriggerV2>> getTriggerFromPmV2Ids(Set<Long> pmIds) throws Exception{
        Map<Long,List<PMTriggerV2>> pmTriggerMap = new HashMap<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmTriggerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER);
        List<FacilioField> pmTriggerFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_TRIGGER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmTriggerFields);

        for(long pmId : pmIds){
            SelectRecordsBuilder<PMTriggerV2> builder = new SelectRecordsBuilder<PMTriggerV2>()
                    .module(pmTriggerModule)
                    .select(pmTriggerFields)
                    .beanClass(PMTriggerV2.class)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"),String.valueOf(pmId),NumberOperators.EQUALS));
            List<PMTriggerV2> pmTriggerV2List = builder.get();
            if(pmTriggerV2List == null || pmTriggerV2List.isEmpty()){
                continue;
            }
            for(PMTriggerV2 pmTriggerV2 : pmTriggerV2List){
                if(pmTriggerV2.getFrequencyEnum() != null){
                    JSONParser parser = new JSONParser();
                    ScheduleInfo scheduleInfo = FieldUtil.getAsBeanFromJson((JSONObject)parser.parse(pmTriggerV2.getSchedule()), ScheduleInfo.class);
                    pmTriggerV2.setScheduleMsg(scheduleInfo.getFrequencyTypeEnum().getDescription());
                    pmTriggerV2.setFrequency(computeFrequencyForCalendar(scheduleInfo.getFrequencyTypeEnum()));
                }
            }
            pmTriggerMap.put(pmId,pmTriggerV2List);
        }
        return pmTriggerMap;
    }

    public static Map<Long, PlannedMaintenance> getActivePpm(Set<Long> pmIds) throws Exception{
        Map<Long,PlannedMaintenance> pmMap = new HashMap<>();
        Map<Long,List<PMTriggerV2>> pmTriggerMap = new HashMap<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedMaintenanceModule = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        List<FacilioField> plannedMaintenanceFields = modBean.getAllFields(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(plannedMaintenanceFields);

        for(long pmId : pmIds){
            SelectRecordsBuilder<PlannedMaintenance> builder = new SelectRecordsBuilder<PlannedMaintenance>()
                    .module(plannedMaintenanceModule)
                    .select(plannedMaintenanceFields)
                    .beanClass(PlannedMaintenance.class)
                    .andCondition(CriteriaAPI.getIdCondition(pmId,plannedMaintenanceModule))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("pmStatus"),String.valueOf(PlannedMaintenance.PMStatus.ACTIVE.getVal()),NumberOperators.EQUALS));
            PlannedMaintenance plannedMaintenance = builder.fetchFirst();
            if(plannedMaintenance != null ) {
                pmMap.put(plannedMaintenance.getId(), plannedMaintenance);
            }
        }
        return pmMap;
    }

    public static Map<Long, List<Map<String, Object>>> getScheduledWofromPpmId(long startTimeInSeconds, long endTimeInSeconds, Criteria filterCriteria) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        long startTime = startTimeInSeconds * 1000;
        long endTime = endTimeInSeconds * 1000;
        SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>();
        builder.module(module)
                .beanClass(V3WorkOrderContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), String.valueOf(endTime), NumberOperators.LESS_THAN))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"), CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("pmV2"), CommonOperators.IS_NOT_EMPTY))
                .orderBy("createdTime")
                .skipModuleCriteria();
        if (filterCriteria != null) {
            builder.andCriteria(filterCriteria);
        }
        List<V3WorkOrderContext> workorders = builder.get();
        Map<Long, List<Map<String, Object>>> pmWos = new HashMap<>();
        if (workorders != null && !workorders.isEmpty()) {
            for (V3WorkOrderContext wo : workorders) {
                Map<String, Object> prop = new HashMap<>();
                prop.put("id", wo.getId());
                prop.put("nextExecutionTime", wo.getScheduledStart());
                prop.put("orgId", wo.getOrgId());
                prop.put("pmId", wo.getPmV2());
                prop.put("woSubject",wo.getSubject());
                if (wo.getResource() != null && wo.getResource().getId() > 0) {
                    prop.put("resourceId", wo.getResource().getId());
                }
                if (wo.getAssignmentGroup() != null) {
                    prop.put("assignmentGroupId", wo.getAssignmentGroup().getId());
                }
                if (wo.getAssignedTo() != null) {
                    prop.put("assignedToId", wo.getAssignedTo().getId());
                }
                if (wo.getType().getId() > 0){
                    prop.put("type", TicketAPI.getType(wo.getOrgId(),wo.getType().getId()).getName());
                }
                if (wo.getPmTriggerV2() != null ) {
                    prop.put("pmTriggerId", wo.getPmTriggerV2());

                    List<Map<String, Object>> woList = pmWos.get(wo.getPmTriggerV2());
                    if (woList == null) {
                        woList = new ArrayList<>();
                        pmWos.put(wo.getPmTriggerV2(), woList);
                    }
                    woList.add(prop);
                }
            }
        }
        return pmWos;

    }
    public static PlannedMaintenance getPmV2fromId(long pmV2Id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedMaintenanceModule = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        List<FacilioField> plannedMaintenanceFields = modBean.getAllFields(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);

        SelectRecordsBuilder<PlannedMaintenance> plannedMaintenanceSelectRecordsBuilder = new SelectRecordsBuilder<PlannedMaintenance>()
                .module(plannedMaintenanceModule)
                .select(plannedMaintenanceFields)
                .beanClass(PlannedMaintenance.class)
                .andCondition(CriteriaAPI.getIdCondition(pmV2Id,plannedMaintenanceModule));
        PlannedMaintenance plannedMaintenance = plannedMaintenanceSelectRecordsBuilder.fetchFirst();
        List<V3SiteContext> sites = getPmV2SitesFromPMv2Id(pmV2Id);
        plannedMaintenance.setSites(sites);
        return plannedMaintenance;
    }
    public static PMPlanner getPmPlannerFromId(long plannerId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmPlannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_PLANNER);
        List<FacilioField> pmPlannerFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_PLANNER);

        SelectRecordsBuilder<PMPlanner> pmPlannerSelectRecordsBuilder = new SelectRecordsBuilder<PMPlanner>()
                .module(pmPlannerModule)
                .select(pmPlannerFields)
                .beanClass(PMPlanner.class)
                .andCondition(CriteriaAPI.getIdCondition(plannerId,pmPlannerModule));
        PMPlanner pmPlanner = pmPlannerSelectRecordsBuilder.fetchFirst();
        return pmPlanner;
    }
    public static PMTriggerV2 getPmV2TriggerFromId(long pmV2TriggerId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmV2TriggerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER);
        List<FacilioField> pmV2TriggerFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_TRIGGER);

        SelectRecordsBuilder<PMTriggerV2> pmTriggerV2SelectRecordsBuilder = new SelectRecordsBuilder<PMTriggerV2>()
                .module(pmV2TriggerModule)
                .select(pmV2TriggerFields)
                .beanClass(PMTriggerV2.class)
                .andCondition(CriteriaAPI.getIdCondition(pmV2TriggerId,pmV2TriggerModule));
        PMTriggerV2 pmTriggerV2 = pmTriggerV2SelectRecordsBuilder.fetchFirst();
        return pmTriggerV2;
    }
    public static int deleteTriggerByIds(List<Long> triggerIdList) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmTriggerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_TRIGGER);

        DeleteRecordBuilder<PMTriggerV2> builder = new DeleteRecordBuilder<PMTriggerV2>()
                .module(pmTriggerModule)
                .andCondition(CriteriaAPI.getIdCondition(triggerIdList,pmTriggerModule));
        int count = builder.markAsDelete();
        return count;
    }
    public static void deleteWorkOrderFromPmId(List<Long> pmIdList) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Criteria criteria = new Criteria();
        if(pmIdList == null || pmIdList.isEmpty()){
            return;
        }
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("pmV2"), StringUtils.join(pmIdList,","), NumberOperators.EQUALS));
        deleteWorkOrdersInBatch(criteria);
        return;
    }
    public static void deleteWorkOrdersFromPlannerId(List<Long> plannerIdList) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Criteria criteria = new Criteria();
        if(plannerIdList == null || plannerIdList.isEmpty()){
            return;
        }
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("pmPlanner"), StringUtils.join(plannerIdList,","),NumberOperators.EQUALS));
        deleteWorkOrdersInBatch(criteria);
        return;
    }
    private static void deleteWorkOrdersInBatch(Criteria criteria) throws Exception {
        if(criteria == null){
            return;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<FacilioField> workOrderFields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(workOrderFields);
        List<Integer> jobStatus = new ArrayList<>();
        jobStatus.add(V3WorkOrderContext.JobsStatus.ACTIVE.getValue());
        jobStatus.add(V3WorkOrderContext.JobsStatus.SCHEDULED.getValue());
        SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>()
                .module(workOrderModule)
                .select(workOrderFields)
                .beanClass(V3WorkOrderContext.class)
                .andCriteria(criteria)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"),StringUtils.join(jobStatus,","),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleState"),CommonOperators.IS_EMPTY))
                .skipModuleCriteria();
        SelectRecordsBuilder.BatchResult<V3WorkOrderContext> batchResult = builder.getInBatches("WorkOrders.ID", 5000);
        int i = 0;
        List<List<Long>> workOrderIdsToBeDeleted = new ArrayList<>();
        while (batchResult.hasNext()) {
            List<V3WorkOrderContext> workOrderContextList = batchResult.get();
            LOGGER.info("Batch ID == " + i++ + "Count of WorkOrder Records Fetched for this batch == " + workOrderContextList.size());
            workOrderIdsToBeDeleted.add(workOrderContextList.stream().map(V3WorkOrderContext::getId).collect(Collectors.toList()));
        }
        List<Long> woIds = new ArrayList<>();
        if(CollectionUtils.isEmpty(workOrderIdsToBeDeleted)){
            LOGGER.info("WorkOrder List is Empty");
        }
        for (List<Long> workOrderIds : workOrderIdsToBeDeleted) {
            woIds.addAll(workOrderIds);
        }
        LOGGER.info("Pre open WorkOrder Id's to be deleted === " + woIds);
        DeleteRecordBuilder<V3WorkOrderContext> workOrderContextDeleteRecordBuilder = new DeleteRecordBuilder<V3WorkOrderContext>()
                .module(workOrderModule)
                .recordsPerBatch(5000)
                .skipModuleCriteria();
        int workOrderDeletedCount = workOrderContextDeleteRecordBuilder.batchMarkAsDeleteById(woIds);
        LOGGER.info("COUNT OF DELETED PRE OPEN WORKORDERS ===  " + workOrderDeletedCount + " for template ID === ");
    }
    public static List<PMResourcePlanner> getPMResourcePlannerListFromJobPlanId(long jobPlanId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmResourcePlannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        List<FacilioField> resourcePlannerFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(resourcePlannerFields);

        SelectRecordsBuilder<PMResourcePlanner> builder = new SelectRecordsBuilder<PMResourcePlanner>()
                .module(pmResourcePlannerModule)
                .select(resourcePlannerFields)
                .beanClass(PMResourcePlanner.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobPlan"),String.valueOf(jobPlanId),NumberOperators.EQUALS));
        return builder.get();
    }
    public static List<Map<String,Object>> getPPmDetailsAssociatedWithJobPlan(long jobPlanId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pmResourcePlannerModule = modBean.getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        List<FacilioField> resourcePlannerFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        FacilioModule plannedMaintenacenanceModule = modBean.getModule(FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);

        List<FacilioField> fields = new ArrayList<>();
        FacilioField pmName = modBean.getField("name",FacilioConstants.ContextNames.PLANNEDMAINTENANCE);
        FacilioField pmId = modBean.getField("pmId",FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        FacilioField jobPlan = modBean.getField("jobPlan",FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
        FacilioField pmStatus = modBean.getField("pmStatus",FacilioConstants.PM_V2.PM_V2_MODULE_NAME);
        FacilioField sysDeletedField = FieldFactory.getIsDeletedField(ticketModule);
        fields.add(pmName);
        fields.add(pmId);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(pmResourcePlannerModule.getTableName())
                .select(fields)
                .innerJoin(plannedMaintenacenanceModule.getTableName())
                .on(pmResourcePlannerModule.getTableName()+".PM_ID = "+plannedMaintenacenanceModule.getTableName()+".ID")
                .innerJoin(ticketModule.getTableName())
                .on(pmResourcePlannerModule.getTableName()+".PM_ID = "+ticketModule.getTableName()+".ID")
                .andCondition(CriteriaAPI.getCondition(jobPlan,String.valueOf(jobPlanId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(pmStatus,String.valueOf(PlannedMaintenance.PMStatus.ACTIVE.getVal()),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(sysDeletedField,String.valueOf(false), BooleanOperators.IS));
        return builder.get();
    }
    public static int computeFrequencyForCalendar(ScheduleInfo.FrequencyType frequencyType){
        int frequency = 0;
        switch (frequencyType){
            case DAILY:
                frequency = FacilioFrequency.DAILY.getValue();
                break;
            case WEEKLY:
                frequency = FacilioFrequency.WEEKLY.getValue();
                break;
            case MONTHLY_DAY:
            case MONTHLY_WEEK:
                frequency =  FacilioFrequency.MONTHLY.getValue();
                break;
            case QUARTERLY_DAY:
            case QUARTERLY_WEEK:
                frequency = FacilioFrequency.QUARTERTLY.getValue();
                break;
            case HALF_YEARLY_DAY:
            case HALF_YEARLY_WEEK:
                frequency =  FacilioFrequency.HALF_YEARLY.getValue();
                break;
            case YEARLY:
            case YEARLY_WEEK:
                frequency = FacilioFrequency.ANNUALLY.getValue();
                break;
        }
        return frequency;
    }
    public static List<V3SiteContext> getPmV2SitesFromPMv2Id(long pmId)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule plannedMaintenanceSite = modBean.getModule(FacilioConstants.PM_V2.PM_V2_SITES);
        List<FacilioField> plannedMaintenanceSiteFields = modBean.getAllFields(FacilioConstants.PM_V2.PM_V2_SITES);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(plannedMaintenanceSiteFields);

       GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
               .table(plannedMaintenanceSite.getTableName())
               .select(plannedMaintenanceSiteFields)
               .andCondition(CriteriaAPI.getCondition(fieldMap.get("left"),String.valueOf(pmId),NumberOperators.EQUALS));
       List<Map<String,Object>> prop =  builder.get();
        List<V3SiteContext> sites = new ArrayList<>();
       if(CollectionUtils.isEmpty(prop)){
           return sites;
       }
       for(Map<String,Object> map : prop){
           sites.add(V3SpaceAPI.getSiteSpace((Long)map.get("right")));
       }
       return sites;
    }
}
