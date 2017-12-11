package com.facilio.events.constants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;

import com.facilio.bmsconsole.commands.TransactionExceptionHandler;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.events.commands.AddEventCommand;
import com.facilio.events.commands.AddEventRuleCommand;
import com.facilio.events.commands.GetEventDetailCommand;
import com.facilio.events.commands.GetEventListCommand;
import com.facilio.events.commands.GetEventRulesCommand;
import com.facilio.events.commands.UpdateAlarmAssetMappingCommand;
import com.facilio.events.commands.UpdateEventAssetsMappingCommand;
import com.facilio.events.commands.UpdateEventRulesCommand;
import com.facilio.events.commands.UpdateNodeToAssetMappingCommand;

public class EventConstants {
	
	public static class EventContextNames {
	
		public static final String EVENT_ID = "eventId";
		public static final String EVENT = "event";
		public static final String EVENT_PAYLOAD = "eventPayload";
		public static final String EVENT_LIST = "events";
		public static final String EVENT_PROPERTY = "eventProperty";
		public static final String EVENT_RULE = "eventRule";
		public static final String EVENT_RULE_LIST = "eventRules";
		public static final String NODE = "node";
		public static final String ASSET_ID = "assetId";
		public static final String ALARM_ID = "alarmId";
	
	}
	
