package com.facilio.modules.fields.relations;

import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FieldType;

public class TimeDeltaRelationContext extends BaseRelationContext {

    private TimeDeltaUnit unit;
    public int getUnit() {
        return unit != null ? unit.getIndex() : -1;
    }
    public void setUnit(int type) {
        this.unit = TimeDeltaUnit.indexOf(type);
    }

    @Override
    public Object calculateValue(ReadingContext reading, ReadingDataMeta previousRDM) throws Exception {
        if (olderReading(reading, previousRDM)) {
            // should not called for older reading..
            return null;
        }

        long ttime = previousRDM.getTtime();
        long actualTime = reading.getTtime();

        Object prevReading = previousRDM.getValue();
        if (prevReading instanceof Boolean) {
            if ((Boolean) prevReading) {
                long deltaInMillis = actualTime - ttime;
                if (unit == TimeDeltaUnit.SECONDS) {
                    return (deltaInMillis / 1000);
                }
                return deltaInMillis;
            }
        }
        return 0;
    }

    @Override
    public void validate() throws Exception {
        validateType(getBaseField(), FieldType.BOOLEAN);
        validateType(getDerivedField(), FieldType.DECIMAL, FieldType.NUMBER);
    }

    public enum TimeDeltaUnit implements FacilioEnum {
        SECONDS("Seconds"),
        MILLI("Milli-seconds");

        private String name;

        TimeDeltaUnit(String name) {
            this.name = name;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static TimeDeltaUnit indexOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type-1];
            }
            return null;
        }
    }
}
