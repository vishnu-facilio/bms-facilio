package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FeedbackTypeContext;
import com.facilio.bmsconsole.util.DevicesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddOrUpdateFeedbackTypeCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
	
	FacilioModule feedbackTypeModule=ModuleFactory.getFeedbackTypeModule();
	List<FacilioField> feedbackTypeFields=FieldFactory.getFeedbackTypeFields();
	
		
   FeedbackTypeContext feedbackType=(FeedbackTypeContext) context.get(FacilioConstants.ContextNames.RECORD);

   Map<String, Object> propMap=FieldUtil.getAsProperties(feedbackType);
   		if(feedbackType.getId()>0)//update
   		{
   			
  			GenericUpdateRecordBuilder updateRecordBuilder=new GenericUpdateRecordBuilder()
   					.table(feedbackTypeModule.getTableName())
   					.fields(feedbackTypeFields)
   					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(feedbackTypeModule), Collections.singletonList(feedbackType.getId()), NumberOperators.EQUALS));
  			
  			updateRecordBuilder.update(propMap);
  			DevicesAPI.deleteCatalogsFromFeedbackType(feedbackType);
  			DevicesAPI.addCatalogsToFeedbackType(feedbackType);
  			
   		}
   		
   		else {
   			//add
   			GenericInsertRecordBuilder insertRecordBuilder=new GenericInsertRecordBuilder()
   					.table(feedbackTypeModule.getTableName())
   					.fields(feedbackTypeFields);
  			long feedbackTypeId=insertRecordBuilder.insert(propMap);
  			feedbackType.setId(feedbackTypeId);
  			DevicesAPI.addCatalogsToFeedbackType(feedbackType);  			  			   			
   		}
   

	
		return false;
	}

	
	
}
