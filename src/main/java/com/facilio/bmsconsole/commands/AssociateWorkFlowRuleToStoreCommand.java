package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AssociateWorkFlowRuleToStoreCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long storeRoomId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		Long ruleId = (Long)context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID);
		
		List<FacilioField> fields = FieldFactory.getStoreNotificationModuleFields();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getStoreNotificationConfigModule().getTableName())
				.fields(fields);

		Map<String, Object> props = new HashMap<>();
		props.put("storeRoomId", storeRoomId);
		props.put("workflowRuleId", ruleId);
	
		insertBuilder.addRecord(props);
		insertBuilder.save();

		return false;
	}

}
