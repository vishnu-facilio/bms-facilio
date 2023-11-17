package com.facilio.fsm.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fsm.context.ServiceAppointmentContext;
import com.facilio.fsm.context.TimeSheetContext;
import com.facilio.fsm.context.TripContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class HomePageUtil {
    public static PagesContext getMobileFSMHomePage() throws Exception {

        User currentUser  = AccountUtil.getCurrentUser();
        if(currentUser != null && currentUser.getPeopleId() > 0) {

            PagesContext homePage = new PagesContext("fsmHomePage","Home",null,null,true,false,false);
            homePage.setId(1);
            PageTabContext mobileTab = new PageTabContext("home","Home",10D, PageTabContext.TabType.SIMPLE,false,-1);
            mobileTab.setId(11L);
            homePage.setLayouts(new HashMap<String, List<PageTabContext>>());
            homePage.getLayouts().put(PagesContext.PageLayoutType.MOBILE.name(), new ArrayList<>(Collections.singletonList(mobileTab)));

            PageColumnContext column = new PageColumnContext(10D, PageColumnContext.ColumnWidth.FULL_WIDTH.getWidth());
            column.setId(21L);
            mobileTab.setColumns(new ArrayList<>(Collections.singletonList(column)));
            column.setParentContext(mobileTab);



            V3PeopleContext peopleContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, currentUser.getPeopleId(), V3PeopleContext.class);
            if (peopleContext != null) {
                JSONObject summaryWidgetParams = new JSONObject();
                summaryWidgetParams.put(FacilioConstants.ContextNames.PEOPLE, FieldUtil.getAsJSON(peopleContext));
                PageSectionContext person = new PageSectionContext("peopleSummary","",CollectionUtils.isNotEmpty(column.getSections()) ? ((column.getSections().size()+1) * 10D ) : 10,null);
                person.setId(31L);
                if(column.getSections() == null) {
                    column.setSections(new ArrayList<>(Collections.singletonList(person)));
                }
                else {
                    column.getSections().add(person);
                }
                person.setParentContext(column);

                PageSectionWidgetContext peopleSummaryWidget = new PageSectionWidgetContext("peopleStatusWidget","Summary", PageWidget.WidgetType.PEOPLE_STATUS_WIDGET,10D,0,0,null,summaryWidgetParams);
                peopleSummaryWidget.setId(41L);
                peopleSummaryWidget.setHeight(9L);
                peopleSummaryWidget.setWidth(12L);
                peopleSummaryWidget.setConfigType(WidgetConfigContext.ConfigType.FLEXIBLE);
                person.setWidgets(new ArrayList<>(Collections.singletonList(peopleSummaryWidget)));
                peopleSummaryWidget.setParentContext(person);
            }

            List<FacilioField> saFields = Constants.getModBean().getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

            PageSectionContext Ongoing = new PageSectionContext("ongoing","",CollectionUtils.isNotEmpty(column.getSections()) ? ((column.getSections().size()+1) * 10D ) : 10,null);
            Ongoing.setId(32L);

            List<TimeSheetContext> ongoingTimeSheet = ServiceAppointmentUtil.getOngoingTimeSheets(currentUser.getPeopleId(), null);
            if (CollectionUtils.isNotEmpty(ongoingTimeSheet)) {
                JSONObject workWidgetParams = new JSONObject();
                workWidgetParams.put(FacilioConstants.TimeSheet.TIME_SHEET, FieldUtil.getAsMapList(ongoingTimeSheet,TimeSheetContext.class));
                workWidgetParams.put("tabName","timesheet");
                PageSectionWidgetContext ongoingWorkWidget = new PageSectionWidgetContext("ongoingTimeSheetWidget","Ongoing Work", PageWidget.WidgetType.ONGOING_WORK_WIDGET,CollectionUtils.isNotEmpty(Ongoing.getWidgets()) ? ((Ongoing.getWidgets().size()+1) * 10D ) : 10,0,0,null,workWidgetParams);
                ongoingWorkWidget.setId(42L);
                ongoingWorkWidget.setHeight(6L);
                ongoingWorkWidget.setWidth(12L);
                ongoingWorkWidget.setConfigType(WidgetConfigContext.ConfigType.FLEXIBLE);
                Ongoing.setWidgets(new ArrayList<>(Collections.singletonList(ongoingWorkWidget)));
                ongoingWorkWidget.setParentContext(Ongoing);
            }

            List<TripContext> ongoingTrips = ServiceAppointmentUtil.getOngoingTrips(currentUser.getPeopleId(), null);
            if (CollectionUtils.isNotEmpty(ongoingTrips)) {
                JSONObject tripWidgetParams = new JSONObject();
                tripWidgetParams.put(FacilioConstants.Trip.TRIP, FieldUtil.getAsMapList(ongoingTrips,TripContext.class));
                tripWidgetParams.put("tabName","trip");
                PageSectionWidgetContext ongoingTripWidget = new PageSectionWidgetContext("ongoingTripWidget","Ongoing Trip", PageWidget.WidgetType.ONGOING_TRIP_WIDGET,CollectionUtils.isNotEmpty(Ongoing.getWidgets()) ? ((Ongoing.getWidgets().size()+1) * 10D ) : 10,0,0,null,tripWidgetParams);
                ongoingTripWidget.setId(43L);
                ongoingTripWidget.setHeight(6L);
                ongoingTripWidget.setWidth(12L);
                ongoingTripWidget.setConfigType(WidgetConfigContext.ConfigType.FLEXIBLE);
                if(Ongoing.getWidgets() == null) {
                    Ongoing.setWidgets(new ArrayList<>(Collections.singletonList(ongoingTripWidget)));
                }
                else {
                    Ongoing.getWidgets().add(ongoingTripWidget);
                }
                ongoingTripWidget.setParentContext(Ongoing);
            }

            if (CollectionUtils.isNotEmpty(Ongoing.getWidgets())) {
                if(column.getSections() == null) {
                    column.setSections(new ArrayList<>(Collections.singletonList(Ongoing)));
                }
                else {
                    column.getSections().add(Ongoing);
                }
                Ongoing.setParentContext(column);
            }

            PageSectionContext appointments = new PageSectionContext("appointments","",CollectionUtils.isNotEmpty(column.getSections()) ? ((column.getSections().size()+1) * 10D ) : 10,null);
            appointments.setId(33L);
            appointments.setWidgets(new ArrayList<>());

            JSONObject overdueWidgetParams = new JSONObject();
            overdueWidgetParams.put("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            overdueWidgetParams.put("viewName", "overdueAppointments");
            PageSectionWidgetContext overdueWidget = new PageSectionWidgetContext("overdueServiceAppointmentsWidget","Overdue Appointments", PageWidget.WidgetType.OVERDUE_SERVICE_APPOINTMENTS_WIDGET,CollectionUtils.isNotEmpty(appointments.getWidgets()) ? ((appointments.getWidgets().size()+1) * 10D ) : 10,0,0,null,overdueWidgetParams);
            overdueWidget.setId(44L);
            overdueWidget.setHeight(21L);
            overdueWidget.setWidth(12L);
            overdueWidget.setConfigType(WidgetConfigContext.ConfigType.FLEXIBLE);
            appointments.getWidgets().add(overdueWidget);
            overdueWidget.setParentContext(appointments);

            Criteria todayCriteria = new Criteria();
            todayCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), DateOperators.TODAY));
            todayCriteria.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), DateOperators.TODAY));

            JSONObject todayWidgetParams = new JSONObject();
            todayWidgetParams.put("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            todayWidgetParams.put("viewName", "myOpenAppointments");
            todayWidgetParams.put("clientCriteria", FieldUtil.getAsJSON(todayCriteria));
            PageSectionWidgetContext todayWidget = new PageSectionWidgetContext("todayServiceAppointmentsWidget","Today Appointments", PageWidget.WidgetType.TODAY_SERVICE_APPOINTMENTS_WIDGET,CollectionUtils.isNotEmpty(appointments.getWidgets()) ? ((appointments.getWidgets().size()+1) * 10D ) : 10,0,0,null,todayWidgetParams);
            todayWidget.setId(45L);
            todayWidget.setHeight(21L);
            todayWidget.setWidth(12L);
            todayWidget.setConfigType(WidgetConfigContext.ConfigType.FLEXIBLE);
            appointments.getWidgets().add(todayWidget);
            todayWidget.setParentContext(appointments);

            Criteria upcomingCriteria = new Criteria();
            upcomingCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), DateOperators.STARTING_TOMORROW));

            JSONObject upcomingWidgetParams = new JSONObject();
            upcomingWidgetParams.put("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            upcomingWidgetParams.put("viewName", "myOpenAppointments");
            upcomingWidgetParams.put("clientCriteria", FieldUtil.getAsJSON(upcomingCriteria));

            PageSectionWidgetContext upcomingWidget = new PageSectionWidgetContext("upcomingServiceAppointmentsWidget","Upcoming Appointments", PageWidget.WidgetType.UPCOMING_SERVICE_APPOINTMENTS_WIDGET,CollectionUtils.isNotEmpty(appointments.getWidgets()) ? ((appointments.getWidgets().size()+1) * 10D ) : 10,0,0,null,upcomingWidgetParams);
            upcomingWidget.setId(46L);
            upcomingWidget.setHeight(21L);
            upcomingWidget.setWidth(12L);
            upcomingWidget.setConfigType(WidgetConfigContext.ConfigType.FLEXIBLE);
            appointments.getWidgets().add(upcomingWidget);
            upcomingWidget.setParentContext(appointments);

            if (CollectionUtils.isNotEmpty(appointments.getWidgets())) {
                if(column.getSections() == null) {
                    column.setSections(new ArrayList<>(Collections.singletonList(appointments)));
                }
                else {
                    column.getSections().add(appointments);
                }
                appointments.setParentContext(column);
            }

            return homePage;
        }
        return null;
    }
}
