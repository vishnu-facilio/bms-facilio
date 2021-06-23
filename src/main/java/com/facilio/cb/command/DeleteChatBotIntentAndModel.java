package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DeleteChatBotIntentAndModel extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		delete(ModuleFactory.getCBIntentModule());
		delete(ModuleFactory.getCBModelVersionModule());
		delete(ModuleFactory.getCBModelModule());
		
		return false;
	}
	
	
	private int delete(FacilioModule module) throws Exception {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(module.getTableName())
		.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module));
		
		return deleteRecordBuilder.delete();
	}
}
