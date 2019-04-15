package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.sql.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateEventResourcesMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String source = (String) context.get(EventConstants.EventContextNames.SOURCE);
		long resourceId = (long) context.get(EventConstants.EventContextNames.RESOURCE_ID);
		EventContext event = new EventContext();
		event.setResourceId(resourceId);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(EventConstants.EventFieldFactory.getEventFields())
														.table(EventConstants.EventModuleFactory.getEventModule().getTableName())
														.andCustomWhere("ORGID = ? AND SOURCE = ?", AccountUtil.getCurrentOrg().getOrgId(), source);
		updateBuilder.update(FieldUtil.getAsProperties(event));
		return false;
	}

}
