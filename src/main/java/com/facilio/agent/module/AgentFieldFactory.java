package com.facilio.agent.module;

import java.util.ArrayList;
import java.util.List;

import com.facilio.agentv2.AgentConstants;
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
			fields.add(FieldFactory.getField("orgId", "ORGID", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("topic", "TOPIC", module, FieldType.STRING));
         fields.add(FieldFactory.getField("partitionId", "PARTITION_ID", module, FieldType.NUMBER));
         fields.add(FieldFactory.getField("maxConsumers", "MAX_CONSUMERS", module, FieldType.NUMBER));
         fields.add(FieldFactory.getField("maxConsumersPerInstance", "MAX_CONSUMERS_PER_INSTANCE", module, FieldType.NUMBER));
         fields.add(FieldFactory.getField("createdTime", "CREATED_TIME", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("lastModifiedTime", "LAST_MODIFIED_TIME", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("lastEnabledTime", "LAST_ENABLED_TIME", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("lastDisabledTime", "LAST_DISABLED_TIME", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("messageSource", "MESSAGE_SOURCE", module, FieldType.STRING));
			return fields;
		}
	 
	 public static List<FacilioField> getAgentDisableFields() {
			FacilioModule module = AgentModuleFactory.getAgentDisableModule();
			List<FacilioField> fields = new ArrayList<>();
			fields.add(FieldFactory.getIdField(module));
			fields.add(FieldFactory.getField(AgentConstants.ORGID, "ORGID", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField(AgentConstants.AGENT_ID, "AGENT_ID", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField(AgentConstants.IS_DISABLE, "IS_DISABLE", module, FieldType.BOOLEAN));
			fields.add(FieldFactory.getField(AgentConstants.RECORD_ID, "RECORD_ID", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("enabledTime", "ENABLED_TIME", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("disabledTime", "DISABLED_TIME", module, FieldType.NUMBER));
			return fields;
		}
	 
	 public static List<FacilioField> getLonWorksPointFields(){
	        FacilioModule module = AgentModuleFactory.getLonWorksPointModule();
	        List<FacilioField> fields = new ArrayList<>();
	        fields.addAll(FieldFactory.getPointFields());
	        fields.add(FieldFactory.getIdField(module));
	        fields.add(FieldFactory.getControllerIdField(module));
	        fields.add(FieldFactory.getField(AgentConstants.TARGET_NAME, "TARGET_NAME", module, FieldType.STRING));
	        fields.add(FieldFactory.getField(AgentConstants.TARGET_COMP, "TARGET_COMP", module, FieldType.STRING));
	        fields.add(FieldFactory.getField(AgentConstants.LINK_TYPE, "LINK_TYPE", module, FieldType.STRING));
	        fields.add(FieldFactory.getFieldDeviceId(module));
	        return fields;
	    }

	public static List<FacilioField> getRdmPointFields() {
		FacilioModule module = AgentModuleFactory.getRdmPointModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(FieldFactory.getPointFields());
        fields.add(FieldFactory.getIdField(module));
		fields.add(FieldFactory.getControllerIdField(module));
		fields.add(FieldFactory.getField(AgentConstants.PATH, "PATH", module, FieldType.STRING));
		fields.add(FieldFactory.getField(AgentConstants.DETAILS, "DETAILS", module, FieldType.STRING));
		fields.add(FieldFactory.getField(AgentConstants.RDM_POINT_CLASS, "CLASS", module, FieldType.STRING));
		fields.add(FieldFactory.getFieldDeviceId(module));
		return fields;
	}
}
