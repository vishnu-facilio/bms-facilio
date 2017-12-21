package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateNotificationSettings implements Command {
	@Override
	public boolean execute(Context context) throws Exception {
		WorkflowRuleContext workflow=(WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_UPDATE);
		System.out.println("....UpdateNotification");

		if(workflow != null){
			System.out.println("....UpdateNotification condition");

			workflow.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			Map<String, Object> workflowProps= FieldUtil.getAsProperties(workflow);
			try{
				List<FacilioField> fields= FieldFactory.getWorkflowRuleFields();
				GenericUpdateRecordBuilder builder=new GenericUpdateRecordBuilder().table("Workflow_Rule").fields(fields).andCustomWhere("ID = ?", workflow.getId());
				builder.update(workflowProps);
				context.put(FacilioConstants.ContextNames.RESULT, "success");
			} catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			
		}
		return false;
	}
}