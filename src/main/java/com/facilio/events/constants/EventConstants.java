package com.facilio.events.constants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;

import com.facilio.bmsconsole.commands.TransactionExceptionHandler;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.events.commands.AddEventCommand;
import com.facilio.events.commands.AddEventRuleCommand;
import com.facilio.events.commands.AddOrUpdateAlarmCommand;
import com.facilio.events.commands.ExecuteEventMappingRuleCommand;
import com.facilio.events.commands.ExecuteEventRuleCommand;
import com.facilio.events.commands.GetEventListCommand;
import com.facilio.events.commands.GetEventRulesCommand;
import com.facilio.events.commands.UpdateEventFilterCommand;
import com.facilio.events.commands.UpdateEventPropertyCommand;
import com.facilio.events.commands.UpdateEventThresholdRulesCommand;
import com.facilio.events.commands.UpdateEventTransformRuleCommand;

public class EventConstants {
	
	public static final String EVENT = "event";
	public static final String EVENT_LIST = "events";
	public static final String EVENT_PROPERTY = "eventProperty";
	public static final String EVENT_RULE = "eventRule";
	public static final String EVENT_RULE_LIST = "eventRules";
	public static final String EVENT_CRITERIA_MAP = "eventCriteriaList";
	public static final String IGNORE_EVENT = "ignoreEvent";
	public static final String FILTER_CRITERIA_PATTERN = "filterCriteriaPattern";
	public static final String FILTER_CONDITIONS = "filterConditions";
	public static final String CUSTOMIZE_CRITERIA_PATTERN = "customizeCriteriaPattern";
	public static final String CUSTOMIZE_CONDITIONS = "customizeConditions";
	public static final String CUSTOMIZE_ALARM_TEMPLATE = "customizeAlarmTemplate";
	public static final String EVENT_THRESHOLD_RULE_LIST = "eventThresholdRuleList";
	
