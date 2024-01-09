package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class PeopleModule extends BaseModuleConfig{
    public PeopleModule(){
        setModuleName(FacilioConstants.ContextNames.PEOPLE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> people = new ArrayList<FacilioView>();
        people.add(getAllPeople().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PEOPLE);
        groupDetails.put("views", people);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPeople() {

        FacilioModule peopleModule = ModuleFactory.getPeopleModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("People");
        allView.setModuleName(peopleModule.getName());
        allView.setSortFields(sortFields);
        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);

        FacilioForm peopleForm = new FacilioForm();
        peopleForm.setDisplayName("NEW PEOPLE");
        peopleForm.setName("default_people_web");
        peopleForm.setModule(peopleModule);
        peopleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        peopleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> peopleFormFields = new ArrayList<>();
        peopleFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        peopleFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        peopleFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.NUMBER, "Phone", FormField.Required.OPTIONAL, 3, 3));
        peopleFormFields.add(new FormField("mobile", FacilioField.FieldDisplayType.NUMBER, "Mobile", FormField.Required.OPTIONAL, 4, 3));
        peopleFormFields.add(new FormField("timezone", FacilioField.FieldDisplayType.TIMEZONE, "Time Zone", FormField.Required.OPTIONAL, 5, 3));
        peopleFormFields.add(new FormField("language",FacilioField.FieldDisplayType.LANGUAGE,"Language",FormField.Required.OPTIONAL,6,3));

        FormSection section = new FormSection("Default", 1, peopleFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        peopleForm.setSections(Collections.singletonList(section));
        peopleForm.setIsSystemForm(true);
        peopleForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(peopleForm);
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields fsmApp = new ScopeVariableModulesFields();
        fsmApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_fsm_territory"));
        fsmApp.setModuleId(module.getModuleId());
        fsmApp.setFieldName("territories");

        scopeConfigList = Arrays.asList(fsmApp);
        return scopeConfigList;
    }
}
