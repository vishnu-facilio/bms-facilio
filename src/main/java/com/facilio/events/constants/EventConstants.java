package com.facilio.events.constants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.GetExportValueField;
import com.facilio.bmsconsole.commands.UpdateEventCountCommand;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.chain.FacilioChain;
import com.facilio.events.commands.EventToAlarmCommand;
import com.facilio.events.commands.GetEventDetailCommand;
import com.facilio.events.commands.GetEventListCommand;
import com.facilio.events.commands.InsertEventCommand;
import com.facilio.events.commands.ProcessEventCommand;
import com.facilio.events.commands.UpdateAlarmAssetMappingCommand;
import com.facilio.events.commands.UpdateEventCommand;
import com.facilio.events.commands.UpdateEventResourcesMappingCommand;
import com.facilio.events.commands.UpdateSourceToResourceMappingCommand;

public class EventConstants {
	
	public static class EventContextNames {
	
		public static final String EVENT_ID = "eventId";
		public static final String EVENT = "event";
		public static final String EVENT_PAYLOAD = "eventPayload";
		public static final String EVENT_TIMESTAMP = "eventTimeStamp";
		public static final String EVENT_LAST_TIMESTAMP = "eventLastTimeStamp";
		public static final String EVENT_COUNT_MAP = "eventCountMap";
		public static final String EVENT_LIST = "events";
		public static final String READING_VALUES = "readingValue";
		public static final String FILEURL = "fileurl";
		public static final String EVENT_PROPERTY = "eventProperty";
		public static final String EVENT_RULE = "eventRule";
		public static final String EVENT_RULE_LIST = "eventRules";
		public static final String SOURCE = "source";
		public static final String RESOURCE_ID = "resourceId";
		public static final String ALARM_ID = "alarmId";
		public static final String FIELD_ID = "fieldId";
		public static final String TYPE = "type";
		public static final String PARENT_ID = "parentId";
		public static final String CONTROLLER_ID = "controllerId";
	}
	
