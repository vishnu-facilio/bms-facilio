package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DeleteBusinessHourCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
