package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class WorkOrderModule extends BaseModuleConfig {

    public WorkOrderModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.WORK_ORDER);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails = new HashMap<>();
        int order = 1;
        ArrayList<FacilioView> open = new ArrayList<FacilioView>();
        open.add(getAllOpenWorkOrders().setOrder(order++));
        open.add(getAllOverdueWorkOrders().setOrder(order++));
        open.add(getAllDueTodayWorkOrders().setOrder(order++));
        open.add(getOpenPlannedWorkOrders().setOrder(order++));
        open.add(getOpenUnPlannedWorkOrders().setOrder(order++));
        open.add(getUnassignedWorkorders().setOrder(order++));
        groupDetails.put("name", "openworkorders");
        groupDetails.put("displayName", "Open Work Orders");
        groupDetails.put("views", open);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> myopen = new ArrayList<FacilioView>();
        myopen.add(getMyOpenWorkOrders().setOrder(order++));
        myopen.add(getMyTeamOpenWorkOrders().setOrder(order++));
        myopen.add(getMyOverdueWorkOrders().setOrder(order++));
        myopen.add(getMyDueTodayWorkOrders().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "myopenworkorders");
        groupDetails.put("displayName", "My Open Work Orders");
        groupDetails.put("views", myopen);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> resolved = new ArrayList<FacilioView>();
        resolved.add(getAllResolvedWorkOrders().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "resolvedworkorders");
        groupDetails.put("displayName", "Resolved Work Orders");
        groupDetails.put("views", resolved);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> closed = new ArrayList<FacilioView>();
        closed.add(getAllClosedWorkOrders().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "closedworkorders");
        groupDetails.put("displayName", "Closed Work Orders");
        groupDetails.put("views", closed);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> all = new ArrayList<FacilioView>();
        all.add(getAllWorkOrders().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "allworkorders");
        groupDetails.put("displayName", "All Work Orders");
        groupDetails.put("views", all);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> upcoming = new ArrayList<FacilioView>();
        upcoming.add(getUpcomingWorkOrdersThisWeek().setOrder(order++));
        upcoming.add(getUpcomingWorkOrdersNextWeek().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "upcomingworkorders");
        groupDetails.put("displayName", "Upcoming Work Orders");
        groupDetails.put("views", upcoming);
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> occupantSystemViews = new ArrayList<FacilioView>();
        occupantSystemViews.add(getMyAllRequestWorkorders().setOrder(order++));
        occupantSystemViews.add(getMyOpenRequestWorkorders().setOrder(order++));
        occupantSystemViews.add(getMyClosedRequestWorkorders().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "occupantportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("views", occupantSystemViews);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> vendorSystemViews = new ArrayList<FacilioView>();
        vendorSystemViews.add(getVendorOpenWorkOrders().setOrder(order++));
        vendorSystemViews.add(getVendorClosedWorkOrders().setOrder(order++));
        vendorSystemViews.add(getVendorWorkOrders().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "vendorportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("views", vendorSystemViews);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> tenantSystemViews = new ArrayList<FacilioView>();
        tenantSystemViews.add(getTenantWorkorders("tenantWorkorder").setOrder(order++));
        tenantSystemViews.add(getTenantOpenWorkOrders().setOrder(order++));
        tenantSystemViews.add(getTenantClosedWorkOrders().setOrder(order++));
        tenantSystemViews.add(getTenantAllWorkOrders().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "tenantportal-systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("views", tenantSystemViews);
        groupDetails.put("appLinkNames", Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        groupVsViews.add(groupDetails);

        ArrayList<FacilioView> systemViews = new ArrayList<FacilioView>();
        systemViews.add(getFireSafetyWOs().setOrder(order++));
        systemViews.add(getReportView().setOrder(order++));
        systemViews.add(getMyRequestWorkorders("myrequests").setOrder(order++));
        systemViews.add(getRequestedStateApproval("pendingapproval", true).setOrder(order++));
        systemViews.add(getMyRequestWorkorders("myapproval").setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("views", systemViews);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }

    private static FacilioView getAllOpenWorkOrders() {
        // All Open Tickets
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getOpenStatusCondition());
        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("open");
        openTicketsView.setDisplayName("Open");
        openTicketsView.setCriteria(criteria);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        openTicketsView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        openTicketsView.setAppLinkNames(appLinkNames);

        return openTicketsView;
    }
    private static FacilioView getAllOverdueWorkOrders() {
        FacilioView overdueView = new FacilioView();
        overdueView.setName("overdue");
        overdueView.setDisplayName("Overdue");
        Criteria criteria = getOverdueCriteria();
        criteria.addAndCondition(getOpenStatusCondition());
        overdueView.setCriteria(criteria);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        SortField sortField = new SortField(createdTime, false);
        overdueView.setSortFields(Arrays.asList(sortField));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        overdueView.setAppLinkNames(appLinkNames);

        return overdueView;
    }
    private static FacilioView getAllDueTodayWorkOrders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        Criteria criteria = getDueTodayCriteria();
        criteria.addAndCondition(getOpenStatusCondition());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView overdueView = new FacilioView();
        overdueView.setName("duetoday");
        overdueView.setDisplayName("Due Today");
        overdueView.setCriteria(criteria);
        overdueView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        overdueView.setAppLinkNames(appLinkNames);

        return overdueView;
    }
    private static FacilioView getOpenPlannedWorkOrders() {
        FacilioModule ticketsModule = ModuleFactory.getTicketsModule();
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField sourceType = new FacilioField();
        sourceType.setName("sourceType");
        sourceType.setColumnName("SOURCE_TYPE");
        sourceType.setDataType(FieldType.NUMBER);
        sourceType.setModule(ticketsModule);
        Condition sourceTypeCondition = new Condition();
        sourceTypeCondition.setField(sourceType);
        sourceTypeCondition.setOperator(NumberOperators.EQUALS);
        sourceTypeCondition.setValue(String.valueOf(TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIndex()));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(sourceTypeCondition);
        criteria.addAndCondition(getOpenStatusCondition());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView overdueView = new FacilioView();
        overdueView.setName("planned");
        overdueView.setDisplayName("Planned");
        overdueView.setCriteria(criteria);
        overdueView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        overdueView.setAppLinkNames(appLinkNames);

        return overdueView;
    }
    private static FacilioView getOpenUnPlannedWorkOrders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioModule ticketsModule = ModuleFactory.getTicketsModule();
        FacilioField sourceType = new FacilioField();
        sourceType.setName("sourceType");
        sourceType.setColumnName("SOURCE_TYPE");
        sourceType.setDataType(FieldType.NUMBER);
        sourceType.setModule(ticketsModule);
//		sourceType.setExtendedModule(ModuleFactory.getTicketsModule());
        Condition sourceTypeCondition = new Condition();
        sourceTypeCondition.setField(sourceType);
        sourceTypeCondition.setOperator(NumberOperators.NOT_EQUALS);
        sourceTypeCondition.setValue(String.valueOf(TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIndex()));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(sourceTypeCondition);
        criteria.addAndCondition(getOpenStatusCondition());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView overdueView = new FacilioView();
        overdueView.setName("unplanned");
        overdueView.setDisplayName("Un Planned");
        overdueView.setCriteria(criteria);
        overdueView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        overdueView.setAppLinkNames(appLinkNames);

        return overdueView;
    }
    private static FacilioView getUnassignedWorkorders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        Criteria unassignedWOCriteria = getUnAssignedCriteria();
        unassignedWOCriteria.addAndCondition(getOpenStatusCondition());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView unassignedWOView = new FacilioView();
        unassignedWOView.setName("unassigned");
        unassignedWOView.setDisplayName("Unassigned");
        unassignedWOView.setCriteria(unassignedWOCriteria);
        unassignedWOView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        unassignedWOView.setAppLinkNames(appLinkNames);

        return unassignedWOView;
    }
    private static FacilioView getMyOpenWorkOrders() {
        Criteria criteria = new Criteria();
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        criteria.addAndCondition(getOpenStatusCondition());
        criteria.addAndCondition(getMyUserCondition());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("myopen");
        openTicketsView.setDisplayName("My Work Orders");
        openTicketsView.setCriteria(criteria);
        openTicketsView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        openTicketsView.setAppLinkNames(appLinkNames);

        return openTicketsView;
    }
    private static FacilioView getMyTeamOpenWorkOrders() {
        Criteria criteria = new Criteria();
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        criteria.addAndCondition(getOpenStatusCondition());
        criteria.addAndCondition(getMyTeamCondition());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("myteamopen");
        openTicketsView.setDisplayName("My Team Work Orders");
        openTicketsView.setCriteria(criteria);
        openTicketsView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        openTicketsView.setAppLinkNames(appLinkNames);

        return openTicketsView;
    }
    private static FacilioView getMyOverdueWorkOrders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        Criteria criteria = getOverdueCriteria();
        criteria.addAndCondition(getOpenStatusCondition());
        criteria.addAndCondition(getMyUserCondition());
        FacilioView view = new FacilioView();
        view.setName("myoverdue");
        view.setDisplayName("My Overdue");
        view.setCriteria(criteria);
        view.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }
    private static FacilioView getMyDueTodayWorkOrders() {
        FacilioField dueField = new FacilioField();
        dueField.setName("dueDate");
        dueField.setColumnName("DUE_DATE");
        dueField.setDataType(FieldType.DATE_TIME);
        dueField.setModule(ModuleFactory.getTicketsModule());
//		dueField.setExtendedModule(ModuleFactory.getTicketsModule());
        Condition overdue = new Condition();
        overdue.setField(dueField);
        overdue.setOperator(DateOperators.TODAY);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(overdue);
        criteria.addAndCondition(getMyUserCondition());
        criteria.addAndCondition(getOpenStatusCondition());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView view = new FacilioView();
        view.setName("myduetoday");
        view.setDisplayName("My Due Today");
        view.setCriteria(criteria);
        view.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }
    private static FacilioView getFireSafetyWOs() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioModule ticketsModule = ModuleFactory.getTicketsModule();
        LookupField categoryField = new LookupField();
        categoryField.setName("category");
        categoryField.setColumnName("CATEGORY_ID");
        categoryField.setDataType(FieldType.LOOKUP);
        categoryField.setModule(ticketsModule);
        categoryField.setLookupModule(ModuleFactory.getTicketCategoryModule());
        Condition fireSafetyCategory = new Condition();
        fireSafetyCategory.setField(categoryField);
        fireSafetyCategory.setOperator(LookupOperator.LOOKUP);
        fireSafetyCategory.setCriteriaValue(getFireSafetyCategoryCriteria());
        FacilioField sourceField = new FacilioField();
        sourceField.setName("sourceType");
        sourceField.setColumnName("SOURCE_TYPE");
        sourceField.setDataType(FieldType.NUMBER);
        sourceField.setModule(ticketsModule);
        Condition alarmSourceCondition = new Condition();
        alarmSourceCondition.setField(sourceField);
        alarmSourceCondition.setOperator(NumberOperators.EQUALS);
        alarmSourceCondition.setValue(String.valueOf(TicketContext.SourceType.ALARM.getIndex()));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(fireSafetyCategory);
        criteria.addAndCondition(getOpenStatusCondition());
        criteria.addAndCondition(alarmSourceCondition);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView fireSafetyWOView = new FacilioView();
        fireSafetyWOView.setName("openfirealarms");
        fireSafetyWOView.setDisplayName("Fire Alarm Workorders");
        fireSafetyWOView.setCriteria(criteria);
        fireSafetyWOView.setSortFields(sortFields);
        fireSafetyWOView.setHidden(true);
        return fireSafetyWOView;
    }
    private static FacilioView getAllWorkOrders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Workorders");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }
    private static FacilioView getAllResolvedWorkOrders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView resolvedTicketsView = new FacilioView();
        resolvedTicketsView.setName("resolved");
        resolvedTicketsView.setDisplayName("Resolved");
        resolvedTicketsView.setCriteria(getTicketStatusCriteria("Resolved"));
        resolvedTicketsView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        resolvedTicketsView.setAppLinkNames(appLinkNames);

        return resolvedTicketsView;
    }
    private static FacilioView getAllClosedWorkOrders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("closed");
        openTicketsView.setDisplayName("Closed Workorders");
        openTicketsView.setCriteria(getClosedTicketsCriteria());
        openTicketsView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        openTicketsView.setAppLinkNames(appLinkNames);

        return openTicketsView;
    }
    private static FacilioView getReportView() {
        FacilioView reportView = new FacilioView();
        reportView.setName("report");
        reportView.setHidden(true);
        return reportView;
    }
    private static FacilioView getMyRequestWorkorders(String viewName) {
        Criteria criteria = new Criteria();
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        criteria.addAndCondition(getMyRequestCondition());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView myAllWorkordersView = new FacilioView();
        myAllWorkordersView.setName(viewName);
        myAllWorkordersView.setDisplayName("My Work Orders");
        myAllWorkordersView.setCriteria(criteria);
        myAllWorkordersView.setSortFields(sortFields);
        myAllWorkordersView.setHidden(true);
        return myAllWorkordersView;
    }
    private static FacilioView getUpcomingWorkOrdersThisWeek() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        FacilioField pm = new FacilioField();
        pm.setName("pm");
        pm.setDataType(FieldType.NUMBER);
        pm.setColumnName("PM_ID");
        pm.setModule(ModuleFactory.getWorkOrdersModule());
        FacilioField pmv2 = new FacilioField();
        pmv2.setName("pmv2");
        pmv2.setDataType(FieldType.NUMBER);
        pmv2.setColumnName("PM_V2_ID");
        pmv2.setModule(ModuleFactory.getWorkOrdersModule());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getPreOpenStatusCondition());
        criteria.addAndCondition(CriteriaAPI.getCondition(createdTime, DateOperators.CURRENT_WEEK));
        Criteria criteria1 = new Criteria();
        criteria1.addAndCondition(CriteriaAPI.getCondition(pm, CommonOperators.IS_NOT_EMPTY));
        criteria1.addOrCondition(CriteriaAPI.getCondition(pmv2, CommonOperators.IS_NOT_EMPTY));
        criteria.andCriteria(criteria1);
        FacilioView preOpenTicketsView = new FacilioView();
        preOpenTicketsView.setExcludeModuleCriteria(true);
        preOpenTicketsView.setName("upcomingThisWeek");
        preOpenTicketsView.setDisplayName("Upcoming This Week");
        preOpenTicketsView.setCriteria(criteria);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        preOpenTicketsView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        preOpenTicketsView.setAppLinkNames(appLinkNames);

        return preOpenTicketsView;
    }
    private static FacilioView getUpcomingWorkOrdersNextWeek() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        FacilioField pm = new FacilioField();
        pm.setName("pm");
        pm.setDataType(FieldType.NUMBER);
        pm.setColumnName("PM_ID");
        pm.setModule(ModuleFactory.getWorkOrdersModule());
        FacilioField pmv2 = new FacilioField();
        pmv2.setName("pmv2");
        pmv2.setDataType(FieldType.NUMBER);
        pmv2.setColumnName("PM_V2_ID");
        pmv2.setModule(ModuleFactory.getWorkOrdersModule());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getPreOpenStatusCondition());
        criteria.addAndCondition(CriteriaAPI.getCondition(createdTime, DateOperators.NEXT_WEEK));
        Criteria criteria1 = new Criteria();
        criteria1.addAndCondition(CriteriaAPI.getCondition(pm, CommonOperators.IS_NOT_EMPTY));
        criteria1.addOrCondition(CriteriaAPI.getCondition(pmv2, CommonOperators.IS_NOT_EMPTY));
        criteria.andCriteria(criteria1);
        FacilioView preOpenTicketsView = new FacilioView();
        preOpenTicketsView.setExcludeModuleCriteria(true);
        preOpenTicketsView.setName("upcomingNextWeek");
        preOpenTicketsView.setDisplayName("Upcoming Next Week");
        preOpenTicketsView.setCriteria(criteria);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        preOpenTicketsView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        preOpenTicketsView.setAppLinkNames(appLinkNames);

        return preOpenTicketsView;
    }
    private static FacilioView getVendorWorkOrders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView allView = new FacilioView();
        allView.setName("vendorWorkorder");
        allView.setDisplayName("All Workorders");
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
        return allView;
    }
    private static FacilioView getTenantWorkorders(String viewName) {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView allView = new FacilioView();
        allView.setName(viewName);
        allView.setDisplayName("Tenant Workorders");
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        return allView;
    }
    private static FacilioView getTenantOpenWorkOrders() {
        // All Open Tickets
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getOpenStatusCondition());
        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("tenantOpen");
        openTicketsView.setDisplayName("Open");
        openTicketsView.setCriteria(criteria);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        openTicketsView.setSortFields(sortFields);
        openTicketsView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        return openTicketsView;
    }
    private static FacilioView getTenantClosedWorkOrders() {
        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("tenantClosed");
        openTicketsView.setDisplayName("Closed");
        openTicketsView.setCriteria(getClosedTicketsCriteria());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        openTicketsView.setSortFields(sortFields);
        openTicketsView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        return openTicketsView;
    }
    private static FacilioView getTenantAllWorkOrders() {
        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("tenantAll");
        openTicketsView.setDisplayName("All");
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        openTicketsView.setSortFields(sortFields);
        openTicketsView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        return openTicketsView;
    }
    private static FacilioView getVendorOpenWorkOrders() {
        // All Open Tickets
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getOpenStatusCondition());
        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("vendorOpen");
        openTicketsView.setDisplayName("Open");
        openTicketsView.setCriteria(criteria);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        openTicketsView.setSortFields(sortFields);
        openTicketsView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
        return openTicketsView;
    }
    private static FacilioView getVendorClosedWorkOrders() {
        // All Closed Tickets
        FacilioView closedTicketsView = new FacilioView();
        closedTicketsView.setName("vendorClosed");
        closedTicketsView.setDisplayName("Closed");
        closedTicketsView.setCriteria(getClosedTicketsCriteria());
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        closedTicketsView.setSortFields(sortFields);
        closedTicketsView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
        return closedTicketsView;
    }
    private static FacilioView getMyAllRequestWorkorders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView myAllWorkordersView = new FacilioView();
        myAllWorkordersView.setName("myAllWo");
        myAllWorkordersView.setDisplayName("All");
        myAllWorkordersView.setSortFields(sortFields);
        myAllWorkordersView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
        return myAllWorkordersView;
    }
    private static FacilioView getMyOpenRequestWorkorders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView myWorkordersView = new FacilioView();
        myWorkordersView.setName("myOpenWo");
        myWorkordersView.setDisplayName("Open");
        myWorkordersView.setSortFields(sortFields);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getOpenStatusCondition());
        myWorkordersView.setCriteria(criteria);
        myWorkordersView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
        return myWorkordersView;
    }
    private static FacilioView getMyClosedRequestWorkorders() {
        FacilioModule workOrdersModule = ModuleFactory.getWorkOrdersModule();
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(workOrdersModule);
        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView myWorkordersView = new FacilioView();
        myWorkordersView.setName("myClosedWo");
        myWorkordersView.setDisplayName("Closed");
        myWorkordersView.setSortFields(sortFields);
        myWorkordersView.setCriteria(getClosedTicketsCriteria());
        myWorkordersView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
        return myWorkordersView;
    }
    public static FacilioView getRequestedStateApproval(String viewName, boolean isHidden) {
        FacilioModule module = ModuleFactory.getTicketsModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        Criteria requestedStateCriteria = getRequestedStateCriteria(true);
        Condition requested = new Condition();
        requested.setField(statusField);
        requested.setOperator(LookupOperator.LOOKUP);
        requested.setCriteriaValue(requestedStateCriteria);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(requested);
        LookupField approvalStatusField = new LookupField();
        approvalStatusField.setName("approvalStatus");
        approvalStatusField.setColumnName("ARRPOVAL_STATE");
        approvalStatusField.setDataType(FieldType.LOOKUP);
        approvalStatusField.setModule(module);
        approvalStatusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        Criteria c = new Criteria();
        c.addAndCondition(CriteriaAPI.getCondition(approvalStatusField, CommonOperators.IS_NOT_EMPTY));
        c.addAndCondition(CriteriaAPI.getCondition(approvalStatusField, requestedStateCriteria, LookupOperator.LOOKUP));
        criteria.orCriteria(c);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getWorkOrdersModule());
        FacilioView rejectedApproval = new FacilioView();
        rejectedApproval.setName(viewName);
        rejectedApproval.setDisplayName("Pending Approval");
        rejectedApproval.setCriteria(criteria);
        rejectedApproval.setHidden(isHidden);
        rejectedApproval.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        return rejectedApproval;
    }
    private static Condition getMyRequestCondition() {
        FacilioModule workOrderRequestsModule = ModuleFactory.getWorkOrdersModule();
        LookupField userField = new LookupField();
        userField.setName("requester");
        userField.setColumnName("REQUESTER_ID");
        userField.setDataType(FieldType.LOOKUP);
        userField.setModule(workOrderRequestsModule);
        userField.setSpecialType(FacilioConstants.ContextNames.REQUESTER);
        Condition myUserCondition = new Condition();
        myUserCondition.setField(userField);
        myUserCondition.setOperator(PickListOperators.IS);
        myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);
        return myUserCondition;
    }
    public static Condition getOpenStatusCondition() {
        FacilioModule module = ModuleFactory.getTicketsModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getOpenStatusCriteria());
        return open;
    }
    public static Criteria getOpenStatusCriteria() {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("typeCode");
        statusTypeField.setColumnName("STATUS_TYPE");
        statusTypeField.setDataType(FieldType.NUMBER);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());
        Condition statusOpen = new Condition();
        statusOpen.setField(statusTypeField);
        statusOpen.setOperator(NumberOperators.EQUALS);
        statusOpen.setValue(String.valueOf(FacilioStatus.StatusType.OPEN.getIntVal()));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusOpen);
        return criteria;
    }
    public static Criteria getRequestedStateCriteria(boolean isRequested) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("requestedState");
        statusTypeField.setColumnName("REQUESTED_STATE");
        statusTypeField.setDataType(FieldType.BOOLEAN);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());
        Condition status = new Condition();
        status.setField(statusTypeField);
        status.setOperator(BooleanOperators.IS);
        status.setValue(String.valueOf(isRequested));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(status);
        return criteria;
    }
    public static Criteria getClosedTicketsCriteria () {
        FacilioModule module = ModuleFactory.getTicketsModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
//		statusField.setExtendedModule(ModuleFactory.getTicketsModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        Condition ticketClose = new Condition();
        ticketClose.setField(statusField);
        ticketClose.setOperator(LookupOperator.LOOKUP);
        ticketClose.setCriteriaValue(getCloseStatusCriteria());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(ticketClose);
        return criteria;
    }
    public static Criteria getCloseStatusCriteria() {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("typeCode");
        statusTypeField.setColumnName("STATUS_TYPE");
        statusTypeField.setDataType(FieldType.NUMBER);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());
        Condition statusClose = new Condition();
        statusClose.setField(statusTypeField);
        statusClose.setOperator(NumberOperators.EQUALS);
        statusClose.setValue(String.valueOf(FacilioStatus.StatusType.CLOSED.getIntVal()));
        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusClose);
        return criteria;
    }
    public static Condition getPreOpenStatusCondition() {
        FacilioModule module = ModuleFactory.getTicketsModule();
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        Condition preopen = CriteriaAPI.getCondition(statusField, CommonOperators.IS_EMPTY);
        return preopen;
    }
    private static Criteria getFireSafetyCategoryCriteria() {
        FacilioField categoryNameField = new FacilioField();
        categoryNameField.setName("name");
        categoryNameField.setColumnName("NAME");
        categoryNameField.setDataType(FieldType.STRING);
        categoryNameField.setModule(ModuleFactory.getTicketCategoryModule());
        Condition fireSafety = new Condition();
        fireSafety.setField(categoryNameField);
        fireSafety.setOperator(StringOperators.IS);
        fireSafety.setValue("Fire Safety");
        Criteria criteria = new Criteria();
        criteria.addAndCondition(fireSafety);
        return criteria;
    }
    public static Condition getMyUserCondition() {
        LookupField userField = new LookupField();
        userField.setName("assignedTo");
        userField.setColumnName("ASSIGNED_TO_ID");
        userField.setDataType(FieldType.LOOKUP);
        userField.setModule(ModuleFactory.getTicketsModule());
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);
        Condition myUserCondition = new Condition();
        myUserCondition.setField(userField);
        myUserCondition.setOperator(PickListOperators.IS);
        myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);
        return myUserCondition;
    }
    private static Criteria getOverdueCriteria() {
        FacilioField dueField = new FacilioField();
        dueField.setName("dueDate");
        dueField.setColumnName("DUE_DATE");
        dueField.setDataType(FieldType.DATE_TIME);
        dueField.setModule(ModuleFactory.getTicketsModule());
        Condition overdue = new Condition();
        overdue.setField(dueField);
        overdue.setOperator(DateOperators.TILL_NOW);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(overdue);
        return criteria;
    }
    private static Condition getMyTeamCondition() {
        LookupField groupField = new LookupField();
        groupField.setName("assignmentGroup");
        groupField.setColumnName("ASSIGNMENT_GROUP_ID");
        groupField.setDataType(FieldType.LOOKUP);
        groupField.setModule(ModuleFactory.getTicketsModule());
        groupField.setSpecialType(FacilioConstants.ContextNames.GROUPS);
        /*
         * String groupIds = ""; try { List<Group> myGroups =
         * AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().
         * getId()); if (myGroups != null) { for (int i=0; i< myGroups.size();
         * i++) { if (i != 0) { groupIds += ","; } groupIds +=
         * myGroups.get(i).getId(); } } } catch (Exception e) {
         * e.printStackTrace(); } if (groupIds.equals("")) { groupIds = "-100";
         * }
         */
        Condition myTeamCondition = new Condition();
        myTeamCondition.setField(groupField);
        myTeamCondition.setOperator(PickListOperators.IS);
        myTeamCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP);
        return myTeamCondition;
    }
    private static Criteria getTicketStatusCriteria(String status) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("status");
        statusTypeField.setColumnName("STATUS");
        statusTypeField.setDataType(FieldType.STRING);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());
        Condition statusClose = new Condition();
        statusClose.setField(statusTypeField);
        statusClose.setOperator(StringOperators.IS);
        statusClose.setValue(status);
        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(statusClose);
        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(ModuleFactory.getTicketsModule());
