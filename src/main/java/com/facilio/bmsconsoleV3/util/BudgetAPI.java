package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetMonthlyAmountContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.CurrencyUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class BudgetAPI {

    public static String CURRENCY_PATTERN ="0.00";

    public static List<BudgetAmountContext> setBudgetAmount(Long id, boolean fetchSplitUp, Map<String, Object> currencyInfo) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET_AMOUNT);
        List<FacilioField> budgetAmountFields = modBean.getAllFields(module.getName());
        budgetAmountFields.addAll(FieldFactory.getCurrencyPropsFields(module));
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(budgetAmountFields);

        SelectRecordsBuilder<BudgetAmountContext> builder = new SelectRecordsBuilder<BudgetAmountContext>()
                .module(module)
                .beanClass(BudgetAmountContext.class)
                .select(budgetAmountFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("budget"), String.valueOf(id), NumberOperators.EQUALS))
                .orderBy("ID asc")
                ;
        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        LookupField resourceField = (LookupField) fieldMap.get("account");
        additionaLookups.add(resourceField);

        builder.fetchSupplements(additionaLookups);

        List<BudgetAmountContext> budgetAmounts = builder.get();
        if(CollectionUtils.isNotEmpty(budgetAmounts)){
            if(fetchSplitUp) {
                for (BudgetAmountContext budgetAmount : budgetAmounts) {
                    List<BudgetMonthlyAmountContext> splitList =  getMonthlySplitUp(budgetAmount.getId(), currencyInfo);
                    if(CollectionUtils.isNotEmpty(splitList)) {
                        List<Map<String, Object>> mapList = FieldUtil.getAsMapList(splitList, BudgetMonthlyAmountContext.class);
                        for(Map<String, Object> map : mapList){
                            map.remove("account");
                            map.remove("resource");
                            map.remove("budgetAmount");
                            map.values().removeAll(Collections.singleton(null));
                        }
                        budgetAmount.setMonthlyAmountSplitUp(mapList);
                        CurrencyUtil.checkAndFillBaseCurrencyToRecord(budgetAmount, currencyInfo);
                    }
                }
            }
            return budgetAmounts;
        }

        return null;
    }


    private static List<BudgetMonthlyAmountContext> getMonthlySplitUp(Long parentId, Map<String, Object> currencyInfo) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Budget.BUDGET_MONTHLY_AMOUNT);
        List<FacilioField> budgetMonthlyAmountFields = modBean.getAllFields(module.getName());
        budgetMonthlyAmountFields.addAll(FieldFactory.getCurrencyPropsFields(module));
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(budgetMonthlyAmountFields);

        SelectRecordsBuilder<BudgetMonthlyAmountContext> builder = new SelectRecordsBuilder<BudgetMonthlyAmountContext>()
                .module(module)
                .beanClass(BudgetMonthlyAmountContext.class)
                .select(budgetMonthlyAmountFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("budgetAmount"), String.valueOf(parentId), NumberOperators.EQUALS))
                ;

        List<BudgetMonthlyAmountContext> monthlyList = builder.get();
        if(CollectionUtils.isNotEmpty(monthlyList))
        {
            for (BudgetMonthlyAmountContext budgetMonthlyAmountContext : monthlyList) {
                CurrencyUtil.checkAndFillBaseCurrencyToRecord(budgetMonthlyAmountContext, currencyInfo);
            }

            return monthlyList;
        }
        return null;
    }

}
