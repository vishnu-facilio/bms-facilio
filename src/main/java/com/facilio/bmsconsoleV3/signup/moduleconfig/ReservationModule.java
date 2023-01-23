package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.*;

public class ReservationModule extends BaseModuleConfig{
    public ReservationModule(){
        setModuleName(FacilioConstants.ContextNames.Reservation.RESERVATION);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> reservation = new ArrayList<FacilioView>();
        reservation.add(getTodayReservationView().setOrder(order++));
        reservation.add(getThisWeekReservationView().setOrder(order++));
        reservation.add(getNextWeekReservationView().setOrder(order++));
        reservation.add(getOnGoingReservationView().setOrder(order++));
        reservation.add(getCompletedReservationView().setOrder(order++));
        reservation.add(getAllReservationView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Reservation.RESERVATION);
        groupDetails.put("views", reservation);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getTodayReservationView() {
        FacilioView view = getScheduledReservationView();
        view.setName("nearing");
        view.setDisplayName("Nearing");

        Criteria criteria = new Criteria();
        criteria.addOrCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.TODAY));
        criteria.addOrCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.YESTERDAY));
        criteria.addOrCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.TOMORROW));

        view.getCriteria().andCriteria(criteria);

        return view;
    }

    private static FacilioView getThisWeekReservationView() {
        FacilioView view = getScheduledReservationView();
        view.setName("thisweek");
        view.setDisplayName("This Week");
        view.getCriteria().addAndCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.CURRENT_WEEK));

        return view;
    }

    private static FacilioView getNextWeekReservationView() {
        FacilioView view = getScheduledReservationView();
        view.setName("nextweek");
        view.setDisplayName("Next Week");
        view.getCriteria().addAndCondition(CriteriaAPI.getCondition(getReservationScheduledTimeField(), DateOperators.NEXT_WEEK));

        return view;
    }

    private static FacilioView getOnGoingReservationView() {
        FacilioView view = getScheduledReservationView();
        view.setName("ongoing");
        view.setDisplayName("On Going");
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(getReservationStatusField(), String.valueOf(ReservationContext.ReservationStatus.ON_GOING.getIndex()), EnumOperators.IS));
        view.setCriteria(criteria);

        return view;
    }

    private static FacilioView getCompletedReservationView() {
        FacilioView view = getScheduledReservationView();
        view.setName("completed");
        view.setDisplayName("Completed");
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(getReservationStatusField(), String.valueOf(ReservationContext.ReservationStatus.FINISHED.getIndex()), EnumOperators.IS));
        view.setCriteria(criteria);

        return view;
    }

    private static FacilioView getAllReservationView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Reservations");

        return allView;
    }

    private static FacilioView getScheduledReservationView() {
        FacilioView view = new FacilioView();

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(getReservationStatusField(), String.valueOf(ReservationContext.ReservationStatus.SCHEDULED.getIndex()), EnumOperators.IS));
        view.setCriteria(criteria);

        view.setSortFields(Arrays.asList(new SortField(getReservationScheduledTimeField(), false)));

        return view;
    }

    private static FacilioField getReservationStatusField() {
        SystemEnumField field = (SystemEnumField) FieldFactory.getField("status", "Reservations.STATUS", FieldType.SYSTEM_ENUM);
        field.setEnumName("ReservationStatus");
        return field;
    }

    private static FacilioField getReservationScheduledTimeField() {
        return FieldFactory.getField("scheduledStartTime","Reservations.SCHEDULED_START_TIME", FieldType.DATE_TIME);
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule reservationModule = modBean.getModule(FacilioConstants.ContextNames.RESERVATION);

        FacilioForm reservationForm = new FacilioForm();
        reservationForm.setDisplayName("RESERVATIONS");
        reservationForm.setName("default_reservation_web");
        reservationForm.setModule(reservationModule);
        reservationForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        reservationForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> reservationFormFields = new ArrayList<>();
        reservationFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        reservationFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        reservationFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        reservationFormFields.add(new FormField("space", FacilioField.FieldDisplayType.RESERVABLE_SPACES, "Space", FormField.Required.REQUIRED, 4, 1));
        reservationFormFields.add(new FormField("durationType", FacilioField.FieldDisplayType.SELECTBOX, "Duration Type", FormField.Required.REQUIRED, 5, 1));
        reservationFormFields.add(new FormField("scheduledStartTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled Start Time", FormField.Required.REQUIRED, 6, 2));
        reservationFormFields.add(new FormField("scheduledEndTime", FacilioField.FieldDisplayType.DATETIME, "Scheduled End Time", FormField.Required.OPTIONAL, 6, 3));
        reservationFormFields.add(new FormField("noOfAttendees", FacilioField.FieldDisplayType.NUMBER, "No. Of Attendees", FormField.Required.OPTIONAL, 7, 1));
        reservationFormFields.add(new FormField("reservedFor", FacilioField.FieldDisplayType.USER, "Reserved For", FormField.Required.REQUIRED, 8, 1));
        reservationFormFields.add(new FormField("internalAttendees", FacilioField.FieldDisplayType.MULTI_USER_LIST, "Internal Attendees", FormField.Required.OPTIONAL, 9, 1));
        reservationFormFields.add(new FormField("externalAttendees", FacilioField.FieldDisplayType.EXTERNAL_ATTENDEES, "External Attendees", FormField.Required.OPTIONAL, 10, 1));
//        reservationForm.setFields(reservationFormFields);

        FormSection section = new FormSection("Default", 1, reservationFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        reservationForm.setSections(Collections.singletonList(section));
        reservationForm.setIsSystemForm(true);
        reservationForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(reservationForm);
    }
}