//		statusField.setExtendedModule(ModuleFactory.getTicketsModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());
        Condition ticketClose = new Condition();
        ticketClose.setField(statusField);
        ticketClose.setOperator(LookupOperator.LOOKUP);
        ticketClose.setCriteriaValue(statusCriteria);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(ticketClose);
        return criteria;
    }
    private static Criteria getUnAssignedCriteria() {
        LookupField userField = new LookupField();
        userField.setName("assignedTo");
        userField.setColumnName("ASSIGNED_TO_ID");
        userField.setDataType(FieldType.LOOKUP);
        FacilioModule module = ModuleFactory.getTicketsModule();
        userField.setModule(module);
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);
        Condition userFieldCondition = new Condition();
        userFieldCondition.setField(userField);
        userFieldCondition.setOperator(CommonOperators.IS_EMPTY);
        // LookupField groupField = (LookupField)
        // FieldFactory.getField("assignmentGroup", "ASSIGNMENT_GROUP_ID",
        // workOrdersModule, FieldType.LOOKUP);
        // groupField.setExtendedModule(ModuleFactory.getTicketsModule());
        // groupField.setSpecialType(FacilioConstants.ContextNames.GROUPS);
        //
        // Condition groupFieldCondition = new Condition();
        // groupFieldCondition.setField(groupField);
        // groupFieldCondition.setOperator(CommonOperators.IS_EMPTY);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(userFieldCondition);
        // criteria.addAndCondition(groupFieldCondition);
        return criteria;
    }
    private static Criteria getDueTodayCriteria() {
        FacilioModule ticketsModule = ModuleFactory.getTicketsModule();
        FacilioField dueField = new FacilioField();
        dueField.setName("dueDate");
        dueField.setColumnName("DUE_DATE");
        dueField.setDataType(FieldType.DATE_TIME);
        dueField.setModule(ticketsModule);
        Condition overdue = new Condition();
        overdue.setField(dueField);
        overdue.setOperator(DateOperators.TODAY);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(overdue);
        return criteria;
    }
    public static FacilioView getCustomModuleAllView(FacilioModule moduleObj) throws Exception {
        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", moduleObj);
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All " + moduleObj.getDisplayName());
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        allView.setDefault(true);
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

        FacilioForm serviceWorkOrderForm = new FacilioForm();
        serviceWorkOrderForm.setDisplayName("Standard");
        serviceWorkOrderForm.setName("default_workorder_portal");
        serviceWorkOrderForm.setModule(workOrderModule);
        serviceWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        serviceWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> serviceWorkOrderFormFields = new ArrayList<>();
        serviceWorkOrderFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site" ,2, 1));
        serviceWorkOrderFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 3, 1));
        serviceWorkOrderFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 4, 1));
        FormField urgency = new FormField("urgency", FacilioField.FieldDisplayType.URGENCY, "Urgency", FormField.Required.OPTIONAL, 5, 1);
        urgency.setValueObject(WorkOrderContext.WOUrgency.NOTURGENT.getValue());
        serviceWorkOrderFormFields.add(urgency);
        serviceWorkOrderFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachment", FormField.Required.OPTIONAL, 6, 1));


        FormSection serviceWorkOrderFormSection = new FormSection("WORKORDER", 1, serviceWorkOrderFormFields, true);
        serviceWorkOrderFormSection.setSectionType(FormSection.SectionType.FIELDS);
        serviceWorkOrderForm.setSections(Collections.singletonList(serviceWorkOrderFormSection));
        serviceWorkOrderForm.setIsSystemForm(true);
        serviceWorkOrderForm.setType(FacilioForm.Type.FORM);

        FacilioForm approvalForm = new FacilioForm();
        approvalForm.setDisplayName("Approval");
        approvalForm.setName("workOrder");
        approvalForm.setModule(workOrderModule);
        approvalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        approvalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> approvalFormfields = new ArrayList<>();
        approvalFormfields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        approvalFormfields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 2, 1));
        approvalFormfields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        approvalFormfields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "ticketcategory", 4, 2));
        approvalFormfields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 4, 3));
        approvalFormfields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", FormField.Required.OPTIONAL, "tickettype", 5, 1));
        approvalFormfields.add(new FormField("resource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, 6, 1));
        approvalFormfields.add(new FormField("urgency", FacilioField.FieldDisplayType.URGENCY, "Urgency", FormField.Required.OPTIONAL, 7, 1));
        approvalFormfields.add(new FormField("assignment", FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.OPTIONAL, 8, 1));
        approvalFormfields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, "attachment", 9, 1));

        FormSection approvalFormFormSection = new FormSection("WORKORDER", 1, approvalFormfields, true);
        approvalFormFormSection.setSectionType(FormSection.SectionType.FIELDS);
        approvalForm.setSections(Collections.singletonList(approvalFormFormSection));
        approvalForm.setIsSystemForm(true);
        approvalForm.setType(FacilioForm.Type.FORM);

        FacilioForm mobileApprovalForm = new FacilioForm();
        mobileApprovalForm.setDisplayName("Approval");
        mobileApprovalForm.setName("mobile_approval");
        mobileApprovalForm.setModule(workOrderModule);
        mobileApprovalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        mobileApprovalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> mobileApprovalFormFields = new ArrayList<>();
        mobileApprovalFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        mobileApprovalFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.REQUIRED, 2, 1));
        mobileApprovalFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 1));
        mobileApprovalFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.REQUIRED, 4, 1));
        mobileApprovalFormFields.add(new FormField("assignment", FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.REQUIRED, 5, 1));
        mobileApprovalFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 6, 1));
        mobileApprovalFormFields.add(new FormField("urgency", FacilioField.FieldDisplayType.URGENCY, "Urgency", FormField.Required.OPTIONAL, "urgency" , 7, 1));
        mobileApprovalFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachment", FormField.Required.OPTIONAL, 8, 1));

        FormSection mobileApprovalFormSection = new FormSection("WORKORDER", 1, mobileApprovalFormFields, true);
        mobileApprovalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        mobileApprovalForm.setSections(Collections.singletonList(mobileApprovalFormSection));
        mobileApprovalForm.setIsSystemForm(true);
        mobileApprovalForm.setType(FacilioForm.Type.FORM);

        FacilioForm mobileWorkOrderForm = new FacilioForm();
        mobileWorkOrderForm.setDisplayName("SUBMIT WORKORDER");
        mobileWorkOrderForm.setName("mobile_default");
        mobileWorkOrderForm.setModule(workOrderModule);
        mobileWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        mobileWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> mobileMobileWorkOrderFormFields = new ArrayList<>();
        mobileMobileWorkOrderFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("resource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, 4, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("assignment", FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.OPTIONAL, 5, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "ticketcategory", 6, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 7, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachment", FormField.Required.OPTIONAL, 8, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("sendForApproval", FacilioField.FieldDisplayType.DECISION_BOX, "Send For Approval", FormField.Required.OPTIONAL, 9, 1));
        mobileMobileWorkOrderFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, 10, 1));

        FormSection mobileMobileWorkOrderFormSection = new FormSection("WORKORDER", 1, mobileMobileWorkOrderFormFields, true);
        mobileMobileWorkOrderFormSection.setSectionType(FormSection.SectionType.FIELDS);
        mobileWorkOrderForm.setSections(Collections.singletonList(mobileMobileWorkOrderFormSection));
        mobileWorkOrderForm.setIsSystemForm(true);
        mobileWorkOrderForm.setType(FacilioForm.Type.FORM);

        FacilioForm webWorkOrderForm = new FacilioForm();
        webWorkOrderForm.setDisplayName("Standard");
        webWorkOrderForm.setName("default_workorder_web");
        webWorkOrderForm.setModule(workOrderModule);
        webWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        webWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> webWorkOrderFormDefaultFields = new ArrayList<>();

        FacilioField srField = null;
        try {
            srField = modBean.getField("serviceRequest", FacilioConstants.ContextNames.WORK_ORDER);
        }
        catch(Exception e) {

        }
        webWorkOrderFormDefaultFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        webWorkOrderFormDefaultFields.add(getSiteField());
        webWorkOrderFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        webWorkOrderFormDefaultFields.addAll(getWoClassifierFields());
        webWorkOrderFormDefaultFields.add(getWoResourceField());
        webWorkOrderFormDefaultFields.add(new FormField("assignment", FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.OPTIONAL, 7, 1));
        webWorkOrderFormDefaultFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, "attachment", 8, 1));
        webWorkOrderFormDefaultFields.add(new FormField("parentWO", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Parent WorkOrder", FormField.Required.OPTIONAL, 9, 1));
        webWorkOrderFormDefaultFields.add(new FormField("sendForApproval", FacilioField.FieldDisplayType.DECISION_BOX, "Send For Approval", FormField.Required.OPTIONAL, 10, 1));
        webWorkOrderFormDefaultFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, 11, 1));
        webWorkOrderFormDefaultFields.add(new FormField(srField.getId(), "serviceRequest", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Service Request", FormField.Required.OPTIONAL, 14, 1, true));

        List<FormField> webWorkOrderFormTaskFields = new ArrayList<>();
        webWorkOrderFormTaskFields.add(new FormField("tasks", FacilioField.FieldDisplayType.TASKS, "TASKS", FormField.Required.OPTIONAL, 13, 1));

        List<FormField> webWorkOrderFormFields = new ArrayList<>();
        webWorkOrderFormFields.addAll(webWorkOrderFormDefaultFields);
        webWorkOrderFormFields.addAll(webWorkOrderFormTaskFields);

        FormSection defaultSection = new FormSection("WORKORDER", 1, webWorkOrderFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection tasksSection = new FormSection("TASKS", 2, webWorkOrderFormTaskFields, true);
        tasksSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> webWorkOrderFormSections = new ArrayList<>();
        webWorkOrderFormSections.add(defaultSection);
        webWorkOrderFormSections.add(tasksSection);

        webWorkOrderForm.setSections(webWorkOrderFormSections);
        webWorkOrderForm.setIsSystemForm(true);
        webWorkOrderForm.setType(FacilioForm.Type.FORM);


        FacilioForm alarmWorkOrderForm = new FacilioForm();
        alarmWorkOrderForm.setDisplayName("Alarm Workorder");
        alarmWorkOrderForm.setName("alarm_workorder_web");
        alarmWorkOrderForm.setModule(workOrderModule);
        alarmWorkOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        alarmWorkOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        alarmWorkOrderForm.setHideInList(true);
        alarmWorkOrderForm.setIgnoreCustomFields(true);

        List<FormField> alarmWorkOrderFormFields = new ArrayList<>();
        alarmWorkOrderFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "ticketcategory", 2, 1));
        alarmWorkOrderFormFields.add(new FormField("assignment", FacilioField.FieldDisplayType.TEAMSTAFFASSIGNMENT, "Team/Staff", FormField.Required.OPTIONAL, 3, 1));
        alarmWorkOrderFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 4, 1));

        FormSection alarmWorkOrderFormSection = new FormSection("WORKORDER", 1, alarmWorkOrderFormFields, true);
        alarmWorkOrderFormSection.setSectionType(FormSection.SectionType.FIELDS);
        alarmWorkOrderForm.setSections(Collections.singletonList(alarmWorkOrderFormSection));
        alarmWorkOrderForm.setIsSystemForm(true);
        alarmWorkOrderForm.setType(FacilioForm.Type.FORM);

        FacilioForm preventiveMaintenanceForm =new FacilioForm();
        preventiveMaintenanceForm.setDisplayName("PREVENTIVE MAINTENANCE");
        preventiveMaintenanceForm.setName("web_pm");
        preventiveMaintenanceForm.setModule(workOrderModule);
        preventiveMaintenanceForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        preventiveMaintenanceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        preventiveMaintenanceForm.setHideInList(true);

        List<FormField> preventiveMaintenanceFormFields = new ArrayList<>();
        preventiveMaintenanceFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 1, 1));
        preventiveMaintenanceFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", FormField.Required.REQUIRED, "tickettype", 2, 1));
        preventiveMaintenanceFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 3, 1));
        preventiveMaintenanceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 4, 1));
        preventiveMaintenanceFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "ticketcategory", 5, 2));
        preventiveMaintenanceFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 5, 3));
        preventiveMaintenanceFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION, "Due Duration", FormField.Required.OPTIONAL, "duration", 6, 1));
        preventiveMaintenanceFormFields.add(new FormField("estimatedWorkDuration", FacilioField.FieldDisplayType.DURATION, "Estimated Duration", FormField.Required.OPTIONAL, "duration", 7, 1));
        preventiveMaintenanceFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, "vendors", 11, 1));
        FormField groups = new FormField("groups", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Team", FormField.Required.OPTIONAL, "groups", 8, 1);
        groups.addToConfig("isFiltersEnabled", true); // groups is special form field without actual field
        groups.addToConfig("lookupModuleName", "groups");
        preventiveMaintenanceFormFields.add(groups);
        preventiveMaintenanceFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, "attachment", 9, 1));

        FormSection preventiveMaintenanceFormSection = new FormSection("WORKORDER", 1, preventiveMaintenanceFormFields, true);
        preventiveMaintenanceFormSection.setSectionType(FormSection.SectionType.FIELDS);
        preventiveMaintenanceForm.setSections(Collections.singletonList(preventiveMaintenanceFormSection));
        preventiveMaintenanceForm.setIsSystemForm(true);
        preventiveMaintenanceForm.setType(FacilioForm.Type.FORM);


        FacilioForm multiPreventiveMaintenanceForm =new FacilioForm();
        multiPreventiveMaintenanceForm.setDisplayName("PREVENTIVE MAINTENANCE");
        multiPreventiveMaintenanceForm.setName("multi_web_pm");
        multiPreventiveMaintenanceForm.setModule(workOrderModule);
        multiPreventiveMaintenanceForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        multiPreventiveMaintenanceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
        multiPreventiveMaintenanceForm.setHideInList(false);

        List<FormField> multiPreventiveMaintenanceFormFields = new ArrayList<>();
        multiPreventiveMaintenanceFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", FormField.Required.REQUIRED, "tickettype", 2, 1));
        multiPreventiveMaintenanceFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 3, 1));
        multiPreventiveMaintenanceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 4, 1));
        multiPreventiveMaintenanceFormFields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "ticketcategory", 5, 2));
        multiPreventiveMaintenanceFormFields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 5, 3));
        multiPreventiveMaintenanceFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.DURATION, "Due Duration", FormField.Required.OPTIONAL, "duration", 6, 1));
        multiPreventiveMaintenanceFormFields.add(new FormField("estimatedWorkDuration", FacilioField.FieldDisplayType.DURATION, "Estimated Duration", FormField.Required.OPTIONAL, "duration", 7, 1));
        multiPreventiveMaintenanceFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.OPTIONAL, "vendors", 11, 1));
