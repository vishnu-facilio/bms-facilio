package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateApplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ApplicationContext application = (ApplicationContext) context.get(FacilioConstants.ContextNames.APPLICATION);
		if (application != null) {
			application.setIsDefault(false);
			if (application.getId() > 0) {
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getApplicationModule().getTableName())
						.fields(FieldFactory.getApplicationFields()).andCondition(
								CriteriaAPI.getIdCondition(application.getId(), ModuleFactory.getApplicationModule()));
				builder.update(FieldUtil.getAsProperties(application));
			} else {
				GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getApplicationModule().getTableName())
						.fields(FieldFactory.getApplicationFields());
				builder.insert(FieldUtil.getAsProperties(application));
			}

		}
		return false;
	}

}
