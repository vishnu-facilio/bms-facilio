package com.facilio.bmsconsole.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

public class GetVisitorAndInviteForQrScanCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long inviteId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_INVITE_ID);
		Long visitorId = (Long)context.get(FacilioConstants.ContextNames.VISITOR_ID);
		
		if(inviteId > 0 && visitorId > 0) {
			VisitorInviteContext invite = VisitorManagementAPI.getVisitorInvite(inviteId);
			if(invite != null) {
				InviteVisitorRelContext inviteVisitorRel = VisitorManagementAPI.getVisitorInviteRel(inviteId, visitorId);
				
				if(invite.isRecurring()) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VISITOR_INVITE_REL);
					FacilioStatus status = TicketAPI.getStatus(module, "CheckedOut");
					if(status != null && inviteVisitorRel != null && inviteVisitorRel.getModuleState().getId() == status.getId()) {
						throw new IllegalArgumentException("This invite is expired for this visitor as he/she has completed work");
					}
					BusinessHoursContext businessHours = invite.getRecurringVisitTime();
					long currentTime = System.currentTimeMillis();
					Date date=new Date(currentTime);
					String format = "MM-dd-yyyy";
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
					String currentDate = simpleDateFormat.format(date);
					for(BusinessHourContext eachDayVisit : businessHours.getSingleDaybusinessHoursList()) {
						if(simpleDateFormat.format(new Date(eachDayVisit.getDateOfTheDay())).equals(currentDate)) {
							if(currentTime <= eachDayVisit.getDateOfTheDay() + TimeUnit.NANOSECONDS.toMillis(eachDayVisit.getStartTimeAsLocalTime().toNanoOfDay()) || currentTime >= eachDayVisit.getDateOfTheDay() + TimeUnit.NANOSECONDS.toMillis(eachDayVisit.getEndTimeAsLocalTime().toNanoOfDay())) {
								throw new IllegalArgumentException("This invite is expired for this visitor");
							}
							break;
						}
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
