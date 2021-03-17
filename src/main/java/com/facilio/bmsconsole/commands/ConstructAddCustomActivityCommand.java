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

public class ConstructAddCustomActivityCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(ContextNames.CHANGE_SET);
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
			changeObj.put("oldValue", oldValue);
			changeObj.put("newValue", newValue);
			changeList.add(changeObj);
		}	
		info.put("changeSet", changeList);

		CommonCommandUtil.addActivityToContext(recordId, -1, CommonActivityType.ADD_RECORD, info, (FacilioContext) context);
		return false;
	}

	

}
