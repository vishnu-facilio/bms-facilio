package com.facilio.agent.module;

import java.util.ArrayList;
import java.util.List;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

public class AgentFieldFactory {
	
	 public static List<FacilioField> getMessageTopicFields() {
			FacilioModule module = AgentModuleFactory.getMessageToipcModule();
			List<FacilioField> fields = new ArrayList<>();
			fields.add(FieldFactory.getIdField(module));
			fields.add(FieldFactory.getField("isDisable", "IS_DISABLE", module, FieldType.BOOLEAN));
			fields.add(FieldFactory.getField("topic", "TOPIC", module, FieldType.STRING));
			fields.add(FieldFactory.getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("lastModifiedTime", "LAST_MODIFIED_TIME", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("lastEnabledTime", "LAST_ENABLED_TIME", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("lastDisabledTime", "LAST_DISABLED_TIME", module, FieldType.NUMBER));
			return fields;
		}

}
