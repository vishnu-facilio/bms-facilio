package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.activity.WorkOrderActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class AddActivityForWoPostCreateCommand extends FacilioCommand {
    
	@Override
    public boolean executeCommand(Context context) throws Exception {
		
		V3WorkOrderContext workorder = (V3WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		 List<FacilioField> fields = Constants.getModBean().getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
         Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		List<EventType> activities = new ArrayList<>();
        activities.add(EventType.CREATE);

        //TODO remove single ACTIVITY_TYPE once handled in TicketActivity
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

        String status = workorder.getStatus().getStatus();

        if (status != null && status.equals("Assigned")) {
            activities.add(EventType.ASSIGN_TICKET);
        }
        context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, activities);

        List<UpdateChangeSet> changeSets = FieldUtil.constructChangeSet(workorder.getId(), FieldUtil.getAsProperties(workorder), fieldMap);
        if (changeSets != null && !changeSets.isEmpty()) {
            Map<Long, List<UpdateChangeSet>> changeSetMap = new HashMap<>();
            changeSetMap.put(workorder.getId(), changeSets);
            context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, Collections.singletonMap(FacilioConstants.ContextNames.WORK_ORDER, changeSetMap));
        }
        if (!changeSets.isEmpty()) {
            context.put(FacilioConstants.ContextNames.CHANGE_SET, changeSets);
            Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = CommonCommandUtil.getChangeSetMap((FacilioContext)context);
            Map<Long, List<UpdateChangeSet>> currentChangeSet = changeSetMap.get(FacilioConstants.ContextNames.WORK_ORDER);

            List<UpdateChangeSet> changeSetList = currentChangeSet.get(workorder.getId());
            JSONObject addWO = new JSONObject();
            List<Object> wolist = new ArrayList<Object>();

            JSONObject newinfo = new JSONObject();
            newinfo.put("pmid", workorder.getPmV2());
            wolist.add(newinfo);


            for (UpdateChangeSet changeset : changeSetList) {
                long fieldid = changeset.getFieldId();
                Object oldValue = changeset.getOldValue();
                Object newValue = changeset.getNewValue();
                if (newValue ==null || oldValue == null) {
                    continue;
                }
                FacilioField field = Constants.getModBean().getField(fieldid, "workorder");

                JSONObject info = new JSONObject();
                info.put("field", field.getName());
                info.put("displayName", field.getDisplayName());
                info.put("oldValue", oldValue);
                info.put("newValue", newValue);
                wolist.add(info);
            }

            addWO.put("addPMWO", wolist);

            CommonCommandUtil.addActivityToContext(workorder.getId(), -1, WorkOrderActivityType.ADD_PM_WO, addWO, (FacilioContext) context);
        }
		return false;
	}

}
