package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.ItemActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.*;

public class GenericUpdateModuleDataCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

		if(record != null && recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			if (fields == null) {
				fields = modBean.getAllFields(moduleName);
			}
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
																					.module(module)
																					.fields(fields)
																					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			
			Boolean withChangeSet = (Boolean) context.get(FacilioConstants.ContextNames.WITH_CHANGE_SET);
			if (withChangeSet != null && withChangeSet) {
				updateBuilder.withChangeSet(ModuleBaseWithCustomFields.class);
			}
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(record));
			if (withChangeSet != null && withChangeSet) {
				context.put(FacilioConstants.ContextNames.CHANGE_SET, updateBuilder.getChangeSet());
				Map<Long, List<UpdateChangeSet>> changeSets = new HashMap<>();
				changeSets.putAll(updateBuilder.getChangeSet());
				if (!changeSets.isEmpty()) {

					Iterator it = recordIds.iterator();
					List<UpdateChangeSet> changeSetList = null;
					while (it.hasNext()) {
						Object singlerecord = it.next();
						 changeSetList = changeSets == null ? null : changeSets.get(singlerecord);
					}
	                JSONObject itemupdate = new JSONObject();
	                List<Object> itemlist = new ArrayList<Object>();
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
						itemlist.add(info);

					}	
					itemupdate.put("itemtypesupdate", itemlist);

					CommonCommandUtil.addActivityToContext(recordIds.get(0), -1, ItemActivityType.ITEMTYPES_UPDATE, itemupdate, (FacilioContext) context);

				}
			}
		}
		
		return false;
	}

}
