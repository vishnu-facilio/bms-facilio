package com.facilio.events.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
import com.facilio.events.context.EventContext;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ExecuteEventRuleCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		
		Map<String, Object> propsMap = (Map<String, Object>) context.get(EventConstants.EVENT_PROPERTY);
		boolean ignoreEvent = false;
		if(propsMap.get("hasEventRule") != null && (Boolean) propsMap.get("hasEventRule"))
		{
			EventContext event = (EventContext) context.get(EventConstants.EVENT);
			Map<String, Object> ruleprops = null;
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
			{
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(EventConstants.getEventRuleFields())
														.table("Event_Rule")
														.andCustomWhere("ORGID = ?", event.getOrgId());	//Org Id
				
				List<Map<String, Object>> rulepropslist = builder.get();
				ruleprops = rulepropslist.get(0);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				throw e;
			}
			
			if(ruleprops.get("hasEventFilter") != null && (Boolean) ruleprops.get("hasEventFilter"))
			{
				try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection())
				{
					Criteria criteria = CriteriaAPI.getCriteria(event.getOrgId(), (long) ruleprops.get("filterCriteriaId") ,conn);
					ignoreEvent = criteria.computePredicate().evaluate(event);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					throw e;
				}
			}
			if(!ignoreEvent)
			{
				if(ruleprops.get("hasCustomizeRule") != null && (Boolean) ruleprops.get("hasCustomizeRule"))
				{
					try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection())
					{
						Criteria criteria = CriteriaAPI.getCriteria(event.getOrgId(), (long) ruleprops.get("customizeCriteriaId") ,conn);
						if(criteria.computePredicate().evaluate(event))
						{
							AlarmTemplate template = (AlarmTemplate) TemplateAPI.getTemplate(event.getOrgId(), (Long) ruleprops.get("alarmTemplateId"));
							JSONParser parser = new JSONParser();
							
							Map<String, Object> props = FieldUtil.getAsProperties(event);
							JSONObject content = (JSONObject) parser.parse((String) template.getTemplate(props).get("content"));
							
							event = FieldUtil.getAsBeanFromJson(content, EventContext.class);
							context.put(EventConstants.EVENT, event);
						}
					}
					catch (Exception e) 
					{
						e.printStackTrace();
						throw e;
					}
				}
				if(ruleprops.get("hasThresholdRule") != null && (Boolean) ruleprops.get("hasThresholdRule"))
				{
					Criteria criteria = new Criteria();
					ignoreEvent = criteria.computePredicate().evaluate(event);
				}
			}
		}
		context.put(EventConstants.IGNORE_EVENT, ignoreEvent);
		return false;
	}
}
