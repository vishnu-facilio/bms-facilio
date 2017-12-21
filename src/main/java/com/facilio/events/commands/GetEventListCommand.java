package com.facilio.events.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.events.context.EventContext;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class GetEventListCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String cvName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		
		FacilioView view = ViewFactory.getView("event-" + cvName);
		
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
			
			for(Map<String, Object> eventMap : eventList)
			{
				EventContext event = new EventContext();
				BeanUtils.populate(event, eventMap);
				events.add(event);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		context.put(EventConstants.EventContextNames.EVENT_LIST, events);
		return false;
	}
}
