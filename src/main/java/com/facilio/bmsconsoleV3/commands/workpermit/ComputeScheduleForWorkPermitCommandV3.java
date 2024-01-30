package com.facilio.bmsconsoleV3.commands.workpermit;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.workpermit.V3WorkPermitContext;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class ComputeScheduleForWorkPermitCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkPermitContext> workPermitList = recordMap.get(moduleName);

        EventType eventType = (EventType)context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);


        if(CollectionUtils.isNotEmpty(workPermitList)) {
            for(V3WorkPermitContext permit : workPermitList) {
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
                        V3TenantContext tenant = V3PeopleAPI.getTenantForUser(permit.getRequestedBy().getId());
                        if(tenant != null) {
                            permit.setTenant(tenant);
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
