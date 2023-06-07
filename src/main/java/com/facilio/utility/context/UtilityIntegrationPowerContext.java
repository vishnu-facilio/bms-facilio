package com.facilio.utility.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UtilityIntegrationPowerContext extends V3Context {

    private static final long serialVersionUID = 1L;

    UtilityIntegrationBillContext utilityIntegrationBill;
    String name;
    String unit;
    Double volume;
    String powerType;

}
