package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
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
        defaultEventForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        List<FormField> defaultEventFormFields = new ArrayList<>();
        defaultEventFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX,"Name", FormField.Required.REQUIRED,1,1));
        defaultEventFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA,"Description", FormField.Required.OPTIONAL,2,1));
        defaultEventFormFields.add(new FormField("eventConfiguration", FacilioField.FieldDisplayType.CALENDAR_EVENT_CONFIGURATION, "Event Configuration", FormField.Required.OPTIONAL, 3, 1));

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
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        groupDetails.put("views", calendarEvent);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    public static FacilioView getAllCalendarEventView() throws Exception{
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Events");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        allView.setFields(getAllViewColumns());


        return allView;
    }
    private static List<ViewField> getAllViewColumns() {
        List<ViewField> columns = new ArrayList<ViewField>();
        columns.add(new ViewField("name", "Name"));
        columns.add(new ViewField("eventType", "Type"));
        columns.add(new ViewField("eventFrequency", "Frequency"));
        columns.add(new ViewField("validityStartTime", "Start Time"));
        columns.add(new ViewField("validityEndTime", "End Time"));
        columns.add(new ViewField("sysModifiedTime", "Modified Time"));
        return  columns;
    }
}
