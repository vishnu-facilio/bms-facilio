package com.facilio.qa.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.qa.displaylogic.context.DisplayLogicContext;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;

public class AddOrUpdateDisplayLogicCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		DisplayLogicContext displaylogic = (DisplayLogicContext) context.get(DisplayLogicUtil.DISPLAY_LOGIC_CONTEXT);
		
		if(displaylogic.getId() != null) {
			
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getQAndADisplayLogicTriggerQuestionModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicTriggerQuestionFields(), "displayLogicId"), displaylogic.getId()+"", NumberOperators.EQUALS));
			
			deleteRecordBuilder.delete();
			
			deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getQAndADisplayLogicActionModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getQAndADisplayLogicActionFields(), "displayLogicId"), displaylogic.getId()+"", NumberOperators.EQUALS));
			
			deleteRecordBuilder.delete();
			
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getQAndADisplayLogicModule().getTableName())
					.fields(FieldFactory.getQAndADisplayLogicFields())
					.andCondition(CriteriaAPI.getIdCondition(displaylogic.getId(), ModuleFactory.getQAndADisplayLogicModule()));

			Map<String, Object> props = FieldUtil.getAsProperties(displaylogic);
			updateBuilder.update(props);
			
		}
		else {
			displaylogic.setStatus(Boolean.TRUE);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getQAndADisplayLogicModule().getTableName())
					.fields(FieldFactory.getQAndADisplayLogicFields());
			
			Map<String, Object> props = FieldUtil.getAsProperties(displaylogic);
			insertBuilder.addRecord(props);
			insertBuilder.save();
			
			displaylogic.setId((Long) props.get("id"));
		}
		return false;
	}

}
