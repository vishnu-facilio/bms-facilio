package com.facilio.modules.fields.relations;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;

public class DeltaCalculation extends BaseRelationContext {

    @Override
    public Object calculateValue(ReadingContext reading, ReadingDataMeta previousRDM) throws Exception {
        if (olderReading(reading, previousRDM)) {
            // should not called for older reading..
            return null;
        }

        Object previousValue = FacilioUtil.castOrParseValueAsPerType(getBaseField(), previousRDM.getValue());
        Object currentValue = FacilioUtil.castOrParseValueAsPerType(getBaseField(), reading.getReading(getBaseField().getName()));

        Double delta = ((Number) currentValue).doubleValue() - ((Number) previousValue).doubleValue();
        return delta;
    }

    @Override
    public void validate() throws Exception {
        validateType(getBaseField(), FieldType.NUMBER, FieldType.DECIMAL);
        validateType(getDerivedField(), FieldType.NUMBER, FieldType.DECIMAL);
    }
}
