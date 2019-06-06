package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteSingleDayBusinessHoursCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id=(long)context.get(FacilioConstants.ContextNames.ID);
		
		if(id!=-1){
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
					.andCustomWhere("PARENT_ID = ?", id);
			builder.delete();
		}
		return false;
	}

}
