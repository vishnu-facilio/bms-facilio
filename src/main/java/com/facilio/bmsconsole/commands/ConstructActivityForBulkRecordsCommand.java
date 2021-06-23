package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;

public class ConstructActivityForBulkRecordsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<Long> recordsList = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(ContextNames.CHANGE_SET);
		if (changeSet == null) {
			return false;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (moduleName == null) {
			moduleName = ContextNames.ASSET;
		}
		FacilioModule module = modBean.getModule(moduleName);
		if (recordsList != null && recordsList.size() > 0) {
		Map<Long, JSONObject> infoMap = new HashMap<>();
		for (int i = 0; i < recordsList.size(); i++) {
			List<UpdateChangeSet> changeSets = changeSet.get(recordsList.get(i));
			JSONObject info = new JSONObject();
			List<Object> changeList = new ArrayList<Object>();
			if (changeSets != null && changeSets.size() > 0) {
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
			}
			info.put("changeSet", changeList);
			infoMap.put(recordsList.get(i), info);
			
		}
		
		if (module != null && 
				(module.getName() == "asset" || (module.getExtendModule() != null && module.getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET)))) {
			
			CommonCommandUtil.addBulkActivityToContext(recordsList, -1, AssetActivityType.ADD, infoMap, (FacilioContext) context);
		}
		}
		
		return false;
	}

}
