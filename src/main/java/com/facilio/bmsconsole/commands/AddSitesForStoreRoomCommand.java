package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;

public class AddSitesForStoreRoomCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<Long> sites = (List<Long>) context.get(FacilioConstants.ContextNames.SITES_FOR_STORE_ROOM);
		long storeRoomId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (storeRoomId > 0 && sites != null && !sites.isEmpty()) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getSitesForStoreRoomModule().getTableName())
					.fields(FieldFactory.getSitesForStoreRoomFields());

			List<Map<String, Object>> propsList = new ArrayList<>();
			for (Long siteId : sites) {
				Map<String, Object> props = new HashMap<>();
				props.put("storeRoomId", storeRoomId);
				props.put("siteId", siteId);
				propsList.add(props);
			}
			insertBuilder.addRecords(propsList);
			insertBuilder.save();
		}
		return false;
	}

}
