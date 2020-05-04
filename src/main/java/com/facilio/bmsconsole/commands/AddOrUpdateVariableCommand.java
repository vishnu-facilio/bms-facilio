package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.VariableContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateVariableCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		VariableContext variable = (VariableContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (variable != null) {
			variable.setOrgId(AccountUtil.getCurrentOrg().getId());
			variable.setSysModifiedTime(System.currentTimeMillis());
			variable.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
			if (variable.getId() > 0) {
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getVariablesModule().getTableName())
						.fields(FieldFactory.getVariablesFields())
						.andCondition(CriteriaAPI.getIdCondition(variable.getId(), ModuleFactory.getVariablesModule()));
				
				Map<String, Object> props = FieldUtil.getAsProperties(variable);
				updateBuilder.update(props);
			}
			else {
				variable.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
				variable.setSysCreatedTime(System.currentTimeMillis());
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getVariablesModule().getTableName())
						.fields(FieldFactory.getVariablesFields());

				Map<String, Object> props = FieldUtil.getAsProperties(variable);

				insertBuilder.addRecord(props);
				insertBuilder.save();
				variable.setId((Long) props.get("id"));
				
			}
			context.put(FacilioConstants.ContextNames.RECORD,variable);
		}
		
		return false;
	}

}
