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
    private FiscalYear fiscalYear;

    //year of budget
    private FiscalYearStart fiscalYearStart;

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


    public enum FiscalYearStart implements FacilioEnum {
        FY2019("2019"),
        FY2020("2020"),
        FY2021("2021"),
        FY2022("2022"),
        FY2023("2023"),
        FY2024("2024"),
        FY2025("2025"),
        FY2026("2026"),
        FY2027("2027"),
        FY2028("2028"),
        FY2029("2029"),
        FY2030("2030");

        private String name;

        FiscalYearStart(String name) {
            this.name = name;
        }

        public static FiscalYearStart valueOf(int value) {
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

    public void setFiscalYearStart(Integer type) {
        if (type != null) {
            this.fiscalYearStart = FiscalYearStart.valueOf(type);
        }
    }

    public FiscalYearStart getFiscalYearStartEnum() {
        return fiscalYearStart;
    }
    public Integer getFiscalYearStart() {
        if (fiscalYearStart != null) {
            return fiscalYearStart.getIndex();
        }
        return null;
    }

    public enum FiscalYear implements FacilioEnum {
        JAN("January"),
        FEB("February"),
        MAR("March"),
        APR("April"),
        MAY("May"),
        JUN("June"),
        JUL("July"),
        AUG("August"),
        SEP("September"),
        OCT("October"),
        NOV("November"),
        DEC("December")

                ;
        private String name;

        FiscalYear(String name) {
            this.name = name;
        }

        public static FiscalYear valueOf(int value) {
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

    public void setFiscalYear(Integer type) {
        if (type != null) {
            this.fiscalYear = FiscalYear.valueOf(type);
        }
    }

    public FiscalYear getFiscalYearEnum() {
        return fiscalYear;
    }
    public Integer getFiscalYear() {
        if (fiscalYear != null) {
            return fiscalYear.getIndex();
        }
        return null;
    }


    private Double actualTotalExpense;
    private Double actualTotalIncome;

    public Double getActualTotalIncome() {
        return actualTotalIncome;
    }

    public void setActualTotalIncome(Double totalInc) {
        if(totalInc != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.actualTotalIncome = Double.valueOf(df.format(totalInc));
        }
    }

    public Double getActualTotalExpense() {
        return actualTotalExpense;
    }

    public void setActualTotalExpense(Double totalExp) {
        if(totalExp != null) {
            final DecimalFormat df = new DecimalFormat(BudgetAPI.CURRENCY_PATTERN);
            this.actualTotalExpense = Double.valueOf(df.format(totalExp));
        }
    }



}
