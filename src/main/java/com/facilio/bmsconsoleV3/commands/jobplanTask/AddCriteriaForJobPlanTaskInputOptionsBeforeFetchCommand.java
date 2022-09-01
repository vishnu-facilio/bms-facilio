package com.facilio.bmsconsoleV3.commands.jobplanTask;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;

/**
 * AddCriteriaForJobPlanTaskInputOptionsBeforeFetchCommand adds criteria to fetch the JobPlan Tasks' InputOptions
 * - JobPlan Task ID(s) in query params
 *       - fetches all the JobPlan Tasks' input options in JobPlan with ID `jobPlanTaskId`
 *
 *  API endpoint: /api/v3/modules/data/list?moduleName=jobPlanTaskInputOptions&jobplantask={JOBPLAN_TASK_ID(s)}
 */
public class AddCriteriaForJobPlanTaskInputOptionsBeforeFetchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        // Get the Query Params
        HashMap<String, List> queryParams = (HashMap<String, List>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);

        // check if task ID is available in query params
        if (queryParams != null && queryParams.containsKey(FacilioConstants.ContextNames.JOB_PLAN_TASK)) {
            String jobPlanTaskId = (String) queryParams.get(FacilioConstants.ContextNames.JOB_PLAN_TASK).get(0);
            FacilioField jobPlanTaskField = modBean.getField("jobPlanTask", moduleName);

            // add criteria
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(jobPlanTaskField, jobPlanTaskId, NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        }
        return false;
    }
}
