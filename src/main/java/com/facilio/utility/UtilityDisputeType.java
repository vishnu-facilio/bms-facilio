package com.facilio.utility;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
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

    BILL_MISSING(1,"Bill missing") {
        @Override
        public  UtilityDisputeContext execute(FacilioContext context) throws Exception {
                //long billDate,UtilityIntegrationMeterContext list)
            long billDate = (long) context.get("billDate");
            UtilityIntegrationMeterContext list = (UtilityIntegrationMeterContext) context.get("list");

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



    },
    BILL_FOR_TERMINATED_ACCOUNT (2, "Bill for terminated account"){
        public UtilityDisputeContext execute(FacilioContext context) throws Exception {
            UtilityIntegrationBillContext bill = (UtilityIntegrationBillContext) context.get("billList");

            String subject = "Bill generated for a terminated account";

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,bill.getUtilityIntegrationCustomer().getId())),UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject,this,customerContext,bill, UtilityDisputeContext.BillStatus.DISPUTE);

            return dispute;
        }

    },
    CONSUMPTION_READING_MISMATCH(3,"Consumption mismatch"){
        @Override
        public UtilityDisputeContext execute(FacilioContext context) throws Exception {
                //UtilityIntegrationBillContext context,Double actualConsumption, Double billMeterConsumption,Double difference) throws Exception {
            UtilityIntegrationBillContext bill = (UtilityIntegrationBillContext) context.get("bill");
            Double actualConsumption = (Double) context.get("actualConsumption");
            Double billMeterConsumption = (Double) context.get("billTotalVolume");
            Double difference= (Double) context.get("difference");

            String subject = "Consumption mismatch in the bill generated";

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,bill.getUtilityIntegrationCustomer().getId())),UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject,this,customerContext,bill,UtilityDisputeContext.BillStatus.DISPUTE,actualConsumption,billMeterConsumption,difference);
            return dispute;
        }

    },
    TARIFF_MAPPING_MISMATCH (4,"Tariff mapping mismatch"){
        @Override
        public UtilityDisputeContext execute(FacilioContext context) throws Exception {
            UtilityIntegrationBillContext bill = (UtilityIntegrationBillContext) context.get("bill");
            String tariffToBeApplied = (String) context.get("meterServiceTariff");
            String tariffApplied = (String) context.get("billServiceTariff");

            String subject = "Bill generated with tariff mismatch";

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER, bill.getUtilityIntegrationCustomer().getId())), UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject, this, customerContext, bill, UtilityDisputeContext.BillStatus.DISPUTE, tariffToBeApplied, tariffApplied);

            return dispute;
        }
    },
    COST_MISMATCH(5,"Cost mismatch") {
        public UtilityDisputeContext execute(FacilioContext context) throws Exception{
            //UtilityIntegrationBillContext context,Double calculatedBillAmount,Double billAmount,Double difference
            UtilityIntegrationBillContext bill = (UtilityIntegrationBillContext) context.get("bill");
                    Double calculatedBillAmount = (Double) context.get("calculatedBill");
                    Double billAmount = (Double) context.get("billTotalCost");
                    Double difference = (Double) context.get("diff");

            String subject = "Total cost  mismatch in the bill generated";

            UtilityIntegrationCustomerContext customerContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER,bill.getUtilityIntegrationCustomer().getId())),UtilityIntegrationCustomerContext.class);
            UtilityDisputeContext dispute = new UtilityDisputeContext(subject,this,customerContext,bill,calculatedBillAmount,billAmount,difference);
            return dispute;
        }

    };

    private final Integer type;
    private final String value;


    private UtilityDisputeType(Integer type,String value) {
        this.type = type;
        this.value = value;

    }


    public Integer getType() {
        return type;
    }
    @Override
    public String getValue() {
        return value;
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
