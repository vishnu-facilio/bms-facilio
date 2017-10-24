package com.facilio.bmts.constants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;

import com.facilio.bmsconsole.commands.TransactionExceptionHandler;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmts.bmsconsole.commands.AddEventCommand;
import com.facilio.bmts.bmsconsole.commands.AddOrUpdateAlarmCommand;
import com.facilio.bmts.bmsconsole.commands.ExecuteEventMappingRuleCommand;
import com.facilio.bmts.bmsconsole.commands.ExecuteEventRuleCommand;

public class BmtsConstants {
	
	public static final String EVENT = "event";
	public static final String EVENT_PROPERTY = "eventProperty";
	public static final String IGNORE_EVENT = "ignoreEvent";
	
	public static Chain getAddEventChain() {
		Chain c = new ChainBase();
		c.addCommand(new ExecuteEventRuleCommand());
		c.addCommand(new ExecuteEventMappingRuleCommand());
		c.addCommand(new AddEventCommand());
		c.addCommand(new AddOrUpdateAlarmCommand());
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
		
		FacilioField hasEventFilter = new FacilioField();
		hasEventFilter.setName("hasEventFilter");
		hasEventFilter.setDataType(FieldType.BOOLEAN);
		hasEventFilter.setColumnName("HASEVENTFILTER");
		hasEventFilter.setModule(module);
		fields.add(hasEventFilter);
		
		FacilioField filterCriteriaId = new FacilioField();
		filterCriteriaId.setName("filterCriteriaId");
		filterCriteriaId.setDataType(FieldType.NUMBER);
		filterCriteriaId.setColumnName("FILTER_CRITERIAID");
		filterCriteriaId.setModule(module);
		fields.add(filterCriteriaId);
		
		FacilioField hasCustomizeRule = new FacilioField();
		hasCustomizeRule.setName("hasCustomizeRule");
		hasCustomizeRule.setDataType(FieldType.BOOLEAN);
		hasCustomizeRule.setColumnName("HASCUSTOMIZERULE");
		hasCustomizeRule.setModule(module);
		fields.add(hasCustomizeRule);
		
		FacilioField hasThresholdRule = new FacilioField();
		hasThresholdRule.setName("hasThresholdRule");
		hasThresholdRule.setDataType(FieldType.BOOLEAN);
		hasThresholdRule.setColumnName("HASTHRESHOLDRULE");
		hasThresholdRule.setModule(module);
		fields.add(hasThresholdRule);
		
		return fields;
	}
}