	public static Chain getAddEventChain() {
		Chain c = new ChainBase();
		c.addCommand(new ExecuteEventRuleCommand());
		c.addCommand(new ExecuteEventMappingRuleCommand());
		c.addCommand(new AddEventCommand());
		c.addCommand(new AddOrUpdateAlarmCommand());
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
	
	public static Chain updateEventPropertyChain() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateEventPropertyCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateEventFilterChain() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateEventFilterCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateEventTransformRuleChain() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateEventTransformRuleCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain updateEventThresholdRulesChain() {
		Chain c = new ChainBase();
		c.addCommand(new UpdateEventThresholdRulesCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	public static Chain getEventListChain() {
		Chain c = new ChainBase();
		c.addCommand(new GetEventListCommand());
		addCleanUpCommand(c);
		return c;
	}
	
	private static void addCleanUpCommand(Chain c)
	{
		c.addCommand(new TransactionExceptionHandler());
	}
	
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
	
	public static FacilioModule getEventThresholdRuleModule() {
		FacilioModule eventthresholdrule = new FacilioModule();
		eventthresholdrule.setName("eventthresholdrule");
		eventthresholdrule.setDisplayName("Event Threshold Rule");
		eventthresholdrule.setTableName("Event_Threshold_Rule");
		return eventthresholdrule;
	}
	
	public static FacilioModule getEventMappingRuleModule() {
		FacilioModule eventmappingrule = new FacilioModule();
		eventmappingrule.setName("eventmappingrule");
		eventmappingrule.setDisplayName("Event Mapping Rule");
		eventmappingrule.setTableName("Event_Mapping_Rule");
		return eventmappingrule;
	}
	
	public static FacilioField getOrgIdField(FacilioModule module) {
		FacilioField field = new FacilioField();
		field.setName("orgId");
		field.setDataType(FieldType.NUMBER);
		field.setColumnName("ORGID");
		if(module != null) {
			field.setModule(module);
		}
		return field;
	}
	
	public static List<FacilioField> getEventFields() {
		FacilioModule module = getEventModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("id");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("ID");
		id.setModule(module);
		fields.add(id);
		
		fields.add(getOrgIdField(module));
		
		FacilioField source = new FacilioField();
		source.setName("source");
		source.setDataType(FieldType.STRING);
		source.setColumnName("SOURCE");
		source.setModule(module);
		fields.add(source);
		
		FacilioField node = new FacilioField();
		node.setName("node");
		node.setDataType(FieldType.STRING);
		node.setColumnName("NODE");
		node.setModule(module);
		fields.add(node);
		
		FacilioField eventType = new FacilioField();
		eventType.setName("eventType");
		eventType.setDataType(FieldType.STRING);
		eventType.setColumnName("EVENT_TYPE");
		eventType.setModule(module);
		fields.add(eventType);
		
		FacilioField messageKey = new FacilioField();
		messageKey.setName("messageKey");
		messageKey.setDataType(FieldType.STRING);
		messageKey.setColumnName("MESSAGE_KEY");
		messageKey.setModule(module);
		fields.add(messageKey);
		
		FacilioField severity = new FacilioField();
		severity.setName("severity");
		severity.setDataType(FieldType.STRING);
		severity.setColumnName("SEVERITY");
		severity.setModule(module);
		fields.add(severity);
		
		FacilioField state = new FacilioField();
		state.setName("state");
		state.setDataType(FieldType.STRING);
		state.setColumnName("STATE");
		state.setModule(module);
		fields.add(state);
		
		FacilioField createdTime = new FacilioField();
		createdTime.setName("createdTime");
		createdTime.setDataType(FieldType.NUMBER);
		createdTime.setColumnName("CREATED_TIME");
		createdTime.setModule(module);
		fields.add(createdTime);
		
		FacilioField description = new FacilioField();
		description.setName("description");
		description.setDataType(FieldType.STRING);
		description.setColumnName("DESCRIPTION");
		description.setModule(module);
		fields.add(description);
		
		FacilioField additionalInfo = new FacilioField();
		additionalInfo.setName("additionalInfo");
		additionalInfo.setDataType(FieldType.STRING);
		additionalInfo.setColumnName("ADDITIONAL_INFO");
		additionalInfo.setModule(module);
		fields.add(additionalInfo);
		
		return fields;
	}
	
	public static List<FacilioField> getEventPropertyFields() {
		FacilioModule module = getEventPropertyModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("eventPropertyId");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("EVENT_PROPERTY_ID");
		id.setModule(module);
		fields.add(id);
		
		fields.add(getOrgIdField(module));
		
		FacilioField hasEventRule = new FacilioField();
		hasEventRule.setName("hasEventRule");
		hasEventRule.setDataType(FieldType.BOOLEAN);
		hasEventRule.setColumnName("HASEVENTRULE");
		hasEventRule.setModule(module);
		fields.add(hasEventRule);
		
		FacilioField hasMappingRule = new FacilioField();
		hasMappingRule.setName("hasMappingRule");
		hasMappingRule.setDataType(FieldType.BOOLEAN);
		hasMappingRule.setColumnName("HASMAPPINGRULE");
		hasMappingRule.setModule(module);
		fields.add(hasMappingRule);
		
		return fields;
	}
	
	public static List<FacilioField> getEventRuleFields() {
		FacilioModule module = getEventRuleModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("eventRuleId");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("EVENT_RULE_ID");
		id.setModule(module);
		fields.add(id);
		
		fields.add(getOrgIdField(module));
		
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(module);
		fields.add(name);
		
		FacilioField ruleOrder = new FacilioField();
		ruleOrder.setName("ruleOrder");
		ruleOrder.setDataType(FieldType.NUMBER);
		ruleOrder.setColumnName("RULE_ORDER");
		ruleOrder.setModule(module);
		fields.add(ruleOrder);
		
		FacilioField ignoreEvent = new FacilioField();
		ignoreEvent.setName("ignoreEvent");
		ignoreEvent.setDataType(FieldType.BOOLEAN);
		ignoreEvent.setColumnName("IGNOREEVENT");
		ignoreEvent.setModule(module);
		fields.add(ignoreEvent);
		
		FacilioField baseCriteriaId = new FacilioField();
		baseCriteriaId.setName("baseCriteriaId");
		baseCriteriaId.setDataType(FieldType.NUMBER);
		baseCriteriaId.setColumnName("BASE_CRITERIAID");
		baseCriteriaId.setModule(module);
		fields.add(baseCriteriaId);
		
		FacilioField hasCustomizeRule = new FacilioField();
		hasCustomizeRule.setName("hasCustomizeRule");
		hasCustomizeRule.setDataType(FieldType.BOOLEAN);
		hasCustomizeRule.setColumnName("HASCUSTOMIZERULE");
		hasCustomizeRule.setModule(module);
		fields.add(hasCustomizeRule);
		
		FacilioField customizeCriteriaId = new FacilioField();
		customizeCriteriaId.setName("customizeCriteriaId");
		customizeCriteriaId.setDataType(FieldType.NUMBER);
		customizeCriteriaId.setColumnName("CUSTOMIZE_CRITERIAID");
		customizeCriteriaId.setModule(module);
		fields.add(customizeCriteriaId);
		
		FacilioField alarmTemplateId = new FacilioField();
		alarmTemplateId.setName("alarmTemplateId");
		alarmTemplateId.setDataType(FieldType.NUMBER);
		alarmTemplateId.setColumnName("ALARM_TEMPLATE_ID");
		alarmTemplateId.setModule(module);
		fields.add(alarmTemplateId);
		
		FacilioField hasThresholdRule = new FacilioField();
		hasThresholdRule.setName("hasThresholdRule");
		hasThresholdRule.setDataType(FieldType.BOOLEAN);
		hasThresholdRule.setColumnName("HASTHRESHOLDRULE");
		hasThresholdRule.setModule(module);
		fields.add(hasThresholdRule);
		
		return fields;
	}
	
	public static List<FacilioField> getEventThresholdRuleFields() {
		FacilioModule module = getEventThresholdRuleModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("eventThresholdRuleId");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("EVENT_THRESHOLD_RULE_ID");
		id.setModule(module);
		fields.add(id);
		
		fields.add(getOrgIdField(module));
		
		FacilioField eventRuleId = new FacilioField();
		eventRuleId.setName("eventRuleId");
		eventRuleId.setDataType(FieldType.NUMBER);
		eventRuleId.setColumnName("EVENT_RULE_ID");
		eventRuleId.setModule(module);
		fields.add(eventRuleId);
		
		FacilioField hasFilterCriteria = new FacilioField();
		hasFilterCriteria.setName("hasFilterCriteria");
		hasFilterCriteria.setDataType(FieldType.BOOLEAN);
		hasFilterCriteria.setColumnName("HASFILTERCRITERIA");
		hasFilterCriteria.setModule(module);
		fields.add(hasFilterCriteria);
		
		FacilioField filterCriteriaId = new FacilioField();
		filterCriteriaId.setName("filterCriteriaId");
		filterCriteriaId.setDataType(FieldType.NUMBER);
		filterCriteriaId.setColumnName("FILTER_CRITERIAID");
		filterCriteriaId.setModule(module);
		fields.add(filterCriteriaId);
		
		FacilioField filterCriteriaOccurs = new FacilioField();
		filterCriteriaOccurs.setName("filterCriteriaOccurs");
		filterCriteriaOccurs.setDataType(FieldType.NUMBER);
		filterCriteriaOccurs.setColumnName("FILTER_CRITERIA_OCCURS");
		filterCriteriaOccurs.setModule(module);
		fields.add(filterCriteriaOccurs);
		
		FacilioField filterCriteriaOverseconds = new FacilioField();
		filterCriteriaOverseconds.setName("filterCriteriaOverseconds");
		filterCriteriaOverseconds.setDataType(FieldType.NUMBER);
		filterCriteriaOverseconds.setColumnName("FILTER_CRITERIA_OVERSECONDS");
		filterCriteriaOverseconds.setModule(module);
		fields.add(filterCriteriaOverseconds);
		
		FacilioField hasClearCriteria = new FacilioField();
		hasClearCriteria.setName("hasClearCriteria");
		hasClearCriteria.setDataType(FieldType.BOOLEAN);
		hasClearCriteria.setColumnName("HASCLEARCRITERIA");
		hasClearCriteria.setModule(module);
		fields.add(hasClearCriteria);
		
		FacilioField clearCriteriaId = new FacilioField();
		clearCriteriaId.setName("clearCriteriaId");
		clearCriteriaId.setDataType(FieldType.NUMBER);
		clearCriteriaId.setColumnName("CLEAR_CRITERIAID");
		clearCriteriaId.setModule(module);
		fields.add(clearCriteriaId);
		
		FacilioField clearCriteriaOccurs = new FacilioField();
		clearCriteriaOccurs.setName("clearCriteriaOccurs");
		clearCriteriaOccurs.setDataType(FieldType.NUMBER);
		clearCriteriaOccurs.setColumnName("CLEAR_CRITERIA_OCCURS");
		clearCriteriaOccurs.setModule(module);
		fields.add(clearCriteriaOccurs);
		
		FacilioField clearCriteriaOverseconds = new FacilioField();
		clearCriteriaOverseconds.setName("clearCriteriaOverseconds");
		clearCriteriaOverseconds.setDataType(FieldType.NUMBER);
		clearCriteriaOverseconds.setColumnName("CLEAR_CRITERIA_OVERSECONDS");
		clearCriteriaOverseconds.setModule(module);
		fields.add(clearCriteriaOverseconds);
		
		FacilioField ruleOrder = new FacilioField();
		ruleOrder.setName("ruleOrder");
		ruleOrder.setDataType(FieldType.NUMBER);
		ruleOrder.setColumnName("RULE_ORDER");
		ruleOrder.setModule(module);
		fields.add(ruleOrder);
		
		return fields;
	}
	
	public static List<FacilioField> getEventMappingRuleFields() {
		FacilioModule module = getEventMappingRuleModule();
		
		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField id = new FacilioField();
		id.setName("eventMappingRuleId");
		id.setDataType(FieldType.NUMBER);
		id.setColumnName("EVENT_MAPPING_RULE_ID");
		id.setModule(module);
		fields.add(id);
		
		fields.add(getOrgIdField(module));
		
		FacilioField mappingType = new FacilioField();
		mappingType.setName("mappingType");
		mappingType.setDataType(FieldType.NUMBER);
		mappingType.setColumnName("MAPPING_TYPE");
		mappingType.setModule(module);
		fields.add(mappingType);
		
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
		
		FacilioField mappingPairs = new FacilioField();
		mappingPairs.setName("mappingPairs");
		mappingPairs.setDataType(FieldType.STRING);
		mappingPairs.setColumnName("MAPPING_PAIRS");
		mappingPairs.setModule(module);
		fields.add(mappingPairs);
		
		return fields;
	}
}