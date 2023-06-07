package com.facilio.utility.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UtilityIntegrationTOUContext extends V3Context {

    private static final long serialVersionUID = 1L;

    String name;
    String unit;
    Double cost ;
    Double volume;
    String bucket;
    UtilityIntegrationBillContext utilityIntegrationBill;


}
