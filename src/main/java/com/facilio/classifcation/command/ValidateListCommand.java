package com.facilio.classifcation.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class ValidateListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Object filters = context.get(FacilioConstants.ContextNames.FILTERS);
        if (filters == null) {
            // add parent classification
            Condition condition;
            if (Constants.containsQueryParam(context, "parentClassificationId")) {
                Long parentClassificationId = FacilioUtil.parseLong(Constants.getQueryParam(context, "parentClassificationId"));
                condition = CriteriaAPI.getCondition("PARENT_CLASSIFICATION_ID", "parentClassificationId",
                        String.valueOf(parentClassificationId), NumberOperators.EQUALS);
                context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
            } else {
                condition = CriteriaAPI.getCondition("PARENT_CLASSIFICATION_ID", "parentClassificationId",
                        null, CommonOperators.IS_EMPTY);
            }
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
        }
        return false;
    }
}
