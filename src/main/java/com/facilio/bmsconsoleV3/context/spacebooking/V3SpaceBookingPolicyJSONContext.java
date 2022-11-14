package com.facilio.bmsconsoleV3.context.spacebooking;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
@Getter @Setter
public class V3SpaceBookingPolicyJSONContext {
    private static final Logger LOGGER = LogManager.getLogger(V3SpaceBookingPolicyJSONContext.class.getName());

    private PolicyJSON isChargeable;
    private PolicyJSON securityDeposit;
    private PolicyJSON cancellationCharges;
    private PolicyJSON cancellationPeriodInDays;
    private PolicyJSON cancellationPeriodInHours;
    private PolicyJSON bookingAdvanceDays;
    private PolicyJSON bookingAdvanceHours;
    private PolicyJSON isMultipleDayBooking;
    private PolicyJSON maximumAttendees;

    @Getter @Setter
    public class PolicyJSON {
        private String name;
        private String description;
        private boolean enable=false;
        private  long value;
        private String unit;

    }

}