	public static class EventChainFactory {
		public static Chain processEventChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new InsertEventCommand());
//			c.addCommand(new EvalEventBaseCriteriaCommand());
//			c.addCommand(new EventTransformCommand());
//			c.addCommand(new EventThresholdCommand());
//			c.addCommand(new EventCoRelationCommand());
			c.addCommand(new ExecuteEventRulesCommand());
			c.addCommand(new EventToAlarmCommand());
			c.addCommand(new UpdateEventCommand());
			c.setPostTransactionChain(getUpdateEventCountChain());
			return c;
		}
		
		public static Chain getUpdateEventCountChain() {
			FacilioChain chain = FacilioChain.getTransactionChain();
			chain.addCommand(new UpdateEventCountCommand());
			return chain;
		}

		public static Chain getAddEventChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new ProcessEventCommand());
			c.addCommand(processEventChain());
			return c;
		}
		
		public static Chain getEventDetailChain() {
			Chain c = FacilioChain.getNonTransactionChain();
			c.addCommand(new GetEventDetailCommand());
			return c;
		}
		
		public static Chain getActiveEventRuleChain() {
			Chain c = FacilioChain.getNonTransactionChain();
			c.addCommand(new GetActiveEventRulesCommand());
			return c;
		}
		
		public static Chain getEventRuleChain() {
			Chain c = FacilioChain.getNonTransactionChain();
			c.addCommand(new GetNewEventRuleCommand());
			return c;
		}
		
		public static Chain addEventRuleChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new AddNewEventRuleCommand());
			return c;
		}
		
		public static Chain updateEventRuleChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new UpdateNewEventRuleCommand());
			return c;
		}
		
		public static Chain deleteEventRuleChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new DeleteNewEventRuleCommand());
			return c;
		}
		
		public static Chain getEventListChain() {
			Chain c = FacilioChain.getNonTransactionChain();
			c.addCommand(new GetEventListCommand());
			return c;
		}
		
		public static Chain getExportFieldsValue() {
			Chain c = FacilioChain.getNonTransactionChain();
			c.addCommand(new GetEventListCommand());
			c.addCommand(new GetExportValueField());
			return c;
		}
		
		public static Chain updateNodeToResourceMappingChain() {
			FacilioChain c = FacilioChain.getTransactionChain();
			c.addCommand(new UpdateSourceToResourceMappingCommand());
			c.addCommand(new UpdateEventResourcesMappingCommand());
			c.addCommand(new UpdateAlarmAssetMappingCommand());
			return c;
		}
		
	}
	
	public static class EventModuleFactory {
		public static FacilioModule getEventModule() {
			FacilioModule event = new FacilioModule();
			event.setName("event");
			event.setDisplayName("Event");
			event.setTableName("Event");
			return event;
		}
		
		public static FacilioModule getEventPropertyModule() {
			FacilioModule eventproperty = new FacilioModule();
			eventproperty.setName("eventproperty");
			eventproperty.setDisplayName("Event Property");
			eventproperty.setTableName("Event_Property");
			return eventproperty;
		}
		
		public static FacilioModule getEventRuleModule() {
			FacilioModule eventrule = new FacilioModule();
			eventrule.setName("eventrule");
			eventrule.setDisplayName("Event Rule");
			eventrule.setTableName("Event_Rule");
			return eventrule;
		}
		
		public static FacilioModule getEventRulesModule() {
			FacilioModule eventRules = new FacilioModule();
			eventRules.setName("eventrule");
			eventRules.setDisplayName("Event Rules");
			eventRules.setTableName("Event_Rules");
			return eventRules;
		}
		
		public static FacilioModule getEventToAlarmFieldMappingModule() {
			FacilioModule eventmappingrule = new FacilioModule();
			eventmappingrule.setName("eventtoalaemfieldmapping");
			eventmappingrule.setDisplayName("Event To Alarm Field Mapping");
			eventmappingrule.setTableName("Event_To_Alarm_Field_Mapping");
			return eventmappingrule;
		}
		
		public static FacilioModule getSourceToResourceMappingModule() {
			FacilioModule nodeAssetMappingModule = new FacilioModule();
			nodeAssetMappingModule.setName("sourcetoresourcemapping");
			nodeAssetMappingModule.setDisplayName("Source To Resource Mapping");
			nodeAssetMappingModule.setTableName("Source_To_Resource_Mapping");
			return  nodeAssetMappingModule;
		}
	}
	
	public static class EventFieldFactory {
		public static List<FacilioField> getEventFields() {
			FacilioModule module = EventModuleFactory.getEventModule();
			
			List<FacilioField> fields = new ArrayList<>();
			
			fields.add(FieldFactory.getIdField(module));
			/*fields.add(FieldFactory.getOrgIdField(module));*/
			fields.add(FieldFactory.getSiteIdField(module));
			
			FacilioField source = new FacilioField();
			source.setName("condition");
			source.setDataType(FieldType.STRING);
			source.setDisplayName("Condition");
			source.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			source.setColumnName("EVENT_CONDITION");
			source.setModule(module);
			fields.add(source);
			
			FacilioField node = new FacilioField();
			node.setName("source");
			node.setDataType(FieldType.STRING);
			node.setDisplayName("Source");
			node.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			node.setColumnName("SOURCE");
			node.setModule(module);
			fields.add(node);
			
			FacilioField resourceId = new FacilioField();
			resourceId.setName("resourceId");
			resourceId.setDisplayName("Resource");
			resourceId.setDataType(FieldType.NUMBER);
			resourceId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			resourceId.setColumnName("RESOURCE_ID");
			resourceId.setModule(module);
			fields.add(resourceId);
			
			FacilioField eventType = new FacilioField();
			eventType.setName("eventMessage");
			eventType.setDisplayName("Message");
			eventType.setDataType(FieldType.STRING);
			eventType.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			eventType.setColumnName("EVENT_MESSAGE");
			eventType.setModule(module);
			fields.add(eventType);
			
			FacilioField messageKey = new FacilioField();
			messageKey.setName("messageKey");
			messageKey.setDisplayName("Message Key");
			messageKey.setDataType(FieldType.STRING);
			messageKey.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			messageKey.setColumnName("MESSAGE_KEY");
			messageKey.setModule(module);
			fields.add(messageKey);
			
			FacilioField severity = new FacilioField();
			severity.setName("severity");
			severity.setDisplayName("Severity");
			severity.setDataType(FieldType.STRING);
			severity.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			severity.setColumnName("SEVERITY");
			severity.setModule(module);
			fields.add(severity);
			
			FacilioField createdTime = new FacilioField();
			createdTime.setName("createdTime");
			createdTime.setDisplayName("Created Time");
			createdTime.setDataType(FieldType.DATE_TIME);
			createdTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
			createdTime.setColumnName("CREATED_TIME");
			createdTime.setModule(module);
			fields.add(createdTime);
			
			FacilioField eventState = new FacilioField();
			eventState.setName("eventState");
			eventState.setDisplayName("Event State");
			eventState.setDataType(FieldType.NUMBER);
			eventState.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			eventState.setColumnName("EVENT_STATE");
			eventState.setModule(module);
			fields.add(eventState);
			
			FacilioField internalState = new FacilioField();
			internalState.setName("internalState");
			internalState.setDisplayName("Internal State");
			internalState.setDataType(FieldType.NUMBER);
			internalState.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			internalState.setColumnName("INTERNAL_STATE");
			internalState.setModule(module);
			fields.add(internalState);
			
			FacilioField eventRuleId = new FacilioField();
			eventRuleId.setName("eventRuleId");
			eventRuleId.setDisplayName("Event Rule Id");
			eventRuleId.setDataType(FieldType.NUMBER);
			eventRuleId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			eventRuleId.setColumnName("EVENT_RULE_ID");
			eventRuleId.setModule(module);
			fields.add(eventRuleId);
			
			FacilioField alarmId = new FacilioField();
			alarmId.setName("alarmId");
			alarmId.setDisplayName("Alarm Id");
			alarmId.setDataType(FieldType.NUMBER);
			alarmId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			alarmId.setColumnName("ALARM_ID");
			alarmId.setModule(module);
			fields.add(alarmId);
			
			FacilioField priority = new FacilioField();
			priority.setName("priority");
			priority.setDisplayName("Priority");
			priority.setDataType(FieldType.STRING);
			priority.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			priority.setColumnName("PRIORITY");
			priority.setModule(module);
			fields.add(priority);
			
			FacilioField alarmClass = new FacilioField();
			alarmClass.setName("alarmClass");
			alarmClass.setDataType(FieldType.STRING);
			alarmClass.setDisplayName("Alarm Class");
			alarmClass.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			alarmClass.setColumnName("ALARM_CLASS");
			alarmClass.setModule(module);
			fields.add(alarmClass);
			
			FacilioField state = new FacilioField();
			state.setName("state");
			state.setDataType(FieldType.STRING);
			state.setDisplayName("State");
			state.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			state.setColumnName("STATE");
			state.setModule(module);
			fields.add(state);
			
			FacilioField description = new FacilioField();
			description.setName("description");
			description.setDataType(FieldType.STRING);
			description.setDisplayName("Description");
			description.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			description.setColumnName("DESCRIPTION");
			description.setModule(module);
			fields.add(description);
			
			fields.add(FieldFactory.getField("comment", "COMMENT", module, FieldType.STRING));
			
			FacilioField additionalInfo = new FacilioField();
			additionalInfo.setName("additionalInfoJsonStr");
			additionalInfo.setDisplayName("Additional Info");
			additionalInfo.setDataType(FieldType.STRING);
			additionalInfo.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			additionalInfo.setColumnName("ADDITIONAL_INFO");
			additionalInfo.setModule(module);
			fields.add(additionalInfo);
			
			fields.add(FieldFactory.getField("subRuleId", "Sub Rule Id", "SUB_RULE_ID", module, FieldType.LOOKUP));
			
			return fields;
		}
		
		public static List<FacilioField> getEventPropertyFields() {
			FacilioModule module = EventModuleFactory.getEventPropertyModule();
			List<FacilioField> fields = new ArrayList<>();
			
			fields.add(FieldFactory.getIdField(module));
			/*fields.add(FieldFactory.getOrgIdField(module));*/
			
			FacilioField isEventEnabled = new FacilioField();
			isEventEnabled.setName("isEventEnabled");
			isEventEnabled.setDataType(FieldType.BOOLEAN);
			isEventEnabled.setColumnName("IS_EVENT_ENABLED");
			isEventEnabled.setModule(module);
			fields.add(isEventEnabled);
			
			FacilioField eventTopicName = new FacilioField();
			eventTopicName.setName("eventTopicName");
			eventTopicName.setDataType(FieldType.STRING);
			eventTopicName.setColumnName("EVENTS_TOPIC_NAME");
			eventTopicName.setModule(module);
			fields.add(eventTopicName);
			
			return fields;
		}
		
		public static List<FacilioField> getEventRulesFields() {
			FacilioModule module = EventModuleFactory.getEventRulesModule();
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(FieldFactory.getIdField(module));
			/*fields.add(FieldFactory.getOrgIdField(module));*/
			fields.add(FieldFactory.getNameField(module));
			fields.add(FieldFactory.getField("description", "DESCRIPTION", module, FieldType.STRING));
			fields.add(FieldFactory.getField("criteriaId", "CRITERIA_ID", module, FieldType.LOOKUP));
			fields.add(FieldFactory.getField("workflowId", "WORKFLOW_ID", module, FieldType.LOOKUP));
			fields.add(FieldFactory.getField("executionOrder", "EXECUTION_ORDER", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("successAction", "SUCCESS_ACTION", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("transformTemplateId", "TRANSFORM_TEMPLATE_ID", module, FieldType.LOOKUP));
			fields.add(FieldFactory.getField("active", "IS_ACTIVE", module, FieldType.BOOLEAN));
			
			return fields;
		}
		
		public static List<FacilioField> getEventRuleFields() {
			FacilioModule module = EventModuleFactory.getEventRuleModule();
			
			List<FacilioField> fields = new ArrayList<>();
			
			FacilioField id = new FacilioField();
			id.setName("eventRuleId");
			id.setDataType(FieldType.ID);
			id.setColumnName("EVENT_RULE_ID");
			id.setModule(module);
			fields.add(id);
			
			/*fields.add(FieldFactory.getOrgIdField(module));*/
			fields.add(FieldFactory.getNameField(module));
			
			FacilioField ruleOrder = new FacilioField();
			ruleOrder.setName("ruleOrder");
			ruleOrder.setDataType(FieldType.NUMBER);
			ruleOrder.setColumnName("RULE_ORDER");
			ruleOrder.setModule(module);
			fields.add(ruleOrder);
			
			FacilioField baseCriteriaId = new FacilioField();
			baseCriteriaId.setName("baseCriteriaId");
			baseCriteriaId.setDataType(FieldType.NUMBER);
			baseCriteriaId.setColumnName("BASE_CRITERIAID");
			baseCriteriaId.setModule(module);
			fields.add(baseCriteriaId);
			
			FacilioField ignoreEvent = new FacilioField();
			ignoreEvent.setName("ignoreEvent");
			ignoreEvent.setDataType(FieldType.BOOLEAN);
			ignoreEvent.setColumnName("IGNORE_EVENT");
			ignoreEvent.setModule(module);
			fields.add(ignoreEvent);
			
			FacilioField transformCriteriaId = new FacilioField();
			transformCriteriaId.setName("transformCriteriaId");
			transformCriteriaId.setDataType(FieldType.NUMBER);
			transformCriteriaId.setColumnName("TRANSFORM_CRITERIAID");
			transformCriteriaId.setModule(module);
			fields.add(transformCriteriaId);
			
			FacilioField transformAlertTemplateId = new FacilioField();
			transformAlertTemplateId.setName("transformAlertTemplateId");
			transformAlertTemplateId.setDataType(FieldType.NUMBER);
			transformAlertTemplateId.setColumnName("TRANSFORM_ALARM_TEMPLATE_ID");
			transformAlertTemplateId.setModule(module);
			fields.add(transformAlertTemplateId);
			
			FacilioField thresholdCriteriaId = new FacilioField();
			thresholdCriteriaId.setName("thresholdCriteriaId");
			thresholdCriteriaId.setDataType(FieldType.NUMBER);
			thresholdCriteriaId.setColumnName("THRESHOLD_CRITERIAID");
			thresholdCriteriaId.setModule(module);
			fields.add(thresholdCriteriaId);
			
			FacilioField thresholdOccurs = new FacilioField();
			thresholdOccurs.setName("thresholdOccurs");
			thresholdOccurs.setDataType(FieldType.NUMBER);
			thresholdOccurs.setColumnName("THRESHOLD_OCCURS");
			thresholdOccurs.setModule(module);
			fields.add(thresholdOccurs);
			
			FacilioField clearCriteriaOverseconds = new FacilioField();
			clearCriteriaOverseconds.setName("thresholdOverSeconds");
			clearCriteriaOverseconds.setDataType(FieldType.NUMBER);
			clearCriteriaOverseconds.setColumnName("THRESHOLD_OVER_SECONDS");
			clearCriteriaOverseconds.setModule(module);
			fields.add(clearCriteriaOverseconds);
			
			fields.add(FieldFactory.getField("coRelWorkflowId", "CO_REL_WORKFLOW_ID", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("colRelAction", "CO_REL_ACTION", module, FieldType.NUMBER));
			fields.add(FieldFactory.getField("coRelTransformTemplateId", "CO_REL_TRANSFORM_TEMPLATE_ID", module, FieldType.NUMBER));
			
			return fields;
		}
		
		public static List<FacilioField> getEventToAlarmFieldMappingFields() {
			FacilioModule module = EventModuleFactory.getEventToAlarmFieldMappingModule();
			List<FacilioField> fields = new ArrayList<>();
			
			FacilioField eventToAlarmFieldMappingId = new FacilioField();
			eventToAlarmFieldMappingId.setName("eventToAlarmFieldMappingId");
			eventToAlarmFieldMappingId.setDataType(FieldType.ID);
			eventToAlarmFieldMappingId.setColumnName("EVENT_TO_ALARM_FIELD_MAPPING_ID");
			eventToAlarmFieldMappingId.setModule(module);
			fields.add(eventToAlarmFieldMappingId);
			
			/*fields.add(FieldFactory.getOrgIdField(module));*/
			
			FacilioField type = new FacilioField();
			type.setName("type");
			type.setDataType(FieldType.NUMBER);
			type.setColumnName("TRANSFORM_TYPE");
			type.setModule(module);
			fields.add(type);
			
			FacilioField fromField = new FacilioField();
			fromField.setName("fromField");
			fromField.setDataType(FieldType.STRING);
			fromField.setColumnName("FROM_FIELD");
			fromField.setModule(module);
			fields.add(fromField);
			
			FacilioField toField = new FacilioField();
			toField.setName("toField");
			toField.setDataType(FieldType.STRING);
			toField.setColumnName("TO_FIELD");
			toField.setModule(module);
			fields.add(toField);
			
			FacilioField constantValue = new FacilioField();
			constantValue.setName("constantValue");
			constantValue.setDataType(FieldType.STRING);
			constantValue.setColumnName("CONSTANT_VALUE");
			constantValue.setModule(module);
			fields.add(constantValue);
			
			FacilioField mappingOrder = new FacilioField();
			mappingOrder.setName("mappingOrder");
			mappingOrder.setDataType(FieldType.NUMBER);
			mappingOrder.setColumnName("MAPPING_ORDER");
			mappingOrder.setModule(module);
			fields.add(mappingOrder);
			
			FacilioField mappingPairs = new FacilioField();
			mappingPairs.setName("mappingPairs");
			mappingPairs.setDataType(FieldType.STRING);
			mappingPairs.setColumnName("MAPPING_PAIRS");
			mappingPairs.setModule(module);
			fields.add(mappingPairs);
			
			return fields;
		}
		
		public static List<FacilioField> getSourceToResourceMappingFields() {
			FacilioModule module = EventModuleFactory.getSourceToResourceMappingModule();
			List<FacilioField> fields = new ArrayList<>();
			
			fields.add(FieldFactory.getIdField(module));
			/*fields.add(FieldFactory.getOrgIdField(module));*/
			
			FacilioField node = new FacilioField();
			node.setName(EventConstants.EventContextNames.SOURCE);
			node.setDataType(FieldType.STRING);
			node.setColumnName("SOURCE");
			node.setModule(module);
			fields.add(node);
			
			FacilioField assetId = new FacilioField();
			assetId.setName(EventConstants.EventContextNames.RESOURCE_ID);
			assetId.setDataType(FieldType.NUMBER);
			assetId.setColumnName("RESOURCE_ID");
			assetId.setModule(module);
			fields.add(assetId);
			
			
			FacilioField controllerId = new FacilioField();
			controllerId.setName(EventConstants.EventContextNames.CONTROLLER_ID);
			controllerId.setDataType(FieldType.NUMBER);
			controllerId.setColumnName("CONTROLLER_ID");
			controllerId.setModule(module);
			fields.add(controllerId);
			
			return fields;
		}
	}
}