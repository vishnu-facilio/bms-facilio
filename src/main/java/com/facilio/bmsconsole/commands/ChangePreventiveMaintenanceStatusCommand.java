package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ChangePreventiveMaintenanceStatusCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
			Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
			
			FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(FieldFactory.getPreventiveMaintenanceFields())
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			builder.update(pmProps);
			
			List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getPMsDetails(recordIds);
			if (pm.isActive()) {
				context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
			}
		}
		return false;
	}
}
