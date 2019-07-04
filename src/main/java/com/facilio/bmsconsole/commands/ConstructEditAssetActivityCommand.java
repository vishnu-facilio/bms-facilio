package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;

public class ConstructEditAssetActivityCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(ContextNames.CHANGE_SET);
		if (changeSet == null) {
			return false;
		}
		System.out.println("123"+ changeSet);
		if(recordIds != null && !recordIds.isEmpty()) {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<UpdateChangeSet> changeSets = changeSet.get(recordIds.get(0));
		
		JSONObject info = new JSONObject();
		List<Object> changeList = new ArrayList<Object>();
		System.out.println("123changeSets"+ changeSets);
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

		CommonCommandUtil.addActivityToContext(recordIds.get(0), -1, AssetActivityType.UPDATE, info, (FacilioContext) context);
		}

		return false;
	}

}
