package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.context.budget.ChartOfAccountContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

public class V3TransactionContext extends V3Context {

    private Long transactionDate;
    private String transactionName;
    private String transactionRollUpModuleName;
    private String transactionRollUpFieldName;
    private String transactionSourceModuleName;
    private Long transactionSourceRecordId;
    private ResourceContext transactionResource;
    private ChartOfAccountContext account;
    private TransactionTypes transactionType;

    public enum TransactionTypes implements FacilioIntEnum {
        CREDIT("Credit"),
        DEBIT("Debit")
        ;
        private String name;

        TransactionTypes(String name) {
            this.name = name;
        }

        public static TransactionTypes valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public void setTransactionType(Integer type) {
        if (type != null) {
            this.transactionType = TransactionTypes.valueOf(type);
        }
    }

    public TransactionTypes getTransactionTypeEnum() {
        return transactionType;
    }
    public Integer getTransactionType() {
        if (transactionType != null) {
            return transactionType.getIndex();
        }
        return null;
    }


    private Double transactionAmount;

    public Long getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Long transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public ResourceContext getTransactionResource() {
        return transactionResource;
    }

    public void setTransactionResource(ResourceContext transactionResource) {
        this.transactionResource = transactionResource;
    }

    public ChartOfAccountContext getAccount() {
        return account;
    }

    public void setAccount(ChartOfAccountContext account) {
        this.account = account;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionRollUpModuleName() {
        return transactionRollUpModuleName;
    }

    public void setTransactionRollUpModuleName(String transactionRollUpModuleName) {
        this.transactionRollUpModuleName = transactionRollUpModuleName;
    }

    public String getTransactionRollUpFieldName() {
        return transactionRollUpFieldName;
    }

    public void setTransactionRollUpFieldName(String transactionRollUpFieldName) {
        this.transactionRollUpFieldName = transactionRollUpFieldName;
    }

    public String getTransactionSourceModuleName() {
        return transactionSourceModuleName;
    }

    public void setTransactionSourceModuleName(String transactionSourceModuleName) {
        this.transactionSourceModuleName = transactionSourceModuleName;
    }

    public Long getTransactionSourceRecordId() {
        return transactionSourceRecordId;
    }

    public void setTransactionSourceRecordId(Long transactionSourceRecordId) {
        this.transactionSourceRecordId = transactionSourceRecordId;
    }
}
