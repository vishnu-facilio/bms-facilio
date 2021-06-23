package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class MarkApplicationAsDefaultCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		if (appId > 0) {

			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getApplicationModule().getTableName())
					.select(FieldFactory.getApplicationFields()).andCondition(CriteriaAPI.getCondition(
							"Application.IS_DEFAULT", "isDefault", String.valueOf(true), BooleanOperators.IS));

			List<ApplicationContext> applications1 = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(),
					ApplicationContext.class);
			if (applications1 != null && !applications1.isEmpty()) {
				ApplicationContext application = applications1.get(0);
				application.setIsDefault(false);
				updateRecord(application);
			}

			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getApplicationModule().getTableName())
					.select(FieldFactory.getApplicationFields())
					.andCondition(CriteriaAPI.getIdCondition(appId, ModuleFactory.getApplicationModule()));

			List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
					ApplicationContext.class);
			if (applications != null && !applications.isEmpty()) {
				ApplicationContext application = applications.get(0);
				application.setIsDefault(true);
				updateRecord(application);
			}
		}
		return false;
	}

	public void updateRecord(ApplicationContext applicationContext) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getApplicationModule().getTableName()).fields(FieldFactory.getApplicationFields())
				.andCondition(
						CriteriaAPI.getIdCondition(applicationContext.getId(), ModuleFactory.getApplicationModule()));
		updateBuilder.update(FieldUtil.getAsProperties(applicationContext));
	}
}
