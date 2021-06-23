package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;

public class DeletePrinterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long printerId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if (printerId > 0) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getPrinterModule().getTableName())					
					.andCondition(CriteriaAPI.getIdCondition(printerId, ModuleFactory.getPrinterModule()));
			
			builder.delete();
		}
		
		return false;
	}

}
