package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
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
    protected void addForms() throws Exception {

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
}
