package com.facilio.workflowv2.util;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflowlog.context.WorkflowVersionHistoryContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

@Log4j
public class WorkflowHistoryUtil {

    public static void trackWorkflowVersionHistory(WorkflowContext newWorkflow ) throws Exception {

        try {
             if(StringUtils.isNotEmpty(newWorkflow.getWorkflowV2String())) {
                 WorkflowContext oldWorkflow = WorkflowUtil.getWorkflowContext(newWorkflow.getId());
                 int currentVersion = WorkflowV2Util.getWorkflowVersionHistoryMaxVersion(newWorkflow.getId());
                 boolean canTrackVersion = true;

                 if (currentVersion > 0) {
                     canTrackVersion = !oldWorkflow.getWorkflowV2String().equals(newWorkflow.getWorkflowV2String());
                 }

                 if (canTrackVersion) {
                     WorkflowVersionHistoryContext version = new WorkflowVersionHistoryContext(newWorkflow.getId(), newWorkflow.getWorkflowV2String(), oldWorkflow.getSysModifiedTime(), newWorkflow.getSysModifiedTime());
                     UserBean userBean = (UserBean) BeanFactory.lookup("UserBean", AccountUtil.getCurrentOrg().getId());

                     Long createdByPeopleId = userBean.getUser(oldWorkflow.getSysModifiedBy(), true).getPeopleId();
                     Long modifiedByPeopleId = userBean.getUser(newWorkflow.getSysModifiedBy(), true).getPeopleId();

                     V3PeopleContext createdByPeopleContext = new V3PeopleContext();
                     createdByPeopleContext.setId(createdByPeopleId);

                     V3PeopleContext modifiedByPeopleContext = new V3PeopleContext();
                     modifiedByPeopleContext.setId(modifiedByPeopleId);

                     version.setCreatedByPeople(createdByPeopleContext);
                     version.setModifiedByPeople(modifiedByPeopleContext);

                     if (createdByPeopleId > 0 && modifiedByPeopleId > 0) {
                         V3Util.createRecord(Constants.getModBean().getModule(FacilioConstants.Workflow.WORKFLOW_VERSION_HISTORY), FieldUtil.getAsProperties(version));
                     }
                 }
             }
        }
        catch( Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
