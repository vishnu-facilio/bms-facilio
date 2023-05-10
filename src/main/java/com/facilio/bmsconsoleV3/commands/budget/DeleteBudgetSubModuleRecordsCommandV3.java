package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteBudgetSubModuleRecordsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<BudgetContext> recordList = (List<BudgetContext>) context.get("deletedRecords");
        List<Long> deletedIds = recordList.stream().map(r -> r.getId()).collect(Collectors.toList());
        if(deletedIds == null || deletedIds.isEmpty()){
            return false;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        deleteBudgetAmountsForBudget(deletedIds, modBean);
        return false;
    }

    private static void deleteBudgetAmountsForBudget(List<Long> deletedIds, ModuleBean modBean) throws Exception {
        String budgetAmountModuleName = FacilioConstants.ContextNames.BUDGET_AMOUNT;
        FacilioModule module = modBean.getModule(budgetAmountModuleName);
        if(module.isTrashEnabled()) {
            FacilioField budgetField = modBean.getField(FacilioConstants.ContextNames.BUDGET, budgetAmountModuleName);
            Criteria criteria = new Criteria();
            Condition budgetCondition = CriteriaAPI.getCondition(budgetField, StringUtils.join(deletedIds, ','), NumberOperators.EQUALS);
            criteria.addAndCondition(budgetCondition);
            Map<Long, BudgetAmountContext> budgetAmountRecords = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.BUDGET_AMOUNT, null, BudgetAmountContext.class, criteria);
            if(budgetAmountRecords == null || budgetAmountRecords.isEmpty()){
                return;
            }
            Set<Long> budgetAmountIds = budgetAmountRecords.keySet();
            deleteBudgetMonthlyAmountForBudgetAmount(budgetAmountIds);
            V3RecordAPI.deleteRecords(budgetAmountModuleName, criteria, true);
        }
    }

    static void deleteBudgetMonthlyAmountForBudgetAmount( Set<Long> budgetAmountIds) throws Exception {
        String budgetMontlyAmounts = FacilioConstants.ContextNames.BUDGET_MONTHLY_AMOUNT;
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(budgetMontlyAmounts);
        if(module.isTrashEnabled()) {
            FacilioField budgetAmtField = modBean.getField("budgetAmount", budgetMontlyAmounts);
            Criteria budgetAmtCriteria = new Criteria();
            Condition budgetAmtCondition = CriteriaAPI.getCondition(budgetAmtField, StringUtils.join(budgetAmountIds, ','), NumberOperators.EQUALS);
            budgetAmtCriteria.addAndCondition(budgetAmtCondition);
            V3RecordAPI.deleteRecords(budgetMontlyAmounts, budgetAmtCriteria, true);
        }
    }
}
