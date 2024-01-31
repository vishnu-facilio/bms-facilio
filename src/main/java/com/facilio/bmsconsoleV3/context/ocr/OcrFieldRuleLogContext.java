package com.facilio.bmsconsoleV3.context.ocr;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrFieldRuleLogContext extends V3Context {
    private BillTemplateContext billTemplate;
    private ParsedBillContext parsedBill;
    private ActualBillContext actualBill;
    private long mapFieldId;
    private OcrErrorOccuredAtEnum errorAt;
    private ParseStatusEnum parseStatus;
    private String errorMessage;

    public Integer getType() {
        if (errorAt == null) {
            return null;
        }
        return errorAt.getIndex();
    }

    public void setType(Integer type) {
        if (type != null) {
            this.errorAt = OcrErrorOccuredAtEnum.valueOf(type);
        } else {
            this.errorAt = null;
        }
    }

    public Integer getParseStatus() {
        if (parseStatus == null) {
            return null;
        }
        return parseStatus.getIndex();
    }

    public void setParseStatus(Integer status) {
        if (status != null) {
            this.parseStatus = ParseStatusEnum.valueOf(status);
        } else {
            this.parseStatus = null;
        }
    }

    public static enum ParseStatusEnum implements FacilioIntEnum {
        SUCCESS,
        FAILURE;

        public int getVal() {
            return ordinal() + 1;
        }

        String name;

        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }

        private static final ParseStatusEnum[] RULE_TYPES = ParseStatusEnum.values();

        public static ParseStatusEnum valueOf(int type) {
            if (type > 0 && type <= RULE_TYPES.length) {
                return RULE_TYPES[type - 1];
            }
            return null;
        }
    }


    public OcrErrorOccuredAtEnum getTypeEnum() {
        return errorAt;
    }

    public static enum OcrErrorOccuredAtEnum implements FacilioIntEnum {
        FIELD_RULE,
        LINEITEM_RULE,
        PARSING_BILL;

        public int getVal() {
            return ordinal() + 1;
        }

        String name;

        @Override
        public String getValue() {
            // TODO Auto-generated method stub
            return this.name;
        }

        private static final OcrErrorOccuredAtEnum[] RULE_TYPES = OcrErrorOccuredAtEnum.values();

        public static OcrErrorOccuredAtEnum valueOf(int type) {
            if (type > 0 && type <= RULE_TYPES.length) {
                return RULE_TYPES[type - 1];
            }
            return null;
        }
    }
}
