package com.facilio.events.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.events.context.EventToAlarmFieldMapping;
import com.facilio.sql.GenericSelectRecordBuilder;

public class EventRulesAPI {
	public static final EventRule getEventRule(long orgId, long eventRuleId) throws Exception {
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
												.select(EventConstants.getEventRuleFields())
												.table("Event_Rule")
												.andCustomWhere("ORGID = ? AND EVENT_RULE_ID = ?", orgId, eventRuleId);
		
		List<Map<String, Object>> eventRules = rulebuilder.get();
		if(eventRules != null && !eventRules.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(eventRules.get(0), EventRule.class);
		}
		return null;
	}
	
	public static final List<EventRule> getEventRules(long orgId) throws Exception {
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
														.select(EventConstants.getEventRuleFields())
														.table(EventConstants.getEventRuleModule().getTableName())
														.orderBy("RULE_ORDER")
														.andCustomWhere("ORGID = ?", orgId);

		List<Map<String, Object>> eventRulesProps = rulebuilder.get();
		if(eventRulesProps != null && !eventRulesProps.isEmpty()) {
			List<EventRule> eventRules = new ArrayList<>();
			for(Map<String, Object> eventRuleProp : eventRulesProps) {
				eventRules.add(FieldUtil.getAsBeanFromMap(eventRuleProp, EventRule.class));
			}
			return eventRules;
 		}
		return null;
	}
	
	public static final List<EventToAlarmFieldMapping> getEventToAlarmFieldMappings(long orgId) throws Exception {
		GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
														.select(EventConstants.getEventToAlarmFieldMappingFields())
														.table(EventConstants.getEventToAlarmFieldMappingModule().getTableName())
														.orderBy("MAPPING_ORDER")
														.andCustomWhere("ORGID = ?", orgId);
		
		List<Map<String, Object>> eventRulesProps = rulebuilder.get();
		if(eventRulesProps != null && !eventRulesProps.isEmpty()) {
			List<EventToAlarmFieldMapping> eventTransformMappings = new ArrayList<>();
			for(Map<String, Object> eventRuleProp : eventRulesProps) {
					eventTransformMappings.add(FieldUtil.getAsBeanFromMap(eventRuleProp, EventToAlarmFieldMapping.class));
			}
			return eventTransformMappings;
		}
		return null;
	}
}
