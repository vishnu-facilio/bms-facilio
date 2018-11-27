package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddPreventiveMaintenanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		addDefaultProps(pm, context);
		addResource(pm);
		Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getPreventiveMaintenancetModule().getTableName())
				.fields(FieldFactory.getPreventiveMaintenanceFields()).addRecord(pmProps);

		builder.save();
		long id = (long) pmProps.get("id");
		pm.setId(id);
		TemplateAPI.addIncludeExcludePropsForPM(pm);
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

	private static void addDefaultProps(PreventiveMaintenance pm, Context context) {
		long templateId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

		pm.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		pm.setTemplateId(templateId);
		pm.setCreatedById(AccountUtil.getCurrentUser().getId());
		pm.setCreatedTime(System.currentTimeMillis());
		pm.setStatus(true);
		pm.setResourceType(ResourceType.PM);

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
