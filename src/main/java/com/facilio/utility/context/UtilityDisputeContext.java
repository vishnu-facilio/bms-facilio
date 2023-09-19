package com.facilio.utility.context;

import com.facilio.accounts.dto.User;
import com.facilio.alarms.sensor.SensorRuleType;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.utility.UtilityDisputeType;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;

import java.util.Map;

@Getter
@Setter
public class UtilityDisputeContext extends V3Context {
    private static final long serialVersionUID = 1L;
    private static final UtilityDisputeType[] UTILITY_DISPUTE_TYPES = UtilityDisputeType.values();
    String subject;
    UtilityDisputeType type;


    public UtilityDisputeType getTypeEnum() {
        return type;
    }

    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }

    public void setType(UtilityDisputeType type) {
        this.type = type;
    }

    public void setType(int type) {
        if (type > 0) {
            this.type = UTILITY_DISPUTE_TYPES[type - 1];
        } else {
            this.type = null;
        }
    }
//    public void setType(UtilityDisputeType type) {
//        this.type = type;
//    }
//    public UtilityDisputeType geTypeEnum() {
//        return type;
//    }
//    public int getType() {
//        if(type != null) {
//            return type.getValue();
//        }
//        return -1;
//    }
//    public void setType(int type) {
//        this.type = UtilityDisputeType.valueOf(type);
//    }
    UtilityIntegrationCustomerContext account;
    UtilityIntegrationBillContext utilityBill;

    String utilityType;
    BillStatus billStatus;

    public BillStatus getBillStatusEnum() {
        return billStatus;
    }

    public Integer getBillStatus() {
        if(billStatus != null) {
            return billStatus.getIndex();
        }
        else {
            return null;
        }
    }
    public void setBillStatus(Integer billStatus) {
        if (billStatus != null) {
            this.billStatus = BillStatus.valueOf(billStatus);
        }
    }
    public  enum BillStatus implements FacilioIntEnum {

        DISPUTE(1,"Under Dispute"),
        RESOLVED(2,"Dispute Resolved");

        Integer intVal;
        String value;
        BillStatus(Integer intVal,String value) {
            this.intVal = intVal;
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }

        public Integer getIntVal() {
            return intVal;
        }
        public void setIntVal(Integer intVal) {
            this.intVal = intVal;
        }
        public static BillStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }


    }
   String accountNumber;
    Long billDate;
    Long cutOffDate;
    String supplier;
    Long terminatedOn;

    //double disputedCost;
    Double billTotal;

    Double actualMeterConsumption;
    Double billMeterConsumption;

    Double disputedConsumption;

    Double expectedCost;
    Double actualCost;
    Double differenceInCost;
//
//    double averageConsumptionLowerLimit;
//    double averageConsumptionUpperLimit;
//

    public UtilityDisputeContext() {super();}

    String tariffToBeApplied;
    String tariffApplied;
    PeopleContext resolvedBy;
    Long resolvedTime;

    public UtilityDisputeContext( String subject, UtilityDisputeType type, UtilityIntegrationCustomerContext account, UtilityIntegrationBillContext utilityBill,BillStatus status,String tariffToBeApplied,String tariffApplied) {

        this.subject = subject;
        this.type = type;
        this.account = account;
        this.utilityBill  = utilityBill;
        this.billStatus = BillStatus.valueOf(BillStatus.DISPUTE.getIntVal());
        this.tariffToBeApplied = tariffToBeApplied;
        this.tariffApplied = tariffApplied;


        if(utilityBill != null) {
            this.accountNumber = utilityBill.getBillingAccount();
            //this. billDate = utilityBill.getBillStatementDate();
            this.billTotal = utilityBill.getBillTotalCost();
            this.utilityType = utilityBill.getServiceClass();
        }
        if(this.account != null) {
            this.supplier = this.account.getUtilityID();
        }

    }

    public UtilityDisputeContext( String subject, UtilityDisputeType type, UtilityIntegrationCustomerContext account, long billDate,BillStatus status) {

        this.subject = subject;
        this.type = type;
        this.account = account;
        this.billDate = billDate;
        this.billStatus = BillStatus.valueOf(BillStatus.DISPUTE.getIntVal());



        if(utilityBill != null) {
            this.accountNumber = utilityBill.getBillingAccount();
           // this. billDate = utilityBill.getBillStatementDate();
            this.billTotal = utilityBill.getBillTotalCost();
            this.utilityType = utilityBill.getServiceClass();
        }
        if(this.account != null) {
            this.supplier = this.account.getUtilityID();
        }
    }
    public UtilityDisputeContext( String subject, UtilityDisputeType type, UtilityIntegrationCustomerContext account, UtilityIntegrationBillContext utilityBill,BillStatus status) {

        this.subject = subject;
        this.type = type;
        this.account = account;
        //this.billDate = billDate;
        this.billStatus = BillStatus.valueOf(BillStatus.DISPUTE.getIntVal());
        this.utilityBill = utilityBill;


        if(utilityBill != null) {
            this.accountNumber = utilityBill.getBillingAccount();
            //this. billDate = utilityBill.getBillStatementDate();
            this.billTotal = utilityBill.getBillTotalCost();
            this.utilityType = utilityBill.getServiceClass();
        }
        if(this.account != null) {
            this.supplier = this.account.getUtilityID();
            this.terminatedOn = this.account.getRevoked();
        }
    }

    public UtilityDisputeContext( String subject, UtilityDisputeType type, UtilityIntegrationCustomerContext account, UtilityIntegrationBillContext utilityBill,BillStatus status,Double actualConsumption,Double billMeterConsumption,Double difference) {

        this.subject = subject;
        this.type = type;
        this.account = account;
       // this.billDate = billDate;
        this.utilityBill = utilityBill;
        this.billStatus = BillStatus.valueOf(BillStatus.DISPUTE.getIntVal());

        if(utilityBill != null) {
            this.accountNumber = utilityBill.getBillingAccount();
            //this. billDate = utilityBill.getBillStatementDate();
            this.billTotal = utilityBill.getBillTotalCost();
            this.utilityType = utilityBill.getServiceClass();
        }
        if(this.account != null) {
            this.supplier = this.account.getUtilityID();
        }
        this.actualMeterConsumption = actualConsumption;
        this.billMeterConsumption =  billMeterConsumption;
        this.disputedConsumption = difference;
    }
    public UtilityDisputeContext( String subject, UtilityDisputeType type, UtilityIntegrationCustomerContext account, UtilityIntegrationBillContext utilityBill,Double calculatedBillAmount,Double billAmount,Double difference) {

        this.subject = subject;
        this.type = type;
        this.account = account;
        this.billStatus = BillStatus.valueOf(BillStatus.DISPUTE.getIntVal());
        this.utilityBill = utilityBill;

        if(utilityBill != null) {
            this.accountNumber = utilityBill.getBillingAccount();
            //this. billDate = utilityBill.getBillStatementDate();
            this.billTotal = utilityBill.getBillTotalCost();
            this.utilityType = utilityBill.getServiceClass();
        }
        if(this.account != null) {
            this.supplier = this.account.getUtilityID();
        }
        this.expectedCost = calculatedBillAmount;
        this.actualCost =  billAmount;
        this.differenceInCost = difference;
    }




}
