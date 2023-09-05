package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.jobPlan.AddJobPlanModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class CalendarEventModule extends BaseModuleConfig{
    public CalendarEventModule() throws Exception{
        setModuleName(FacilioConstants.Calendar.EVENT_MODULE_NAME);
    }
    public List<FacilioForm> getModuleForms() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule eventModule = modBean.getModule(FacilioConstants.Calendar.EVENT_MODULE_NAME);

        FacilioForm defaultEventForm = new FacilioForm();
        defaultEventForm.setDisplayName("Standard");
        defaultEventForm.setModule(eventModule);
        defaultEventForm.setName("default_event_web");
        defaultEventForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        defaultEventForm.setShowInWeb(true);
        defaultEventForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> defaultEventFormFields = new ArrayList<>();
        defaultEventFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX,"Name", FormField.Required.REQUIRED,1,1));
        defaultEventFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTBOX,"Description", FormField.Required.OPTIONAL,2,1));
        defaultEventFormFields.add(new FormField("eventType", FacilioField.FieldDisplayType.SELECTBOX,"Type", FormField.Required.REQUIRED,3,1));
        defaultEventFormFields.add(new FormField("eventSequence", FacilioField.FieldDisplayType.TEXTBOX,"Event Sequence", FormField.Required.REQUIRED,4,1));
        defaultEventFormFields.add(new FormField("eventFrequency", FacilioField.FieldDisplayType.SELECTBOX,"Frequency", FormField.Required.OPTIONAL,5,1));
        defaultEventFormFields.add(new FormField("scheduledYear", FacilioField.FieldDisplayType.SELECTBOX,"Year", FormField.Required.OPTIONAL,6,1));
        defaultEventFormFields.add(new FormField("scheduledMonth", FacilioField.FieldDisplayType.SELECTBOX,"Month", FormField.Required.OPTIONAL,7,1));
        defaultEventFormFields.add(new FormField("scheduledDate", FacilioField.FieldDisplayType.SELECTBOX,"Date", FormField.Required.OPTIONAL,8,1));
        defaultEventFormFields.add(new FormField("scheduledWeekNumber", FacilioField.FieldDisplayType.SELECTBOX,"week", FormField.Required.OPTIONAL,9,1));
        defaultEventFormFields.add(new FormField("scheduledDay", FacilioField.FieldDisplayType.SELECTBOX,"Day", FormField.Required.OPTIONAL,10,1));

        FormSection section = new FormSection("Default", 1, defaultEventFormFields, false);
        defaultEventForm.setSections(Collections.singletonList(section));
        defaultEventForm.setIsSystemForm(true);
        defaultEventForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(defaultEventForm);
    }
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> calendarEvent = new ArrayList<FacilioView>();
        calendarEvent.add(getAllCalendarEventView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Calendar.EVENT_MODULE_NAME);
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        groupDetails.put("views", calendarEvent);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getAllCalendarEventView() throws Exception{
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Events");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule eventModule = modBean.getModule(FacilioConstants.Calendar.EVENT_MODULE_NAME);

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", eventModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);

        return allView;
    }
}
