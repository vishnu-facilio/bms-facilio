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

            PageTabContext mobileTab = new PageTabContext("home","Home",10D, PageTabContext.TabType.SIMPLE,false,-1);
            homePage.setLayouts(new HashMap<String, List<PageTabContext>>());
            homePage.getLayouts().put(PagesContext.PageLayoutType.MOBILE.name(), new ArrayList<>(Arrays.asList(mobileTab)));

            PageColumnContext column = new PageColumnContext(10D, PageColumnContext.ColumnWidth.FULL_WIDTH.getWidth());
            mobileTab.setColumns(new ArrayList<>(Arrays.asList(column)));
            column.setParentContext(mobileTab);



            V3PeopleContext peopleContext = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, currentUser.getPeopleId(), V3PeopleContext.class);
            if (peopleContext != null) {
                JSONObject summaryWidgetParams = new JSONObject();
                summaryWidgetParams.put(FacilioConstants.ContextNames.PEOPLE, FieldUtil.getAsJSON(peopleContext));
                PageSectionContext person = new PageSectionContext("peopleSummary","Summary",CollectionUtils.isNotEmpty(column.getSections()) ? ((column.getSections().size()+1) * 10D ) : 10,null);
                if(column.getSections() == null) {
                    column.setSections(new ArrayList<>(Arrays.asList(person)));
                }
                else {
                    column.getSections().add(person);
                }
                person.setParentContext(column);

                PageSectionWidgetContext peopleSummaryWidget = new PageSectionWidgetContext("peopleStatusWidget","Summary", PageWidget.WidgetType.PEOPLE_STATUS_WIDGET,10D,0,0,null,summaryWidgetParams);
                peopleSummaryWidget.setHeight(9L);
                peopleSummaryWidget.setWidth(12L);
                peopleSummaryWidget.setConfigType(WidgetConfigContext.ConfigType.FIXED);
                person.setWidgets(new ArrayList<>(Arrays.asList(peopleSummaryWidget)));
                peopleSummaryWidget.setParentContext(person);
            }

            List<FacilioField> saFields = Constants.getModBean().getAllFields(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
            Map<String, FacilioField> saFieldMap = FieldFactory.getAsMap(saFields);

            PageSectionContext Ongoing = new PageSectionContext("ongoing","Ongoing Work Items",CollectionUtils.isNotEmpty(column.getSections()) ? ((column.getSections().size()+1) * 10D ) : 10,null);

            List<TimeSheetContext> ongoingTimeSheet = ServiceAppointmentUtil.getOngoingTimeSheets(currentUser.getPeopleId(), null);
            if (CollectionUtils.isNotEmpty(ongoingTimeSheet)) {
                JSONObject workWidgetParams = new JSONObject();
                workWidgetParams.put(FacilioConstants.TimeSheet.TIME_SHEET, FieldUtil.getAsMapList(ongoingTimeSheet,TimeSheetContext.class));
                PageSectionWidgetContext ongoingWorkWidget = new PageSectionWidgetContext("ongoingTimeSheetWidget","Ongoing Work", PageWidget.WidgetType.ONGOING_TIMESHEET_WIDGET,CollectionUtils.isNotEmpty(Ongoing.getWidgets()) ? ((Ongoing.getWidgets().size()+1) * 10D ) : 10,0,0,null,workWidgetParams);
                ongoingWorkWidget.setHeight(6L);
                ongoingWorkWidget.setWidth(12L);
                ongoingWorkWidget.setConfigType(WidgetConfigContext.ConfigType.FIXED);
                Ongoing.setWidgets(new ArrayList<>(Arrays.asList(ongoingWorkWidget)));
                ongoingWorkWidget.setParentContext(Ongoing);
            }

            List<TripContext> ongoingTrips = ServiceAppointmentUtil.getOngoingTrips(currentUser.getPeopleId(), null);
            if (CollectionUtils.isNotEmpty(ongoingTrips)) {
                JSONObject tripWidgetParams = new JSONObject();
                tripWidgetParams.put(FacilioConstants.Trip.TRIP, FieldUtil.getAsMapList(ongoingTrips,TripContext.class));
                PageSectionWidgetContext ongoingTripWidget = new PageSectionWidgetContext("ongoingTripWidget","Ongoing Trip", PageWidget.WidgetType.ONGOING_TRIP_WIDGET,CollectionUtils.isNotEmpty(Ongoing.getWidgets()) ? ((Ongoing.getWidgets().size()+1) * 10D ) : 10,0,0,null,tripWidgetParams);
                ongoingTripWidget.setHeight(6L);
                ongoingTripWidget.setWidth(12L);
                ongoingTripWidget.setConfigType(WidgetConfigContext.ConfigType.FIXED);
                if(Ongoing.getWidgets() == null) {
                    Ongoing.setWidgets(new ArrayList<>(Arrays.asList(ongoingTripWidget)));
                }
                else {
                    Ongoing.getWidgets().add(ongoingTripWidget);
                }
                ongoingTripWidget.setParentContext(Ongoing);
            }

            if (CollectionUtils.isNotEmpty(Ongoing.getWidgets())) {
                if(column.getSections() == null) {
                    column.setSections(new ArrayList<>(Arrays.asList(Ongoing)));
                }
                else {
                    column.getSections().add(Ongoing);
                }
                Ongoing.setParentContext(column);
            }

            PageSectionContext appointments = new PageSectionContext("appointments","Appointments",CollectionUtils.isNotEmpty(column.getSections()) ? ((column.getSections().size()+1) * 10D ) : 10,null);

            FacilioContext overDueListContext = V3Util.fetchList(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, true, "overdueAppointments", null,
                    true, null, null, null,
                    null, 1, 2, false, null, null);

            Map<String, Object> overduesRecordMap = (Map<String, Object>) overDueListContext.get("recordMap");
            List<ServiceAppointmentContext> overdueAppointmentsList = (List<ServiceAppointmentContext>) overduesRecordMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

            if (CollectionUtils.isNotEmpty(overdueAppointmentsList)) {
                JSONObject overdueWidgetParams = new JSONObject();
                overdueWidgetParams.put(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, FieldUtil.getAsMapList(overdueAppointmentsList, ServiceAppointmentContext.class));
                overdueWidgetParams.put("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
                overdueWidgetParams.put("viewName", "overdueAppointments");
                PageSectionWidgetContext overdueWidget = new PageSectionWidgetContext("overdueServiceAppointmentsWidget","Overdue Appointments", PageWidget.WidgetType.OVERDUE_SERVICE_APPOINTMENTS_WIDGET,CollectionUtils.isNotEmpty(appointments.getWidgets()) ? ((appointments.getWidgets().size()+1) * 10D ) : 10,0,0,null,overdueWidgetParams);
                overdueWidget.setHeight(21L);
                overdueWidget.setWidth(12L);
                overdueWidget.setConfigType(WidgetConfigContext.ConfigType.FIXED);
                if(appointments.getWidgets() == null) {
                    appointments.setWidgets(new ArrayList<>(Arrays.asList(overdueWidget)));
                }
                else {
                    appointments.getWidgets().add(overdueWidget);
                }
                overdueWidget.setParentContext(appointments);
            }

            Criteria todayCriteria = new Criteria();
            todayCriteria.addAndCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_START_TIME), DateOperators.TODAY));
            todayCriteria.addOrCondition(CriteriaAPI.getCondition(saFieldMap.get(FacilioConstants.ServiceAppointment.SCHEDULED_END_TIME), DateOperators.TODAY));

            FacilioContext TodayOpenListContext = V3Util.fetchList(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, true, "myOpenAppointments", null,
                    true, null, null, null,
                    null, 1, 2, false, null, todayCriteria);

            Map<String, Object> todayRecordMap = (Map<String, Object>) TodayOpenListContext.get("recordMap");
            List<ServiceAppointmentContext> todayOpenAppointmentsList = (List<ServiceAppointmentContext>) todayRecordMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

            if (CollectionUtils.isNotEmpty(todayOpenAppointmentsList)) {
                JSONObject todayWidgetParams = new JSONObject();
                todayWidgetParams.put(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, FieldUtil.getAsMapList(todayOpenAppointmentsList, ServiceAppointmentContext.class));
                todayWidgetParams.put("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
                todayWidgetParams.put("viewName", "myOpenAppointments");
                todayWidgetParams.put("clientCriteria", FieldUtil.getAsJSON(todayCriteria));
                PageSectionWidgetContext todayWidget = new PageSectionWidgetContext("todayServiceAppointmentsWidget","Today Appointments", PageWidget.WidgetType.TODAY_SERVICE_APPOINTMENTS_WIDGET,CollectionUtils.isNotEmpty(appointments.getWidgets()) ? ((appointments.getWidgets().size()+1) * 10D ) : 10,0,0,null,todayWidgetParams);
                todayWidget.setHeight(21L);
                todayWidget.setWidth(12L);
                todayWidget.setConfigType(WidgetConfigContext.ConfigType.FIXED);
                if(appointments.getWidgets() == null) {
                    appointments.setWidgets(new ArrayList<>(Arrays.asList(todayWidget)));
                }
                else {
                    appointments.getWidgets().add(todayWidget);
                }
                todayWidget.setParentContext(appointments);
            }

            FacilioContext openAppointmentsListContext = V3Util.fetchList(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, true, "myOpenAppointments", null,
                    true, null, null, null,
                    null, 1, 2, false, null, null);

            Map<String, Object> openRecordMap = (Map<String, Object>) openAppointmentsListContext.get("recordMap");
            List<ServiceAppointmentContext> openAppointmentsList = (List<ServiceAppointmentContext>) openRecordMap.get(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);

            if (CollectionUtils.isNotEmpty(openAppointmentsList)) {
                JSONObject openWidgetParams = new JSONObject();
                openWidgetParams.put(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT, FieldUtil.getAsMapList(openAppointmentsList, ServiceAppointmentContext.class));
                openWidgetParams.put("moduleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT);
                openWidgetParams.put("viewName", "myOpenAppointments");
                PageSectionWidgetContext openWidget = new PageSectionWidgetContext("openServiceAppointmentsWidget","Open Appointments", PageWidget.WidgetType.OPEN_SERVICE_APPOINTMENTS_WIDGET,CollectionUtils.isNotEmpty(appointments.getWidgets()) ? ((appointments.getWidgets().size()+1) * 10D ) : 10,0,0,null,openWidgetParams);
                openWidget.setHeight(21L);
                openWidget.setWidth(12L);
                openWidget.setConfigType(WidgetConfigContext.ConfigType.FIXED);
                if(appointments.getWidgets() == null) {
                    appointments.setWidgets(new ArrayList<>(Arrays.asList(openWidget)));
                }
                else {
                    appointments.getWidgets().add(openWidget);
                }
                openWidget.setParentContext(appointments);
            }

            if (CollectionUtils.isNotEmpty(appointments.getWidgets())) {
                if(column.getSections() == null) {
                    column.setSections(new ArrayList<>(Arrays.asList(appointments)));
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
