package com.facilio.bmsconsoleV3.commands.jobplanSection;

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
 * AddCriteriaForJobPlanSectionBeforeFetchCommand adds criteria to fetch the JobPlan Sections
 * - JobPlan ID(s) in query params
 *       - fetches all the JobPlan Sections in JobPlan with ID `jobPlanId`
 *
 *  API Endpoint - /api/v3/modules/data/list?moduleName=jobplansection&jobplan={JOBPLAN_ID(s)}
 */
public class AddCriteriaForJobPlanSectionBeforeFetchCommand extends FacilioCommand {
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

            // add criteria
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(jobPlanIdField, jobPlanId, NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        }
        context.put(FacilioConstants.ContextNames.SORTING_QUERY,"SEQUENCE_NUMBER");
        context.put(FacilioConstants.ContextNames.ORDER_TYPE,"asc");
        return false;
    }
}
