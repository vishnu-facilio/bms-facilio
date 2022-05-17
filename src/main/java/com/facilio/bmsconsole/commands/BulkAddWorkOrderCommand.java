package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BulkAddWorkOrderCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(BulkAddWorkOrderCommand.class.getName());

    public boolean executeCommand(Context context) throws Exception {
        PreventiveMaintenanceAPI.logIf(92L, "Entering BulkAddWorkOrderCommand");
        BulkWorkOrderContext bulkWorkOrderContext = (BulkWorkOrderContext) context.get(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT);

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

        List<WorkOrderContext> workOrders = bulkWorkOrderContext.getWorkOrderContexts();
        if (workOrders == null || workOrders.isEmpty()) {
            LOGGER.log(Level.SEVERE, "WorkOrder list Object cannot be null");
            return false;
        }

        TicketAPI.associateTenant(workOrders);
        TicketAPI.updateTicketAssignedBy(workOrders);
        TicketPriorityContext lowPriority = TicketAPI.getPriority(AccountUtil.getCurrentOrg().getId(), "Low");

        InsertRecordBuilder<WorkOrderContext> builder = new InsertRecordBuilder<WorkOrderContext>()
                .moduleName(moduleName)
                .fields(fields)
                .withChangeSet()
                .withLocalId();

        for (int i = 0; i < workOrders.size(); i++) {
            WorkOrderContext workOrder = workOrders.get(i);
            try {
                TicketAPI.validateSiteSpecificData(workOrder);
            } catch (Exception e) {
                if (workOrder.getPm() != null) {
                    LOGGER.log(Level.SEVERE, "affected PM " + workOrder.getPm().getId());
                }
                throw e;
            }

            Boolean isFromImport = (Boolean) context.get(ImportAPI.ImportProcessConstants.IS_FROM_IMPORT);
            workOrder.setCreatedBy(AccountUtil.getCurrentUser());
            workOrder.setApprovalState(ApprovalState.YET_TO_BE_REQUESTED);
            if (workOrder.getPriority() == null || workOrder.getPriority().getId() == -1) {
                workOrder.setPriority(lowPriority);
            }
            if (isFromImport == null || (isFromImport != null && !isFromImport)) {
                if (workOrder.getScheduledStart() > 0) {
                    if (workOrder.getWoCreationOffset() > -1) {
                        workOrder.setCreatedTime(workOrder.getScheduledStart() - (workOrder.getWoCreationOffset() * 1000L));
                    } else {
                        workOrder.setCreatedTime(workOrder.getScheduledStart());
                        workOrder.setScheduledStart(workOrder.getCreatedTime());
                    }
                } else {
                    workOrder.setCreatedTime(workOrder.getCurrentTime());
                    workOrder.setScheduledStart(workOrder.getCreatedTime());
                }
                workOrder.setModifiedTime(workOrder.getCreatedTime());

                if (workOrder.getDuration() != -1 && workOrder.getDueDate() < 0) {
                    workOrder.setDueDate(workOrder.getScheduledStart() + (workOrder.getDuration() * 1000));
                }

                workOrder.setEstimatedEnd(workOrder.getDueDate());
                if (workOrder.getSiteId() > 0) {
                    workOrder.setClient(RecordAPI.getClientForSite(workOrder.getSiteId()));
                } else {
                    workOrder.setClient(null);
                }
                CommonCommandUtil.handleWOEnums(fields, workOrder.getData());
            }
            builder.addRecord(workOrder);
        }

        builder.save();

        PreventiveMaintenanceAPI.logIf(92L, "Done BulkAddWorkOrderCommand");

        Map<Long, List<UpdateChangeSet>> changes = builder.getChangeSet();

        handleWorkflowContext(context, workOrders, changes);

//        Map<Long, List<UpdateChangeSet>> changeSets = new HashMap<>();
//        if (builder.getChangeSet() != null) {
//            changeSets.putAll(builder.getChangeSet());
//        }
//
//        if (changeSets.isEmpty()) {
//            return false;
//        }
//
////        handleActivitiesContext(context, workOrders, changeSets);


        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, workOrders.stream().map(i -> i.getId()).collect(Collectors.toList()));
//        context.put(FacilioConstants.ContextNames.RECORD_LIST, workOrders);
        return false;
    }

    private void handleActivitiesContext(Context context, List<WorkOrderContext> workOrders, Map<Long, List<UpdateChangeSet>> changeSets) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSets);
        Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
        Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap.get(moduleName);
        Boolean pm_exec= (Boolean) context.get(FacilioConstants.ContextNames.IS_PM_EXECUTION);

        for (WorkOrderContext workOrder: workOrders) {
            List<UpdateChangeSet> changeSetList = currentChangeSet.get(workOrder.getId());
            JSONObject addWO = new JSONObject();
            List<Object> wolist = new ArrayList<Object>();
            if(pm_exec == null) {
                for (UpdateChangeSet changeset : changeSetList) {
                    long fieldid = changeset.getFieldId();
                    Object oldValue = changeset.getOldValue();
                    Object newValue = changeset.getNewValue();
                    FacilioField field = modBean.getField(fieldid, moduleName);

                    JSONObject info = new JSONObject();
                    info.put("field", field.getName());
                    info.put("displayName", field.getDisplayName());
                    info.put("oldValue", oldValue);
                    info.put("newValue", newValue);
                    wolist.add(info);
                }

                addWO.put("addWO", wolist);
                CommonCommandUtil.addActivityToContext(workOrder.getId(), -1, WorkOrderActivityType.ADD, addWO, (FacilioContext) context);
            } else if(pm_exec)  {
                JSONObject newinfo = new JSONObject();
                newinfo.put("pmid", workOrder.getPm().getId());
                wolist.add(newinfo);
                for (UpdateChangeSet changeset : changeSetList) {
                    long fieldid = changeset.getFieldId();
                    Object oldValue = changeset.getOldValue();
                    Object newValue = changeset.getNewValue();
                    FacilioField field = modBean.getField(fieldid, moduleName);

                    JSONObject info = new JSONObject();
                    info.put("field", field.getName());
                    info.put("displayName", field.getDisplayName());
                    info.put("oldValue", oldValue);
                    info.put("newValue", newValue);
                    wolist.add(info);
                }

                addWO.put("addPMWO", wolist);

                CommonCommandUtil.addActivityToContext(workOrder.getId(), -1, WorkOrderActivityType.ADD_PM_WO, addWO, (FacilioContext) context);
            }
        }
    }

    private void handleWorkflowContext(Context context, List<WorkOrderContext> workOrders, Map<Long, List<UpdateChangeSet>> changes) {
        if(context.get(FacilioConstants.ContextNames.EVENT_TYPE) == null) {
            List<EventType> activities = new ArrayList<>();
            activities.add(EventType.CREATE);

            //TODO remove single ACTIVITY_TYPE once handled in TicketActivity
            context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

			/*
			 * String status = workOrders.get(0).getStatus().getStatus(); if (status != null
			 * && status.equals("Assigned")) { activities.add(EventType.ASSIGN_TICKET); }
			 */
            context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, activities);
        }

        context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, changes));
        context.put(FacilioConstants.ContextNames.RECORD_LIST, workOrders);
    }
}
