package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
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
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		pm.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		pm.setTemplateId(templateId);
		
		pm.setCreatedById(AccountUtil.getCurrentUser().getId());
		pm.setCreatedTime(System.currentTimeMillis());
		pm.setStatus(true);
		
		if(workorder.getAsset() != null) {
			pm.setAssetId(workorder.getAsset().getId());
		}
		if(workorder.getSpace() != null) {
			pm.setSpaceId(workorder.getSpace().getId());
		}
		if(workorder.getAssignedTo() != null) {
			pm.setAssignedToid(workorder.getAssignedTo().getId());
		}
		if(workorder.getType() != null) {
			pm.setTypeId(workorder.getType().getId());
		}
		
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
