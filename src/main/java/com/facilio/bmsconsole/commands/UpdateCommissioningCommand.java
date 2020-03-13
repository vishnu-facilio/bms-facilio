package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdateCommissioningCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		CommissioningLogContext log = (CommissioningLogContext) context.get(ContextNames.LOG);
		log.setSysModifiedTime(System.currentTimeMillis());
		log.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
		log.setAgentId(-1);
		
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getCommissioningLogFields())
				.andCondition(CriteriaAPI.getIdCondition(log.getId(), module));

		Map<String, Object> prop = FieldUtil.getAsProperties(log);
		updateBuilder.update(prop);
		
		return false;
	}

}
