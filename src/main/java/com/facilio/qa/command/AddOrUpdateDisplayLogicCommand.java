package com.facilio.qa.command;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.util.V3Util;
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

			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getQAndADisplayLogicModule().getTableName())
					.select(FieldFactory.getQAndADisplayLogicFields())
					.andCondition(CriteriaAPI.getIdCondition(displaylogic.getId(), ModuleFactory.getQAndADisplayLogicModule()));
			List<Map<String, Object>> oldRecordMap = selectBuilder.get();
			DisplayLogicContext oldRecord = FieldUtil.getAsBeanFromMap(oldRecordMap.get(0), DisplayLogicContext.class);

			if(displaylogic.getCriteria()== null && displaylogic.getCriteriaId()==null){
				displaylogic.setCriteriaId((long) -99);
			}

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getQAndADisplayLogicModule().getTableName())
					.fields(FieldFactory.getQAndADisplayLogicFields())
					.andCondition(CriteriaAPI.getIdCondition(displaylogic.getId(), ModuleFactory.getQAndADisplayLogicModule()));
			Map<String, Object> props = FieldUtil.getAsProperties(displaylogic);
			updateBuilder.update(props);

			if (oldRecord != null && oldRecord.getCriteriaId() != null) {
				deleteRecordBuilder = new GenericDeleteRecordBuilder();
				deleteRecordBuilder.table(ModuleFactory.getCriteriaModule().getTableName())
								   .andCondition(CriteriaAPI.getCondition(FieldFactory.filterField(FieldFactory.getCriteriaFields(), "criteriaId"), oldRecord.getCriteriaId() + "", NumberOperators.EQUALS));
				deleteRecordBuilder.delete();
			}
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
