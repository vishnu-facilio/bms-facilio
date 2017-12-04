package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.TicketActivity;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetTicketActivitesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long ticketId = (long) context.get(FacilioConstants.ContextNames.TICKET_ID);
		if(ticketId != -1) {
			FacilioModule module = ModuleFactory.getTicketActivityModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getTicketActivityFields())
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCustomWhere("TICKET_ID = ?", ticketId)
															.orderBy("MODIFIED_TIME desc");
			
			List<Map<String, Object>> activityProps = selectBuilder.get();
			
			if(activityProps != null && !activityProps.isEmpty()) {
				List<TicketActivity> activities = new ArrayList<>();
				for(Map<String, Object> prop : activityProps) {
					activities.add(FieldUtil.getAsBeanFromMap(prop, TicketActivity.class));
				}
				context.put(FacilioConstants.TicketActivity.TICKET_ACTIVITIES, activities);
			}
															
		}
		return false;
	}
	
}
