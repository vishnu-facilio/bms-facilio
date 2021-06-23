package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetTabPermissionsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long tabId = (long) context.get(FacilioConstants.ContextNames.WEB_TAB_ID);
		if(tabId>0) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getNewPermissionModule().getTableName())
                    .select(FieldFactory.getNewPermissionFields())
                    .andCondition(CriteriaAPI.getCondition("TAB_ID", "tabId", String.valueOf(tabId), NumberOperators.EQUALS));
            List<NewPermission> permissions = FieldUtil.getAsBeanListFromMapList(builder.get(), NewPermission.class);
            context.put(FacilioConstants.ContextNames.PERMISSIONS, permissions);
		}
		
		return false;
	}

}
