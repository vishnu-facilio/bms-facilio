package com.facilio.bmsconsole.workflow.rule;

import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class StateFlowTransitionSequenceContext implements Serializable {
    private long id;
    private long stateTransitionId;
    private TransitionActionType transitionActionType;

    public enum TransitionActionType implements FacilioStringEnum{
        comment,
        field_update,
        create_record;
    }
}
