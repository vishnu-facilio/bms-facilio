package com.facilio.v3.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.activity.ActivityType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.v3.context.Constants;

public abstract class BaseActivityForModuleCommand extends FacilioCommand {

	protected void addActivitiesToContext(ModuleBean modBean, String moduleName, ActivityType activityType,
			Context context, String activityContext) throws Exception {
		if (StringUtils.isEmpty(activityContext)) { // No activity context found
			return;
		}

		if (activityType == null) {
			return;
		}

		List<ModuleBaseWithCustomFields> recordList = Constants.getRecordList((FacilioContext) context);
		if (CollectionUtils.isNotEmpty(recordList)) {
			for (ModuleBaseWithCustomFields record : recordList) {
				List<UpdateChangeSet> changeSets = Constants.getRecordChangeSets(context, record.getId());
				JSONObject info = new JSONObject();
				List<Object> changeList = new ArrayList<>();
				for (UpdateChangeSet changeSet : changeSets) {
					long fieldId = changeSet.getFieldId();
					Object oldValue = changeSet.getOldValue();
					Object newValue = changeSet.getNewValue();
					FacilioField field = modBean.getField(fieldId, moduleName);

					if (oldValue == null && newValue == null) {
						continue;
					}

					JSONObject changeObj = new JSONObject();
					changeObj.put("field", field.getName());
					changeObj.put("displayName", field.getDisplayName());
					if (field instanceof LookupField && oldValue != null) {
						long recId = (long) oldValue;
						oldValue = RecordAPI.getPrimaryValue(((LookupField) field).getLookupModule().getName(), recId);
						info.put("oldRecordId", recId);
					}
					changeObj.put("oldValue", oldValue);

					if (newValue != null) {
						if (field instanceof LookupField) {
							long recId = (long) newValue;
							newValue = RecordAPI.getPrimaryValue(((LookupField) field).getLookupModule().getName(), recId);
							info.put("recordId", recId);
						} else if (field instanceof MultiLookupField) {
							newValue = CommonCommandUtil.getMultiLookupValues(newValue, field);
						}
					}
					changeObj.put("newValue", newValue);

					changeList.add(changeObj);
				}
				info.put("changeSet", changeList);

				CommonCommandUtil.addActivityToContext(record.getId(), -1, activityType, info, (FacilioContext) context);
			}
		}
	}
}
