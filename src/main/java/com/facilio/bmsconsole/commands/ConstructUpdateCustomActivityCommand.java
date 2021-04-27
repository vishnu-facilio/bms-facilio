package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.activity.CustomModuleActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;

public class ConstructUpdateCustomActivityCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(ContextNames.CHANGE_SET);
		if (changeSet == null) {
			return false;
		}
		if(recordIds != null && !recordIds.isEmpty()) {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<UpdateChangeSet> changeSets = changeSet.get(recordIds.get(0));
		
		List<Long> fieldIds = new ArrayList<>();
		
		JSONObject info = new JSONObject();
		List<Object> changeList = new ArrayList<Object>();
		for (UpdateChangeSet changeset : changeSets) {
			long fieldid = changeset.getFieldId();
			if (!fieldIds.contains(fieldid) || fieldIds.isEmpty()) {
			    fieldIds.add(fieldid);
				Object oldValue = changeset.getOldValue();
				Object newValue = changeset.getNewValue();
				FacilioField field = modBean.getField(fieldid, moduleName);
				
				JSONObject changeObj = new JSONObject();
				changeObj.put("field", field.getName());
				changeObj.put("displayName", field.getDisplayName());
				changeObj.put("oldValue", oldValue);
				changeObj.put("newValue", newValue);
				changeList.add(changeObj);	
			}
		}	
		info.put("changeSet", changeList);

		CommonCommandUtil.addActivityToContext(recordIds.get(0), -1, CommonActivityType.UPDATE_RECORD, info, (FacilioContext) context);
		}
		
		// Temp... move this to a common place
		context.put(FacilioConstants.ContextNames.STATE_TRANSITION_ONLY_CONDITIONED_CHECK, true);

		return false;
	}

	

}
