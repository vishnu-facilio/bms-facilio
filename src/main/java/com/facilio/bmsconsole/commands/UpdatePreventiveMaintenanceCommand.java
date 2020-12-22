package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
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
import org.apache.commons.collections4.CollectionUtils;

public class UpdatePreventiveMaintenanceCommand extends FacilioCommand{

	private static final Logger LOGGER = Logger.getLogger(UpdatePreventiveMaintenanceCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		PreventiveMaintenance newPm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		PreventiveMaintenance oldPm = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST)).get(0);
		
		updateDefaultProps(newPm, context);

		if (newPm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
			if (CollectionUtils.isNotEmpty(newPm.getSiteIds()) && newPm.getSiteIds().size() == 1) {
				newPm.setSiteId(newPm.getSiteIds().get(0));
			}
		}

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
		updatePMSites(newPm);
		context.put(FacilioConstants.ContextNames.PARENT_ID, oldPm.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID, oldPm.getId());
		return false;
	}

	private static void updatePMSites(PreventiveMaintenance pm) throws Exception {
		if (pm.getPmCreationTypeEnum() != PreventiveMaintenance.PMCreationType.MULTI_SITE) {
			return;
		}

		List<FacilioField> pmSitesFields = FieldFactory.getPMSitesFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(pmSitesFields);

		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(ModuleFactory.getPMSites().getTableName());
		deleteRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmId"), pm.getId()+"", NumberOperators.EQUALS));
		deleteRecordBuilder.delete();

		List<Map<String, Object>> props = new ArrayList<>();
		if (CollectionUtils.isEmpty(pm.getSiteIds()) ) {
			throw new IllegalArgumentException("sites should not be empty");
		}

		for (Long siteId: pm.getSiteIds()) {
			Map<String, Object> prop = new HashMap<>();
			prop.put("siteId", siteId);
			prop.put("pmId", pm.getId());
			props.add(prop);
		}

		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPMSites().getTableName())
				.fields(FieldFactory.getPMSitesFields()).addRecords(props);

		builder.save();
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
