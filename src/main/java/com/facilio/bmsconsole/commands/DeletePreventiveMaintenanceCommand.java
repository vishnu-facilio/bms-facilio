package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DeletePreventiveMaintenanceCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			
			FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table("Preventive_Maintenance")
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());

		}
		return false;
	}
}
