package com.facilio.events.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BMSAlarmContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class UpdateAlarmAssetMappingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String source = (String) context.get(EventConstants.EventContextNames.SOURCE);
		long resourceId = (long) context.get(EventConstants.EventContextNames.RESOURCE_ID);

		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
			ResourceContext resource = ResourceAPI.getResource(resourceId);
			if (resource != null) {
				BMSAlarmContext alarm = new BMSAlarmContext();
				alarm.setResource(resource);

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BMS_ALARM);
				List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.BMS_ALARM);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);

				UpdateRecordBuilder<BMSAlarmContext> builder = new UpdateRecordBuilder<BMSAlarmContext>()
						.fields(Collections.singletonList(fieldMap.get("resource")))
						.module(module)
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("source"), source, StringOperators.IS));
				builder.update(alarm);
			}
		}
		else {
			JSONObject json = new JSONObject();
			json.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
			json.put("source", source);
			json.put("resourceId", resourceId);

			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			String server = FacilioProperties.getConfig("clientapp.url");
			String url = server + "/internal/updateAlarmResource";
			AwsUtil.doHttpPost(url, headers, null, json.toJSONString());
		}
		
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}

}
