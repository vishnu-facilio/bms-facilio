package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class DeleteApplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		if (appId > 0) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getApplicationModule().getTableName())
					.select(FieldFactory.getApplicationFields())
					.andCondition(CriteriaAPI.getIdCondition(appId, ModuleFactory.getApplicationModule()));

			List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
					ApplicationContext.class);
			if (applications != null && !applications.isEmpty()) {
				ApplicationContext application = applications.get(0);
				if (application.isDefault()) {
					throw new IllegalArgumentException("Default application cannot be deleted");
				} else {
					GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
							.table(ModuleFactory.getApplicationModule().getTableName())
							.andCondition(CriteriaAPI.getIdCondition(appId, ModuleFactory.getApplicationModule()));
					deleteBuilder.delete();
				}
			}

		}
		return false;
	}

}
