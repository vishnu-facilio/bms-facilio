package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteSitesForStoreRoomCommad implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
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
