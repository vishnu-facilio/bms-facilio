package com.facilio.bmsconsole.commands.reservation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.reservation.ExternalAttendeeContext;
import com.facilio.bmsconsole.context.reservation.InternalAttendeeContext;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FetchAttendeesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ReservationContext reservation = (ReservationContext) context.get(FacilioConstants.ContextNames.RECORD);

        if (reservation != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            reservation.setSpace(SpaceAPI.getSpace(reservation.getSpace().getId()));
            reservation.setInternalAttendees(fetchInternalAttendees(modBean, reservation.getId()));
            reservation.setExternalAttendees(fetchexternalAttendees(modBean, reservation.getId()));
        }

        return false;
    }

    private List<User> fetchInternalAttendees(ModuleBean modBean, long id) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Reservation.RESERVATIONS_INTERNAL_ATTENDEE);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField reservationField = fieldMap.get("reservation");
        FacilioField userField = fieldMap.get("attendee");

        List<InternalAttendeeContext> attendees = new SelectRecordsBuilder<InternalAttendeeContext>()
                                                        .select(fields)
                                                        .module(module)
                                                        .andCondition(CriteriaAPI.getCondition(reservationField, String.valueOf(id), PickListOperators.IS))
                                                        .fetchSupplement((LookupField) userField)
                                                        .beanClass(InternalAttendeeContext.class)
                                                        .get();

        if (CollectionUtils.isNotEmpty(attendees)) {
            return attendees.stream().map(InternalAttendeeContext::getAttendee).collect(Collectors.toList());
        }
        return null;
    }

    private List<ExternalAttendeeContext> fetchexternalAttendees(ModuleBean modBean, long id) throws Exception {
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Reservation.RESERVATIONS_EXTERNAL_ATTENDEE);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        FacilioField reservationField = fieldMap.get("reservation");

        List<ExternalAttendeeContext> attendees = new SelectRecordsBuilder<ExternalAttendeeContext>()
                .select(fields)
                .module(module)
                .andCondition(CriteriaAPI.getCondition(reservationField, String.valueOf(id), PickListOperators.IS))
                .beanClass(ExternalAttendeeContext.class)
                .get();

        if (CollectionUtils.isNotEmpty(attendees)) {
            return attendees;
        }
        return null;
    }

}
