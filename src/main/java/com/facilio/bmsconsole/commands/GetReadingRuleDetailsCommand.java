package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetReadingRuleDetailsCommand implements Command {
	
	private static final Logger LOGGER = LogManager.getLogger(GetReadingRuleDetailsCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<AlarmContext> alarms = (List<AlarmContext>) context.get(FacilioConstants.ContextNames.ALARM_LIST);
		
		Boolean isFromSummary = (Boolean) context.get(FacilioConstants.ContextNames.IS_FROM_SUMMARY);
		
		if (alarms != null && !alarms.isEmpty() && isFromSummary) {
			List<Long> ruleIds = alarms.stream()
										.filter(a -> a instanceof ReadingAlarmContext && ((ReadingAlarmContext) a).getRuleId() != -1)
										.map(a -> ((ReadingAlarmContext) a).getRuleId())
										.collect(Collectors.toList())
										;
			
			if (!ruleIds.isEmpty()) {
				
				Map<Long, AlarmRuleContext> alarmRuleContextMap = ReadingRuleAPI.getAlarmRuleMap(ruleIds);
				
				for (AlarmContext alarm : alarms) {
					if (alarm instanceof ReadingAlarmContext && ((ReadingAlarmContext) alarm).getRuleId() != -1) {
						
						ReadingAlarmContext readingAlarmContext = (ReadingAlarmContext) alarm;
						AlarmRuleContext alarmRuleContext = alarmRuleContextMap.get(readingAlarmContext.getRuleId());
						readingAlarmContext.setAlarmRuleContext(alarmRuleContext);
						
						if(alarmRuleContext.getAlarmRCARules() != null) {
							List<Long> rcaIds = new ArrayList<>();
							
							JSONArray rcaJSONArray = new JSONArray();
							for(ReadingRuleContext rcaRule :alarmRuleContext.getAlarmRCARules()) {
								
								rcaIds.add(rcaRule.getId());
							}
							List<FacilioField> eventFields = EventConstants.EventFieldFactory.getEventFields();
							Map<String, FacilioField> eventFieldsMap = FieldFactory.getAsMap(eventFields);
							List<FacilioField> fields = new ArrayList<>();
							
							fields.add(eventFieldsMap.get("subRuleId"));
							
							fields.add(FieldFactory.getField("createdTime", "MAX(CREATED_TIME)", FieldType.NUMBER));
						
							FacilioModule module = EventConstants.EventModuleFactory.getEventModule();
							GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
																							.table(module.getTableName())
																							.select(fields)
																							.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																							.andCondition(CriteriaAPI.getConditionFromList("SUB_RULE_ID", "subRuleId", rcaIds, NumberOperators.EQUALS))
																							.groupBy("ORGID,SUB_RULE_ID")
																							;
							
							List<Map<String, Object>> props = genericSelectRecordBuilder.get();
							
							LOGGER.error("genericSelectRecordBuilder --- "+genericSelectRecordBuilder);
							
							Map<Long,EventContext> eventMap = new HashMap<>();
							if(props != null && !props.isEmpty()) {
								for(Map<String, Object> prop :props) {
									
									EventContext eventContext = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
									eventMap.put(eventContext.getSubRuleId(), eventContext);
								}
							}
							for(ReadingRuleContext rcaRule :alarmRuleContext.getAlarmRCARules()) {
								EventContext event = eventMap.get(rcaRule.getId());
								
								if(event != null) {
									JSONObject rcaJson = new JSONObject();
									rcaJson.put("rcaRule", rcaRule);
									rcaJson.put("event", event);
									
									if(readingAlarmContext.getModifiedTime() == event.getCreatedTime()) {
										rcaJson.put("isActive", true);
									}
									else {
										rcaJson.put("isActive", false);
									}
									rcaJSONArray.add(rcaJson);
								}
							}
							alarm.addAdditionInfo("rcaJSONArray", rcaJSONArray);
						}
					}
				}
			}
			
		}
		return false;
	}

}
