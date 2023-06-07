package com.facilio.utility;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.time.DateTimeUtil;
import com.facilio.utility.context.UtilityDisputeContext;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import com.facilio.utility.context.UtilityIntegrationMeterContext;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;

public  enum UtilityDisputeType implements RuleInterface, FacilioIntEnum {

    BILL_MISSING(1) {
        @Override
        public UtilityDisputeContext execute(UtilityIntegrationBillContext context,String tariffToBeApplied,String tariffApplied) throws Exception {
            return null;
        }
        @Override
        public  UtilityDisputeContext ValidateBillMissing(long billDate,UtilityIntegrationMeterContext list)throws Exception {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            LocalDate billMonth = LocalDate.parse(formatter.format(billDate));
            Month month = billMonth.getMonth();
            int year = billMonth.getYear();

            long date =  DateTimeUtil.addMonths(DateTimeUtil.getMonthStartTime(), -1);

            String subject = "No bill Generated for " + month + "," + year;

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,list.getUtilityIntegrationCustomer().getId())),UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject,this,customerContext,date,UtilityDisputeContext.BillStatus.DISPUTE);

            return dispute;
        }

        @Override
        public UtilityDisputeContext validateConumptionMismatch(UtilityIntegrationBillContext context,Double actualConsumption,Double billMeterConsumption,Double difference) throws Exception {
            return null;
        }
        public UtilityDisputeContext validateCostMismatch(UtilityIntegrationBillContext context,Double calculatedBillAmount,Double billAmount,Double difference) throws Exception{
            return null;
        }
        public UtilityDisputeContext validateTerminatedAccount(UtilityIntegrationBillContext context) throws Exception {
            return null;
        }

    },
    BILL_FOR_TERMINATED_ACCOUNT (2){
        public UtilityDisputeContext execute(UtilityIntegrationBillContext context,String tariffToBeApplied,String tariffApplied) throws Exception {
            return null;
        }
        @Override
        public UtilityDisputeContext validateTerminatedAccount(UtilityIntegrationBillContext context) throws Exception {

            String subject = "Bill generated for a terminated account";

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,context.getUtilityIntegrationCustomer().getId())),UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject,this,customerContext,context, UtilityDisputeContext.BillStatus.DISPUTE);

            return dispute;

        }

        @Override
        public  UtilityDisputeContext ValidateBillMissing(long billDate,UtilityIntegrationMeterContext list)throws Exception {
            return null;
        }
        @Override
        public UtilityDisputeContext validateConumptionMismatch(UtilityIntegrationBillContext context,Double actualConsumption,Double billMeterConsumption,Double difference) throws Exception {
            return null;
        }
        public UtilityDisputeContext validateCostMismatch(UtilityIntegrationBillContext context,Double calculatedBillAmount,Double billAmount,Double difference) throws Exception{
            return null;
        }
    },
    CONSUMPTION_READING_MISMATCH(3){
        @Override
        public UtilityDisputeContext validateConumptionMismatch(UtilityIntegrationBillContext context,Double actualConsumption, Double billMeterConsumption,Double difference) throws Exception {

            String subject = "Consumption mismatch in the bill generated";

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,context.getUtilityIntegrationCustomer().getId())),UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject,this,customerContext,context,UtilityDisputeContext.BillStatus.DISPUTE,actualConsumption,billMeterConsumption,difference);
            return dispute;
        }
        @Override
        public  UtilityDisputeContext ValidateBillMissing(long billDate, UtilityIntegrationMeterContext list)throws Exception {

            return null;
        }
        @Override
        public UtilityDisputeContext execute(UtilityIntegrationBillContext context,String tariffToBeApplied,String tariffApplied) throws Exception {
            return null;
        }
        public UtilityDisputeContext validateCostMismatch(UtilityIntegrationBillContext context,Double calculatedBillAmount,Double billAmount,Double difference) throws Exception{
            return null;
        }
        public UtilityDisputeContext validateTerminatedAccount(UtilityIntegrationBillContext context) throws Exception {
            return null;
        }
    },
    TARIFF_MAPPING_MISMATCH (4){
        @Override
        public UtilityDisputeContext execute(UtilityIntegrationBillContext context,String tariffToBeApplied,String tariffApplied) throws Exception {

            String subject = "Bill generated with tariff mismatch";

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,context.getUtilityIntegrationCustomer().getId())),UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject,this,customerContext,context,UtilityDisputeContext.BillStatus.DISPUTE,tariffToBeApplied,tariffApplied);

            return dispute;
        }
        @Override
        public  UtilityDisputeContext ValidateBillMissing(long billDate, UtilityIntegrationMeterContext list)throws Exception {

        return null;
    }
        @Override
        public UtilityDisputeContext validateConumptionMismatch(UtilityIntegrationBillContext context,Double actualConsumption,Double billMeterConsumption,Double difference) throws Exception {
            return null;
        }
        public UtilityDisputeContext validateCostMismatch(UtilityIntegrationBillContext context,Double calculatedBillAmount,Double billAmount,Double difference) throws Exception{
            return null;
        }
        public UtilityDisputeContext validateTerminatedAccount(UtilityIntegrationBillContext context) throws Exception {
            return null;
        }

    },
    COST_MISMATCH(5) {
        public UtilityDisputeContext validateCostMismatch(UtilityIntegrationBillContext context,Double calculatedBillAmount,Double billAmount,Double difference) throws Exception{

            String subject = "Total cost  mismatch in the bill generated";

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,context.getUtilityIntegrationCustomer().getId())),UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject,this,customerContext,context,calculatedBillAmount,billAmount,difference);
            return dispute;
        }
        @Override
        public UtilityDisputeContext validateConumptionMismatch(UtilityIntegrationBillContext context, Double actualConsumption, Double billMeterConsumption, Double difference) throws Exception {

            return null;
        }

        @Override
        public UtilityDisputeContext ValidateBillMissing(long billDate, UtilityIntegrationMeterContext list) throws Exception {

            return null;
        }

        @Override
        public UtilityDisputeContext execute(UtilityIntegrationBillContext context,String tariffToBeApplied,String tariffApplied) throws Exception {
            return null;
        }
        public UtilityDisputeContext validateTerminatedAccount(UtilityIntegrationBillContext context) throws Exception {
            return null;
        }
    };

    Integer type;

    private UtilityDisputeType(Integer type) {
        this.type = type;

    }


    public Integer getType() {
        return type;
    }

    public Integer getIndex() {
        return ordinal()+1;
    }

    public static UtilityDisputeType valueOf (int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

}
