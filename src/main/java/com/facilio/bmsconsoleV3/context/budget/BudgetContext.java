package com.facilio.bmsconsoleV3.context.budget;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.util.BudgetAPI;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class BudgetContext extends V3Context {
    private String name;

    //start month  of fiscal year of budget
    private Integer fiscalYear;

    //year of budget
    private Integer fiscalYearStart;

    private Long startDate;
    private Long endDate;

    private FocalPointType focalPointType;

    public enum FocalPointType implements FacilioEnum {
        COMPANY("Company"),
        ASSET("Asset"),
        LOCATION("Location")
        ;
        private String name;

        FocalPointType(String name) {
            this.name = name;
        }

        public static FocalPointType valueOf(int value) {
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

    public void setFocalPointType(Integer type) {
        if (type != null) {
            this.focalPointType = FocalPointType.valueOf(type);
        }
    }

    public FocalPointType getFocalPointTypeEnum() {
        return focalPointType;
    }
    public Integer getFocalPointType() {
        if (focalPointType != null) {
            return focalPointType.getIndex();
        }
        return null;
    }


    private ResourceContext focalPointResource;
    private Double totalIncome;
    private Double totalExpenses;
    private Double totalNetIncome;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public Integer getFiscalYearStart() {
        return fiscalYearStart;
    }

    public void setFiscalYearStart(Integer fiscalYearStart) {
        this.fiscalYearStart = fiscalYearStart;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalInc) {
        if(totalInc != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.totalIncome = Double.valueOf(df.format(totalInc));
        }
    }

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpense) {
        if(totalExpense != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.totalExpenses = Double.valueOf(df.format(totalExpense));
        }
    }

    public Double getTotalNetIncome() {
        totalNetIncome =  0d;
        if(totalIncome != null)  {
            totalNetIncome = totalIncome;
        }
        if(totalExpenses != null) {
            totalNetIncome = totalNetIncome - totalExpenses;
        }
        return totalNetIncome;
    }

    public void setTotalNetIncome(Double totalNetIncome) {
        this.totalNetIncome = totalNetIncome;
    }

    public ResourceContext getFocalPointResource() {
        return focalPointResource;
    }

    public void setFocalPointResource(ResourceContext focalPointResource) {
        this.focalPointResource = focalPointResource;
    }

    private List<BudgetAmountContext> budgetAmountList;

    public List<BudgetAmountContext> getBudgetAmountList() {
        return budgetAmountList;
    }

    public void setBudgetAmountList(List<BudgetAmountContext> budgetAmountList) {
        this.budgetAmountList = budgetAmountList;
    }

    public String getTotalIncomeString() {
        if(totalIncome != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            return df.format(this.totalIncome);
        }
        return null;
    }

    public String getTotalExpensesString() {
        if(totalExpenses != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            return df.format(this.totalExpenses);
        }
        return null;
    }
}
