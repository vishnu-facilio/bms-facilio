package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateEventAssetsMappingCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String node = (String) context.get(EventConstants.EventContextNames.NODE);
		long assetId = (long) context.get(EventConstants.EventContextNames.ASSET_ID);
		EventContext event = new EventContext();
		event.setAssetId(assetId);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(EventConstants.EventFieldFactory.getEventFields())
														.table(EventConstants.EventModuleFactory.getEventModule().getTableName())
														.andCustomWhere("ORGID = ? AND NODE = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), node);
		updateBuilder.update(FieldUtil.getAsProperties(event));
		return false;
	}

}
