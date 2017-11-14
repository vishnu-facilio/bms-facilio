package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateEventTransformRuleCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
//		// TODO Auto-generated method stub
//		
//		EventRule eventRule = (EventRule) context.get(EventConstants.EVENT_RULE);
//		eventRule.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
//		
//		if(eventRule.hasCustomizeRule())
//		{
//			Criteria criteria = new Criteria();
//			criteria.setPattern((String) context.get(EventConstants.CUSTOMIZE_CRITERIA_PATTERN));
//			criteria.setConditions((Map<Integer, Condition>) context.get(EventConstants.CUSTOMIZE_CONDITIONS));
//			
//			long criteriaId = CriteriaAPI.addCriteria(criteria, OrgInfo.getCurrentOrgInfo().getOrgid());
//			eventRule.setCustomizeCriteriaId(criteriaId);
//		}
//		else
//		{
//			eventRule.setCustomizeCriteriaId(-1);
//		}
//		
//		JSONObject json = new JSONObject();
//		json.put("orgId", "$(organizations.orgId)");
//		json.put("source", "$(alarm.source)");
//		json.put("node", "$(alarm.node)");
//		json.put("subject", "$(alarm.subject)");
//		json.put("description", "$(alarm.description)");
//		Map<String, String> templateMap = (Map<String, String>) context.get(EventConstants.CUSTOMIZE_ALARM_TEMPLATE);
//		for(Map.Entry<String, String> template : templateMap.entrySet()) 
//		{
//			String key = template.getKey();
//			String value = template.getValue();
//			json.put(key, value);
//		}
//		JSONObject content = new JSONObject();
//		content.put("alarm", json);
//		
//		Map<String, String> headers = new HashMap<>();
//		headers.put("Content-Type","application/json");
//		String server = AwsUtil.getConfig("servername");
//		String url = "http://" + server + "/internal/addAlarmTemplate";
//        String response = AwsUtil.doHttpPost(url, headers, null, content.toString());
//        
//        JSONObject resultObj = (JSONObject) new JSONParser().parse(response);
//        long alarmTemplateId = Long.parseLong((String) resultObj.get("alarmTemplateId"));
//        eventRule.setAlarmTemplateId(alarmTemplateId);
//		
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.setSerializationInclusion(Include.NON_DEFAULT);
//		Map<String, Object> eventRules = mapper.convertValue(eventRule, Map.class);
//		System.out.println(eventRules);
//		
//		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
//		{
//			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
//													.connection(conn)
//													.select(EventConstants.getEventRuleFields())
//													.table("Event_Rule")
//													.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());	//Org Id
//			
//			List<Map<String, Object>> rulepropslist = builder.get();
//			if(rulepropslist.isEmpty())
//			{
//				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
//						.table("Event_Rule")
//						.fields(EventConstants.getEventRuleFields())
//						.addRecord(eventRules);
//
//				insertBuilder.save();
//			}
//			else
//			{
//				List<FacilioField> fields = EventConstants.getEventRuleFields();
//				GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
//														.connection(conn)
//														.table("Event_Rule")
//														.fields(fields)
//														.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
//				updatebuilder.update(eventRules);
//			}
//		} 
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//			throw e;
//		}
		
		return false;
	}
}
