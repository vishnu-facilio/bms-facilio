package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
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
		fields.addAll(FieldFactory.getPMJobFields());
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table("Preventive_Maintenance")
															.innerJoin("Jobs")
															.on("Preventive_Maintenance.ID = Jobs.JOBID")
															.andCustomWhere("Preventive_Maintenance.ORGID = ? AND Jobs.JOBNAME = ?", AccountUtil.getCurrentOrg().getOrgId(), "PreventiveMaintenance")
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
		if((long) context.get(FacilioConstants.ContextNames.ASSET_ID) != -1)
		{
			selectRecordBuilder.andCustomWhere("Preventive_Maintenance.ASSET_ID = ?", (long) context.get(FacilioConstants.ContextNames.ASSET_ID));
		}
		
		List<Map<String, Object>> pmProps = selectRecordBuilder.get();
		
		if(pmProps != null && !pmProps.isEmpty()) {
			List<PreventiveMaintenance> pms = new ArrayList<>();
			
			for(Map<String, Object> prop : pmProps) {
				pms.add(FieldUtil.getAsBeanFromMap(prop, PreventiveMaintenance.class));
			}
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
		}
		
		return false;
	}

}
