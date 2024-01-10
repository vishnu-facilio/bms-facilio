package com.facilio.common.reading;

import com.facilio.db.criteria.manager.NamedCondition;
import com.facilio.modules.FacilioIntEnum;

public enum OperationType implements FacilioIntEnum {

    ADD, UPDATE, DELETE;

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }

    public static OperationType valueOf(int index) {
        if (index >= 1 && index <= NamedCondition.Type.values().length) {
            return OperationType.values()[index - 1];
        }
        return null;
    }
}
