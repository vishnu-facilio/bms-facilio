package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.*;
import com.facilio.chain.FacilioContext;
import com.facilio.serializable.SerializableCommand;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

//Move to jobs package after migrations are completed and remove SerializableCommand interface
public class ScheduleNewPMCommand extends FacilioJob implements SerializableCommand {

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
    private void schedulePM(PreventiveMaintenance pm, Context context) throws Exception {
        LOGGER.log(Level.SEVERE, "Generating work orders for PM: " + pm.getId());
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
                                    LOGGER.log(Level.SEVERE,"work order not generated PMID: " + pm.getId() + "ResourceId: " + resourceId);
                                    CommonCommandUtil.emailAlert("work order not generated", "PMID: " + pm.getId() + "ResourceId: " + resourceId);
                                }
                                workorderTemplate.setResource(ResourceAPI.getResource(resourceId));
                                if (trigger.getSchedule() != null) {
                                    long startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());

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
                        long startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
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
                                WorkOrderContext wo = PreventiveMaintenanceAPI.createWOContextsFromPMOnce(context, pm, trigger, workorderTemplate, startTime);
                                wos.add(wo);
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

    @Override
    public void execute(JobContext jc) throws Exception {
        LOGGER.log(Level.INFO, "Creating jobs for pm: "+ jc.getJobId());
        FacilioContext context = new FacilioContext();
        List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMsDetails(Arrays.asList(jc.getJobId()));
        Map<String,PMTriggerContext> triggerMap = new HashMap<>();
        for (int i = 0; i < pms.get(0).getTriggers().size(); i++) {
            PMTriggerContext triggerContext = pms.get(0).getTriggers().get(i);
            triggerMap.put(triggerContext.getName(), triggerContext);
        }
        pms.get(0).setTriggerMap(triggerMap);
        context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pms.get(0));
        execute(context);
        PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(Arrays.asList(jc.getJobId()), false);
    }

}
