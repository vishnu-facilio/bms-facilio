package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetMonthlyAmountContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AddOrUpdateMonthlyBudgetAmountCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<Long, ModuleBaseWithCustomFields> oldRecordsMap = Constants.getOldRecordMap(context);
        List<EventType> eventTypes = CommonCommandUtil.getEventTypes(context);
        boolean isEdit = false;
        if (eventTypes.contains(EventType.EDIT)) {
            isEdit = true;
        }

        List<BudgetContext> budgetList = recordMap.get(FacilioConstants.ContextNames.Budget.BUDGET);
        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
        Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);
        List<String> patchFieldNames;

        BudgetContext oldBudgetRecord = null;
        List<BudgetAmountContext> oldBudgetRecordBudgetAmountList = null;

        if(CollectionUtils.isNotEmpty(budgetList)) {
            if (isEdit) {
                oldBudgetRecord = (BudgetContext) oldRecordsMap.get(budgetList.get(0).getId());
                oldBudgetRecordBudgetAmountList = oldBudgetRecord.getBudgetAmountList();
            }
            List<BudgetAmountContext> budgetAmountList = recordMap.get(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule budgetAmountModule = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
            List<FacilioField> fields = modBean.getAllFields(budgetAmountModule.getName());
            List<FacilioField> budgetAmountMultiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsFromFields(fields);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

            FacilioModule monthlyAmountModule = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET_MONTHLY_AMOUNT);
            List<FacilioField> monthlyAmountModuleFields = modBean.getAllFields(monthlyAmountModule.getName());
            CurrencyUtil.addMultiCurrencyFieldsToFields(monthlyAmountModuleFields, monthlyAmountModule);
            List<FacilioField> monthlyAmountMultiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsFromFields(monthlyAmountModuleFields);

            if(CollectionUtils.isNotEmpty(budgetAmountList)) {
                for (BudgetAmountContext budgetAmnt : budgetAmountList) {
                    List<BudgetAmountContext> newBudgetAmountList = new ArrayList<>();
                    BudgetContext budget = (BudgetContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Budget.BUDGET, budgetAmnt.getBudget().getId(), BudgetContext.class);

                    List<Map<String, Object>> monthlyAmountSplitUp = budgetAmnt.getMonthlyAmountSplitUp();

                    DeleteRecordBuilder<BudgetMonthlyAmountContext> deleteBuilder = new DeleteRecordBuilder<BudgetMonthlyAmountContext>()
                            .module(monthlyAmountModule)
                            .andCondition(CriteriaAPI.getCondition("AMOUNT_ID", "budgetAmount", String.valueOf(budgetAmnt.getId()), NumberOperators.EQUALS));
                    deleteBuilder.delete();
                    List<BudgetMonthlyAmountContext> monthlySplit = new ArrayList<>();
                    Double yearlyAmnt = 0.0;

                    if (CollectionUtils.isNotEmpty(monthlyAmountSplitUp)) {
                        if (monthlyAmountSplitUp.size() < 12) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid monthly split up");
                        }
                        for (BudgetMonthlyAmountContext month : FieldUtil.getAsBeanListFromMapList(monthlyAmountSplitUp, BudgetMonthlyAmountContext.class)) {
                            month.setBudgetAmount(budgetAmnt);
                            month.setResource(budget.getFocalPointResource());
                            month.setAccount(budgetAmnt.getAccount());
                            if (isEdit) {
                                patchFieldNames = new ArrayList<>();
                                patchFieldNames.add("monthlyAmount");
                                Map<String, Object> monthlyAmountAsProps = FieldUtil.getAsProperties(month);
                                monthlyAmountAsProps.put("currencyCode", budget.getCurrencyCode());
                                CurrencyUtil.checkAndUpdateCurrencyProps(monthlyAmountAsProps, oldBudgetRecord, baseCurrency, currencyMap, patchFieldNames, monthlyAmountMultiCurrencyFields);
                                month = FieldUtil.getAsBeanFromMap(monthlyAmountAsProps, BudgetMonthlyAmountContext.class);

                            } else {
                                month = (BudgetMonthlyAmountContext) CurrencyUtil.addMultiCurrencyData(monthlyAmountModule.getName(), monthlyAmountMultiCurrencyFields, Collections.singletonList(month), BudgetMonthlyAmountContext.class, baseCurrency, currencyMap, false).get(0);
                            }
                            monthlySplit.add(month);
                            Double monthlyAmount = month.getMonthlyAmount();
                            yearlyAmnt += monthlyAmount;
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
                            Double monthlyAmount = month.getMonthlyAmount();
                            yearlyAmnt += monthlyAmount;
                            monthlySplit.add(month);
                            count++;
                        }
                    }
                    V3RecordAPI.addRecord(false, monthlySplit, monthlyAmountModule, monthlyAmountModuleFields);

                    //updating yearly amnt in budget amnt
                    budgetAmnt.setYearlyAmount(yearlyAmnt);
                    budgetAmnt.setResource(budget.getFocalPointResource());
                    if (budgetAmnt.getAccount() != null) {
                        budgetAmnt.setAmountType(budgetAmnt.getAccount().getAmountType());
                    }

                    if (isEdit) {
                        patchFieldNames = new ArrayList<>();
                        patchFieldNames.add("yearlyAmount");
                        Map<String, Object> budgetAmountAsProps = FieldUtil.getAsProperties(budgetAmnt);
                        budgetAmountAsProps.put("currencyCode", budget.getCurrencyCode());
                        CurrencyUtil.checkAndUpdateCurrencyProps(budgetAmountAsProps, oldBudgetRecord, baseCurrency, currencyMap, patchFieldNames, budgetAmountMultiCurrencyFields);
                        budgetAmnt = FieldUtil.getAsBeanFromMap(budgetAmountAsProps, BudgetAmountContext.class);
                    } else {
                        budgetAmnt = (BudgetAmountContext) CurrencyUtil.addMultiCurrencyData(budgetAmountModule.getName(), budgetAmountMultiCurrencyFields, Collections.singletonList(budgetAmnt), BudgetAmountContext.class, baseCurrency, currencyMap).get(0);
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
