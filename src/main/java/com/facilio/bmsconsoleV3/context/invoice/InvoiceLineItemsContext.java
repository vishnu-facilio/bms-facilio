package com.facilio.bmsconsoleV3.context.invoice;

import com.facilio.bmsconsoleV3.context.BaseLineItemContext;
import com.facilio.modules.FacilioIntEnum;

public class InvoiceLineItemsContext extends BaseLineItemContext {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvoiceContextV3 getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceContextV3 invoice) {
        this.invoice = invoice;
    }

    private InvoiceContextV3 invoice;
    private Type type;



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

    private enum Type implements FacilioIntEnum {
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
