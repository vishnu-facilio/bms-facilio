package com.facilio.bmsconsoleV3.context.budget;

import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

public class BudgetAmountContext extends V3Context {

    private AmountType amountType;
    private ChartOfAccountContext account;
    private Double yearlyAmount;

    public enum AmountType implements FacilioEnum {
        INCOME("Income"),
        EXPENSE("Expense")
        ;
        private String name;

        AmountType(String name) {
            this.name = name;
        }

        public static AmountType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public void setAmountType(Integer type) {
        if (type != null) {
            this.amountType = AmountType.valueOf(type);
        }
    }

    public AmountType getAmountTypeEnum() {
        return amountType;
    }
    public Integer getAmountType() {
        if (amountType != null) {
            return amountType.getIndex();
        }
        return null;
    }

    public ChartOfAccountContext getAccount() {
        return account;
    }

    public void setAccount(ChartOfAccountContext account) {
        this.account = account;
    }

    public Double getYearlyAmount() {
        return yearlyAmount;
    }

    public void setYearlyAmount(Double yearlyAmnt) {
        if(yearlyAmnt != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.yearlyAmount = Double.valueOf(df.format(yearlyAmnt));
        }
    }

    private Map<String, Object> monthlyAmountSplitUp;

    public Map<String, Object> getMonthlyAmountSplitUp() {
        return monthlyAmountSplitUp;
    }

    public void setMonthlyAmountSplitUp(Map<String, Object> monthlyAmount) {
        this.monthlyAmountSplitUp = monthlyAmount;
    }

    private BudgetContext budget;

    public BudgetContext getBudget() {
        return budget;
    }

    public void setBudget(BudgetContext budget) {
        this.budget = budget;
    }

    public String getYearlyAmountString() {
        if(yearlyAmount != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            return df.format(this.yearlyAmount);
        }
        return null;
    }
}
