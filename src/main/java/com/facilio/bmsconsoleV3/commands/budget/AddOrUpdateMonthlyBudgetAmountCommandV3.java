package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetMonthlyAmountContext;
import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class AddOrUpdateMonthlyBudgetAmountCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<BudgetContext> budgetList = recordMap.get(FacilioConstants.ContextNames.Budget.BUDGET);
        if(CollectionUtils.isNotEmpty(budgetList)) {
            List<BudgetAmountContext> budgetAmountList = recordMap.get(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule budgetAmountModule = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
            List<FacilioField> fields = modBean.getAllFields(budgetAmountModule.getName());
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);


            if(CollectionUtils.isNotEmpty(budgetAmountList)) {
                for (BudgetAmountContext budgetAmnt : budgetAmountList) {
                    BudgetContext budget = (BudgetContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Budget.BUDGET, budgetAmnt.getBudget().getId(), BudgetContext.class);
                    FacilioModule monthlyAmountModule = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET_MONTHLY_AMOUNT);
                    DeleteRecordBuilder<BudgetMonthlyAmountContext> deleteBuilder = new DeleteRecordBuilder<BudgetMonthlyAmountContext>()
                            .module(monthlyAmountModule)
                            .andCondition(CriteriaAPI.getCondition("AMOUNT_ID", "budgetAmount", String.valueOf(budgetAmnt.getId()), NumberOperators.EQUALS));
                    deleteBuilder.delete();
                    List<BudgetMonthlyAmountContext> monthlySplit = new ArrayList<>();
                    Double yearlyAmnt = 0.0;

                    if (CollectionUtils.isNotEmpty(budgetAmnt.getMonthlyAmountSplitUp())) {
                        if (budgetAmnt.getMonthlyAmountSplitUp().size() < 12) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid monthly split up");
                        }
                        for (BudgetMonthlyAmountContext month : FieldUtil.getAsBeanListFromMapList(budgetAmnt.getMonthlyAmountSplitUp(), BudgetMonthlyAmountContext.class)) {
                            month.setBudgetAmount(budgetAmnt);
                            month.setResource(budget.getFocalPointResource());
                            month.setAccount(budgetAmnt.getAccount());
                            monthlySplit.add(month);
                            yearlyAmnt += month.getMonthlyAmount();
                            continue;
                        }
                    } else {
                        int i = budget.getFiscalYear();
                        int count = 0;
                        while (count <= 11) {
                            BudgetMonthlyAmountContext month = new BudgetMonthlyAmountContext();
                            month.setStartDate(DateTimeUtil.getMonthStartTime((i + count), budget.getFiscalYearStart()));
                            month.setMonthlyAmount((Double) budgetAmnt.getYearlyAmount() / 12);
                            month.setBudgetAmount(budgetAmnt);
                            month.setResource(budget.getFocalPointResource());
                            month.setAccount(budgetAmnt.getAccount());
                            yearlyAmnt += month.getMonthlyAmount();
                            monthlySplit.add(month);
                            count++;
                        }
                    }
                    V3RecordAPI.addRecord(false, monthlySplit, monthlyAmountModule, modBean.getAllFields(monthlyAmountModule.getName()));

                    //updating yearly amnt in budget amnt
                    budgetAmnt.setYearlyAmount(yearlyAmnt);
                    budgetAmnt.setResource(budget.getFocalPointResource());
                    if (budgetAmnt.getAccount() != null) {
                        budgetAmnt.setAmountType(budgetAmnt.getAccount().getAmountType());
                    }

                    FacilioChain updateChain = ChainUtil.getUpdateChain(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
                    FacilioContext newContext = updateChain.getContext();
                    newContext.put(Constants.RECORD_ID, budgetAmnt.getId());
                    Constants.setModuleName(newContext, FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
                    Constants.setRawInput(newContext, FieldUtil.getAsJSON(budgetAmnt));
                    newContext.put(Constants.BEAN_CLASS, BudgetAmountContext.class);
                    newContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
                    updateChain.execute();
                  }
            }

        }
        return false;
    }
}
