package com.facilio.bmsconsole.commands.reservation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

public class ValidateAndSetReservationPropCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ReservationContext reservation = (ReservationContext) context.get(FacilioConstants.ContextNames.Reservation.RESERVATION);

        if (reservation == null) {
            throw new IllegalArgumentException("Reservation object cannot be null during addition");
        }

        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.Reservation.RESERVATION);
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
        if (StringUtils.isEmpty(reservation.getName())) {
            throw new IllegalArgumentException("Reservation Name cannot be null during addition");
        }
        if (reservation.getSpace() == null) {
            throw new IllegalArgumentException("Space cannot be null during addition of reservation");
        }
        if (reservation.getScheduledStartTime() == -1) {
            throw new IllegalArgumentException("Scheduled start time cannot be null during addition of reservation");
        }
        if (reservation.getDurationTypeEnum() == null) {
            throw new IllegalArgumentException("Duration type cannot be null during addition of reservation");
        }
        computeEndTime(reservation);

        int totalAttendees = (reservation.getInternalAttendees() == null ? 0 : reservation.getInternalAttendees().size())
                                + (reservation.getExternalAttendees() == null ? 0 : reservation.getExternalAttendees().size());
        if (reservation.getNoOfAttendees() != -1 && totalAttendees > reservation.getNoOfAttendees()) {
            throw new IllegalArgumentException("Total attendees list shouldn't exceed noOfAttendees during addition of reservation");
        }

        // Have to do validation check

        context.put(FacilioConstants.ContextNames.RECORD, reservation);
        CommonCommandUtil.addToRecordMap((FacilioContext) context, FacilioConstants.ContextNames.Reservation.RESERVATION, reservation);

        return false;
    }

    private void computeEndTime(ReservationContext reservation) {
        switch (reservation.getDurationTypeEnum()) {
            case HALF_AN_HOUR:
                reservation.setScheduledEndTime(reservation.getScheduledStartTime() + Duration.ofMinutes(30).toMillis());
                break;
            case ONE_HOUR:
                reservation.setScheduledEndTime(reservation.getScheduledStartTime() + Duration.ofHours(1).toMillis());
                break;
            case ONE_AND_HALF_HOUR:
                reservation.setScheduledEndTime(reservation.getScheduledStartTime() + Duration.ofMinutes(90).toMillis());
                break;
            case TWO_HOURS:
                reservation.setScheduledEndTime(reservation.getScheduledStartTime() + Duration.ofHours(2).toMillis());
                break;
            case ALL_DAY:
                long starTime = reservation.getScheduledStartTime();
                reservation.setScheduledStartTime(DateTimeUtil.getDayStartTimeOf(starTime));
                reservation.setScheduledEndTime(DateTimeUtil.getDayEndTimeOf(starTime));
                break;
            case CUSTOM:
                if (reservation.getScheduledEndTime() == -1) {
                    throw new IllegalArgumentException("Scheduled end time cannot be null when durationType is custom during addition of reservation");
                }
                break;
        }
    }
}
