package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetPreventiveMaintenanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Boolean status = null;
		if(context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STATUS) != null)
		{
			status = (Boolean) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STATUS);
		}
		
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.orderBy("Preventive_Maintenance.CREATION_TIME DESC")
															;
		
		if (status != null) {
			if (status) {
				selectRecordBuilder.andCustomWhere("(Preventive_Maintenance.STATUS = ? OR Preventive_Maintenance.STATUS IS NULL)", status);
			}
			else {
				selectRecordBuilder.andCustomWhere("Preventive_Maintenance.STATUS = ?", status);
			}
		}
		if(context.get(FacilioConstants.ContextNames.ASSET_ID) != null && (long) context.get(FacilioConstants.ContextNames.ASSET_ID) != -1)
		{
			selectRecordBuilder.andCustomWhere("Preventive_Maintenance.ASSET_ID = ?", (long) context.get(FacilioConstants.ContextNames.ASSET_ID));
		}
		if(context.get(FacilioConstants.ContextNames.SPACE_ID) != null && (long) context.get(FacilioConstants.ContextNames.SPACE_ID) != -1)
		{
			selectRecordBuilder.andCustomWhere("Preventive_Maintenance.SPACE_ID = ?", (long) context.get(FacilioConstants.ContextNames.SPACE_ID));
		}
		
		List<Map<String, Object>> pmProps = selectRecordBuilder.get();
		
		if(pmProps != null && !pmProps.isEmpty()) {
			List<PreventiveMaintenance> pms = new ArrayList<>();
			
			for(Map<String, Object> prop : pmProps) {
				pms.add(FieldUtil.getAsBeanFromMap(prop, PreventiveMaintenance.class));
			}
			
			Map<Long, List<PMTriggerContext>> pmTriggers = PreventiveMaintenanceAPI.getPMTriggers(pms);
			
			for(PreventiveMaintenance pm : pms) {
				pm.setTriggers(pmTriggers.get(pm.getId()));
			}
			
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
		}
		
		return false;
	}

}
