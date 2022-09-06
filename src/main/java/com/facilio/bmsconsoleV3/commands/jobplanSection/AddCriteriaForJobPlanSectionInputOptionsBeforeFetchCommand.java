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
 * AddCriteriaForJobPlanSectionInputOptionsBeforeFetchCommand adds criteria to fetch the JobPlan Section's InputOptions
 * - JobPlan Section ID(s) in query params
 *       - fetches all the JobPlan Sections' input options in JobPlan with ID `jobPlanSectionId`
 *
 *  API endpoint: /api/v3/modules/data/list?moduleName=jobPlanSectionInputOptions&jobplansection={JOBPLAN_SECTION_ID(s)}
 */
public class AddCriteriaForJobPlanSectionInputOptionsBeforeFetchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        // Get the Query Params
        HashMap<String, List> queryParams = (HashMap<String, List>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);

        // check if section ID is available in query params
        if (queryParams != null && queryParams.containsKey(FacilioConstants.ContextNames.JOB_PLAN_SECTION)) {
            String jobPlanSectionId = (String) queryParams.get(FacilioConstants.ContextNames.JOB_PLAN_SECTION).get(0);
            FacilioField jobPlanSectionField = modBean.getField("jobPlanSection", moduleName);

            // add criteria
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(jobPlanSectionField, jobPlanSectionId, NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        }
        return false;
    }
}
