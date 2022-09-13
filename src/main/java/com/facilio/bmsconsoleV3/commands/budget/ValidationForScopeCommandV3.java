package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidationForScopeCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<BudgetContext> budgets = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(budgets)) {
            for(BudgetContext budget : budgets){
                if((budget.getFocalPointType() == 2|| budget.getFocalPointType() == 3 ) && budget.getFocalPointResource() == null ){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Space/Asset  should not be null ");
                }
            }
        }


        return false;
}
}
