package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.CalendarViewTemplatePage;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;


import java.util.*;

public class CalendarModule extends BaseModuleConfig{
    public CalendarModule() throws Exception{
        setModuleName(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
    }
    public List<FacilioForm> getModuleForms() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule calendarModule = modBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);

        FacilioForm defaultCalendarForm = new FacilioForm();
        defaultCalendarForm.setDisplayName("Standard");
        defaultCalendarForm.setModule(calendarModule);
        defaultCalendarForm.setName("default_calendar_web");
        defaultCalendarForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultCalendarForm.setShowInWeb(true);
        defaultCalendarForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> defaultCalendarFormFields = new ArrayList<>();
        defaultCalendarFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX,"Name", FormField.Required.REQUIRED,1,1));
        defaultCalendarFormFields.add(new FormField("description",FacilioField.FieldDisplayType.TEXTAREA,"Description", FormField.Required.OPTIONAL,2,1));
        defaultCalendarFormFields.add(new FormField("calendarType", FacilioField.FieldDisplayType.SELECTBOX, "Type", FormField.Required.REQUIRED,3, 3));
        defaultCalendarFormFields.add(new FormField("client", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Client", FormField.Required.REQUIRED,"client",3, 3,false));
        defaultCalendarForm.setFields(defaultCalendarFormFields);
        List<FormSection> sectionList = new ArrayList<>();
        sectionList.add(new FormSection("General Information", 1, defaultCalendarFormFields, false));
        List<FormField> defaultCalendarSec2FormFields = new ArrayList<>();
        defaultCalendarSec2FormFields.add(new FormField("calendarEventList", FacilioField.FieldDisplayType.CALENDAR_CONFIGURATION,"Event List", FormField.Required.OPTIONAL,1,1));
        defaultCalendarForm.setFields(defaultCalendarSec2FormFields);
        sectionList.add(new FormSection("Calendar Events", 2, defaultCalendarSec2FormFields, false));
        defaultCalendarForm.setSections(sectionList);
        defaultCalendarForm.setIsSystemForm(true);
        defaultCalendarForm.setType(FacilioForm.Type.FORM);
        FormRuleContext singleRule = addCalendarEditDisabilityRule();
        defaultCalendarForm.setDefaultFormRules(Arrays.asList(singleRule));
        return Collections.singletonList(defaultCalendarForm);
    }

    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> calenderViews = new ArrayList<FacilioView>();
        calenderViews.add(getAllCalendarView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        groupDetails.put("views", calenderViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getAllCalendarView() throws Exception{
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Calendar");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule calendarModule = modBean.getModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME);
        if(calendarModule == null){
            throw new IllegalArgumentException("calendar Module Not Found");
        }
        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", calendarModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);
        return allView;
    }
    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());

        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getCalendarViewPage(app, module));
        }
        return appNameVsPage;
    }

    private List<PagesContext> getCalendarViewPage(ApplicationContext app, FacilioModule module) throws Exception {
        return new ModulePages()
                .addPage("calendarViewPage", "Default Calendar View Page", "", null, false, true, true)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("calendarEventView", "Calendar View", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendarEventViewFields", null, null)
                .addWidget("calendarEventViewFieldsWidget", "Calendar Event View", PageWidget.WidgetType.CALENDAR_EVENT_VIEW, "webCalendarEventViewWidget-47*12", 0, 0, null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("calendarsummary", "SUMMARY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("calendarsummaryfields", null, null)
                .addWidget("calendarsummaryfieldswidget", "Calendar Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, CalendarViewTemplatePage.getSummaryWidgetDetails(FacilioConstants.Calendar.CALENDAR_MODULE_NAME, app))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages();
    }
    private FormRuleContext addCalendarEditDisabilityRule() {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Calendar Edit Form Disability  Rule");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        singleRule.setExecuteType(2);

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();
        List<FormRuleTriggerFieldContext> triggerFieldContexts = new ArrayList<>();
        singleRule.setTriggerFields(triggerFieldContexts);
        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.DISABLE_FIELD.getVal());

        List<FormRuleActionFieldsContext> actionFieldsContexts = new ArrayList<>();

        FormRuleActionFieldsContext typeActionField = new FormRuleActionFieldsContext();
        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        typeActionField.setFormFieldName("Type");
        actionField.setFormFieldName("Client");
        actionFieldsContexts.add(typeActionField);
        actionFieldsContexts.add(actionField);

        filterAction.setFormRuleActionFieldsContext(actionFieldsContexts);

        actions.add(filterAction);

        singleRule.setActions(actions);
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        return singleRule;
    }

}
