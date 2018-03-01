package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateEventResourcesMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String node = (String) context.get(EventConstants.EventContextNames.NODE);
		long resourceId = (long) context.get(EventConstants.EventContextNames.RESOURCE_ID);
		EventContext event = new EventContext();
		event.setResourceId(resourceId);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(EventConstants.EventFieldFactory.getEventFields())
														.table(EventConstants.EventModuleFactory.getEventModule().getTableName())
														.andCustomWhere("ORGID = ? AND NODE = ?", AccountUtil.getCurrentOrg().getOrgId(), node);
		updateBuilder.update(FieldUtil.getAsProperties(event));
		return false;
	}

}
