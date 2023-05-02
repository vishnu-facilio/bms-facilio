package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.*;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.serializable.SerializableCommand;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//Move to jobs package after migrations are completed and remove SerializableCommand interface
public class CorrectPMTriggerSelection extends FacilioJob implements SerializableCommand {

    private static final Logger LOGGER = Logger.getLogger(CorrectPMTriggerSelection.class.getName());
    private boolean isBulkUpdate;

    public static final long serialVersionUID = -1436898572846920375L;
    private static final String ENDTIME = "endTime";
    private static final String ACTION = "action";
    private long startTime;
    private long endTime;

    private boolean doMig = false;


    public CorrectPMTriggerSelection() {}

    public CorrectPMTriggerSelection(boolean isBulkUpdate) {
        this.isBulkUpdate = isBulkUpdate;
    }

    @Override
    public boolean execute(Context context) throws Exception {
        List<PreventiveMaintenance> pms;
        if (isBulkUpdate) {
            pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
        }
        else {
            pms = Collections.singletonList((PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
        }

        if (pms == null) {
            return false;
        }

        for(PreventiveMaintenance pm: pms) {
            if (pm.getTriggers() != null && pm.isActive()) {
                try {
                    schedulePM(pm, context, endTime);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Exception scheduling " + pm.getId());
                    CommonCommandUtil.emailException("Exception scheduling ", "PM ID "+pm.getId(), e);
                    throw e;
                }
            }
        }

        List<WorkOrderContext> ws = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WO_CONTEXTS);

        Map<String, List<WorkOrderContext>> resourceTriggerWoMap = new HashMap<>();
        if (ws != null && !ws.isEmpty()) {
            for (WorkOrderContext w: ws) {
                if (w.getResource() != null && w.getResource().getId() > 0) {
                    List<WorkOrderContext> woList = resourceTriggerWoMap.get(""+w.getResource().getId()+w.getTrigger().getId());
                    if (woList == null) {
                        woList = new ArrayList<>();
                    }
                    woList.add(w);
                    resourceTriggerWoMap.put(""+w.getResource().getId()+w.getTrigger().getId(), woList);
                }
            }
        }

        PreventiveMaintenanceAPI.scheduleReminders(resourceTriggerWoMap, pms);
        return false;
    }

    private void schedulePM(PreventiveMaintenance pm, Context context, long endTimeFromProp) throws Exception {
        LOGGER.log(Level.FINE, "Generating work orders for PM: " + pm.getId());
        Map<Long, List<Triple<Long, Long, Set<String>>>> nextExecutionTimes = new HashMap<>();
        long templateId = pm.getTemplateId();
        WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);

        long endTime = endTimeFromProp;

        if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
            switch (pm.getTriggerTypeEnum()) {
                case ONLY_SCHEDULE_TRIGGER:
                    if (workorderTemplate != null) {
                        Long baseSpaceId = pm.getBaseSpaceId();
                        if (baseSpaceId == null || baseSpaceId < 0) {
                            baseSpaceId = pm.getSiteId();
                        }
                        List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), false);

                        Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
                        List<ResourceContext> resourceObjs = ResourceAPI.getResources(resourceIds, false); // ?
                        Map<Long, ResourceContext> resourceMap = new HashMap<>();
                        if(resourceObjs != null && !resourceObjs.isEmpty()) {
                            for (ResourceContext resource : resourceObjs) {
                                resourceMap.put(resource.getId(), resource);
                            }
                        }

                        for(Long resourceId :resourceIds) {
                            List<PMTriggerContext> triggers = null;
                            if (pmResourcePlanner.get(resourceId) != null) {
                                PMResourcePlannerContext currentResourcePlanner = pmResourcePlanner.get(resourceId);
                                triggers = new ArrayList<>();
                                for (PMTriggerContext trig : currentResourcePlanner.getTriggerContexts()) {
                                    if (pm.getTriggerMap() != null && pm.getTriggerMap().get(trig.getName()) != null) {
                                        triggers.add(pm.getTriggerMap().get(trig.getName()));
                                    }
                                }
                                if (currentResourcePlanner.getAssignedToId() != null && currentResourcePlanner.getAssignedToId() > 0 ) {
                                    workorderTemplate.setAssignedToId(currentResourcePlanner.getAssignedToId());
                                }
                            }

                            if (triggers == null) {
                                triggers = PreventiveMaintenanceAPI.getDefaultTrigger(pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers(), pm.getTriggers());
                            }

                            for (PMTriggerContext trigger : triggers) {
                                if (resourceMap.get(resourceId) != null) {
                                    workorderTemplate.setResource(resourceMap.get(resourceId));
                                    if (workorderTemplate.getResource() != null) {
                                        workorderTemplate.setResourceId(workorderTemplate.getResource().getId());
                                    }
                                } else {
                                    LOGGER.log(Level.SEVERE,"work order not generated PMID: " + pm.getId() + "ResourceId: " + resourceId);
                                    CommonCommandUtil.emailAlert("work order not generated", "PMID: " + pm.getId() + "ResourceId: " + resourceId);
                                }
                                workorderTemplate.setResource(ResourceAPI.getResource(resourceId));
                                if (trigger.getSchedule() != null) {
                                    switch (pm.getTriggerTypeEnum()) {
                                        case ONLY_SCHEDULE_TRIGGER:
                                            List<Triple<Long, Long, Set<String>>> execTimes = createBulkWoContextsFromPM(context, pm, trigger, startTime, endTime, -1, workorderTemplate);
                                            if (nextExecutionTimes.get(resourceId) == null) {
                                                nextExecutionTimes.put(resourceId, new ArrayList<>());
                                            }
                                            nextExecutionTimes.get(resourceId).addAll(execTimes);
                                            break;
                                        case FIXED:
                                        case FLOATING:
                                            // pmJob = PreventiveMaintenanceAPI.createPMJobOnce(pm, trigger, startTime);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case FIXED:
                case FLOATING:
                    throw new IllegalArgumentException("PM Of type Multiple cannot have this type of trigger");
                default:
                    break;
            }
        }
        else {
            if (templateId > 0) {
                ResourceContext resource = ResourceAPI.getResource(workorderTemplate.getResourceIdVal());
                workorderTemplate.setResource(resource);
                for (PMTriggerContext trigger : pm.getTriggers()) {
                    if (trigger.getSchedule() != null) {
                        // PMJobsContext pmJob = null;
                        switch (pm.getTriggerTypeEnum()) {
                            case ONLY_SCHEDULE_TRIGGER:
                                List<Triple<Long, Long, Set<String>>> execTimes = createBulkWoContextsFromPM(context, pm, trigger, startTime, endTime, -1, workorderTemplate);
                                if (nextExecutionTimes.get(resource.getId()) == null) {
                                    nextExecutionTimes.put(resource.getId(), new ArrayList<>());
                                }
                                nextExecutionTimes.get(resource.getId()).addAll(execTimes);
                                break;
                        }
                    }
                }
            }
        }

        List<WorkOrderContext> pmWorkOrders = getPmWorkOrders(pm.getId());

        for (WorkOrderContext workOrderContext: pmWorkOrders) {
            LOGGER.log(Level.SEVERE, "Work order ID " + workOrderContext.getId());
            long resourceId = workOrderContext.getResource().getId();
            List<Triple<Long, Long, Set<String>>> execs = nextExecutionTimes.get(resourceId);

            if (execs == null) {
                LOGGER.log(Level.SEVERE, "no start " + workOrderContext.getId());
                continue;
            }

            Map<Long, List<Triple<Long, Long, Set<String>>>> execMap = new HashMap<>();
            for (Triple<Long, Long, Set<String>> exec: execs) {
                if (execMap.get(exec.getMiddle()) == null) {
                    execMap.put(exec.getMiddle(), new ArrayList<>());
                }
                execMap.get(exec.getMiddle()).add(exec);
            }

            List<Triple<Long, Long, Set<String>>> exec = execMap.get(workOrderContext.getCreatedTime()/1000);

            if (CollectionUtils.isEmpty(exec)) {
                LOGGER.log(Level.SEVERE, "No exec present " + workOrderContext.getId());
                continue;
            }

            if (exec.size() > 1) {
                LOGGER.log(Level.SEVERE, "duplicates in exec " + workOrderContext.getId());
                continue;
            }

            Map<Long, TaskSectionContext> taskSections = TicketAPI.getTaskSectionFromParentTicketID(workOrderContext.getId());

            Set<String> sectionNames = new HashSet<>();
            for (String name: exec.get(0).getRight()) {
                sectionNames.add(StringUtils.trim(name));
            }

            List<Map.Entry<Long, TaskSectionContext>> missing = new ArrayList<>();
            List<String> missingString = new ArrayList<>();
            List<String> woArray = new ArrayList<>();
            List<String> check = Arrays.asList("annually","monthly","yearly","quarterly","half-yearly","half yearly","halfyearly", "daily", "weekly");

            for (Map.Entry<Long, TaskSectionContext> entry: taskSections.entrySet()) {
                String trimmed = StringUtils.trim(entry.getValue().getName());
                woArray.add(entry.getValue().getName());
                if (!match(sectionNames, trimmed)) {
                    if (trimmed.toLowerCase().contains("annually") || trimmed.toLowerCase().contains("monthly") || trimmed.toLowerCase().contains("yearly")
                            || trimmed.toLowerCase().contains("quarterly") || trimmed.toLowerCase().contains("half-yearly") || trimmed.toLowerCase().contains("half yearly")
                            || trimmed.toLowerCase().contains("daily") || trimmed.toLowerCase().contains("weekly")) {
                        missing.add(entry);
                        missingString.add(trimmed);
                    }
                }
            }

            LOGGER.log(Level.SEVERE, workOrderContext.getId() + " ghost array " + Arrays.toString(sectionNames.toArray()));
            LOGGER.log(Level.SEVERE, workOrderContext.getId() + " woArray array " + Arrays.toString(woArray.toArray()));
            LOGGER.log(Level.SEVERE, workOrderContext.getId() + " missing array " + Arrays.toString(missingString.toArray()));

            if (isDoMig()) {
                deleteMissingEntry(missing);
            }
        }
    }

    private static boolean match(Set<String> sectionNames, String check) {
        for (String sectionName: sectionNames) {
            if (sectionName.contains(check)) {
                return true;
            }

            if (check.contains(sectionName)) {
                return true;
            }
        }
        return false;
    }

    private static List<WorkOrderContext> getPmWorkOrders(long pmId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
                .module(module)
                .beanClass(WorkOrderContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
                .andCondition(CriteriaAPI.getCondition("PM_ID", "pmId", pmId+"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("JOB_STATUS", "jobStatus", 3+"", NumberOperators.EQUALS))
                ;
        return builder.get();
    }

    private static void deleteMissingEntry(List<Map.Entry<Long, TaskSectionContext>> missing) throws Exception {
        for (Map.Entry<Long, TaskSectionContext> miss: missing) {
            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
            deleteRecordBuilder.table("Tasks")
                    .andCondition(CriteriaAPI.getCondition("SECTION_ID", "section", miss.getKey()+"", NumberOperators.EQUALS))
                    .delete();

            GenericDeleteRecordBuilder sectionDelete = new GenericDeleteRecordBuilder();
            sectionDelete.table("Task_Section")
                    .batchDeleteById(Collections.singletonList(miss.getKey()));
        }
    }

    private static List<Triple<Long, Long, Set<String>>> createBulkWoContextsFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, long startTime, long endTime, long minTime, WorkorderTemplate woTemplate) throws Exception {
        Pair<Long, Integer> nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(Pair.of(startTime, 0));
        int currentCount = pm.getCurrentExecutionCount();
        List<Long> nextExecutionTimes = new ArrayList<>();
        LOGGER.log(Level.SEVERE, "PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId() + " next exec time " + nextExecutionTime.getLeft() + " end time " + endTime);
        while (nextExecutionTime.getLeft() <= endTime && (pm.getMaxCount() == -1 || currentCount < pm.getMaxCount()) && (pm.getEndTime() == -1 || nextExecutionTime.getLeft() <= pm.getEndTime())) {
            if (nextExecutionTime.getLeft() < minTime) {
                nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
                if (pmTrigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
                    break;
                }
                continue;
            }

            nextExecutionTimes.add(nextExecutionTime.getLeft());

            nextExecutionTime = pmTrigger.getSchedule().nextExecutionTime(nextExecutionTime);
            currentCount++;
            if (pmTrigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
                break;
            }
        }

        LOGGER.log(Level.SEVERE, "PM "+ pm.getId() + " PM Trigger ID: "+pmTrigger.getId() + " next exec times " + Arrays.toString(nextExecutionTimes.toArray()));

        return createBulkContextFromPM(context, pm, pmTrigger, woTemplate, nextExecutionTimes);
    }

    private static List<Triple<Long, Long, Set<String>>> createBulkContextFromPM(Context context, PreventiveMaintenance pm, PMTriggerContext pmTrigger, WorkorderTemplate workorderTemplate, List<Long> nextExecutionTimes) throws Exception {
        List<Triple<Long, Long, Set<String>>> result = new ArrayList<>();
        for (long nextExecutionTime: nextExecutionTimes) {
            WorkorderTemplate clonedWoTemplate = FieldUtil.cloneBean(workorderTemplate, WorkorderTemplate.class);

            if (workorderTemplate.getSectionTemplates() != null) {
                List<TaskSectionTemplate> sectionTemplates = new ArrayList<>();
                for (TaskSectionTemplate sectionTemplate: workorderTemplate.getSectionTemplates()) {
                    TaskSectionTemplate template = FieldUtil.cloneBean(sectionTemplate, TaskSectionTemplate.class);
                    sectionTemplates.add(template);
                }
                clonedWoTemplate.setSectionTemplates(sectionTemplates);
            }

            for (TaskTemplate taskTemplate: workorderTemplate.getTaskTemplates()) {
                List<TaskTemplate> taskTemplates = new ArrayList<>();
                TaskTemplate template = FieldUtil.cloneBean(taskTemplate, TaskTemplate.class);
                taskTemplates.add(template);
                clonedWoTemplate.setTaskTemplates(taskTemplates);
            }

            if (workorderTemplate.getPreRequestSectionTemplates() != null) {
                List<TaskSectionTemplate> sectionTemplates = new ArrayList<>();
                for (TaskSectionTemplate sectionTemplate: workorderTemplate.getPreRequestSectionTemplates()) {
                    TaskSectionTemplate template = FieldUtil.cloneBean(sectionTemplate, TaskSectionTemplate.class);
                    sectionTemplates.add(template);
                }
                clonedWoTemplate.setPreRequestSectionTemplates(sectionTemplates);
            }

            for (TaskTemplate taskTemplate: workorderTemplate.getPreRequestTemplates()) {
                List<TaskTemplate> taskTemplates = new ArrayList<>();
                TaskTemplate template = FieldUtil.cloneBean(taskTemplate, TaskTemplate.class);
                taskTemplates.add(template);
                clonedWoTemplate.setPreRequestTemplates(taskTemplates);
            }

            WorkOrderContext wo = clonedWoTemplate.getWorkorder();
            wo.setScheduledStart(nextExecutionTime * 1000);
            if (clonedWoTemplate.getResourceIdVal() > 0) {
                if (clonedWoTemplate.getResource() != null && clonedWoTemplate.getResource().getId() > 0) {
                    wo.setResource(clonedWoTemplate.getResource());
                } else {
                    ResourceContext resourceContext = getResource(context, clonedWoTemplate.getResourceIdVal());
                    clonedWoTemplate.setResource(resourceContext);
                }
            }
            int preRequisiteCount= clonedWoTemplate.getPreRequestSectionTemplates() != null ? clonedWoTemplate.getPreRequestSectionTemplates().size() : 0;
            wo.setPrerequisiteEnabled(preRequisiteCount != 0);
            if (wo.getPrerequisiteEnabled()) {
                wo.setPreRequestStatus(WorkOrderContext.PreRequisiteStatus.NOT_STARTED.getValue());
            } else {
                wo.setPreRequestStatus(WorkOrderContext.PreRequisiteStatus.COMPLETED.getValue());
            }
            wo.setPm(pm);

            wo.setTrigger(pmTrigger);
            wo.setJobStatus(WorkOrderContext.JobsStatus.ACTIVE);
            wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
            wo.setPm(pm);
            if (wo.getSpace() != null && wo.getSpace().getId() != -1){
                wo.setResource(wo.getSpace());
            }

            Map<String, List<TaskContext>> taskMap = null;
            Map<String, List<TaskContext>> taskMapForNewPmExecution = null;	// should be handled in above if too

            boolean isNewPmType = false;

            if(clonedWoTemplate.getSectionTemplates() != null) {
                for(TaskSectionTemplate sectiontemplate : clonedWoTemplate.getSectionTemplates()) {// for new pm_Type section should be present and every section should have a AssignmentType
                    if(sectiontemplate.getAssignmentType() < 0) {
                        isNewPmType =  false;
                        break;
                    }
                    else {
                        isNewPmType = true;
                    }
                }
            }

            if(isNewPmType) {
                Long woTemplateResourceId = wo.getResource() != null ? wo.getResource().getId() : -1;
                if(woTemplateResourceId > 0) {
                    Long currentTriggerId = pmTrigger.getId();
                    taskMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(context, clonedWoTemplate.getSectionTemplates(), woTemplateResourceId, currentTriggerId, false);
                }
            } else {
                taskMapForNewPmExecution = clonedWoTemplate.getTasks();
            }

            if(taskMapForNewPmExecution != null) {
                taskMap = taskMapForNewPmExecution;
            }

            if (taskMap != null) {
                Set<String> keys = taskMap.keySet();
                result.add(Triple.of(pmTrigger.getId(), nextExecutionTime, keys));
            }

            Map<String, List<TaskContext>> preRequestMap = null;
            Map<String, List<TaskContext>> preRequestMapForNewPmExecution = null;

            preRequestMapForNewPmExecution = clonedWoTemplate.getPreRequests();

            if (preRequestMapForNewPmExecution != null) {
                preRequestMap = preRequestMapForNewPmExecution;
            }

            PreventiveMaintenanceAPI.updateResourceDetails(wo, taskMap);

            if (taskMap == null || taskMap.isEmpty()) {
                LOGGER.log(Level.SEVERE, "task map is empty pm id " + wo.getPm().getId());
            }
        }

        return result;
    }

    private static ResourceContext getResource(Context context, Long resourceId) throws Exception {
        Map<Long, ResourceContext> resourceMap = (Map<Long, ResourceContext>) context.get(FacilioConstants.ContextNames.RESOURCE_MAP);
        if (resourceMap == null) {
            resourceMap = new HashMap<>();
            context.put(FacilioConstants.ContextNames.RESOURCE_MAP, resourceMap);
        }
        ResourceContext resourceContext = resourceMap.get(resourceId);
        if (resourceContext == null) {
            resourceContext = ResourceAPI.getResource(resourceId);
            resourceMap.put(resourceId, resourceContext);
        }
        return resourceContext;
    }

    private static FacilioStatus getStatus(Context context, FacilioStatus.StatusType statusType) throws Exception {
        Map<FacilioStatus.StatusType, FacilioStatus> statusMap = (Map<FacilioStatus.StatusType, FacilioStatus>) context.get(FacilioConstants.ContextNames.STATUS_MAP);
        if (statusMap == null) {
            statusMap = new HashMap<>();
            context.put(FacilioConstants.ContextNames.STATUS_MAP, statusMap);
        }

        FacilioStatus result = statusMap.get(statusType);
        if (result == null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
            List<FacilioStatus> statusOfStatusType = TicketAPI.getStatusOfStatusType(workorderModule, statusType);
            result = statusOfStatusType.get(0);
            statusMap.put(statusType, result);
        }

        return result;
    }

    @Override
    public void execute(JobContext jc) throws Exception {
        List<Long> recordIds = Collections.singletonList(jc.getJobId());
        FacilioContext context = new FacilioContext();
        List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMsDetails(recordIds);

        Map<String,PMTriggerContext> triggerMap = new HashMap<>();
        if (pms.get(0).getTriggers() != null && !pms.get(0).getTriggers().isEmpty()) {
            List<PMTriggerContext> pmTriggerContexts = pms.get(0).getTriggers().stream().filter(i -> i.getTriggerExecutionSourceEnum() == PMTriggerContext.TriggerExectionSource.SCHEDULE).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(pmTriggerContexts)) {

            } else {
                for (int i = 0; i < pmTriggerContexts.size(); i++) {
                    PMTriggerContext triggerContext = pmTriggerContexts.get(i);
                    triggerMap.put(triggerContext.getName(), triggerContext);
                }
                pms.get(0).setTriggerMap(triggerMap);

                context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pms.get(0));
                execute(context);
            }
        } else {

        }
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isDoMig() {
        return doMig;
    }

    public void setDoMig(boolean doMig) {
        this.doMig = doMig;
    }
}
