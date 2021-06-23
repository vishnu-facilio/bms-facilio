package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class ChangePreventiveMaintenanceStatusCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
			Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
			
			FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getPreventiveMaintenanceFields())
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			builder.update(pmProps);
			
			if (pm.isActive()) {
				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMsDetails(recordIds);
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
			}
		}
		return false;
	}
}
