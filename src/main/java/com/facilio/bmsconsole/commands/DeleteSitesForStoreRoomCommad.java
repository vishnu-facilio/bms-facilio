package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.modules.ModuleFactory;

public class DeleteSitesForStoreRoomCommad extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (storeRoomId > 0){
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getSitesForStoreRoomModule().getTableName())
				.andCustomWhere("STORE_ROOM_ID = ?", storeRoomId);
		builder.delete();
		context.put(FacilioConstants.ContextNames.RECORD_ID, storeRoomId);
		}
		return false;
	}

}
