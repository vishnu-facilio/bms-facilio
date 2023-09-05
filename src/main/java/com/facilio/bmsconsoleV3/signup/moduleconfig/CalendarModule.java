package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;


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
        defaultCalendarForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> defaultCalendarFormFields = new ArrayList<>();
        defaultCalendarFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX,"Name", FormField.Required.REQUIRED,1,1));
        defaultCalendarFormFields.add(new FormField("description",FacilioField.FieldDisplayType.TEXTBOX,"Description", FormField.Required.OPTIONAL,2,1));
        defaultCalendarFormFields.add(new FormField("calendarType",FacilioField.FieldDisplayType.SELECTBOX,"Type", FormField.Required.REQUIRED,3,1));
        defaultCalendarForm.setFields(defaultCalendarFormFields);

        FormSection section = new FormSection("Default", 1, defaultCalendarFormFields, false);
        defaultCalendarForm.setSections(Collections.singletonList(section));
        defaultCalendarForm.setIsSystemForm(true);
        defaultCalendarForm.setType(FacilioForm.Type.FORM);
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
        groupDetails.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
        groupDetails.put("views", calenderViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getAllCalendarView() throws Exception{
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Calendar");
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
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

}
