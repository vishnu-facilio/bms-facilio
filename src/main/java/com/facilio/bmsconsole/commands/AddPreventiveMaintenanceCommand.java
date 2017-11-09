package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class AddPreventiveMaintenanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		pm.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		pm.setTemplateId(templateId);
		
		Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
															.table("Preventive_Maintenance")
															.fields(FieldFactory.getPreventiveMaintenanceFields())
															.addRecord(pmProps);
		
		builder.save();
		
		FacilioTimer.scheduleCalendarJob(1, "test", 30, null, "priority", 1507638900l);
		return false;
	}
	
}
