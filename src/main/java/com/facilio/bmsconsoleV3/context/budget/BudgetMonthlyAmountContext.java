package com.facilio.bmsconsoleV3.context.budget;

import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.v3.context.V3Context;

import java.text.DecimalFormat;

public class BudgetMonthlyAmountContext extends V3Context {

    private Integer monthIdentifier;
    private BudgetAmountContext budgetAmount;
    private  Double monthlyAmount;

    public Integer getMonthIdentifier() {
        return monthIdentifier;
    }

    public void setMonthIdentifier(Integer monthIdentifier) {
        this.monthIdentifier = monthIdentifier;
    }

    public BudgetAmountContext getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BudgetAmountContext budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public Double getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(Double monthlyAmnt) {
        if(monthlyAmnt != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            monthlyAmount = Double.valueOf(df.format(monthlyAmnt));
        }
    }
    public String getMonthlyAmountString() {
        if(monthlyAmount != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            return df.format(this.monthlyAmount);
        }
        return null;
    }

}
