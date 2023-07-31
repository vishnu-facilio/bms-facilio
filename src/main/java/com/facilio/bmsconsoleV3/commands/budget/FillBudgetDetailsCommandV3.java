package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillBudgetDetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);

        if(CollectionUtils.isNotEmpty(recordIds)) {
            Map<String, Object> currencyInfo = CurrencyUtil.getCurrencyInfo();
            for(Long recId : recordIds) {
                BudgetContext budget = (BudgetContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.Budget.BUDGET, recId);
                if (budget != null) {
                    budget.setBudgetAmountList(BudgetAPI.setBudgetAmount(budget.getId(), true, currencyInfo));
                }
            }
        }
        return false;
    }
}
