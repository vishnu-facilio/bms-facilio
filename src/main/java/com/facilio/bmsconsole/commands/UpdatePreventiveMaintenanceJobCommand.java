package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdatePreventiveMaintenanceJobCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		PMJobsContext pmJobs = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_JOB);
		long pmId = (Long) context.get(FacilioConstants.ContextNames.PM_ID);
		long resourceId = (Long) context.get(FacilioConstants.ContextNames.PM_RESOURCE_ID);
		Map<String, Object> props = FieldUtil.getAsProperties(pmJobs);
		
		String ids = StringUtils.join(recordIds, ",");
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(ModuleFactory.getPMJobsModule()));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(ids);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getPMJobsModule().getTableName())
				.fields(FieldFactory.getPMJobFields())
				.andCondition(idCondition);
		
		if(resourceId != -1)
		{
			List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
			FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.select(fields)
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCustomWhere("Preventive_Maintenance.ID = ?", pmId);
																;
			List<Map<String, Object>> pmProps = selectRecordBuilder.get();
			
			long templateId = (long) pmProps.get(0).get("templateId");
			JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), templateId);
			JSONObject content = template.getTemplate(null);
			WorkOrderContext wo = FieldUtil.getAsBeanFromJson((JSONObject) content.get(FacilioConstants.ContextNames.WORK_ORDER), WorkOrderContext.class);
			User user = new User();
			user.setId(resourceId);
			wo.setAssignedTo(user);
			
			JSONTemplate workorderTemplate = new JSONTemplate();
			workorderTemplate.setName(wo.getSubject());
			
			content.put(FacilioConstants.ContextNames.WORK_ORDER, FieldUtil.getAsJSON(wo));
			workorderTemplate.setContent(content.toJSONString());

			long newTemplateId = TemplateAPI.addJsonTemplate(AccountUtil.getCurrentOrg().getOrgId(), workorderTemplate);
			
			props.put("templateId", newTemplateId);
		}
		
		updateBuilder.update(props);
		return false;
	}
}
