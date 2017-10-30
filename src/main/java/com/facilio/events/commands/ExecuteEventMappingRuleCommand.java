package com.facilio.events.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.context.EventContext;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ExecuteEventMappingRuleCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		boolean ignoreEvent = (Boolean) context.get(EventConstants.IGNORE_EVENT);
		if(!ignoreEvent)
		{
			Map<String, Object> propsMap = (Map<String, Object>) context.get(EventConstants.EVENT_PROPERTY);
			if((Boolean) propsMap.get("hasMappingRule"))
			{
				EventContext event = (EventContext) context.get(EventConstants.EVENT);
				Map<String, Object> ruleprops = null;
				try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
				{
					GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
															.connection(conn)
															.select(EventConstants.getEventMappingRuleFields())
															.table("Event_Mapping_Rule")
															.andCustomWhere("ORGID = ?", event.getOrgId());	//Org Id
					
					List<Map<String, Object>> rulepropslist = builder.get();
					ruleprops = rulepropslist.get(0);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					throw e;
				}
				Map<String, Object> props = FieldUtil.getAsProperties(event);
				int mappingType = (Integer) ruleprops.get("mappingType");
				if(mappingType == 1)
				{
					if(props.containsKey((String)ruleprops.get("fromField")))
					{
						String value = (String) props.get((String)ruleprops.get("fromField"));
						
						JSONParser parser = new JSONParser();
						JSONObject mappingPairs = (JSONObject) parser.parse((String) ruleprops.get("mappingPairs"));
						if(mappingPairs.containsKey(value))
						{
							props.put((String)ruleprops.get("toField"), mappingPairs.get(value));
						}
					}
				}
				else if(mappingType == 2)
				{
					props.put((String)ruleprops.get("toField"), ruleprops.get("constantValue"));
				}
				ObjectMapper mapper = new ObjectMapper();
				mapper.setSerializationInclusion(Include.NON_DEFAULT);
				mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
				
				event = mapper.readValue(props.toString(), EventContext.class);
			}
		}
		return false;
	}
}
