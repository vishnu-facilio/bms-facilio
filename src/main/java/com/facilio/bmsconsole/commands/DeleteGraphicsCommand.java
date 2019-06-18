package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;

public class DeleteGraphicsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getGraphicsModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(recordId, ModuleFactory.getGraphicsModule()));
		
		delete.delete();
		return false;
	}

}
