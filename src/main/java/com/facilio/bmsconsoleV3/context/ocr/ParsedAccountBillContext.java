package com.facilio.bmsconsoleV3.context.ocr;

import java.util.List;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ParsedAccountBillContext extends V3Context {
    public ParsedAccountBillContext(long id){
        this.setId(id);
    };
    private String name;
    private Long billMonth;
    private BillTemplateContext billTemplate;
    String billingContact;
    String billingAddress;
    String billingAccount;
    Long billStatementDate;
    String billTotalUnit;
    Double billTotalCost;
    Double billTotalVolume;
    
    List<ParsedBillContext> bills;


    private ParsedAccountBillStatus status;
    private OcrAccountType ocrAccountType;

    public Integer getStatus() {
        if (status == null) {
            return null;
        }
        return status.getIndex();
    }
    public void setStatus(Integer status) {
        if (status != null) {
            this.status = ParsedAccountBillStatus.valueOf(status);
        } else {
            this.status = null;
        }
    }

    @AllArgsConstructor
    public static enum ParsedAccountBillStatus implements FacilioIntEnum {
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
        private static final ParsedAccountBillStatus[] STATUS = ParsedAccountBillStatus.values();
        public static ParsedAccountBillStatus valueOf(int type) {
            if (type > 0 && type <= STATUS.length) {
                return STATUS[type - 1];
            }
            return null;
        }
    }

    @AllArgsConstructor
    public static enum OcrAccountType implements FacilioIntEnum {
        RESIDENTIAL("residential");

        public int getVal() {
            return ordinal() + 1;
        }
        String name;
        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }
        private static final OcrAccountType[] STATUS = OcrAccountType.values();
        public static OcrAccountType valueOf(int type) {
            if (type > 0 && type <= STATUS.length) {
                return STATUS[type - 1];
            }
            return null;
        }
    }
}
