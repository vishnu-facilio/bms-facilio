package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DigestConfigContext;
import com.facilio.bmsconsole.context.PreferenceMetaContext;
import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.util.DigestConfigAPI;
import com.facilio.bmsconsole.util.PreferenceAPI;
import com.facilio.bmsconsole.util.ScheduledActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddEnabledPreferenceMetaCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
			FacilioModule prefModule = ModuleFactory.getPreferenceMetaModule();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule((String)context.get(FacilioConstants.ContextNames.MODULE_NAME));
			Long ruleId = (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
			
			Long enabledPrefId = (Long)context.get(FacilioConstants.ContextNames.PREFERENCE_ID);
			PreferenceMetaContext preferenceMeta = null;
			if(enabledPrefId != null && enabledPrefId > 0) {
				preferenceMeta = PreferenceAPI.getEnabledPreference(enabledPrefId);
				if(preferenceMeta != null) {
					Long oldruleId = preferenceMeta.getRuleId();
					
					Map<String, Object> map = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PREFERENCE_VALUE_LIST);
					JSONObject jObj = FieldUtil.getAsJSON(map);
					preferenceMeta.setFormData(jObj.toString());
					preferenceMeta.setIsActive(true);
					if(ruleId != null) {
						preferenceMeta.setRuleId(ruleId);
					}
					PreferenceAPI.updatePref(FieldUtil.getAsProperties(preferenceMeta), Collections.singletonList(preferenceMeta.getId()));
					if(oldruleId > 0) {
						WorkflowRuleAPI.deleteWorkflowRule(oldruleId); 
					}
				}
			}
			else {
				preferenceMeta = new PreferenceMetaContext();
				preferenceMeta.setPreferenceName((String)context.get(FacilioConstants.ContextNames.PREFERENCE_NAME));
				preferenceMeta.setModuleId(module.getModuleId());
				if(context.get(FacilioConstants.ContextNames.RECORD_ID) != null) {
					preferenceMeta.setRecordId((Long)context.get(FacilioConstants.ContextNames.RECORD_ID));
				}
				Map<String, Object> map = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PREFERENCE_VALUE_LIST);
				JSONObject jObj = FieldUtil.getAsJSON(map);
				preferenceMeta.setFormData(jObj.toString());
				preferenceMeta.setIsActive(true);
				if(ruleId != null) {
					preferenceMeta.setRuleId(ruleId);
				}
				Map<String, Object> props = FieldUtil.getAsProperties(preferenceMeta);
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
							.table(prefModule.getTableName())
							.fields(FieldFactory.getPreferencesMetaFields())
							.addRecord(props);
				insertBuilder.save();
					
				preferenceMeta.setId((long) props.get("id"));
			}
			context.put(FacilioConstants.ContextNames.PREFERENCE_META,preferenceMeta);
			
			
		return false;
	}

}
