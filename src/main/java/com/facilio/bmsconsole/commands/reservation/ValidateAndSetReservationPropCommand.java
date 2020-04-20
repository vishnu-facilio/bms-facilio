package com.facilio.bmsconsole.commands.reservation;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class ValidateAndSetReservationPropCommand extends FacilioCommand {

    private boolean isAdd = false;
    public ValidateAndSetReservationPropCommand(boolean isAdd) {
        this.isAdd = isAdd;
    }

    private ModuleBean moduleBean = null;
    private ModuleBean getModBean() throws Exception {
        if (moduleBean == null) {
            moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        }
        return moduleBean;
    }

    private List<FacilioField> reservationFields = null;
    private List<FacilioField> fetchFields() throws Exception {
        if (reservationFields == null) {
            reservationFields = getModBean().getAllFields(FacilioConstants.ContextNames.Reservation.RESERVATION);
        }
        return reservationFields;
    }

    private FacilioModule reservationModule = null;
    private FacilioModule fetchReservationModule() throws Exception {
        if (reservationModule == null) {
            reservationModule = getModBean().getModule(FacilioConstants.ContextNames.Reservation.RESERVATION);
        }
        return reservationModule;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ReservationContext reservation = (ReservationContext) context.get(FacilioConstants.ContextNames.Reservation.RESERVATION);

        if (reservation == null) {
            throw new IllegalArgumentException("Reservation object cannot be null");
        }

        if (!isAdd) {
           validateState(reservation);
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.Reservation.RESERVATION);
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);

        if (isAdd) {
            checkForNull(reservation);
        }
        if (reservation.getNoOfAttendees() > 0 ) {
            int totalAttendees = (reservation.getInternalAttendees() == null ? 0 : reservation.getInternalAttendees().size())
                    + (reservation.getExternalAttendees() == null ? 0 : reservation.getExternalAttendees().size());
            if (totalAttendees > reservation.getNoOfAttendees()) {
                throw new IllegalArgumentException("Total attendees list shouldn't exceed noOfAttendees during addition of reservation");
            }
        }
        if (reservation.getDurationTypeEnum() != null) { //For now if duration type is changed, both scheduled Start and scheduledEnd and space should be sent again
            computeEndTime(reservation);
            checkOverlapOfReservation(reservation);
        }

        if (!isAdd && (CollectionUtils.isNotEmpty(reservation.getInternalAttendees()) || CollectionUtils.isNotEmpty(reservation.getExternalAttendees()))) {
            deleteOldAttendees(reservation);
        }

        context.put(FacilioConstants.ContextNames.RECORD, reservation);
        if (isAdd) {
            reservation.setStatus(ReservationContext.ReservationStatus.SCHEDULED);
            CommonCommandUtil.addToRecordMap((FacilioContext) context, FacilioConstants.ContextNames.Reservation.RESERVATION, reservation);
        }
        else {
            context.put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(reservation.getId()));
        }

        return false;
    }

    private static final long CHECK_IN_BUFFER = Duration.ofMinutes(30).toMillis();
    private void validateState (ReservationContext reservation) throws Exception {
        if (reservation.getId() <= 0) {
            throw new IllegalArgumentException("Reservation ID is mandatory during updation");
        }
        ReservationContext oldRecord = fetchOld(reservation.getId());
        if (oldRecord == null) {
            throw new IllegalArgumentException("Invalid ID for updation of Reservation");
        }

        switch (oldRecord.getStatusEnum()) {
            case SCHEDULED:
                if (reservation.getStatusEnum() != null) {
                    switch (reservation.getStatusEnum()) {
                        case ON_GOING:
                            if (oldRecord.getScheduledStartTime() - System.currentTimeMillis() > CHECK_IN_BUFFER) {
                                throw new IllegalArgumentException("Reservaton can be Checked-In only half an hour prior to Scheduled Start Time");
                            }
                            else {
                                reservation.setActualStartTime(System.currentTimeMillis());
                            }
                            break;
                        case SCHEDULED:
                        case CANCELLED:
                            break;
                        default:
                            throw new IllegalArgumentException("Scheduled Reservaton can only be cancelled/ Checked-In");

                    }
                }
                break;
            case ON_GOING:
                if (ReservationContext.ReservationStatus.FINISHED == reservation.getStatusEnum()) {
                    reservation.setActualEndTime(System.currentTimeMillis());
                }
                else {
                    throw new IllegalArgumentException("Cannot edit an On Going Reservation");
                }
                break;
            case FINISHED:
                throw new IllegalArgumentException("Cannot edit a completed Reservation");
            case CANCELLED:
                throw new IllegalArgumentException("Cannot edit a cancelled Reservation");
            default:
                break;
        }
    }

    private ReservationContext fetchOld (long id) throws Exception {
        List<ReservationContext> reservations = new SelectRecordsBuilder<ReservationContext>()
                                                                .select(fetchFields())
                                                                .module(fetchReservationModule())
                                                                .beanClass(ReservationContext.class)
                                                                .andCondition(CriteriaAPI.getIdCondition(id, fetchReservationModule()))
                                                                .get();

        if (CollectionUtils.isNotEmpty(reservations)) {
            return reservations.get(0);
        }
        return null;
    }

    private void checkOverlapOfReservation(ReservationContext reservation) throws Exception {
        ModuleBean modBean = getModBean();
        List<FacilioField> fields = fetchFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField spaceField = fieldMap.get("space");
        FacilioField scheduledStartField = fieldMap.get("scheduledStartTime");
        FacilioField scheduledEndField = fieldMap.get("scheduledEndTime");
        FacilioField statusField = fieldMap.get("status");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Reservation.RESERVATION);

        SelectRecordsBuilder<ReservationContext> selectBuilder = new SelectRecordsBuilder<ReservationContext>()
                                                                .select(fields)
                                                                .module(module)
                                                                .beanClass(ReservationContext.class)
                                                                .andCondition(CriteriaAPI.getCondition(spaceField, String.valueOf(reservation.getSpace().getId()), PickListOperators.IS))
                                                                .andCondition(CriteriaAPI.getCondition(scheduledStartField, String.valueOf(reservation.getScheduledEndTime()), DateOperators.IS_BEFORE))
                                                                .andCondition(CriteriaAPI.getCondition(scheduledEndField, String.valueOf(reservation.getScheduledStartTime()), DateOperators.IS_AFTER))
                                                                .andCondition(CriteriaAPI.getCondition(statusField, String.valueOf(ReservationContext.ReservationStatus.SCHEDULED.getIndex()), EnumOperators.IS))
                                                                ;

        if (reservation.getId() > 0) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), String.valueOf(reservation.getId()), NumberOperators.NOT_EQUALS));
        }
        List<ReservationContext> reservations = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(reservations)) {
            throw new IllegalArgumentException("The space is not available for the specified time slot");
        }
    }



    private void checkForNull (ReservationContext reservation) {
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
    }

    private void computeEndTime(ReservationContext reservation) {
        if (reservation.getScheduledStartTime() == -1) {
            throw new IllegalArgumentException("Scheduled start time cannot be null when durationType is changed");
        }

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
                if (reservation.getScheduledEndTime() <= 0) {
                    throw new IllegalArgumentException("Scheduled end time cannot be null when durationType is custom during addition of reservation");
                }
                break;
        }

        if (reservation.getScheduledStartTime() >= reservation.getScheduledEndTime()) {
            throw new IllegalArgumentException("Invalid time range for reservation");
        }
    }

    private void deleteOldAttendees(ReservationContext reservation) throws Exception {
        commondDeleteAttendees(reservation.getId(), FacilioConstants.ContextNames.Reservation.RESERVATIONS_INTERNAL_ATTENDEE);
        commondDeleteAttendees(reservation.getId(), FacilioConstants.ContextNames.Reservation.RESERVATIONS_EXTERNAL_ATTENDEE);
    }

    private void commondDeleteAttendees (long id, String module) throws Exception {
        ModuleBean modBean = getModBean();
        FacilioField reservationField = modBean.getField("reservation", module);

        new DeleteRecordBuilder<>()
                .moduleName(module)
                .andCondition(CriteriaAPI.getCondition(reservationField, String.valueOf(id), PickListOperators.IS))
                .delete();
    }
}
