package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddBusinessHourCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		BusinessHoursContext businessHours = (BusinessHoursContext) context
				.get(FacilioConstants.ContextNames.BUSINESS_HOUR);
		if (businessHours != null) {
			Map<String, Object> props = FieldUtil.getAsProperties(businessHours);
			props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
//			props.put("name", businessHours.getName());
			GenericInsertRecordBuilder businessHoursBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getBusinessHoursModule().getTableName())
					.fields(FieldFactory.getBusinessHoursFields()).addRecord(props);
			businessHoursBuilder.save();
			long id = (long) props.get("id");
			context.put(FacilioConstants.ContextNames.ID, id);
        }
		return false;
	}
}