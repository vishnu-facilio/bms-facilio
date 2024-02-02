package com.facilio.bmsconsoleV3.context.quotation;

import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

public class QuotationLineItemsContext extends BaseLineItemContext {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private QuotationContext quote;
    private Type type;
    @Getter
    @Setter
    private String unitOfMeasureEnum;

    public QuotationContext getQuote() {
        return quote;
    }

    public void setQuote(QuotationContext quotation) {
        this.quote = quotation;
    }

    public void setType(Integer type) {
        if (type != null) {
            this.type = Type.valueOf(type);
        }
    }

    public Type getTypeEnum() {
        return type;
    }
    public Integer getType() {
        if (type != null) {
            return type.getIndex();
        }
        return null;
    }

    public enum Type implements FacilioIntEnum {
        ITEM_TYPE("Item Type"),
        TOOL_TYPE("Tool Type"),
        SERVICE("Service"),
        LABOUR("Labour"),
        OTHERS("Others");
        private String name;

        Type(String name) {
            this.name = name;
        }

        public static Type valueOf(int value) {
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

}
