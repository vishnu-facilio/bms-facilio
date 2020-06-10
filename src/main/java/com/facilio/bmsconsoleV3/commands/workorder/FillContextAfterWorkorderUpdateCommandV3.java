package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3InventoryAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class FillContextAfterWorkorderUpdateCommandV3 extends FacilioCommand {
    private static final List<EventType> TYPES = Arrays.asList(EventType.EDIT, EventType.ASSIGN_TICKET, EventType.CLOSE_WORK_ORDER,  EventType.SOLVE_WORK_ORDER, EventType.HOLD_WORK_ORDER, EventType.STATE_TRANSITION);

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        List<V3WorkOrderContext> oldWos = (List<V3WorkOrderContext>)context.get(FacilioConstants.TicketActivity.OLD_TICKETS);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> recordIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        FacilioModule module = modBean.getModule(moduleName);
        EventType activityType = (EventType)context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);

        if(CollectionUtils.isNotEmpty(wos)) {
            V3WorkOrderContext workOrder = wos.get(0);
            if (((List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST) != null && CollectionUtils.containsAny(TYPES, (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST)))
                    || TYPES.contains(activityType) || workOrder.getPriority() != null) {
                SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>()
                        .module(module)
                        .beanClass(V3WorkOrderContext.class)
                        .select(modBean.getAllFields(moduleName))
                        .andCondition(CriteriaAPI.getIdCondition(recordIds, module))
                        .orderBy("ID");

                List<V3WorkOrderContext> workOrders = builder.get();
                if (workOrder.getOfflineModifiedTime() != -1) {
                    workOrders.forEach(wo -> wo.setOfflineModifiedTime(workOrder.getOfflineModifiedTime()));
                }
                context.put(FacilioConstants.ContextNames.RECORD_LIST, workOrders);
            }

            addActivity(workOrder, oldWos, changeSet, recordIds, modBean, moduleName, context);
        }

        return false;
    }

    private void addActivity(V3WorkOrderContext workOrder, List<V3WorkOrderContext> oldWos, Map<Long, List<UpdateChangeSet>> changeSets, List<Long> recordIds, ModuleBean modBean, String moduleName, Context context) throws Exception {
        if (workOrder.getSiteId() != -1) {
            JSONObject info = new JSONObject();
            JSONObject woupdate = new JSONObject();
            List<Object> wolist = new ArrayList<Object>();
            info.put("field", "site");
            info.put("displayName", "Site");
            info.put("newValue", workOrder.getSiteId());
            wolist.add(info);
            woupdate.put("woupdate", wolist);
            CommonCommandUtil.addActivityToContext(recordIds.get(0), workOrder.getCurrentTime(), WorkOrderActivityType.UPDATE, woupdate, (FacilioContext) context);
        }
        if ( (workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() != -1) || (workOrder.getAssignmentGroup() != null && workOrder.getAssignmentGroup().getId() != -1) ) {
            for (V3WorkOrderContext oldWo: oldWos) {
                addAssignmentActivity(workOrder, oldWo.getId(), oldWo, context);
            }
        } else if (workOrder.getVendor() != null && workOrder.getVendor().getId() != -1) {
            for (V3WorkOrderContext oldWo : oldWos) {
                addVendorAssignmentActivity(workOrder, oldWo.getId(), oldWo, context);
            }
        }
        else if (!changeSets.isEmpty() && workOrder.getApprovalStateEnum() == null && workOrder.getStatus() == null) {

            Iterator it = recordIds.iterator();
            List<UpdateChangeSet> changeSetList = null;
            while (it.hasNext()) {
                Object record = it.next();
                changeSetList = changeSets == null ? null : changeSets.get(record);
            }
            JSONObject woupdate = new JSONObject();
            List<Object> wolist = new ArrayList<Object>();
            boolean updatingPrereqApproved = false;
            for (UpdateChangeSet changeset : changeSetList) {
                long fieldid = changeset.getFieldId();
                Object oldValue = changeset.getOldValue();
                Object newValue = changeset.getNewValue();
                FacilioField field = modBean.getField(fieldid, moduleName);

                JSONObject info = new JSONObject();
                info.put("field", field.getName());
                info.put("displayName", field.getDisplayName());

                if (field.getName().contains("resource")) {
                    ResourceContext resource = ResourceAPI.getResource((long) newValue);
                    info.put("newValue", resource.getName());
                }
                else if(field.getName().contains("vendor") && workOrder.getVendor().getId() > 0) {
                    V3VendorContext vendor = V3InventoryAPI.getVendor(workOrder.getVendor().getId());
                    info.put("newValue", vendor.getName());
                }
                else {
                    if (field.getName().contains("preRequisiteApproved")) {
                        updatingPrereqApproved = true;
                    }
                    info.put("newValue", newValue);
                }
                info.put("oldValue", oldValue);
                wolist.add(info);

            }
            woupdate.put("woupdate", wolist);
            if (updatingPrereqApproved) {
                CommonCommandUtil.addActivityToContext(recordIds.get(0),  workOrder.getCurrentTime(), WorkOrderActivityType.PREREQUISITE_APPROVE, woupdate,(FacilioContext) context);
            } else {
                CommonCommandUtil.addActivityToContext(recordIds.get(0),  workOrder.getCurrentTime(), WorkOrderActivityType.UPDATE, woupdate,(FacilioContext) context);
            }
        }
    }

    private void addAssignmentActivity(V3WorkOrderContext workOrder, long parentId, V3WorkOrderContext oldWo, Context context) {
        JSONObject info = new JSONObject();
        info.put("assignedBy", workOrder.getAssignedBy().getOuid());
        if (workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() != -1) {
            info.put("assignedTo", workOrder.getAssignedTo().getOuid());
            if (oldWo != null && oldWo.getAssignedTo() != null && oldWo.getAssignedTo().getId() != -1) {
                info.put("prevAssignedTo", oldWo.getAssignedTo().getOuid());
            }
        }
        else {
            info.put("assignmentGroup", workOrder.getAssignmentGroup().getId());
            if (oldWo != null && oldWo.getAssignmentGroup() != null && oldWo.getAssignmentGroup().getId() != -1) {
                info.put("prevAssignmentGroup", oldWo.getAssignmentGroup().getId());
            }
        }
        JSONObject newinfo = new JSONObject();
        newinfo.put("assigned", info);
        CommonCommandUtil.addActivityToContext(parentId,  workOrder.getCurrentTime(), WorkOrderActivityType.ASSIGN, newinfo, (FacilioContext) context);
    }

    private void addVendorAssignmentActivity(V3WorkOrderContext workOrder, long parentId, V3WorkOrderContext oldWo, Context context) throws Exception{
        JSONObject info = new JSONObject();
        if (workOrder.getVendor() != null && workOrder.getVendor().getId() > 0) {
            V3VendorContext vendor = V3InventoryAPI.getVendor(workOrder.getVendor().getId());
            info.put("vendor", vendor.getName());
            JSONObject newinfo = new JSONObject();
            newinfo.put("vendor", info);
            CommonCommandUtil.addActivityToContext(parentId,  workOrder.getCurrentTime(), WorkOrderActivityType.VENDOR_ASSIGNED, newinfo, (FacilioContext) context);
        }
    }
}
