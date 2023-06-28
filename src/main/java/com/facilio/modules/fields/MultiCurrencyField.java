package com.facilio.modules.fields;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MultiCurrencyField extends FacilioField{
    public MultiCurrencyField() {
        super();
    }
    public MultiCurrencyField(FacilioField field, String baseCurrencyValueColumnName) {
        super(field);
        this.baseCurrencyValueColumnName = baseCurrencyValueColumnName;
    }

    private String baseCurrencyValueColumnName;

}