	public static class EventChainFactory {
		public static Chain getAddEventChain() {
			Chain c = new ChainBase();
			c.addCommand(new AddEventCommand());
			addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getEventDetailChain() {
			Chain c = new ChainBase();
			c.addCommand(new GetEventDetailCommand());
			addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getEventRulesChain() {
			Chain c = new ChainBase();
			c.addCommand(new GetEventRulesCommand());
			addCleanUpCommand(c);
			return c;
		}
		
		public static Chain addEventRuleChain() {
			Chain c = new ChainBase();
			c.addCommand(new AddEventRuleCommand());
			addCleanUpCommand(c);
			return c;
		}
		
		public static Chain updateEventRulesChain() {
			Chain c = new ChainBase();
			c.addCommand(new UpdateEventRulesCommand());
			addCleanUpCommand(c);
			return c;
		}
		
		public static Chain getEventListChain() {
			Chain c = new ChainBase();
			c.addCommand(new GetEventListCommand());
			addCleanUpCommand(c);
			return c;
		}
		
		public static Chain updateNodeToAssetMappingChain() {
			Chain c = new ChainBase();
			c.addCommand(new UpdateNodeToAssetMappingCommand());
			c.addCommand(new UpdateEventAssetsMappingCommand());
			c.addCommand(new UpdateAlarmAssetMappingCommand());
			addCleanUpCommand(c);
			return c;
		}
		
		private static void addCleanUpCommand(Chain c)
		{
			c.addCommand(new TransactionExceptionHandler());
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
		
		public static FacilioModule getEventToAlarmFieldMappingModule() {
			FacilioModule eventmappingrule = new FacilioModule();
			eventmappingrule.setName("eventtoalaemfieldmapping");
			eventmappingrule.setDisplayName("Event To Alarm Field Mapping");
			eventmappingrule.setTableName("Event_To_Alarm_Field_Mapping");
			return eventmappingrule;
		}
		
		public static FacilioModule getNodeAssetMappingModule() {
			FacilioModule nodeAssetMappingModule = new FacilioModule();
			nodeAssetMappingModule.setName("nodetoassetmapping");
			nodeAssetMappingModule.setDisplayName("Node To Asset Mapping");
			nodeAssetMappingModule.setTableName("Node_To_Asset_Mapping");
			return  nodeAssetMappingModule;
		}
	}
	
	public static class EventFieldFactory {
		public static List<FacilioField> getEventFields() {
			FacilioModule module = EventModuleFactory.getEventModule();
			
			List<FacilioField> fields = new ArrayList<>();
			
			fields.add(FieldFactory.getIdField(module));
			fields.add(FieldFactory.getOrgIdField(module));
			
			FacilioField source = new FacilioField();
			source.setName("source");
			source.setDataType(FieldType.STRING);
			source.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			source.setColumnName("SOURCE");
			source.setModule(module);
			fields.add(source);
			
			FacilioField node = new FacilioField();
			node.setName("node");
			node.setDataType(FieldType.STRING);
			node.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			node.setColumnName("NODE");
			node.setModule(module);
			fields.add(node);
			
			FacilioField assetId = new FacilioField();
			assetId.setName("assetId");
			assetId.setDataType(FieldType.NUMBER);
			assetId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			assetId.setColumnName("ASSETID");
			assetId.setModule(module);
			fields.add(assetId);
			
			FacilioField eventType = new FacilioField();
			eventType.setName("eventMessage");
			eventType.setDataType(FieldType.STRING);
			eventType.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			eventType.setColumnName("EVENT_MESSAGE");
			eventType.setModule(module);
			fields.add(eventType);
			
			FacilioField messageKey = new FacilioField();
			messageKey.setName("messageKey");
			messageKey.setDataType(FieldType.STRING);
			messageKey.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			messageKey.setColumnName("MESSAGE_KEY");
			messageKey.setModule(module);
			fields.add(messageKey);
			
			FacilioField severity = new FacilioField();
			severity.setName("severity");
			severity.setDataType(FieldType.STRING);
			severity.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			severity.setColumnName("SEVERITY");
			severity.setModule(module);
			fields.add(severity);
			
			FacilioField createdTime = new FacilioField();
			createdTime.setName("createdTime");
			createdTime.setDataType(FieldType.DATE_TIME);
			createdTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
			createdTime.setColumnName("CREATED_TIME");
			createdTime.setModule(module);
			fields.add(createdTime);
			
			FacilioField eventState = new FacilioField();
			eventState.setName("eventState");
			eventState.setDataType(FieldType.NUMBER);
			eventState.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			eventState.setColumnName("EVENT_STATE");
			eventState.setModule(module);
			fields.add(eventState);
			
			FacilioField internalState = new FacilioField();
			internalState.setName("internalState");
			internalState.setDataType(FieldType.NUMBER);
			internalState.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			internalState.setColumnName("INTERNAL_STATE");
			internalState.setModule(module);
			fields.add(internalState);
			
			FacilioField eventRuleId = new FacilioField();
			eventRuleId.setName("eventRuleId");
			eventRuleId.setDataType(FieldType.NUMBER);
			eventRuleId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			eventRuleId.setColumnName("EVENT_RULE_ID");
			eventRuleId.setModule(module);
			fields.add(eventRuleId);
			
			FacilioField alarmId = new FacilioField();
			alarmId.setName("alarmId");
			alarmId.setDataType(FieldType.NUMBER);
			alarmId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
			alarmId.setColumnName("ALARM_ID");
			alarmId.setModule(module);
			fields.add(alarmId);
			
			FacilioField priority = new FacilioField();
			priority.setName("priority");
			priority.setDataType(FieldType.STRING);
			priority.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			priority.setColumnName("PRIORITY");
			priority.setModule(module);
			fields.add(priority);
			
			FacilioField alarmClass = new FacilioField();
			alarmClass.setName("alarmClass");
			alarmClass.setDataType(FieldType.STRING);
			alarmClass.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			alarmClass.setColumnName("ALARM_CLASS");
			alarmClass.setModule(module);
			fields.add(alarmClass);
			
			FacilioField state = new FacilioField();
			state.setName("state");
			state.setDataType(FieldType.STRING);
			state.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			state.setColumnName("STATE");
			state.setModule(module);
			fields.add(state);
			
			FacilioField description = new FacilioField();
			description.setName("description");
			description.setDataType(FieldType.STRING);
			description.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			description.setColumnName("DESCRIPTION");
			description.setModule(module);
			fields.add(description);
			
			FacilioField additionalInfo = new FacilioField();
			additionalInfo.setName("additionalInfoJsonStr");
			additionalInfo.setDataType(FieldType.STRING);
			additionalInfo.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
			additionalInfo.setColumnName("ADDITIONAL_INFO");
			additionalInfo.setModule(module);
			fields.add(additionalInfo);
			
			return fields;
		}
		
		public static List<FacilioField> getEventPropertyFields() {
			FacilioModule module = EventModuleFactory.getEventPropertyModule();
			List<FacilioField> fields = new ArrayList<>();
			
			fields.add(FieldFactory.getIdField(module));
			fields.add(FieldFactory.getOrgIdField(module));
			
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
		
		public static List<FacilioField> getEventRuleFields() {
			FacilioModule module = EventModuleFactory.getEventRuleModule();
			
			List<FacilioField> fields = new ArrayList<>();
			
			FacilioField id = new FacilioField();
			id.setName("eventRuleId");
			id.setDataType(FieldType.NUMBER);
			id.setColumnName("EVENT_RULE_ID");
			id.setModule(module);
			fields.add(id);
			
			fields.add(FieldFactory.getOrgIdField(module));
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
			
			return fields;
		}
		
		public static List<FacilioField> getEventToAlarmFieldMappingFields() {
			FacilioModule module = EventModuleFactory.getEventToAlarmFieldMappingModule();
			List<FacilioField> fields = new ArrayList<>();
			
			FacilioField eventToAlarmFieldMappingId = new FacilioField();
			eventToAlarmFieldMappingId.setName("eventToAlarmFieldMappingId");
			eventToAlarmFieldMappingId.setDataType(FieldType.NUMBER);
			eventToAlarmFieldMappingId.setColumnName("EVENT_TO_ALARM_FIELD_MAPPING_ID");
			eventToAlarmFieldMappingId.setModule(module);
			fields.add(eventToAlarmFieldMappingId);
			
			fields.add(FieldFactory.getOrgIdField(module));
			
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
		
		public static List<FacilioField> getNodeToAssetMappingFields() {
			FacilioModule module = EventModuleFactory.getNodeAssetMappingModule();
			List<FacilioField> fields = new ArrayList<>();
			
			fields.add(FieldFactory.getIdField(module));
			fields.add(FieldFactory.getOrgIdField(module));
			
			FacilioField node = new FacilioField();
			node.setName(EventConstants.EventContextNames.NODE);
			node.setDataType(FieldType.STRING);
			node.setColumnName("NODE");
			node.setModule(module);
			fields.add(node);
			
			FacilioField assetId = new FacilioField();
			assetId.setName(EventConstants.EventContextNames.ASSET_ID);
			assetId.setDataType(FieldType.NUMBER);
			assetId.setColumnName("ASSET_ID");
			assetId.setModule(module);
			fields.add(assetId);
			
			return fields;
		}
	}
}