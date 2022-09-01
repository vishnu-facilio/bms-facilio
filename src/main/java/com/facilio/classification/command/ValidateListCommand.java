package com.facilio.classification.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

public class ValidateListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Condition condition;
        Criteria criteria=new Criteria();
        Object filters = context.get(FacilioConstants.ContextNames.FILTERS);
        if (filters == null && !context.containsKey(FacilioConstants.ContextNames.SEARCH)) {
            // add parent classification
            if (Constants.containsQueryParam(context, "parentClassificationId")) {
                Long parentClassificationId = FacilioUtil.parseLong(Constants.getQueryParam(context, "parentClassificationId"));
                condition = CriteriaAPI.getCondition("PARENT_CLASSIFICATION_ID", "parentClassificationId",
                        String.valueOf(parentClassificationId), NumberOperators.EQUALS);
                criteria.addAndCondition(condition);

            } else {
                condition = CriteriaAPI.getCondition("PARENT_CLASSIFICATION_ID", "parentClassificationId",
                        null, CommonOperators.IS_EMPTY);
                criteria.addAndCondition(condition);
            }
        }
        addFetchAllCondition(context,criteria);
        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria.isEmpty() ? null : criteria);
        return false;
    }
    private void addFetchAllCondition(Context context,Criteria criteria){
        Boolean fetchAll=false;
        if(Constants.containsQueryParam(context,"fetchAll")){
            fetchAll=FacilioUtil.parseBoolean(Constants.getQueryParam(context,"fetchAll"));
        }
        if(!fetchAll){
           Condition condition=CriteriaAPI.getCondition("STATUS", "status",
                    String.valueOf(true), BooleanOperators.IS);
            criteria.addAndCondition(condition);
        }
    }
}
