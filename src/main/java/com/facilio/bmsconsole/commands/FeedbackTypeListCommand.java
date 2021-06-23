package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FeedbackTypeContext;
import com.facilio.bmsconsole.util.DevicesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class FeedbackTypeListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
	

		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder();
		
		builder.table(ModuleFactory.getFeedbackTypeModule().getTableName())
		.select(FieldFactory.getFeedbackTypeFields());
		
		
		List<Map<String,Object>> resultList=builder.get();
	
		List<FeedbackTypeContext> feedbackTypes=new ArrayList<FeedbackTypeContext>();
		
		
		if(resultList!=null) {
			feedbackTypes=FieldUtil.getAsBeanListFromMapList(resultList, FeedbackTypeContext.class);
			
			for (FeedbackTypeContext feedbackType : feedbackTypes) {
				
				
					feedbackType.setCatalogs(DevicesAPI.getCatalogsForType(feedbackType.getId(),false));	
				
					
			}
			
		}
		
			context.put(FacilioConstants.ContextNames.RECORD_LIST,feedbackTypes);

		

		return false;
	}


	

}
