package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class FillContextAfterWorkorderAddCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if(CollectionUtils.isNotEmpty(wos)) {

            V3WorkOrderContext workOrder = wos.get(0);
            if (context.get(FacilioConstants.ContextNames.EVENT_TYPE) == null) {
                List<EventType> activities = new ArrayList<>();
                activities.add(EventType.CREATE);

                //TODO remove single ACTIVITY_TYPE once handled in TicketActivity
                context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

                if (workOrder.getAssignedTo() != null && workOrder.getAssignedTo().getId() > 0) {
                    activities.add(EventType.ASSIGN_TICKET);
                }
                context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, activities);
            }
            context.put(FacilioConstants.ContextNames.RECORD_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, Collections.singletonList(workOrder)));
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(workOrder.getId()));
            Map<Long, List<UpdateChangeSet>> moduleChangeSet = Constants.getModuleChangeSets(context);
            if (MapUtils.isNotEmpty(moduleChangeSet)) {
                addActivity(context, moduleChangeSet, workOrder, modBean, moduleName);
            }
            context.put(FacilioConstants.ContextNames.RECORD, workOrder);
        }

        return false;
    }

    private void addActivity(Context context, Map<Long, List<UpdateChangeSet>> changeSets, V3WorkOrderContext workOrder, ModuleBean modBean, String moduleName) throws Exception {

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

            if(oldValue == null && newValue == null) {
                continue;
            }

            JSONObject info = new JSONObject();
            info.put("field", field.getName());
            info.put("displayName", field.getDisplayName());
            info.put("oldValue", oldValue);
            info.put("newValue", newValue);
            wolist.add(info);
        }

        Boolean pm_exec= (Boolean) context.get(FacilioConstants.ContextNames.IS_PM_EXECUTION);
        if(pm_exec == null) {
            addWO.put("addWO", wolist);
            CommonCommandUtil.addActivityToContext(workOrder.getId(), -1, WorkOrderActivityType.ADD, addWO, (FacilioContext) context);
        }
        else if(pm_exec)  {
            JSONObject newinfo = new JSONObject();
            newinfo.put("pmid", workOrder.getPm().getId());
            wolist.add(newinfo);
            addWO.put("addPMWO", wolist);
            CommonCommandUtil.addActivityToContext(workOrder.getId(), -1, WorkOrderActivityType.ADD_PM_WO, addWO, (FacilioContext) context);
        }
    }

}
