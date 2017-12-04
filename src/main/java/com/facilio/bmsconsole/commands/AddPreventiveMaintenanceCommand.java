package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class AddPreventiveMaintenanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		pm.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		pm.setTemplateId(templateId);
		
		pm.setCreatedById(AccountUtil.getCurrentUser().getId());
		pm.setCreatedTime(System.currentTimeMillis());
		
		Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
													.table(ModuleFactory.getPreventiveMaintenancetModule().getTableName())
													.fields(FieldFactory.getPreventiveMaintenanceFields())
													.addRecord(pmProps);
		
		builder.save();
		long id = (long) pmProps.get("id");
		pm.setId(id);
		
		if(pm.getMaxCount() != -1) {
			FacilioTimer.scheduleCalendarJob(id, "PreventiveMaintenance", pm.getStartTime(), pm.getSchedule(), "priority", pm.getMaxCount());
		}
		else if(pm.getEndTime() != -1) {
			FacilioTimer.scheduleCalendarJob(id, "PreventiveMaintenance", pm.getStartTime(), pm.getSchedule(), "priority", pm.getEndTime());
		}
		else {
			FacilioTimer.scheduleCalendarJob(id, "PreventiveMaintenance", pm.getStartTime(), pm.getSchedule(), "priority");
		}
		return false;
	}
}
