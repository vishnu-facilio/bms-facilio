package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.context.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetMonthlyAmountContext;
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
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

public class AddOrUpdateMonthlyBudgetAmountCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<BudgetAmountContext> budgetAmountList = recordMap.get(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule budgetAmountModule = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
        List<FacilioField> fields = modBean.getAllFields(budgetAmountModule.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);


        if(CollectionUtils.isNotEmpty(budgetAmountList)) {
            for(BudgetAmountContext budgetAmnt : budgetAmountList){
                FacilioModule monthlyAmountModule = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET_MONTHLY_AMOUNT);
                DeleteRecordBuilder<BudgetMonthlyAmountContext> deleteBuilder = new DeleteRecordBuilder<BudgetMonthlyAmountContext>()
                        .module(monthlyAmountModule)
                        .andCondition(CriteriaAPI.getCondition("AMOUNT_ID", "budgetAmount", String.valueOf(budgetAmnt.getId()), NumberOperators.EQUALS));
                deleteBuilder.delete();
                List<BudgetMonthlyAmountContext> monthlySplit = new ArrayList<>();
                Double yearlyAmnt = 0.0;

                if(MapUtils.isNotEmpty(budgetAmnt.getMonthlyAmountSplitUp())) {
                    Set<String> keys = budgetAmnt.getMonthlyAmountSplitUp().keySet();
                    if(keys.size() != 12) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Monthly Splitup");
                    }
                    for (Object key : keys) {
                        Integer keyInt  = Integer.parseInt((String)key);
                        if(keyInt != null && keyInt <= 12) {
                            Map<String, Object> monthMap = (Map<String, Object>) budgetAmnt.getMonthlyAmountSplitUp().get(String.valueOf(keyInt));
                            BudgetMonthlyAmountContext month = FieldUtil.getAsBeanFromMap(monthMap, BudgetMonthlyAmountContext.class);
                            month.setBudgetAmount(budgetAmnt);
                            monthlySplit.add(month);
                            yearlyAmnt += month.getMonthlyAmount();
                            continue;
                        }
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Month Identifier in the Monthly Splitup");
                    }
                }
                else {
                    int i =1;
                    while (i <= 12){
                        BudgetMonthlyAmountContext month = new BudgetMonthlyAmountContext();
                        month.setMonthIdentifier(i);
                        month.setMonthlyAmount((Double) budgetAmnt.getYearlyAmount()/12);
                        month.setBudgetAmount(budgetAmnt);
                        yearlyAmnt += month.getMonthlyAmount();
                        monthlySplit.add(month);
                        i++;
                    }
                }
                V3RecordAPI.addRecord(false, monthlySplit, monthlyAmountModule, modBean.getAllFields(monthlyAmountModule.getName()));

                //updating yearly amnt in budget amnt
                budgetAmnt.setYearlyAmount(yearlyAmnt);
                if(budgetAmnt.getAccount() != null) {
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
        return false;
    }
}
