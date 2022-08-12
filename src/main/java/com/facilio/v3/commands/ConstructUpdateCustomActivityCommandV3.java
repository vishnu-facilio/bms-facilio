package com.facilio.v3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.workflow.rule.EventType;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.v3.context.Constants;

import lombok.var;

public class ConstructUpdateCustomActivityCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = recordMap.get(Constants.getModuleName(context));

        for (var record: moduleBaseWithCustomFields) {
            long recordId = record.getId();
            Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = (Map<String, Map<Long, List<UpdateChangeSet>>>) context.get(ContextNames.CHANGE_SET_MAP);
            Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) changeSetMap.get(Constants.getModuleName(context));
            if (changeSet == null) {
                return false;
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<UpdateChangeSet> changeSets = changeSet.get(recordId);
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            JSONObject info = new JSONObject();
            List<Object> changeList = new ArrayList<Object>();
            for (UpdateChangeSet changeset : changeSets) {
                long fieldid = changeset.getFieldId();
                Object oldValue = changeset.getOldValue();
                Object newValue = changeset.getNewValue();
                FacilioField field = modBean.getField(fieldid, moduleName);

                JSONObject changeObj = new JSONObject();
                changeObj.put("field", field.getName());
                changeObj.put("displayName", field.getDisplayName());
                if (field instanceof LookupField && oldValue != null) {
					long recId = (long) oldValue;
					oldValue = RecordAPI.getPrimaryValue(((LookupField)field).getLookupModule().getName(), recId);
					info.put("oldRecordId", recId);
				}
                changeObj.put("oldValue", oldValue);
                
                if (newValue != null) {
					if (field instanceof LookupField) {
						long recId = (long) newValue;
						newValue = RecordAPI.getPrimaryValue(((LookupField)field).getLookupModule().getName(), recId);
						info.put("recordId", recId);
					}
					else if (field instanceof MultiLookupField) {
						newValue = CommonCommandUtil.getMultiLookupValues(newValue, field);
					}
				}
                changeObj.put("newValue", newValue);
                
                changeList.add(changeObj);
            }
            info.put("changeSet", changeList);

            // Adding this conditional check as this is used in both Create & Update Chain
            EventType eventType = (EventType) context.get(ContextNames.EVENT_TYPE);
            if(eventType == EventType.CREATE){
                CommonCommandUtil.addActivityToContext(recordId, -1, CommonActivityType.ADD_RECORD, info, (FacilioContext) context);
            }else if (eventType == EventType.EDIT){
                CommonCommandUtil.addActivityToContext(recordId, -1, CommonActivityType.UPDATE_RECORD, info, (FacilioContext) context);
            }
        }
        
        // Temp... move this to a common place
     	context.put(FacilioConstants.ContextNames.STATE_TRANSITION_ONLY_CONDITIONED_CHECK, true);

        return false;
	}

	

}
