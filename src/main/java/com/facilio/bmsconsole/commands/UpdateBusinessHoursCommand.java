package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateBusinessHoursCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		BusinessHoursContext businessHours = (BusinessHoursContext) context
				.get(FacilioConstants.ContextNames.BUSINESS_HOUR);
		if (businessHours != null) {
			GenericUpdateRecordBuilder businessHoursBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getBusinessHoursModule().getTableName())
					.fields(FieldFactory.getBusinessHoursFields()).andCustomWhere("id = ?", businessHours.getId());
			Map<String, Object> props = FieldUtil.getAsProperties(businessHours);
			businessHoursBuilder.update(props);
			context.put(FacilioConstants.ContextNames.ID, businessHours.getId());
		}
		return false;

	}
}
