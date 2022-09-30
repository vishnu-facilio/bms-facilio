package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Getter
@Setter
public class ShiftAction extends V3Action {
    private static Logger LOGGER = LogManager.getLogger(ShiftAction.class.getName());

    private String timelineMetric;
    private Long timelineValue;
    private Long peopleID;
    private Long shiftID;
    private Long shiftStart;
    private Long shiftEnd;

    public String list() throws Exception {
        if (timelineValue == null || timelineMetric == null || peopleID == null) {
            LOGGER.trace("timelineValue, timelineMetric & peopleID are mandatory");
            return ERROR;
        }

        FacilioChain chain = TransactionChainFactoryV3.getShiftPlannerListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Shift.TIMELINE_METRIC, timelineMetric);
        context.put(FacilioConstants.Shift.TIMELINE_VALUE, timelineValue);
        context.put(FacilioConstants.Shift.PEOPLE_ID, peopleID);
        chain.execute();


        setData("shifts", context.get("shifts"));
        return SUCCESS;
    }

    public String update() throws Exception {
        if (shiftStart == null || shiftEnd == null || shiftID == null || peopleID == null) {
            LOGGER.trace("shiftStart, shiftEnd, shiftID  & peopleID are mandatory");
            return ERROR;
        }

        FacilioChain chain = TransactionChainFactoryV3.getShiftPlannerUpdateChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Shift.SHIFT_START, shiftStart);
        context.put(FacilioConstants.Shift.SHIFT_END, shiftEnd);
        context.put(FacilioConstants.Shift.SHIFT_ID, shiftID);
        context.put(FacilioConstants.Shift.PEOPLE_ID, peopleID);
        chain.execute();

        setData("shifts", context.get("shifts"));
        return SUCCESS;
    }
}