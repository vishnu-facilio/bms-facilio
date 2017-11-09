package com.facilio.events.commands;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.events.context.EventThresholdRule;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.parser.JSONParser;

public class UpdateEventThresholdRulesCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EventRule eventRule = (EventRule) context.get(EventConstants.EVENT_RULE);
		eventRule.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_DEFAULT);
		Map<String, Object> eventRules = mapper.convertValue(eventRule, Map.class);
		System.out.println(eventRules);
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
		{
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.connection(conn)
													.select(EventConstants.getEventRuleFields())
													.table("Event_Rule")
													.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());	//Org Id
			
			List<Map<String, Object>> rulepropslist = builder.get();
			if(rulepropslist.isEmpty())
			{
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table("Event_Rule")
						.fields(EventConstants.getEventRuleFields())
						.addRecord(eventRules);

				insertBuilder.save();
			}
			else
			{
				List<FacilioField> fields = EventConstants.getEventRuleFields();
				GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
														.connection(conn)
														.table("Event_Rule")
														.fields(fields)
														.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
				updatebuilder.update(eventRules);
			}
			if(eventRule.getHasThresholdRule() != null && eventRule.getHasThresholdRule())
			{
				List<EventThresholdRule> eventThresholdRules = (List<EventThresholdRule>) context.get(EventConstants.EVENT_THRESHOLD_RULE_LIST);
				
				for(EventThresholdRule eventThresholdRule : eventThresholdRules)
				{
					if(eventThresholdRule.getHasFilterCriteria())
					{
						Criteria filterCriteria = new Criteria();
						filterCriteria.setPattern(eventThresholdRule.getFilterPattern());
						filterCriteria.setConditions(eventThresholdRule.getFilterConditions());
						
						long filterCriteriaId = CriteriaAPI.addCriteria(filterCriteria, OrgInfo.getCurrentOrgInfo().getOrgid());
						eventThresholdRule.setFilterCriteriaId(filterCriteriaId);
					}
					if(eventThresholdRule.getHasClearCriteria())
					{
						Criteria clearCriteria = new Criteria();
						clearCriteria.setPattern(eventThresholdRule.getClearPattern());
						clearCriteria.setConditions(eventThresholdRule.getClearConditions());
						
						long clearCriteriaId = CriteriaAPI.addCriteria(clearCriteria, OrgInfo.getCurrentOrgInfo().getOrgid());
						eventThresholdRule.setClearCriteriaId(clearCriteriaId);
					}
					Map<String, Object> eventThresholdRuleMap = mapper.convertValue(eventRule, Map.class);
					System.out.println(eventThresholdRuleMap);
					
					GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
							.table("Event_Threshold_Rule")
							.fields(EventConstants.getEventThresholdRuleFields())
							.addRecord(eventThresholdRuleMap);

					insertBuilder.save();
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw e;
		}
		
		return false;
	}
}
