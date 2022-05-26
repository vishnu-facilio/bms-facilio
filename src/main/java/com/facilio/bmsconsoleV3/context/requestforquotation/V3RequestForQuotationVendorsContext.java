package com.facilio.bmsconsoleV3.context.requestforquotation;

import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.v3.context.V3Context;

public class V3RequestForQuotationVendorsContext  extends V3Context {
    private V3RequestForQuotationContext left;
    private V3VendorContext right;

    public V3RequestForQuotationContext getLeft() {
        return left;
    }

    public void setLeft(V3RequestForQuotationContext left) {
        this.left = left;
    }

    public V3VendorContext getRight() {
        return right;
    }

    public void setRight(V3VendorContext right) {
        this.right = right;
    }
}
