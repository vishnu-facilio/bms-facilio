package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EventVisitorRelContext;
import com.facilio.bmsconsole.context.VisitorEventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class VisitorEventsAPI {

	public static List<EventVisitorRelContext> getEventInvitees(long eventId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_EVENT_REL);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_EVENT_REL);
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<EventVisitorRelContext> builder = new SelectRecordsBuilder<EventVisitorRelContext>()
														.module(module)
														.beanClass(EventVisitorRelContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("EVENT_ID", "eventId", String.valueOf(eventId), NumberOperators.EQUALS))
														.fetchLookup((LookupField) fieldsAsMap.get("visitorId"))
														;
		
		
		List<EventVisitorRelContext> records = builder.get();
		return records;
	
	}
	
	public static VisitorEventContext getVisitorEvent(long eventId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_EVENT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VISITOR_EVENT);
		SelectRecordsBuilder<VisitorEventContext> builder = new SelectRecordsBuilder<VisitorEventContext>()
														.module(module)
														.beanClass(VisitorEventContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(eventId), NumberOperators.EQUALS))
														;
		
		
		VisitorEventContext records = builder.fetchFirst();
		return records;
	
	}
}
