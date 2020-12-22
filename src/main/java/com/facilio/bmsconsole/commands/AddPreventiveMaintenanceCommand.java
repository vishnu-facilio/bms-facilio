package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.util.PMStatus;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;

public class AddPreventiveMaintenanceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		addDefaultProps(pm, context);
		addResource(pm);

		if (pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTI_SITE) {
			if (CollectionUtils.isNotEmpty(pm.getSiteIds()) && pm.getSiteIds().size() == 1) {
				pm.setSiteId(pm.getSiteIds().get(0));
			}
		}

		Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
		pmProps.put("woGenerationStatus", 0);
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPreventiveMaintenanceModule().getTableName())
				.fields(FieldFactory.getPreventiveMaintenanceFields()).addRecord(pmProps);

		builder.save();
		long id = (long) pmProps.get("id");
		pm.setId(id);
		addPMSites(pm);
		context.put(FacilioConstants.ContextNames.PARENT_ID, id);
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		return false;
	}
	
	private static void addResource(ResourceContext resource) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		InsertRecordBuilder<ResourceContext> insertBuilder = new InsertRecordBuilder<ResourceContext>()
																	.moduleName(FacilioConstants.ContextNames.RESOURCE)
																	.fields(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE))
																	;
		insertBuilder.insert(resource);
	}

	private static void addPMSites(PreventiveMaintenance pm) throws Exception {
		if (pm.getPmCreationTypeEnum() != PreventiveMaintenance.PMCreationType.MULTI_SITE) {
			return;
		}

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

	private static void addDefaultProps(PreventiveMaintenance pm, Context context) {
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

		pm.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		pm.setTemplateId(templateId);
		pm.setCreatedById(AccountUtil.getCurrentUser().getId());
		pm.setCreatedTime(System.currentTimeMillis());
		pm.setStatus(PMStatus.ACTIVE);
		pm.setResourceType(ResourceType.PM);
		pm.setDefaultAllTriggers(true);
		pm.setEnableSkipTriggers(true);

		/*if(workorder.getResource() != null) {
			pm.setResourceId(workorder.getResource().getId());
		}
		if (workorder.getAssignedTo() != null) {
			pm.setAssignedToid(workorder.getAssignedTo().getId());
		}
		if (workorder.getAssignmentGroup() != null) {
			pm.setAssignmentGroupId(workorder.getAssignmentGroup().getId());
		}
		if (workorder.getType() != null) {
			pm.setTypeId(workorder.getType().getId());
		}*/

	}
	
}
