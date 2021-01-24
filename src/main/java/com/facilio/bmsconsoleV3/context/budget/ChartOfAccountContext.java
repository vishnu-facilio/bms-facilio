package com.facilio.bmsconsoleV3.context.budget;

import com.facilio.v3.context.V3Context;

public class ChartOfAccountContext extends V3Context {

    private String name;
    private AccountTypeContext type;
    private ChartOfAccountContext parentAccount;
    private String description;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountTypeContext getType() {
        return type;
    }

    public void setType(AccountTypeContext type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ChartOfAccountContext getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(ChartOfAccountContext parentAccount) {
        this.parentAccount = parentAccount;
    }

    public Integer getAmountType() {
        if(this.type != null) {
            if(type.getGroupEnum() == AccountTypeContext.Group.INCOME){
                return BudgetAmountContext.AmountType.INCOME.getIndex();
            }
            else if(type.getGroupEnum() == AccountTypeContext.Group.EXPENSE) {
                return BudgetAmountContext.AmountType.EXPENSE.getIndex();
            }
        }
        return null;
    }

    private Integer accountType;

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }
}
