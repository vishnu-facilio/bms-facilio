package com.facilio.bmsconsoleV3.context.ocr;



import java.util.ArrayList;
import java.util.List;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParsedBillContext extends V3Context {
    public ParsedBillContext(long id){
        this.setId(id);
    };
    private String name;
    private Long billMonth;
    private BillTemplateContext billTemplate;
    private ActualBillContext actualBill;
    private Long zipFileId;
    private Long rawTextFileId;
    private Long formFileId;
    private Long billFileId;
    private ParsingStatus parsingStatus;
    private ParsedAccountBillContext parsedAccountBill;
    String billUid;
    String meterUid;
    String customerUid;
    String meta;
    Long createdTime;
    Long updatedTime;
    String utilityID;
    String serviceIdentifier;
    String serviceTariff;
    String serviceAddress;
    List<String> meterNumber;
    String billingContact;
    String billingAddress;
    String billingAccount;
    String serviceClass;
    Long billStatementDate;
    Long billStartDate;
    Long billEndDate;
    String billTotalUnit;
    Double billTotalCost;
    Double billTotalVolume;
    String supplierType;
    String supplierName;
    String supplierServiceId;
    String supplierTariff;
    String supplierTotalUnit;
    Double supplierTotalCost;
    Double supplierTotalVolume;
    String sourceType;
    String sourceUrl;
    String sourceDownloadUrl;
    UtilityIntegrationCustomerContext utilityIntegrationCustomer;
    UtilityIntegrationMeterContext utilityIntegrationMeter;
    
    List<PreUtilityIntegerationLineItems> lineItems;

    Type billType;
    public Integer getBillType() {
        if (billType != null) {
            return billType.getValue();
        }
        return -1;
    }

    public void setBillType(Integer billType) {
        if (billType != null) {
            this.billType = ParsedBillContext.Type.valueOf(billType);
        }
    }




    public static enum Type {
        MANUAL(1,"Manually Generated"),
        AUTO_GENERATED(2, "Auto Generated"),

        ;

        Integer intVal;
        String name;

        private Type(Integer intVal, String name) {
            this.intVal = intVal;
            this.name = name;

        }
        public Integer getIntVal() {
            return intVal;
        }
        public void setIntVal(Integer intVal) {
            this.intVal = intVal;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getValue() {
            return ordinal() + 1;
        }

        public static ParsedBillContext.Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public void addLineItems(List<PreUtilityIntegerationLineItems> lineItems) {
        // TODO Auto-generated method stub

        utilityIntegrationLineItemContexts = utilityIntegrationLineItemContexts == null ? new ArrayList<PreUtilityIntegerationLineItems>() :  utilityIntegrationLineItemContexts;

        utilityIntegrationLineItemContexts.addAll(lineItems);

    }

    public void addLineItem(PreUtilityIntegerationLineItems lineItem) {
        // TODO Auto-generated method stub

        utilityIntegrationLineItemContexts = utilityIntegrationLineItemContexts == null ? new ArrayList<PreUtilityIntegerationLineItems>() :  utilityIntegrationLineItemContexts;

        utilityIntegrationLineItemContexts.add(lineItem);

    }

    List<PreUtilityIntegerationLineItems> utilityIntegrationLineItemContexts;
    
    List<ParsedBillTableContext> parsedBillTables;

    private ParsedBillStatus status;

    public Integer getStatus() {
        if (status == null) {
            return null;
        }
        return status.getIndex();
    }
    public void setStatus(Integer status) {
        if (status != null) {
            this.status = ParsedBillStatus.valueOf(status);
        } else {
            this.status = null;
        }
    }

    @AllArgsConstructor
    public static enum ParsedBillStatus implements FacilioIntEnum {
        BILL_UPLOADED("Bill Uploaded"),
        PENDING("Pending"),
        APPROVED("Approved"),
        REJECTED("Rejected")
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final ParsedBillStatus[] STATUS = ParsedBillStatus.values();
        public static ParsedBillStatus valueOf(int type) {
            if (type > 0 && type <= STATUS.length) {
                return STATUS[type - 1];
            }
            return null;
        }
    }

    @AllArgsConstructor
    public static enum ParsingStatus implements FacilioIntEnum {
        PARSING_SUCCESS("Parsing Success"),
        PARTIAL_SUCCESS("Partial Success")
        ;
        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final ParsingStatus[] STATUS = ParsingStatus.values();
        public static ParsingStatus valueOf(int type) {
            if (type > 0 && type <= STATUS.length) {
                return STATUS[type - 1];
            }
            return null;
        }
    }

    public Integer getParsingStatus() {
        if (parsingStatus == null) {
            return null;
        }
        return parsingStatus.getIndex();
    }
    public void setParsingStatus(Integer parsingStatus) {
        if (parsingStatus != null) {
            this.parsingStatus = ParsingStatus.valueOf(parsingStatus);
        } else {
            this.parsingStatus = null;
        }
    }
}
