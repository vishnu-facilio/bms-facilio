package com.facilio.events.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventProperty;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateEventPropertyCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EventProperty eventProperty = (EventProperty) context.get(EventConstants.EventContextNames.EVENT_PROPERTY);
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_DEFAULT);
		Map<String, Object> eventProperties = mapper.convertValue(eventProperty, Map.class);
		System.out.println(eventProperties);
		
		List<FacilioField> fields = EventConstants.EventFieldFactory.getEventPropertyFields();
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
												.connection(((FacilioContext)context).getConnectionWithTransaction())
												.table("Event_Property")
												.fields(fields)
												.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
		builder.update(eventProperties);
		
		return false;
	}
}
