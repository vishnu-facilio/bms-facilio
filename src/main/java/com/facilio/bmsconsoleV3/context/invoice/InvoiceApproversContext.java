package com.facilio.bmsconsoleV3.context.invoice;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class InvoiceApproversContext extends V3Context {
    @Getter
    @Setter
    private InvoiceContextV3 left;

    @Getter @Setter
    private V3PeopleContext right;
}
