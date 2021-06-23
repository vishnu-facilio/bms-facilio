package com.facilio.bmsconsole.commands;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.InviteVisitorRelContext;
import com.facilio.bmsconsole.context.VisitorInviteContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.time.DateTimeUtil;

public class GetVisitorAndInviteForQrScanCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long inviteId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_INVITE_ID);
		Long visitorId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_ID);
		
		if(inviteId > 0 && visitorId > 0) {
			VisitorInviteContext invite = VisitorManagementAPI.getVisitorInvite(inviteId);
			if(invite != null) {
				InviteVisitorRelContext inviteVisitorRel = VisitorManagementAPI.getVisitorInviteRel(inviteId, visitorId);
				long currentTime = System.currentTimeMillis();
				
				if(currentTime < invite.getExpectedStartTime() || currentTime > invite.getExpectedEndTime()) {
					throw new IllegalArgumentException("Invalid invite date range");
				}
				if(invite.isRecurring()) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
					FacilioStatus status = TicketAPI.getStatus(module, "CheckedOut");
					if(status != null && inviteVisitorRel != null && inviteVisitorRel.getModuleState().getId() == status.getId()) {
						throw new IllegalArgumentException("This invite is expired for this visitor as he/she has completed work");
					}
					BusinessHoursContext businessHours = invite.getRecurringVisitTime();
					Date date=new Date(currentTime);
					Calendar calendar = Calendar.getInstance();
			        calendar.setTime(date);
			        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
					int flag = 0;
					for(BusinessHourContext eachDayVisit : businessHours.getSingleDaybusinessHoursList()) {
						if(day_of_week == eachDayVisit.getDayOfWeek()) {
							flag = 1;
							if(currentTime <= DateTimeUtil.getDayStartTime(false) + TimeUnit.NANOSECONDS.toMillis(eachDayVisit.getStartTimeAsLocalTime().toNanoOfDay()) || currentTime >= DateTimeUtil.getDayStartTime(false) + TimeUnit.NANOSECONDS.toMillis(eachDayVisit.getEndTimeAsLocalTime().toNanoOfDay())) {
								throw new IllegalArgumentException("This invite pass is not valid at this time for the visitor");
							}
							break;
						}
					}
					if(flag == 0) {
						throw new IllegalArgumentException("This invite pass is not valid for the day");
					}
				}
				else {
					VisitorLoggingContext visitorLoggingContext = VisitorManagementAPI.getVisitorLogging(visitorId, inviteId);
					if(visitorLoggingContext != null) {
						throw new IllegalArgumentException("This visitor has already checked in for the registered invite");
					}
				
				}
				context.put(FacilioConstants.ContextNames.VISITOR_INVITE_REL, inviteVisitorRel);
				
			}
				
		}
		else {
			throw new IllegalArgumentException("Invalid Invite/visitor ID");
		}
		return false;
	}

}
