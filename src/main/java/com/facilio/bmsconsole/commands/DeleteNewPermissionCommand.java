package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.modules.ModuleFactory;

public class DeleteNewPermissionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Long> permissionIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (permissionIds != null && permissionIds.size() > 0) {
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
			.table(ModuleFactory.getNewPermissionModule().getTableName());
			deleteBuilder.batchDeleteById(permissionIds);
		}
		return false;
	}

}
