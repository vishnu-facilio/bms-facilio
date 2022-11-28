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
 * AddCriteriaForJobPlanTaskBeforeFetchCommand adds criteria to fetch the JobPlan Tasks
 * - JobPlan ID(s) in query params
 *          - fetches all the JobPlan Tasks in JobPlan `jobPlanId`
 * - JobPlan ID(s) and JobPlanSection ID(s) in query params
 *          - fetches all the JobPlan Tasks in `jobPlanSectionId` of JobPlan wih ID `jobPlanId`
 *
 * API Endpoint - /api/v3/modules/data/list?moduleName=jobplantask&jobplan={JOBPLAN_ID}&jobplansection={JOBPLAN_SECTION_ID(s)}
 */
public class AddCriteriaForJobPlanTaskBeforeFetchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        // Get the Query Params
        HashMap<String, List> queryParams = (HashMap<String, List>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);

        // check if jobPlan ID is available in query params
        if (queryParams != null && queryParams.containsKey(FacilioConstants.ContextNames.JOB_PLAN)) {

            String jobPlanId = (String) queryParams.get(FacilioConstants.ContextNames.JOB_PLAN).get(0);
            FacilioField jobPlanIdField = modBean.getField("jobPlan", moduleName);

            // add jobPlanId criteria
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(jobPlanIdField, jobPlanId, NumberOperators.EQUALS));

            // add jobPlanSectionId criteria, if it's available.
            if(queryParams.containsKey(FacilioConstants.ContextNames.JOB_PLAN_SECTION)){
                String jobPlanSectionId = (String) queryParams.get(FacilioConstants.ContextNames.JOB_PLAN_SECTION).get(0);
                FacilioField jobPlanSectionField = modBean.getField("taskSection", moduleName);
                criteria.addAndCondition(CriteriaAPI.getCondition(jobPlanSectionField, jobPlanSectionId, NumberOperators.EQUALS));
            }

            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        }
        
        context.put(FacilioConstants.ContextNames.SORTING_QUERY,"SEQUENCE");
        context.put(FacilioConstants.ContextNames.ORDER_TYPE,"asc");
        return false;
    }
}
