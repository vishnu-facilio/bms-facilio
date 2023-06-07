package com.facilio.utility.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilityIntegrationTariffSlabContext extends V3Context {

    String name;
    Long from;
    Long to;
    Double price;
    UtilityIntegrationTariffContext tariff;
    Double consumption;
    Double amount;
    String unit;

    public int compareTo(UtilityIntegrationTariffSlabContext that) {
        if(this.from < that.from) {
            return -1;
        }
        else if (this.from > that.from) {
            return 1;
        }
        return 0;
    }

}
