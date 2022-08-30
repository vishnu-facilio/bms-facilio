package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddNewPermissionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long tabId = (long) context.get(FacilioConstants.ContextNames.WEB_TAB_ID);
		if (tabId > 0) {
			List<NewPermission> newPermissions = (List<NewPermission>) context
					.get(FacilioConstants.ContextNames.NEW_PERMISSIONS);
			if (newPermissions != null) {
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getNewPermissionFields());
				GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
						.table(ModuleFactory.getNewPermissionModule().getTableName()).andCondition(CriteriaAPI
								.getCondition(fieldMap.get("tabId"), String.valueOf(tabId), NumberOperators.EQUALS));
				deleteBuilder.delete();
				if (newPermissions.size() > 0) {
					for (NewPermission newPermission : newPermissions) {
						if (newPermission.getRoleId() > 0) {
							newPermission.setTabId(tabId);
							GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
									.table(ModuleFactory.getNewPermissionModule().getTableName())
									.fields(FieldFactory.getNewPermissionFields());
							builder.insert(FieldUtil.getAsProperties(newPermission));
						}
					}
				}
			}
		}
		return false;
	}

}
