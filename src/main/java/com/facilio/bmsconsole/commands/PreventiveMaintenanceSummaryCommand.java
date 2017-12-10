package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.WorkorderTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class PreventiveMaintenanceSummaryCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		fields.addAll(FieldFactory.getPMJobFields());
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Preventive_Maintenance")
				.innerJoin("Jobs")
				.on("Preventive_Maintenance.ID = Jobs.JOBID")
				.andCustomWhere("Preventive_Maintenance.ORGID = ? AND Jobs.JOBNAME = ? AND Preventive_Maintenance.ID = ?", AccountUtil.getCurrentOrg().getOrgId(), "PreventiveMaintenance", pmId);

		List<Map<String, Object>> pmProps = selectRecordBuilder.get();
		Map<String, Object> pmProp = pmProps.get(0);
		
		PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(pmProp, PreventiveMaintenance.class);
		
		WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), pm.getTemplateId());
		JSONObject content = (JSONObject) template.getTemplate(new HashMap<String, Object>()).get("workorder");
		
		WorkOrderContext workorder = FieldUtil.getAsBeanFromJson(content, WorkOrderContext.class);
		
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		
		return false;
	}
}
