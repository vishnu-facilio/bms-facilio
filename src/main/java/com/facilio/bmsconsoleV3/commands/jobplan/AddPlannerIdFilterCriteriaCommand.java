package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.JoinContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.*;

/**
 * AddPlannerIdFilterCriteriaCommand checks if Query Params has `PLANNER_ID` and add filter condition to the
 * SQL query to fetch for the Resources that isn't added into the Planner with ID `PLANNER_ID`.
 */
public class AddPlannerIdFilterCriteriaCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        // Get the Query Params
        HashMap<String, List> queryParams = (HashMap<String, List>) context.get(FacilioConstants.ContextNames.QUERY_PARAMS);

        // check if PlannerID is available in query params
        if (queryParams != null && queryParams.containsKey(FacilioConstants.ContextNames.PLANNER_ID)) {

            FacilioField parentIdField = modBean.getField("id", moduleName);

            FacilioModule pmResourcePlannerModule = modBean.getModule("pmResourcePlanner");
            FacilioField pmResourcePlannerResourceIdField = modBean.getField("resource", pmResourcePlannerModule.getName());
            FacilioField pmResourcePlannerPlannerIdField = modBean.getField("planner", pmResourcePlannerModule.getName());

            // get the planner IDs
            List<String> plannerIDs = (List<String>) queryParams.get(FacilioConstants.ContextNames.PLANNER_ID);

            // Construct the left join query
            List<JoinContext> joinContextList = new ArrayList<>();
            for (String id : plannerIDs) {
                JoinContext joinContext = new JoinContext(pmResourcePlannerModule, parentIdField, pmResourcePlannerResourceIdField, JoinContext.JoinType.LEFT_JOIN);

                // add criteria for join
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition(pmResourcePlannerPlannerIdField, id, NumberOperators.EQUALS));
                joinContext.setCriteria(criteria);

                joinContextList.add(joinContext);
            }

            // Construct the filterCriteria
            Criteria filterCriteria = new Criteria();
            filterCriteria.addAndCondition(CriteriaAPI.getCondition(pmResourcePlannerResourceIdField, CommonOperators.IS_EMPTY));

            // Put the constructed queries into the context
            context.put(Constants.JOINS, joinContextList);
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, filterCriteria);
        }
        return false;
    }
}
