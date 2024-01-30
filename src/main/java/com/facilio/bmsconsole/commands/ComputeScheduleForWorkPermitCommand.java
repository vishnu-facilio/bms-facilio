package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;

public class ComputeScheduleForWorkPermitCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkPermitContext> workPermitList = (List<WorkPermitContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		EventType eventType = (EventType)context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		
	
		if(CollectionUtils.isNotEmpty(workPermitList)) {
			for(WorkPermitContext permit : workPermitList) {
				if(permit.getTicket() != null && permit.getTicket().getId() > 0) {
					Map<Long, TicketContext> ticket = TicketAPI.getTickets(String.valueOf(permit.getTicket().getId()));
					if(MapUtils.isNotEmpty(ticket)) {
						permit.setSiteId(ticket.get(permit.getTicket().getId()).getSiteId());
						if(ticket.get(permit.getTicket().getId()).getResource() != null) {
							ResourceContext resource = ResourceAPI.getResource(ticket.get(permit.getTicket().getId()).getResource().getId());
							if(resource != null) {
								BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(resource.getSpaceId());
								permit.setSpace(baseSpace);
							}
						}
					}
				}
				if(eventType == EventType.CREATE) {
					if(permit.getRequestedBy() == null) {
						permit.setRequestedBy(AccountUtil.getCurrentUser());
					}
				}
				if(permit.getRequestedBy() != null && permit.getRequestedBy().getId() > 0) {

						TenantContext tc = PeopleAPI.getTenantForUser(permit.getRequestedBy().getId());
						if(tc != null){
							permit.setTenant(tc);
						}

				}
				BusinessHoursContext visitTime = permit.getRecurringInfo();
				if(permit.isRecurring() && visitTime != null) {
					if(visitTime.getId() > 0) {
						BusinessHoursAPI.updateBusinessHours(visitTime);
						BusinessHoursAPI.deleteSingleBusinessHour(visitTime.getId());
						BusinessHoursAPI.addSingleBusinessHours(visitTime);
					}
					else {
						long id = BusinessHoursAPI.addBusinessHours(permit.getRecurringInfo());
						permit.setRecurringInfoId(id);
					}
				}
			}
		}
		return false;
	}

}
