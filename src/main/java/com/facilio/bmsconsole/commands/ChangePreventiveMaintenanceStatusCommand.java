package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ChangePreventiveMaintenanceStatusCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		
		Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
		
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getPreventiveMaintenancetModule().getTableName())
				.fields(FieldFactory.getPreventiveMaintenanceFields())
				.andCustomWhere("Preventive_Maintenance.ID = ?", pmId);
		
		builder.update(pmProps);
		return false;
	}
	
}
