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

public class TenantUnitSpaceModule extends BaseModuleConfig{
    public TenantUnitSpaceModule(){
        setModuleName(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tenantUnitSpace = new ArrayList<FacilioView>();
        tenantUnitSpace.add(getAllTenantUnitSpace().setOrder(order++));
        tenantUnitSpace.add(getAllTenantUnitSpaceDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TENANT_UNIT_SPACE);
        groupDetails.put("views", tenantUnitSpace);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTenantUnitSpace() {

        FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tenant Unit");
        allView.setModuleName(tenantUnitSpaceModule.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllTenantUnitSpaceDetailsView() {

        FacilioModule tenantUnitSpaceModule = ModuleFactory.getTenantUnitSpaceModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("details");
        allView.setDisplayName("All Tenant Units");
        allView.setModuleName(tenantUnitSpaceModule.getName());
        allView.setSortFields(sortFields);
        allView.setHidden(true);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule tenantUnitModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE);

        FacilioForm tenantUnitSpaceForm = new FacilioForm();
        tenantUnitSpaceForm.setDisplayName("NEW TENANT UNIT");
        tenantUnitSpaceForm.setName("default_tenantunit_web");
        tenantUnitSpaceForm.setModule(tenantUnitModule);
        tenantUnitSpaceForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        tenantUnitSpaceForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> tenantUnitSpaceFormFields = new ArrayList<>();
        tenantUnitSpaceFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        tenantUnitSpaceFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        tenantUnitSpaceFormFields.add(new FormField("area", FacilioField.FieldDisplayType.NUMBER, "Area", FormField.Required.OPTIONAL, 3, 2));
        tenantUnitSpaceFormFields.add(new FormField("maxOccupancy", FacilioField.FieldDisplayType.NUMBER, "Max Occupancy", FormField.Required.OPTIONAL, 3, 3));
        tenantUnitSpaceFormFields.add(new FormField("site", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 4, 2));
        tenantUnitSpaceFormFields.add(new FormField("building", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Building", FormField.Required.OPTIONAL,"building", 4, 3));
        FormField tenant = new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.OPTIONAL,"tenant", 5, 1);
        tenant.setHideField(true);
        tenantUnitSpaceFormFields.add(tenant);
        FormField isOccupied = new FormField("isOccupied", FacilioField.FieldDisplayType.DECISION_BOX, "Occupancy Status", FormField.Required.OPTIONAL, 5, 1);
        isOccupied.setHideField(true);
        tenantUnitSpaceFormFields.add(isOccupied);
        tenantUnitSpaceFormFields.add(new FormField("floor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Floor", FormField.Required.OPTIONAL,"floor", 5, 1));
        tenantUnitSpaceFormFields.add(new FormField("location", FacilioField.FieldDisplayType.GEO_LOCATION, "Location", FormField.Required.OPTIONAL, 4, 1));
        FormField spaceCategory = new FormField("spaceCategory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL,"spacecategory", 3, 1,false);
        spaceCategory.setHideField(true);
        tenantUnitSpaceFormFields.add(spaceCategory);

        tenantUnitSpaceForm.setFields(tenantUnitSpaceFormFields);

        FormSection section = new FormSection("Default", 1, tenantUnitSpaceFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        tenantUnitSpaceForm.setSections(Collections.singletonList(section));
        tenantUnitSpaceForm.setIsSystemForm(true);
        tenantUnitSpaceForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(tenantUnitSpaceForm);
    }
}
