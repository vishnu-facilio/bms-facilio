package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.SummaryWidgetUtil;
import org.json.simple.JSONObject;

import java.util.*;

public class SurveyResponseModule extends BaseModuleConfig{
    public SurveyResponseModule(){
        setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();

        Map<String, Object> serviceRequestGroupDetails;

        int srViewOrder = 1;
        ArrayList<FacilioView> serviceRequestViewFields = new ArrayList<FacilioView>();
        serviceRequestViewFields.add(getServiceRequestViewOne().setOrder(srViewOrder++));

        serviceRequestGroupDetails = new HashMap<>();
        serviceRequestGroupDetails.put("name", "ServiceRequest");
        serviceRequestGroupDetails.put("displayName", "Service Request");
        serviceRequestGroupDetails.put("moduleName", FacilioConstants.Survey.SURVEY_RESPONSE);
        serviceRequestGroupDetails.put("views", serviceRequestViewFields);
        groupVsViews.add(serviceRequestGroupDetails);

        Map<String, Object> workOrderGroupDetails;

        int woViewOrder = 1;
        ArrayList<FacilioView> workOrderViewFields = new ArrayList<FacilioView>();
        workOrderViewFields.add(getWorkOrderViewOne().setOrder(woViewOrder++));

        workOrderGroupDetails = new HashMap<>();
        workOrderGroupDetails.put("name", "WorkOrder");
        workOrderGroupDetails.put("displayName", "Work Order");
        workOrderGroupDetails.put("moduleName", FacilioConstants.Survey.SURVEY_RESPONSE);
        workOrderGroupDetails.put("views", workOrderViewFields);
        groupVsViews.add(workOrderGroupDetails);

        return groupVsViews;
    }
    private static FacilioView getWorkOrderViewOne() throws Exception{

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Survey_Responses.ID", FieldType.NUMBER), true));

        FacilioView workOrderSurveyListView = new FacilioView();
        workOrderSurveyListView.setName("workorder");

        workOrderSurveyListView.setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
        workOrderSurveyListView.setDisplayName("Work Order Surveys");
        workOrderSurveyListView.setSortFields(sortFields);

        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("name", "Survey"));
        columns.add(new ViewField("workOrderId", "WorkOrder"));
        columns.add(new ViewField("responseStatus", "Completion Status"));
        columns.add(new ViewField("assignedTo", "Survey Respondent"));

        workOrderSurveyListView.setFields(columns);
        return workOrderSurveyListView;
    }

    private static FacilioView getServiceRequestViewOne() throws Exception{

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Survey_Responses.ID", FieldType.NUMBER), true));

        FacilioView serviceRequestSurveyListView = new FacilioView();
        serviceRequestSurveyListView.setName("serviceRequest");

        serviceRequestSurveyListView.setModuleName(FacilioConstants.Survey.SURVEY_RESPONSE);
        serviceRequestSurveyListView.setDisplayName("Service Request Surveys");
        serviceRequestSurveyListView.setSortFields(sortFields);

        List<ViewField> columns = new ArrayList<ViewField>();

        columns.add(new ViewField("name", "Survey"));
        columns.add(new ViewField("serviceRequestId", "Service Request"));
        columns.add(new ViewField("responseStatus", "Completion Status"));
        columns.add(new ViewField("assignedTo", "Survey Respondent"));

        serviceRequestSurveyListView.setFields(columns);
        return serviceRequestSurveyListView;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Survey.SURVEY_RESPONSE);

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createSurveyResponsePage(app, module, false, true));
        }
        return appNameVsPage;
    }

    private static List<PagesContext> createSurveyResponsePage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Survey.SURVEY_RESPONSE_ACTIVITY);

        return new ModulePages()
                .addPage("surveyResponsedefaultpage", "Default Survey Response Page", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("surveyResponseSummary", "Summary", PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("surveyResponseSummaryFields", null, null)
                .addWidget("surveyResponseSummaryFieldsWidget", "Survey Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.Survey.SURVEY_RESPONSE))
                .widgetDone()
                .sectionDone()
                .addSection("surveyResponse", null, null)
                .addWidget("surveyResponseWidget", "Survey Response", PageWidget.WidgetType.SURVEY_RESPONSE, "flexibleSurveyResponse_10", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("surveyResponseHistory", "History",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()

                .pageDone().getCustomPages();


    }


    private static JSONObject getSummaryWidgetDetails(ApplicationContext app,String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        List<FacilioField> surveyResponseFields = moduleBean.getAllFields(FacilioConstants.Survey.SURVEY_RESPONSE);
        Map<String, FacilioField> surveyResponseFieldsMap = FieldFactory.getAsMap(surveyResponseFields);


        FacilioField workOrderId = surveyResponseFieldsMap.get("workOrderId");
        FacilioField serviceRequestId = surveyResponseFieldsMap.get("serviceRequestId");
        FacilioField assignedTo = surveyResponseFieldsMap.get("assignedTo");
        FacilioField createdTime = surveyResponseFieldsMap.get("createdTime");
        FacilioField totalScore = surveyResponseFieldsMap.get("totalScore");
        FacilioField fullScore = surveyResponseFieldsMap.get("fullScore");
        FacilioField scorePercent = surveyResponseFieldsMap.get("scorePercent");


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, workOrderId, 1, 1, 2);
        addSummaryFieldInWidgetGroup(widgetGroup, serviceRequestId, 1, 3, 2);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"Survey Respondent",assignedTo,2,1,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"Response Date",createdTime,2,2,1);
//
//        addSummaryFieldInWidgetGroup(widgetGroup, assignedTo, 2, 1, 1);
//        addSummaryFieldInWidgetGroup(widgetGroup, createdTime, 2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup, totalScore, 2,3,1);
        addSummaryFieldInWidgetGroup(widgetGroup, fullScore, 2,4,1);
//        addSummaryFieldInWidgetGroup(widgetGroup, scorePercent, 3,1,1);
        SummaryWidgetUtil.addSummaryFieldInWidgetGroup(widgetGroup,"Percentage Score",scorePercent,3,1,1);



        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);


        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }


}

