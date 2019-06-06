package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.Map;

public class AddOfflineSyncErrorCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long lastSyncTime = (Long) context.get(FacilioConstants.ContextNames.LAST_SYNC_TIME);
		Map<Long, Map<String, Object>> errors = (Map<Long, Map<String, Object>>) context.get(FacilioConstants.ContextNames.CUSTOM_OBJECT);
		
		Map<String, Object> props = new HashMap<>();
		props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		props.put("moduleName", moduleName);
		props.put("syncedBy", AccountUtil.getCurrentUser().getOuid());
		props.put("createdTime", System.currentTimeMillis());
		props.put("lastSyncTime", lastSyncTime);
		props.put("errorInfo", errors);
		
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
															.fields(FieldFactory.getOfflineSyncErrorFields())
															.table(ModuleFactory.getOfflineSyncErrorModule().getTableName())
															.addRecord(props);
															;
		insertRecordBuilder.save();
		
		
		return false;
	}

}
