package com.facilio.bmsconsole.context;

public class AssetRollUpOccurrence extends AlarmOccurrenceContext {
    private static final long serialVersionUID = 1L;

    @Override
    public Type getTypeEnum() {
        return Type.READING;
    }
}
