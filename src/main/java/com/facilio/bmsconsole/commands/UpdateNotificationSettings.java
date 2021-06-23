package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

public class UpdateNotificationSettings extends FacilioCommand {
	private static Logger log = LogManager.getLogger(UpdateNotificationSettings.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
				log.info("Exception occurred ", e);
				throw e;
			}
			
		}
		return false;
	}
}