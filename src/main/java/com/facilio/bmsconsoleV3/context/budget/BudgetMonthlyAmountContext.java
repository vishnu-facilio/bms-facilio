package com.facilio.bmsconsoleV3.context.budget;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.v3.context.V3Context;

import java.text.DecimalFormat;

public class BudgetMonthlyAmountContext extends V3Context {

    private Long startDate;
    private ResourceContext resource;
    private ChartOfAccountContext account;
    private BudgetAmountContext budgetAmount;
    private  Double monthlyAmount;

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

    private Double actualMonthlyAmount;

    public Double getActualMonthlyAmount() {
        return actualMonthlyAmount;
    }

    public void setActualMonthlyAmount(Double actualAmount) {
        if(actualAmount != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.actualMonthlyAmount = Double.valueOf(df.format(actualAmount));
        }
    }

    public String getActualMonthlyAmountString() {
        if(actualMonthlyAmount != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            return df.format(this.actualMonthlyAmount);
        }
        return null;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long monthStartDate) {
        this.startDate = monthStartDate;
    }

    public ResourceContext getResource() {
        return resource;
    }

    public void setResource(ResourceContext resource) {
        this.resource = resource;
    }

    public ChartOfAccountContext getAccount() {
        return account;
    }

    public void setAccount(ChartOfAccountContext account) {
        this.account = account;
    }
}
