package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.sql.GenericSelectRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetEventListCommand implements Command {

	private static Logger log = LogManager.getLogger(GetEventListCommand.class.getName());

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String cvName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		
		FacilioView view = ViewFactory.getView("event", cvName);
		
		long alarmId = (long) context.get(EventConstants.EventContextNames.ALARM_ID);
		
		List<EventContext> events = new ArrayList<>();
		try {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
					.select(EventConstants.EventFieldFactory.getEventFields())
					.table("Event")
					.andCustomWhere("Event.ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.orderBy("CREATED_TIME desc");
			
			if(alarmId != -1)
			{
				selectBuider.andCustomWhere("Event.ALARM_ID = ?", alarmId);
			}
			if (view != null) {
				Criteria criteria = view.getCriteria();
				selectBuider.andCriteria(criteria);
			}
			
			if (pagination != null) {
				selectBuider.offset((int) pagination.get("offset"));
				selectBuider.limit((int) pagination.get("limit"));
			}
	
			List<Map<String, Object>> eventList = selectBuider.get();
			List<Long> resourceIds = new ArrayList<>();
			for(Map<String, Object> eventMap : eventList)
			{
				EventContext event = FieldUtil.getAsBeanFromMap(eventMap, EventContext.class);
				events.add(event);
				
				if (event.getResourceId() != -1) {
					resourceIds.add(event.getResourceId());
				}
			}
			
			Map<Long, ResourceContext> resources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true);
			
			for (EventContext event : events) {
				if (event.getResourceId() != -1) {
					event.setResource(resources.get(event.getResourceId()));
				}
			}
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		context.put(EventConstants.EventContextNames.EVENT_LIST, events);
		return false;
	}
}
