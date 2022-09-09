package com.facilio.events.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FieldUtil;

public class GetEventListCommand extends FacilioCommand {

	private static Logger log = LogManager.getLogger(GetEventListCommand.class.getName());

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String cvName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		
		FacilioView view = ViewFactory.getView("event", cvName);
		
		long alarmId = (long) context.get(EventConstants.EventContextNames.ALARM_ID);
		
		List<BaseEventContext> events = new ArrayList<>();
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule baseEventModule = modBean.getModule(FacilioConstants.ContextNames.BASE_EVENT);
			ArrayList<FacilioField> fields = new ArrayList<>();
			fields.add(modBean.getField("resource", baseEventModule.getName()));
			fields.add(modBean.getField("severity", baseEventModule.getName()));
			fields.add(modBean.getField("eventMessage", baseEventModule.getName()));
			fields.add(modBean.getField("createdTime", baseEventModule.getName()));

			SelectRecordsBuilder<BaseEventContext> selectBuilder = new SelectRecordsBuilder<BaseEventContext>()
					.module(baseEventModule)
					.beanClass(BaseEventContext.class)
					.select(fields);

			if(alarmId != -1)
			{
				selectBuilder.andCondition(CriteriaAPI.getCondition("BaseEvent.ALARM_ID","baseAlarm",String.valueOf(alarmId), NumberOperators.EQUALS));
			}

			if (view != null) {
				Criteria criteria = view.getCriteria();
				selectBuilder.andCriteria(criteria);
			}
			
			if (pagination != null) {
				selectBuilder.offset((int) pagination.get("offset"));
				selectBuilder.limit((int) pagination.get("limit"));
			}
	
			List<BaseEventContext> eventList = selectBuilder.get();
			List<Long> resourceIds = new ArrayList<>();
			List<Long> severityIds = new ArrayList<>();
			for(BaseEventContext event : eventList){
				events.add(event);
				if (event.getResource().getId() != -1) {
					resourceIds.add(event.getResource().getId());
				}
				if (event.getSeverity().getId() != -1) {
					severityIds.add(event.getSeverity().getId());
				}
			}
			
			Map<Long, ResourceContext> resources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true);
			Map<Long, AlarmSeverityContext> severity = AlarmAPI.getAlarmSeverityMap(severityIds);
			
			for (BaseEventContext event : events) {
				if (event.getResource().getId() != -1) {
					event.setResource(resources.get(event.getResource().getId()));
				}
				if (event.getSeverity().getId() != -1) {
					event.setSeverity(severity.get(event.getSeverity().getId()));
				}
				event.setCreatedTime(event.getCreatedTime());
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
