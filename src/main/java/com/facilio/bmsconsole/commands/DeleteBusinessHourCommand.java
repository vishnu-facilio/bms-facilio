package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteBusinessHourCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		if (id != -1) {
			FacilioModule businessHoursTable = ModuleFactory.getBusinessHoursModule();
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(businessHoursTable.getTableName())
					.andCondition(CriteriaAPI.getIdCondition(id, businessHoursTable)).andCondition(
							CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), businessHoursTable));
			builder.delete();
		}
		return false;
	}

}
