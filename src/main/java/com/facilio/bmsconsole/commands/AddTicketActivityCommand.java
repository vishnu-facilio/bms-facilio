package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.UserInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddTicketActivityCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		TicketContext ticket = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		//TicketContext ticket = workOrder.getTicket();
		if(ticket != null) 
		{
			Long ticketId = ticket.getId();
			if(ticketId == -1)
			{
				List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
				List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
				SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
						.table("WorkOrders")
						.moduleName("workorder")
						.beanClass(WorkOrderContext.class)
						.select(fields)
						.andCustomWhere("ID = ?", recordIds.get(0))
						.orderBy("ID");

				List<WorkOrderContext> workorders = builder.get();
				ticketId = workorders.get(0).getId();
			}
			ActivityType activityType = (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
			
			Map<String, Object> map = new HashMap<>();
			map.put("ticketId", ticketId);
			map.put("modifiedTime", System.currentTimeMillis());
			map.put("modifiedBy", UserInfo.getCurrentUser().getOrgId());
			map.put("activityType", activityType.getValue());
			
			JSONObject fieldJson = new JSONObject();
			Map<String, Object> moduleProps = FieldUtil.getAsProperties(ticket);
			Iterator<String> props = moduleProps.keySet().iterator();
			while(props.hasNext())
			{
				String prop = props.next();
				fieldJson.put(prop, moduleProps.get(prop));
			}
			
			JSONArray updatedFields = new JSONArray();
			updatedFields.add(fieldJson);
			
			JSONObject info = new JSONObject();
			info.put("updatedFields", updatedFields);
			
			map.put("info", info.toString());
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table("Ticket_Activity")
					.fields(FieldFactory.getTicketActivityFields())
					.addRecord(map);
			
			//insertBuilder.save();
		}
		else 
		{
			throw new IllegalArgumentException("Ticket Object cannot be null");
		}
		return false;
	}
}
