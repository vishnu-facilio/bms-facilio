package com.facilio.bmsconsoleV3.context.budget;

import com.facilio.v3.context.V3Context;

public class ChartOfAccountContext extends V3Context {

    private String name;
    private AccountTypeContext type;
    private Long parentAccountId;
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

    public Long getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(Long parentAccountId) {
        this.parentAccountId = parentAccountId;
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
}
