package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PMStatus;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class UpdatePreventiveMaintenanceCommand extends FacilioCommand{

	private static final Logger LOGGER = Logger.getLogger(UpdatePreventiveMaintenanceCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		PreventiveMaintenance newPm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		PreventiveMaintenance oldPm = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST)).get(0);
		
		updateDefaultProps(newPm, context);
		Map<String, Object> pmProps = FieldUtil.getAsProperties(newPm);
		if (newPm.getBaseSpaceId() == null || newPm.getBaseSpaceId() == -1) {
			pmProps.put("baseSpaceId", -99);
		}
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		LOGGER.log(Level.SEVERE, "created by id: "+ newPm.getCreatedById()+ " pm: "+oldPm.getId());

		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getPreventiveMaintenanceFields())
				.andCondition(CriteriaAPI.getIdCondition(oldPm.getId(), module));

		updateBuilder.update(pmProps);
		
		newPm.setId(oldPm.getId());
		context.put(FacilioConstants.ContextNames.PARENT_ID, oldPm.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID, oldPm.getId());
		return false;
	}
	
	private static void updateDefaultProps(PreventiveMaintenance pm, Context context) {
//		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		pm.setTemplateId(templateId);

		pm.setModifiedById(AccountUtil.getCurrentUser().getId());
		pm.setLastModifiedTime(System.currentTimeMillis());
		pm.setStatus(PMStatus.ACTIVE);
		
		/*pm.setResourceId(workorder.getResource() != null ? workorder.getResource().getId() : -1);
		pm.setAssignedToid(workorder.getAssignedTo() != null ? workorder.getAssignedTo().getId() : -1);
		pm.setAssignmentGroupId(workorder.getAssignmentGroup() != null ? workorder.getAssignmentGroup().getId() : -1);
		pm.setTypeId(workorder.getType() != null ? workorder.getType().getId() : -1);*/

	}

}
