package com.facilio.bmsconsole.homepage.factory;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.homepage.HomePage;
import com.facilio.bmsconsole.homepage.HomePage.Section;
import com.facilio.bmsconsole.homepage.HomePageWidget;
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
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FieldServiceHome extends HomePageFactory {
    public static HomePage getDefaultPage() throws Exception {
        HomePage defaultPage = new HomePage();
        User currentUser  = AccountUtil.getCurrentUser();
        if(currentUser != null && currentUser.getPeopleId() > 0) {

            defaultPage.setDisplayName("Home");
            defaultPage.setLinkName("default");

            V3PeopleContext peopleContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE,currentUser.getPeopleId(),V3PeopleContext.class);
            if(peopleContext != null){
                Section person = new Section();
                person.setName("Person");
                HomePageWidget peopleSummary = new HomePageWidget(HomePageWidget.WidgetType.PERSON_SUMMARY);
                peopleSummary.addToLayoutParams(person, 12, 3);
                peopleSummary.addToWidgetParams(FacilioConstants.ContextNames.PEOPLE,FieldUtil.getAsJSON(peopleContext));
                person.addWidget(peopleSummary);
                defaultPage.addSection(person);
            }

            List<FacilioField> saFields  = Constants.getModBean().getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            Map<String,FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

            Section Ongoing = new Section();
            Ongoing.setName("Ongoing Items");

            List<TimeSheetContext> ongoingTimeSheet = ServiceAppointmentUtil.getOngoingTimeSheets(currentUser.getPeopleId(),null);
            if(CollectionUtils.isNotEmpty(ongoingTimeSheet)) {
                HomePageWidget ongoingWork = new HomePageWidget(HomePageWidget.WidgetType.ONGOING_WORK);
                ongoingWork.addToLayoutParams(Ongoing, 12, 2);
                ongoingWork.addToWidgetParams(FacilioConstants.TimeSheet.TIME_SHEET,ongoingWork);
                Ongoing.addWidget(ongoingWork);
            }

            List<TripContext> ongoingTrips = ServiceAppointmentUtil.getOngoingTrips(currentUser.getPeopleId(),null);
            if(CollectionUtils.isNotEmpty(ongoingTrips)) {
                HomePageWidget ongoingTrip = new HomePageWidget(HomePageWidget.WidgetType.ONGOING_TRIP);
                ongoingTrip.addToLayoutParams(Ongoing, 12, 2);
                ongoingTrip.addToWidgetParams(FacilioConstants.Trip.TRIP,ongoingTrips);
                Ongoing.addWidget(ongoingTrip);
            }

            if(CollectionUtils.isNotEmpty(Ongoing.getWidgets())) {
                defaultPage.addSection(Ongoing);
            }

            Section appointments = new Section();
            appointments.setName("Appointments");

            FacilioContext overDueListContext = V3Util.fetchList(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, true, "overdueAppointments", null,
                    true, null, null, null,
                    null, 1, 2, false, null, null);

            Map<String, Object> overduesRecordMap = (Map<String, Object>) overDueListContext.get("recordMap");
            List<ServiceAppointmentContext> overdueAppointmentsList = (List<ServiceAppointmentContext>) overduesRecordMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

            if(CollectionUtils.isNotEmpty(overdueAppointmentsList)){
                HomePageWidget overdueAppointments = new HomePageWidget(HomePageWidget.WidgetType.OVERDUE_APPOINTMENTS);
                overdueAppointments.addToLayoutParams(appointments, 6, 5);
                overdueAppointments.addToWidgetParams(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,overdueAppointmentsList);
                overdueAppointments.addToWidgetParams("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
                overdueAppointments.addToWidgetParams("viewName", "overdueAppointments");
                appointments.addWidget(overdueAppointments);
            }

            Criteria clientCriteria = new Criteria();
            clientCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), DateOperators.TODAY));
            clientCriteria.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), DateOperators.TODAY));

            FacilioContext TodayOpenListContext = V3Util.fetchList(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, true, "myOpenAppointments", null,
                    true, null, null, null,
                    null, 1, 2, false, null, clientCriteria);

            Map<String, Object> todayRecordMap = (Map<String, Object>) TodayOpenListContext.get("recordMap");
            List<ServiceAppointmentContext> todayOpenAppointmentsList = (List<ServiceAppointmentContext>) todayRecordMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

            if(CollectionUtils.isNotEmpty(todayOpenAppointmentsList)) {
                HomePageWidget todayAppointments = new HomePageWidget(HomePageWidget.WidgetType.TODAYS_APPOINTMENTS);
                todayAppointments.addToLayoutParams(appointments, 6, 5);
                todayAppointments.addToWidgetParams(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT,todayOpenAppointmentsList);
                todayAppointments.addToWidgetParams("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
                todayAppointments.addToWidgetParams("viewName", "myOpenAppointments");
                todayAppointments.addToWidgetParams("clientCriteria", clientCriteria.toString());
                appointments.addWidget(todayAppointments);
            }

            if(CollectionUtils.isNotEmpty(appointments.getWidgets())) {
                defaultPage.addSection(appointments);
            }
        }
        return defaultPage;
    }
}
