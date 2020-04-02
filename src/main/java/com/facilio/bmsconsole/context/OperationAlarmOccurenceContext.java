package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.fasterxml.jackson.annotation.JsonInclude;

public class OperationAlarmOccurenceContext extends AlarmOccurrenceContext {
	private static final long serialVersionUID = 1L;

	@Override
    public Type getTypeEnum() {
        return Type.OPERATION_OCCURRENCE;
    }
    private OperationAlarmContext.CoverageType coverageType;
    @JsonInclude
    public final int getCoverageType() {
        coverageType = getCoverageTypeEnum();
        if (coverageType != null) {
            return coverageType.getIndex();
        }
        return -1;
    }

    private OperationAlarmContext.CoverageType getCoverageTypeEnum() {
        return coverageType;
    }

    public final void setCoverageType(int eventCoverageType) {
        coverageType = OperationAlarmContext.CoverageType.valueOf(eventCoverageType);
    }
}
