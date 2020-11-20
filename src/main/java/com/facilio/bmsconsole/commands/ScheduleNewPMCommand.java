package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.serializable.SerializableCommand;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

//Move to jobs package after migrations are completed and remove SerializableCommand interface
public class ScheduleNewPMCommand extends FacilioJob implements SerializableCommand {

    private static final Logger LOGGER = Logger.getLogger(ScheduleNewPMCommand.class.getName());
    private boolean isBulkUpdate;

    public static final long serialVersionUID = -1436898572846920375L;
    private static final String ENDTIME = "endTime";
    private static final String ACTION = "action";

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

        long endTime = (long) context.get(ENDTIME);
        PreventiveMaintenanceAPI.ScheduleActions action = (PreventiveMaintenanceAPI.ScheduleActions) context.get(ACTION);

        for(PreventiveMaintenance pm: pms) {
            if (pm.getTriggers() != null && pm.isActive()) {
                try {
                    schedulePM(pm, context, action, endTime);
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

    private void schedulePM(PreventiveMaintenance pm, Context context, PreventiveMaintenanceAPI.ScheduleActions action, long endTimeFromProp) throws Exception {
        LOGGER.log(Level.FINE, "Generating work orders for PM: " + pm.getId());
        Map<Long, List<Long>> nextExecutionTimes = new HashMap<>();
        List<WorkOrderContext> wos = new ArrayList<>();
        List<BulkWorkOrderContext> bulkWorkOrderContexts = new ArrayList<>();
        long templateId = pm.getTemplateId();
        WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);

        long endTime = endTimeFromProp;
        long minTime = -1L;
        if (action == PreventiveMaintenanceAPI.ScheduleActions.GENERATION) {
            minTime = pm.getWoGeneratedUpto();
        }

        if (pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
            switch (pm.getTriggerTypeEnum()) {
                case ONLY_SCHEDULE_TRIGGER:
                    if (workorderTemplate != null) {
                        List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),pm.getSiteIds(),pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts(), true);
                        Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
                        List<ResourceContext> resourceObjs = ResourceAPI.getResources(resourceIds, false); // ?
                        Map<Long, ResourceContext> resourceMap = new HashMap<>();
                        if(resourceObjs != null && !resourceObjs.isEmpty()) {
                            for (ResourceContext resource : resourceObjs) {
                                resourceMap.put(resource.getId(), resource);
                            }
                        }

                        if (action == PreventiveMaintenanceAPI.ScheduleActions.INIT) {
                            List<PMTriggerContext> triggers = null;
                            for (Long resourceId: resourceIds) {
                                PMResourcePlannerContext currentResourcePlanner = pmResourcePlanner.get(resourceId);
                                if (pmResourcePlanner.get(resourceId) != null) {
                                    triggers = new ArrayList<>();
                                    for (PMTriggerContext trig : currentResourcePlanner.getTriggerContexts()) {
                                        if (pm.getTriggerMap() != null && pm.getTriggerMap().get(trig.getName()) != null) {
                                            triggers.add(pm.getTriggerMap().get(trig.getName()));
                                        }
                                    }
                                }
                            }

                            if (triggers == null) {
                                triggers = PreventiveMaintenanceAPI.getDefaultTrigger(pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers(), pm.getTriggers());
                            }

                            endTime = PreventiveMaintenanceAPI.getEndTime(-1L, triggers);
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
                                    long startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
                                    switch (pm.getTriggerTypeEnum()) {
                                        case ONLY_SCHEDULE_TRIGGER:
                                            BulkWorkOrderContext bulkWoContextsFromPM = PreventiveMaintenanceAPI.createBulkWoContextsFromPM(context, pm, trigger, startTime, endTime, minTime, workorderTemplate);
                                            bulkWorkOrderContexts.add(bulkWoContextsFromPM);
                                            nextExecutionTimes.put(trigger.getId(), bulkWoContextsFromPM.getNextExecutionTimes());
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
        } else if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
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

                        if (action == PreventiveMaintenanceAPI.ScheduleActions.INIT) {
                            List<PMTriggerContext> triggers = null;
                            for (Long resourceId: resourceIds) {
                                PMResourcePlannerContext currentResourcePlanner = pmResourcePlanner.get(resourceId);
                                if (pmResourcePlanner.get(resourceId) != null) {
                                    triggers = new ArrayList<>();
                                    for (PMTriggerContext trig : currentResourcePlanner.getTriggerContexts()) {
                                        if (pm.getTriggerMap() != null && pm.getTriggerMap().get(trig.getName()) != null) {
                                            triggers.add(pm.getTriggerMap().get(trig.getName()));
                                        }
                                    }
                                }
                            }

                            if (triggers == null) {
                                triggers = PreventiveMaintenanceAPI.getDefaultTrigger(pm.getDefaultAllTriggers() != null && pm.getDefaultAllTriggers(), pm.getTriggers());
                            }

                            endTime = PreventiveMaintenanceAPI.getEndTime(-1L, triggers);
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
                                    long startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
                                    switch (pm.getTriggerTypeEnum()) {
                                        case ONLY_SCHEDULE_TRIGGER:
                                            BulkWorkOrderContext bulkWoContextsFromPM = PreventiveMaintenanceAPI.createBulkWoContextsFromPM(context, pm, trigger, startTime, endTime, minTime, workorderTemplate);
                                            bulkWorkOrderContexts.add(bulkWoContextsFromPM);
                                            nextExecutionTimes.put(trigger.getId(), bulkWoContextsFromPM.getNextExecutionTimes());
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
                if (action == PreventiveMaintenanceAPI.ScheduleActions.INIT) {
                    endTime = PreventiveMaintenanceAPI.getEndTime(-1, pm.getTriggers());
                }
                for (PMTriggerContext trigger : pm.getTriggers()) {
                    if (trigger.getSchedule() != null) {
                        long startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
                        // PMJobsContext pmJob = null;
                        switch (pm.getTriggerTypeEnum()) {
                            case ONLY_SCHEDULE_TRIGGER:
                                BulkWorkOrderContext bulkWoContextsFromPM = PreventiveMaintenanceAPI.createBulkWoContextsFromPM(context, pm, trigger, startTime, endTime, minTime, workorderTemplate);
                                bulkWorkOrderContexts.add(bulkWoContextsFromPM);
                                nextExecutionTimes.put(trigger.getId(), bulkWoContextsFromPM.getNextExecutionTimes());
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

        if (!bulkWorkOrderContexts.isEmpty()) {
            BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext(bulkWorkOrderContexts);
            context.put(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT, bulkWorkOrderContext);
            context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);

            if (bulkWorkOrderContext.getWorkOrderContexts().isEmpty()) {
                LOGGER.log(Level.SEVERE,"Work orders are empty before sending it to chain");
            }

            FacilioChain addWOChain = TransactionChainFactory.getTempAddPreOpenedWorkOrderChain();
            addWOChain.addCommand(new PMEditBlockRemovalCommand(pm.getId()));
            addWOChain.execute(context);
            if (bulkWorkOrderContext.getWorkOrderContexts() != null && !bulkWorkOrderContext.getWorkOrderContexts().isEmpty()) {
                wos.addAll(bulkWorkOrderContext.getWorkOrderContexts());
            }
        }

        List<WorkOrderContext> ws = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WO_CONTEXTS);
        if (ws == null) {
            ws = new ArrayList<>();
        }

        ws.addAll(wos);

        if (endTime != -1) {
            PreventiveMaintenanceAPI.incrementGenerationTime(pm.getId(), endTime);
        }

        context.put(FacilioConstants.ContextNames.WO_CONTEXTS, ws);
        context.put(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES, nextExecutionTimes);
    }

    @Override
    public void execute(JobContext jc) throws Exception {
        List<Long> recordIds = Collections.singletonList(jc.getJobId());
        FacilioContext context = new FacilioContext();
        List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMsDetails(recordIds);

        JSONObject jobProps = BmsJobUtil.getJobProps(jc.getJobId(), "ScheduleNewPM");
        long endTime = (long) jobProps.get("endTime");
        Long actionVal = (Long) jobProps.get("action");
        PreventiveMaintenanceAPI.ScheduleActions action = PreventiveMaintenanceAPI.ScheduleActions.getEnum(actionVal.intValue());

        context.put(ACTION, action);
        context.put(ENDTIME, endTime);

        if (action == PreventiveMaintenanceAPI.ScheduleActions.INIT) {
            PreventiveMaintenanceAPI.deleteScheduledWorkorders(recordIds);
        } else if (action == PreventiveMaintenanceAPI.ScheduleActions.GENERATION) {
            context.put(ENDTIME, endTime);
        }

        Map<String,PMTriggerContext> triggerMap = new HashMap<>();
        if (pms.get(0).getTriggers() != null && !pms.get(0).getTriggers().isEmpty()) {
            List<PMTriggerContext> pmTriggerContexts = pms.get(0).getTriggers().stream().filter(i -> i.getTriggerExecutionSourceEnum() == PMTriggerContext.TriggerExectionSource.SCHEDULE).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(pmTriggerContexts)) {
                PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(recordIds, 0);
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
            PreventiveMaintenanceAPI.updateWorkOrderCreationStatus(recordIds, 0);
        }
    }
}
