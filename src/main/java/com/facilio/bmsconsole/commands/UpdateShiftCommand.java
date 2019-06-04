package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;
import java.util.stream.Collectors;

public class UpdateShiftCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ShiftContext shift = (ShiftContext) context.get(FacilioConstants.ContextNames.SHIFT);
//		long oldId = shift.getBusinessHoursId();
		
//		BusinessHoursList businessHoursList = BusinessHoursAPI.getBusinessHours(oldId);
		
//		ShiftAPI.scheduleOneTimeJob(shift, businessHoursList);
		
//		ShiftAPI.deleteJobsForshifts(businessHoursList.stream().map(BusinessHourContext::getId).collect(Collectors.toList()));
		
//		long id = BusinessHoursAPI.addBusinessHours(shift.getDays());
//		shift.setBusinessHoursId(id);
		
		ShiftAPI.updateShift(shift);
		
		

//		BusinessHoursAPI.deleteBusinessHours(oldId);
//		ShiftAPI.scheduleJobs(shift.getDays());
		return false;
	}

}