//        FormField group = new FormField("groups", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Team", FormField.Required.OPTIONAL, "groups", 8, 1);
//        group.addToConfig("isFiltersEnabled", true); // groups is special form field without actual field
//        group.addToConfig("lookupModuleName", "groups");
        multiPreventiveMaintenanceFormFields.add(groups);
        multiPreventiveMaintenanceFormFields.add(new FormField("attachedFiles", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, "attachment", 9, 1));

        FormSection multiPreventiveMaintenanceFormSection = new FormSection("WORKORDER", 1, multiPreventiveMaintenanceFormFields, true);
        multiPreventiveMaintenanceFormSection.setSectionType(FormSection.SectionType.FIELDS);
        multiPreventiveMaintenanceForm.setSections(Collections.singletonList(multiPreventiveMaintenanceFormSection));
        multiPreventiveMaintenanceForm.setIsSystemForm(true);
        multiPreventiveMaintenanceForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> workOrderModuleForms = new ArrayList<>();
        workOrderModuleForms.add(serviceWorkOrderForm);
        workOrderModuleForms.add(approvalForm);
        workOrderModuleForms.add(mobileApprovalForm);
        workOrderModuleForms.add(mobileWorkOrderForm);
        workOrderModuleForms.add(webWorkOrderForm);
        workOrderModuleForms.add(alarmWorkOrderForm);
        workOrderModuleForms.add(preventiveMaintenanceForm);
        workOrderModuleForms.add(multiPreventiveMaintenanceForm);

        return workOrderModuleForms;
    }

    public static FormField getSiteField() {
        return new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 2, 1);
    }
    public static List<FormField> getWoClassifierFields() {
        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "ticketcategory", 4, 2));
        fields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Maintenance Type", FormField.Required.OPTIONAL, "tickettype", 4, 3));
        fields.add(new FormField("priority", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Priority", FormField.Required.OPTIONAL, "ticketpriority", 5, 1));
        return fields;
    }
    public static FormField getWoResourceField() {
        return new FormField("resource", FacilioField.FieldDisplayType.WOASSETSPACECHOOSER, "Space/Asset", FormField.Required.OPTIONAL, 6, 1);
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields maintenanceApp = new ScopeVariableModulesFields();
        maintenanceApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_maintenance_site"));
        maintenanceApp.setModuleId(module.getModuleId());
        maintenanceApp.setFieldName("siteId");

//        ScopeVariableModulesFields occupantApp = new ScopeVariableModulesFields();
//        occupantApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_occupant_user"));
//        occupantApp.setModuleId(module.getModuleId());
//        occupantApp.setFieldName("requester");

        ScopeVariableModulesFields tenantApp = new ScopeVariableModulesFields();
        tenantApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_tenant_user"));
        tenantApp.setModuleId(module.getModuleId());
        tenantApp.setFieldName("tenant");

        ScopeVariableModulesFields vendorApp = new ScopeVariableModulesFields();
        vendorApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_vendor_user"));
        vendorApp.setModuleId(module.getModuleId());
        vendorApp.setFieldName("vendor");

        scopeConfigList = Arrays.asList(maintenanceApp,tenantApp,vendorApp);
        return scopeConfigList;
    }
}