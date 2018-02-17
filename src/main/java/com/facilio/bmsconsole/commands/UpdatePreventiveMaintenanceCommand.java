package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdatePreventiveMaintenanceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		
		PreventiveMaintenance newPm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		PreventiveMaintenance oldPm = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST)).get(0);
		
		updateDefaultProps(newPm, context);
		Map<String, Object> pmProps = FieldUtil.getAsProperties(newPm);
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getPreventiveMaintenanceFields())
				.andCondition(CriteriaAPI.getIdCondition(oldPm.getId(), module));

		updateBuilder.update(pmProps);
		
		newPm.setId(oldPm.getId());
		
		return false;
	}
	
	private static void updateDefaultProps(PreventiveMaintenance pm, Context context) {
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		pm.setTemplateId(templateId);

		pm.setModifiedById(AccountUtil.getCurrentUser().getId());
		pm.setLastModifiedTime(System.currentTimeMillis());
		
		pm.setResourceId(workorder.getResource() != null ? workorder.getResource().getId() : -1);
		pm.setAssignedToid(workorder.getAssignedTo() != null ? workorder.getAssignedTo().getId() : -1);
		pm.setAssignmentGroupId(workorder.getAssignmentGroup() != null ? workorder.getAssignmentGroup().getId() : -1);
		pm.setTypeId(workorder.getType() != null ? workorder.getType().getId() : -1);

	}

}
