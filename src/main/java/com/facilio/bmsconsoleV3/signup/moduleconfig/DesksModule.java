package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
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
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class DesksModule extends BaseModuleConfig{
    public DesksModule(){
        setModuleName(FacilioConstants.ContextNames.Floorplan.DESKS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> desks = new ArrayList<FacilioView>();
        desks.add(getAllDesksView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Floorplan.DESKS);
        groupDetails.put("views", desks);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllDesksView() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Desks");
        allView.setModuleName(FacilioConstants.ContextNames.Floorplan.DESKS);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.IWMS_APP));

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);

        FacilioForm desksForm = new FacilioForm();
        desksForm.setDisplayName("New Desk");
        desksForm.setName("default_desks_web");
        desksForm.setModule(deskModule);
        desksForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        desksForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.IWMS_APP));

        List<FormField> desksFormFields = new ArrayList<>();
        desksFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        desksFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.REQUIRED, "employee", 2, 2));
        desksFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.REQUIRED, "department", 3, 2));
        desksFormFields.add(new FormField("deskType", FacilioField.FieldDisplayType.SELECTBOX, "Desk Type", FormField.Required.REQUIRED, 4, 2));
        desksFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 5, 2));
        desksFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 6, 3));
        desksFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 7, 1));

        FormSection desksFormSection = new FormSection("Default", 1, desksFormFields, false);
        desksFormSection.setSectionType(FormSection.SectionType.FIELDS);
        desksForm.setSections(Collections.singletonList(desksFormSection));
        desksForm.setIsSystemForm(true);
        desksForm.setType(FacilioForm.Type.FORM);


        FacilioForm desksPortalForm = new FacilioForm();
        desksPortalForm.setDisplayName("New Desks");
        desksPortalForm.setName("default_desks_portal");
        desksPortalForm.setModule(deskModule);
        desksPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        desksPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> desksPortalFormFields = new ArrayList<>();
        desksPortalFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        desksPortalFormFields.add(new FormField("employee", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Employee", FormField.Required.REQUIRED, "employee", 2, 2));
        desksPortalFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.REQUIRED, "department", 3, 2));
        desksPortalFormFields.add(new FormField("deskType", FacilioField.FieldDisplayType.SELECTBOX, "Desk Type", FormField.Required.REQUIRED, 4, 2));
        desksPortalFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 5, 2));
        desksPortalFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 6, 3));
        desksPortalFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 7, 1));

        FormSection desksPortalFormSection = new FormSection("Default", 1, desksPortalFormFields, false);
        desksPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        desksPortalForm.setSections(Collections.singletonList(desksPortalFormSection));
        desksPortalForm.setIsSystemForm(true);
        desksPortalForm.setType(FacilioForm.Type.FORM);


        List<FacilioForm> deskModuleForms = new ArrayList<>();
        deskModuleForms.add(desksForm);
        deskModuleForms.add(desksPortalForm);

        return deskModuleForms;
    }
}
