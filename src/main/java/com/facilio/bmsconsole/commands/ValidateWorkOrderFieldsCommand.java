package com.facilio.bmsconsole.commands;

import java.util.Calendar;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class ValidateWorkOrderFieldsCommand extends FacilioCommand {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(UpdateWorkOrderCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkOrderContext woContext = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		if (woContext != null) {

			if(woContext.getSubject() == null || woContext.getSubject().isEmpty()) {
				throw new IllegalArgumentException("Subject is invalid");
			}
			else {
				woContext.setSubject(woContext.getSubject().trim());
			}
			
			if(woContext.getDescription() != null && !woContext.getDescription().isEmpty()) {
				woContext.setDescription(woContext.getDescription().trim());
			}

			if(woContext.getDueDate() == 0) {
				Calendar cal = Calendar.getInstance();
				woContext.setDueDate((cal.getTimeInMillis())+TicketContext.DEFAULT_DURATION);
			}
			
			if ( AccountUtil.getCurrentUser() != null) {
				long currentUserId = AccountUtil.getCurrentUser().getOuid();
				try {
					TenantContext tenant = PeopleAPI.getTenantForUser(currentUserId);
					if (tenant != null) {
						woContext.setSiteId(tenant.getSiteId());
						woContext.setTenant(tenant);
					}
				}
				catch(Exception e) {
					// Till people is migrated for all orgs
					log.error("Error occurred in setting tenant", e);
				}
			}
		}
		return false;
	}
}
