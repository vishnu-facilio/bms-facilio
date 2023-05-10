package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DeleteBudgetAmountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<BudgetContext> budgets = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.Budget.BUDGET);

        if (CollectionUtils.isEmpty(budgets)){
            return false;
        }

        List<Long> budgetIds = budgets.stream().map(BudgetContext::getId).collect(Collectors.toList());

        List<Long> budgetAmountIds = budgets.stream()
                .flatMap(budgetContext -> budgetContext.getSubForm().get(FacilioConstants.ContextNames.BUDGET_AMOUNT).stream())
                .map(p -> (Long) p.get("id"))
                .filter(Objects::nonNull)
                .filter(id -> id > 0L)
                .collect(Collectors.toList());

        Set<Long> deleteIds = new HashSet<>(fetchBudgetAmounts(budgetIds));

        budgetAmountIds.forEach(deleteIds::remove);

        if (CollectionUtils.isNotEmpty(deleteIds)){

            DeleteBudgetSubModuleRecordsCommandV3.deleteBudgetMonthlyAmountForBudgetAmount(deleteIds);

            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(deleteIds, ","), NumberOperators.EQUALS));

            V3RecordAPI.deleteRecords(FacilioConstants.ContextNames.BUDGET_AMOUNT, criteria,true);
        }

        return false;
    }

    private List<Long> fetchBudgetAmounts(List<Long> budgetIds) throws Exception {

        SelectRecordsBuilder<BudgetAmountContext> builder = new SelectRecordsBuilder<BudgetAmountContext>()
                .moduleName(FacilioConstants.ContextNames.BUDGET_AMOUNT)
                .select(Constants.getModBean().getModuleFields(FacilioConstants.ContextNames.BUDGET_AMOUNT))
                .beanClass(BudgetAmountContext.class)
                .andCondition(CriteriaAPI.getCondition("BUDGET_ID","budgetId", StringUtils.join(budgetIds,","), NumberOperators.EQUALS));

        List<BudgetAmountContext> budgetAmounts = builder.get();
        return CollectionUtils.isNotEmpty(budgetAmounts) ? budgetAmounts.stream().map(BudgetAmountContext::getId).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

}
