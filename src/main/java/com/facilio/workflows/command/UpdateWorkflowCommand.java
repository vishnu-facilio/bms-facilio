package com.facilio.workflows.command;

import java.util.Collections;
import java.util.Map;

import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowRelUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflowlog.context.WorkflowVersionHistoryContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

import lombok.extern.log4j.Log4j;

@Log4j
public class UpdateWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowContext workflow = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		if(workflow == null) {
			workflow = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
		}

		if (workflow != null) {

			workflow.setSysModifiedBy(AccountUtil.getCurrentUser().getOuid());
			workflow.setSysModifiedTime(DateTimeUtil.getCurrenTime());

			WorkflowUtil.scriptSyntaxValidation(workflow);
			workflow.validateScript();
			
			trackWorkflowVersionHistory(workflow);

			GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
			update.table(ModuleFactory.getWorkflowModule().getTableName());
			update.fields(FieldFactory.getWorkflowFields())
					.andCondition(CriteriaAPI.getIdCondition(workflow.getId(), ModuleFactory.getWorkflowModule()));

			Map<String, Object> prop = FieldUtil.getAsProperties(workflow);
			prop.put("runAsAdmin",workflow.getRunAsAdmin());
			update.update(prop);
			
			WorkflowRelUtil.addWorkflowRelations(workflow);
		}
		
		return false;
	}

	private void trackWorkflowVersionHistory(WorkflowContext newWorkflow) throws Exception {
		
		try {
			
			WorkflowContext oldWorkflow = WorkflowUtil.getWorkflowContext(newWorkflow.getId());
			
			WorkflowVersionHistoryContext version = new WorkflowVersionHistoryContext(newWorkflow.getId(),oldWorkflow.getWorkflowV2String(),oldWorkflow.getSysModifiedTime(),newWorkflow.getSysModifiedTime());
			
			UserBean userBean = (UserBean) BeanFactory.lookup("UserBean", AccountUtil.getCurrentOrg().getId());
			
			Long createdByPeopleId = userBean.getUser(oldWorkflow.getSysModifiedBy(), true).getPeopleId();
			Long modifiedByPeopleId = userBean.getUser(newWorkflow.getSysModifiedBy(), true).getPeopleId();
			
			V3PeopleContext createdByPeopleContext = new V3PeopleContext();
			createdByPeopleContext.setId(createdByPeopleId);
			
			V3PeopleContext modifiedByPeopleContext = new V3PeopleContext();
			modifiedByPeopleContext.setId(modifiedByPeopleId);
			
			version.setCreatedByPeople(createdByPeopleContext);
			version.setModifiedByPeople(modifiedByPeopleContext);
			
			V3Util.createRecord(Constants.getModBean().getModule(FacilioConstants.Workflow.WORKFLOW_VERSION_HISTORY), FieldUtil.getAsProperties(version));
		}
		catch( Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}