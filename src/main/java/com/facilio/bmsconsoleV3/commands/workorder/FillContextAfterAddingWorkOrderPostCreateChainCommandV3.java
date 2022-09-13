package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * FillContextAfterAddingWorkOrderPostCreateChainCommandV3
 *  - adds ASSIGN_TICKET EVENT_TYPE if workorder has assignedTo object
 */
public class FillContextAfterAddingWorkOrderPostCreateChainCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = FacilioConstants.ContextNames.WORK_ORDER;
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (CollectionUtils.isNotEmpty(wos)) {
            //V3WorkOrderContext workOrder = wos.get(0);
            for (V3WorkOrderContext workOrder : wos) {
                List<EventType> activities = new ArrayList<>();

                if (context.get(FacilioConstants.ContextNames.EVENT_TYPE) != null) {
                    EventType eventType_1 = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
                    activities.add(eventType_1);
                    // maybe we need to do something with EVENT_TYPE_LIST
                }
                if (workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() > 0) {
                    activities.add(EventType.ASSIGN_TICKET);
                }
                if (!activities.isEmpty()){
                    context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, activities);
                }

                /*
                    Commented out this code because, ChangeSet is returned only if a context entry is put using either
                    CHANGE_SET_MAP or CHANGE_SET key.
                 */
                //context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(workOrder)));
                //context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(workOrder.getId()));

//                Map<Long, List<UpdateChangeSet>> moduleChangeSet = Constants.getModuleChangeSets(context);
//                if (MapUtils.isNotEmpty(moduleChangeSet)) {
//                    addActivity(context, moduleChangeSet, workOrder, modBean, moduleName);
//                }
            }

            // Commented out this as entry in RECORD isn't used anywhere later.
            //context.put(FacilioConstants.ContextNames.RECORD, workOrder);
        }

        return false;
    }

    /*
        Not using this method due to reason mentioned above
     */
    private void addActivity(Context context, Map<Long, List<UpdateChangeSet>> changeSets,
                             V3WorkOrderContext workOrder, ModuleBean modBean, String moduleName) throws Exception {

        Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext) context);
        Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap == null ? null : changeSetMap.get(moduleName);
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST); //All IDs (bulk and individual) of WOs to be updated
        Iterator it = recordIds.iterator();
        List<UpdateChangeSet> changeSetList = null;
        while (it.hasNext()) {
            Object record = it.next();
            changeSetList = currentChangeSet == null ? null : currentChangeSet.get(record);
        }

        JSONObject addWO = new JSONObject();
        List<Object> wolist = new ArrayList<Object>();
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

        Boolean pm_exec = (Boolean) context.get(FacilioConstants.ContextNames.IS_PM_EXECUTION);
        if (pm_exec == null) {
            addWO.put("addWO", wolist);
            CommonCommandUtil.addActivityToContext(workOrder.getId(), -1, WorkOrderActivityType.ADD, addWO, (FacilioContext) context);
        } else if (pm_exec) {
            JSONObject newinfo = new JSONObject();
            newinfo.put("pmid", workOrder.getPm().getId());
            wolist.add(newinfo);
            addWO.put("addPMWO", wolist);
            CommonCommandUtil.addActivityToContext(workOrder.getId(), -1, WorkOrderActivityType.ADD_PM_WO, addWO, (FacilioContext) context);
        }
    }

}
