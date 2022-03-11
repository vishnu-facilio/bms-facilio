package com.facilio.qa.command;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.qa.displaylogic.context.DisplayLogicAction;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.context.DisplayLogicTriggerQuestions;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;

public class AddDisplayLogicDependentsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		DisplayLogicContext displaylogic = (DisplayLogicContext) context.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getQAndADisplayLogicActionModule().getTableName())
				.fields(FieldFactory.getQAndADisplayLogicActionFields());
		
		for(DisplayLogicAction action : displaylogic.getActions()) {
			action.setDisplayLogicId(displaylogic.getId());
			insertBuilder.addRecord(FieldUtil.getAsProperties(action));
		}
		
		insertBuilder.save();
		
		if(displaylogic.getTriggerQuestions() != null) {
			
			insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getQAndADisplayLogicTriggerQuestionModule().getTableName())
					.fields(FieldFactory.getQAndADisplayLogicTriggerQuestionFields());
			
			for(DisplayLogicTriggerQuestions triggerQuestions : displaylogic.getTriggerQuestions()) {
				triggerQuestions.setDisplayLogicId(displaylogic.getId());
				insertBuilder.addRecord(FieldUtil.getAsProperties(triggerQuestions));
			}
			
			insertBuilder.save();
		}
		
		return false;
	}

}
