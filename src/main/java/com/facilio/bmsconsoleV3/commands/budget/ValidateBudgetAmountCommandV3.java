package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class ValidateBudgetAmountCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<BudgetContext> budgets = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(budgets)) {
            for(BudgetContext budget : budgets){
                Map<String, List<Map<String, Object>>> subforms = budget.getSubForm();
                if(MapUtils.isEmpty(subforms) || !subforms.containsKey(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Budget Amount cannot be empty");
                }
            }

        }
        return false;
    }
}
