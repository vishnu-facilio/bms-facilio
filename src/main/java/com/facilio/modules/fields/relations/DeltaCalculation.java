package com.facilio.modules.fields.relations;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.util.FacilioUtil;

public class DeltaCalculation extends BaseRelation {

    @Override
    public Object calculateValue(ReadingContext reading, ReadingDataMeta previousRDM) throws Exception {
        if (olderReading(reading, previousRDM)) {
            // should not called for older reading..
        }

        Object previousValue = FacilioUtil.castOrParseValueAsPerType(getBaseField(), previousRDM.getValue());
        Object currentValue = FacilioUtil.castOrParseValueAsPerType(getBaseField(), reading.getReading(getBaseField().getName()));


        return null;
    }
}
