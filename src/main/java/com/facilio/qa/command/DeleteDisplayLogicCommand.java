package com.facilio.qa.command;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;

public class DeleteDisplayLogicCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		DisplayLogicContext displaylogic = (DisplayLogicContext) context.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT);
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getQAndADisplayLogicModule().getTableName())
		.andCondition(CriteriaAPI.getIdCondition(displaylogic.getId(), ModuleFactory.getQAndADisplayLogicModule()));
		
		deleteRecordBuilder.delete();
		
		return false;
	}

}
