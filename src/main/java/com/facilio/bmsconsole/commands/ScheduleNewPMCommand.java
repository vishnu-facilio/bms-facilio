package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.*;
import com.facilio.serializable.SerializableCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class ScheduleNewPMCommand implements SerializableCommand {

    private static final Logger LOGGER = Logger.getLogger(PreventiveMaintenanceAPI.class.getName());
    private boolean isBulkUpdate = false;
    private List<PMJobsContext> pmJobsToBeScheduled;

    public static final long serialVersionUID = -1436898572846920375L;

    public ScheduleNewPMCommand() {}

    public ScheduleNewPMCommand(boolean isBulkUpdate) {
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
                    schedulePM(pm, context);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Exception scheduling ", pm.getId());
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
    private void schedulePM(PreventiveMaintenance pm, Context context) throws Exception {
        Map<Long, List<Long>> nextExecutionTimes = new HashMap<>();
        List<WorkOrderContext> wos = new ArrayList<>();

        if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
            switch (pm.getTriggerTypeEnum()) {
                case ONLY_SCHEDULE_TRIGGER:
                    long templateId = pm.getTemplateId();
                    WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
                    if (workorderTemplate != null) {
                        Long baseSpaceId = pm.getBaseSpaceId();
                        if (baseSpaceId == null || baseSpaceId < 0) {
                            baseSpaceId = pm.getSiteId();
                        }
                        List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());

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
                                triggers = new ArrayList<>();
                                triggers.add(PreventiveMaintenanceAPI.getDefaultTrigger(pm.getTriggers()));
                            }

                            for (PMTriggerContext trigger : triggers) {
                                if (resourceMap.get(resourceId) != null) {
                                    workorderTemplate.setResource(resourceMap.get(resourceId));
                                    if (workorderTemplate.getResource() != null) {
                                        workorderTemplate.setResourceId(workorderTemplate.getResource().getId());
                                    }
                                } else {
                                    CommonCommandUtil.emailAlert("work order not generated", "PMID: " + pm.getId() + "ResourceId: " + resourceId);
                                }
                                workorderTemplate.setResource(ResourceAPI.getResource(resourceId));
                                if (trigger.getSchedule() != null) {
                                    long startTime;
                                    if (trigger.getStartTime() < System.currentTimeMillis()) {
                                        startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(System.currentTimeMillis());
                                    } else {
                                        startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
                                    }

                                    switch (pm.getTriggerTypeEnum()) {
                                        case ONLY_SCHEDULE_TRIGGER:
                                            List<WorkOrderContext> workOrderContexts = PreventiveMaintenanceAPI.createWOContextsFromPM(context, pm, trigger, startTime, workorderTemplate);
                                            if (workOrderContexts != null && !workOrderContexts.isEmpty()) {
                                                wos.addAll(workOrderContexts);
                                                List<Long> times = nextExecutionTimes.get(trigger.getId());
                                                if (times == null) {
                                                    times = new ArrayList<>();
                                                }
                                                for (WorkOrderContext wo: workOrderContexts) {

                                                    times.add(wo.getCreatedTime());
                                                }
                                                nextExecutionTimes.put(trigger.getId(), times);
                                            }
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
            long templateId = pm.getTemplateId();
            if (templateId > 0) {
                WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
                ResourceContext resource = ResourceAPI.getResource(workorderTemplate.getResourceId());
                workorderTemplate.setResource(resource);
                for (PMTriggerContext trigger : pm.getTriggers()) {
                    if (trigger.getSchedule() != null) {
                        long startTime;
                        if (trigger.getStartTime() < System.currentTimeMillis()) {
                            startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(System.currentTimeMillis());
                        } else {
                            startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
                        }
                        // PMJobsContext pmJob = null;
                        switch (pm.getTriggerTypeEnum()) {
                            case ONLY_SCHEDULE_TRIGGER:
                                List<WorkOrderContext> workOrderContexts = PreventiveMaintenanceAPI.createWOContextsFromPM(context, pm, trigger, startTime, workorderTemplate);
                                if (workOrderContexts != null && !workOrderContexts.isEmpty()) {
                                    wos.addAll(workOrderContexts);
                                    List<Long> times = nextExecutionTimes.get(trigger.getId());
                                    if (times == null) {
                                        times = new ArrayList<>();
                                    }
                                    for (WorkOrderContext wo: workOrderContexts) {
                                        times.add(wo.getCreatedTime());
                                    }
                                    nextExecutionTimes.put(trigger.getId(), times);
                                }
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
        List<WorkOrderContext> ws = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WO_CONTEXTS);
        if (ws == null) {
            ws = new ArrayList<>();
        }
        ws.addAll(wos);
        context.put(FacilioConstants.ContextNames.WO_CONTEXTS, ws);
        context.put(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES, nextExecutionTimes);
    }

    public List<PMJobsContext> createPMJobsForMultipleResourceAndSchedule (PreventiveMaintenance pm , long endTime, boolean addToDb) throws Exception { //Both in seconds

        List<PMJobsContext> pmJobs = new ArrayList<>();
        pmJobsToBeScheduled = new ArrayList<>();

        Long baseSpaceId = pm.getBaseSpaceId();
        if (baseSpaceId == null || baseSpaceId < 0) {
            baseSpaceId = pm.getSiteId();
        }
        List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());

        Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
        for(Long resourceId :resourceIds) {
            List<PMTriggerContext> triggers  = null;
            if(pmResourcePlanner.get(resourceId) != null) {
                PMResourcePlannerContext currentResourcePlanner = pmResourcePlanner.get(resourceId);
                triggers = new ArrayList<>();
                for (PMTriggerContext trig: currentResourcePlanner.getTriggerContexts()) {
                    if (pm.getTriggerMap() != null && pm.getTriggerMap().get(trig.getName()) != null) {
                        triggers.add(pm.getTriggerMap().get(trig.getName()));
                    }
                }
            }
            if(triggers == null) {
                triggers = new ArrayList<>();
                triggers.add(PreventiveMaintenanceAPI.getDefaultTrigger(pm.getTriggers()));
            }

            if (triggers != null) {
                for (PMTriggerContext trigger : triggers) {
                    long startTime;
                    if (trigger.getStartTime() < System.currentTimeMillis()) {
                        startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(System.currentTimeMillis());
                    } else {
                        startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
                    }
                    long nextExecutionTime = trigger.getSchedule().nextExecutionTime(startTime);

                    boolean isFirst = true;
                    while(nextExecutionTime <= endTime && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
                        PMJobsContext pmJob = PreventiveMaintenanceAPI.getpmJob(pm, trigger, resourceId, nextExecutionTime, addToDb);
                        pmJobs.add(pmJob);
                        if(isFirst) {
                            pmJobsToBeScheduled.add(pmJob);
                        }
                        nextExecutionTime = trigger.getSchedule().nextExecutionTime(nextExecutionTime);
                        isFirst = false;
                    }
                }
            }
        }

        if(addToDb) {
            PreventiveMaintenanceAPI.addPMJobs(pmJobs);
        }
        PreventiveMaintenanceAPI.schedulePMJob(pmJobsToBeScheduled);
        return pmJobs;
    }
}
