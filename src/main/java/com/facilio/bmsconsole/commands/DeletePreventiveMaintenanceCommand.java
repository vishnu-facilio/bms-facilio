package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class DeletePreventiveMaintenanceCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
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
