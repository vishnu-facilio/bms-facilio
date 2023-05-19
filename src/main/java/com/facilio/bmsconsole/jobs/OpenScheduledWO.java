package com.facilio.bmsconsole.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.modules.*;
import com.facilio.util.FacilioUtil;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.activity.AddActivitiesCommand;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;


public class OpenScheduledWO extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(OpenScheduledWO.class.getName());

    private boolean isNMDP() {
        final long NMDP_ID = 429L;
        Organization nmdp = AccountUtil.getCurrentOrg();
        if (nmdp != null) {
            return nmdp.getOrgId() == NMDP_ID;
        }
        return false;
    }

    @Override
    public void execute(JobContext jc) throws Exception {
        LOGGER.log(Level.ERROR, "OpenScheduledWO -> execute(JobContext)");
        try {
            long woId = jc.getJobId();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
            selectRecordsBuilder.select(fields)
                    .module(module)
                    .beanClass(WorkOrderContext.class)
                    .innerJoin("Resources")
                    .on("Resources.ID = Tickets.RESOURCE_ID AND (Resources.SYS_DELETED IS NULL OR Resources.SYS_DELETED = 0)")
                    .andCondition(CriteriaAPI.getIdCondition(woId, module))
                    .skipModuleCriteria();
            List<WorkOrderContext> workOrderContexts = selectRecordsBuilder.get();
            if (workOrderContexts == null || workOrderContexts.isEmpty()) {
                LOGGER.log(Level.ERROR, "Null workOrderContexts.");
                return;
            }

            WorkOrderContext wo = workOrderContexts.get(0);

            PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPM(wo.getPm().getId(), true);

            if (!PreventiveMaintenanceAPI.canOpenWorkOrder(pm)) {
                return;
            }
            if(!FacilioUtil.isEmptyOrNull(wo.getResource())){
                ResourceContext resource = ResourceAPI.getResource(wo.getResource().getId());
                if(resource != null && resource.isDecommission()){
//                    FacilioModule ticketModule = modBean.getModule(FacilioConstants.ContextNames.TICKET);
                    FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

                    DeleteRecordBuilder<WorkOrderContext> delete = new DeleteRecordBuilder<WorkOrderContext>()
                            .module(workOrderModule)
                            .skipModuleCriteria()
                            .andCondition(CriteriaAPI.getIdCondition(wo.getId(), workOrderModule));
                    delete.markAsDelete();

//                    DeleteRecordBuilder<TicketContext> delete = new DeleteRecordBuilder<TicketContext>()
//                            .module(ticketModule)
//                            .andCondition(CriteriaAPI.getIdCondition(wo.getId(), ticketModule));
//                    delete.markAsDelete();

                    LOGGER.log(Level.ERROR, "PMV1 WorkOrder has been deleted as the corresponding resource is decommissioned: WO_ID = " + wo.getId());
                } else {
                    PreventiveMaintenanceAPI.setPreWorkOrderInactive(pm,wo);
            /*
            Boolean markIgnored = pm.getMarkIgnoredWo();

            if( markIgnored != null && markIgnored ) {
                PreventiveMaintenance newPm = wo.getPm();
                String pmId = Long.toString(newPm.getId());
                ResourceContext resource = wo.getResource();
                SelectRecordsBuilder<WorkOrderContext> selectNewRecordsBuilder = new SelectRecordsBuilder<>();
                selectNewRecordsBuilder.select(fields)
                        .module(module)
                        .beanClass(WorkOrderContext.class)
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("pm"), pmId, NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("jobStatus"), 3+"", NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition(fieldMap.get("resource"), Long.toString(resource.getId()), NumberOperators.EQUALS))
                        .orderBy("CREATED_TIME desc")
                        .limit(1);

                List<WorkOrderContext> workOrderNewContexts = selectNewRecordsBuilder.get();

                if( !(workOrderNewContexts == null || workOrderNewContexts.isEmpty()) ) {
                    WorkOrderContext wc = workOrderNewContexts.get(0);
                    String preWoId = Long.toString(wc.getId());
                    long actualStartTime = wc.getActualWorkStart();
                    List<FacilioField> woList = new ArrayList<FacilioField> ();
                    FacilioField markInactive = fieldMap.get("markInactive");
                    woList.add(markInactive);
                    if(actualStartTime == -1 ) {
                        FacilioContext context = new FacilioContext();
                        List<Long> id = new ArrayList<Long> ();
                        id.add(wc.getId());
                        context.put(FacilioConstants.ContextNames.WORK_ORDER, wc);
                        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
                        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

                        wc.setMarkInactive(true);
                        FacilioChain updateWorkOrder = TransactionChainFactory.getUpdateWorkOrderChain();
                        updateWorkOrder.execute(context);

                     //   UpdateRecordBuilder<WorkOrderContext> updateNewRecordBuilder = new UpdateRecordBuilder<>();
                     //   updateNewRecordBuilder.module(module)
                     //           .fields(woList)
                     //           .andCondition(CriteriaAPI.getIdCondition(preWoId, module));
                     //   wc.setMarkInactive(true);
                     //   updateNewRecordBuilder.update(wc);

                    }
                }
            }  */

                    if(wo.getTrigger() != null && wo.getTrigger().getId() > 0) {
                        PMTriggerContext trigger = PreventiveMaintenanceAPI.getPMTriggersByTriggerIds(Collections.singletonList(wo.getTrigger().getId())).get(0);
                        wo.setTrigger(trigger);
                    }

                    wo.setJobStatus(WorkOrderContext.JobsStatus.COMPLETED);

                    UpdateRecordBuilder<WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<>();
                    updateRecordBuilder.module(module)
                            .fields(Arrays.asList(fieldMap.get("status"), fieldMap.get("jobStatus")))
                            .andCondition(CriteriaAPI.getIdCondition(woId, module))
                            .skipModuleCriteria();
                    updateRecordBuilder.update(wo);


                    FacilioContext context = new FacilioContext();

                    List<EventType> activities = new ArrayList<>();
                    activities.add(EventType.CREATE);

                    //TODO remove single ACTIVITY_TYPE once handled in TicketActivity
                    context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

                    FacilioStatus moduleState = wo.getModuleState();
                    String status = wo.getStatus().getStatus();
                    if (isNMDP()) {
                        LOGGER.info("status: " + status);
                        LOGGER.info("moduleState: " + moduleState);
                        LOGGER.info("activities PRE: " + activities);
                    }
                    if (status != null && status.equals("Assigned")) {
                        activities.add(EventType.ASSIGN_TICKET);
                    }
                    if (isNMDP()) {
                        LOGGER.info("activities POST: " + activities);
                    }

                    context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, activities);

                    List<UpdateChangeSet> changeSets = FieldUtil.constructChangeSet(wo.getId(), FieldUtil.getAsProperties(wo), fieldMap);
                    if (changeSets != null && !changeSets.isEmpty()) {
                        Map<Long, List<UpdateChangeSet>> changeSetMap = new HashMap<>();
                        changeSetMap.put(woId, changeSets);
                        context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, changeSetMap));
                    }
                    if (!changeSets.isEmpty()) {
                        context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSets);
                        Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap(context);
                        Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap.get(FacilioConstants.ContextNames.WORK_ORDER);

                        List<UpdateChangeSet> changeSetList = currentChangeSet.get(wo.getId());
                        JSONObject addWO = new JSONObject();
                        List<Object> wolist = new ArrayList<Object>();

                        JSONObject newinfo = new JSONObject();
                        newinfo.put("pmid", wo.getPm().getId());
                        wolist.add(newinfo);


                        for (UpdateChangeSet changeset : changeSetList) {
                            long fieldid = changeset.getFieldId();
                            Object oldValue = changeset.getOldValue();
                            Object newValue = changeset.getNewValue();
                            FacilioField field = modBean.getField(fieldid, "workorder");

                            JSONObject info = new JSONObject();
                            info.put("field", field.getName());
                            info.put("displayName", field.getDisplayName());
                            info.put("oldValue", oldValue);
                            info.put("newValue", newValue);
                            wolist.add(info);
                        }

                        addWO.put("addPMWO", wolist);

                        CommonCommandUtil.addActivityToContext(wo.getId(), -1, WorkOrderActivityType.ADD_PM_WO, addWO, context);


                    }
                    context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(wo)));
                    context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(wo.getId()));


                    FacilioChain c = TransactionChainFactory.getWorkOrderWorkflowsChain(true);
                    c.addCommand(new AddActivitiesCommand(FacilioConstants.ContextNames.WORKORDER_ACTIVITY));
                    c.execute(context);

                    Map<Long, WorkOrderContext> pmToWo = new HashMap<>();
                    pmToWo.put(pm.getId(), wo);

                    PreventiveMaintenanceAPI.schedulePostReminder(Arrays.asList(pm), wo.getResource().getId(), pmToWo, wo.getScheduledStart());

                    LOGGER.log(Level.ERROR, "WorkOrder Status Change success: WO_ID = " + wo.getId());
                }
            }
        } catch (Exception e) {
            CommonCommandUtil.emailException("OpenScheduledWO", ""+jc.getJobId(), e);
            LOGGER.error("WorkOrder Status Change failed: ", e);
            throw e;
        }
    }
}
