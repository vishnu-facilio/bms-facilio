package com.facilio.bmsconsoleV3.context;

import com.facilio.bmsconsoleV3.context.ocr.BillTemplateContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilityAccountBillContext extends V3Context {
    public UtilityAccountBillContext(long id){
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

    private OcrUtilityAccountType ocrAccountType;

    @AllArgsConstructor
    public static enum OcrUtilityAccountType implements FacilioIntEnum {
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
        private static final OcrUtilityAccountType[] STATUS = OcrUtilityAccountType.values();
        public static OcrUtilityAccountType valueOf(int type) {
            if (type > 0 && type <= STATUS.length) {
                return STATUS[type - 1];
            }
            return null;
        }
    }
}
