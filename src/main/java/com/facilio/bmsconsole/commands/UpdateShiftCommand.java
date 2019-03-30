package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;
import java.util.stream.Collectors;

public class UpdateShiftCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ShiftContext shift = (ShiftContext) context.get(FacilioConstants.ContextNames.SHIFT);
		long oldId = shift.getBusinessHoursId();
		
		BusinessHoursList businessHoursList = BusinessHoursAPI.getBusinessHours(oldId);
		
		ShiftAPI.scheduleOneTimeJob(shift, businessHoursList);
		
		ShiftAPI.deleteJobsForshifts(businessHoursList.stream().map(BusinessHourContext::getId).collect(Collectors.toList()));
		
		long id = BusinessHoursAPI.addBusinessHours(shift.getDays());
		shift.setBusinessHoursId(id);
		
		FacilioModule module = ModuleFactory.getShiftModule();
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getShiftField())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(shift.getId(), module))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), module));
		
		Map<String, Object> prop = FieldUtil.getAsProperties(shift);
		builder.update(prop);

		BusinessHoursAPI.deleteBusinessHours(oldId);
		ShiftAPI.scheduleJobs(shift.getDays());
		return false;
	}

}
