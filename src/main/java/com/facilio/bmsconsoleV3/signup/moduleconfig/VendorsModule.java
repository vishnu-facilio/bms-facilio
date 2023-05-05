package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseContext;
import com.facilio.bmsconsole.ModuleSettingConfig.context.GlimpseFieldContext;
import com.facilio.bmsconsole.ModuleSettingConfig.util.GlimpseUtil;
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

public class VendorsModule extends BaseModuleConfig{
    public VendorsModule(){
        setModuleName(FacilioConstants.ContextNames.VENDORS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> vendors = new ArrayList<FacilioView>();
        vendors.add(getAllVendors().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VENDORS);
        groupDetails.put("views", vendors);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllVendors() {

        FacilioModule itemsModule = ModuleFactory.getVendorsModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(itemsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Vendors");
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorsModule = modBean.getModule(FacilioConstants.ContextNames.VENDORS);

        FacilioForm vendorsForm = new FacilioForm();
        vendorsForm.setDisplayName("NEW VENDOR");
        vendorsForm.setName("default_vendors_web");
        vendorsForm.setModule(vendorsModule);
        vendorsForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        vendorsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> vendorsFormFields = new ArrayList<>();
        vendorsFormFields.add(new FormField("vendorLogo", FacilioField.FieldDisplayType.IMAGE, "Vendor Logo", FormField.Required.OPTIONAL, 1, 1));
        vendorsFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        vendorsFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        vendorsFormFields.add(new FormField("website", FacilioField.FieldDisplayType.TEXTBOX, "Website", FormField.Required.OPTIONAL, 4, 1));
        vendorsFormFields.add(new FormField("primaryContactName", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Name", FormField.Required.REQUIRED, 5, 1));
        vendorsFormFields.add(new FormField("primaryContactEmail", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact E-mail", FormField.Required.OPTIONAL, 6, 1));
        vendorsFormFields.add(new FormField("primaryContactPhone", FacilioField.FieldDisplayType.TEXTBOX, "Primary Contact Phone", FormField.Required.REQUIRED, 7, 1));
        vendorsFormFields.add(new FormField("address", FacilioField.FieldDisplayType.ADDRESS, "Address", FormField.Required.OPTIONAL, 8, 1));

        FormSection vendorsFormSection = new FormSection("Default", 1, vendorsFormFields, false);
        vendorsFormSection.setSectionType(FormSection.SectionType.FIELDS);
        vendorsForm.setSections(Collections.singletonList(vendorsFormSection));
        vendorsForm.setIsSystemForm(true);
        vendorsForm.setType(FacilioForm.Type.FORM);


        FacilioForm portalVendorForm = new FacilioForm();
        portalVendorForm.setDisplayName("NEW VENDOR");
        portalVendorForm.setName("default_vendors_portal");
        portalVendorForm.setModule(vendorsModule);
        portalVendorForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        portalVendorForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> portalVendorFormFields = new ArrayList<>();
        portalVendorFormFields.add(new FormField("vendorLogo", FacilioField.FieldDisplayType.IMAGE, "Vendor Logo", FormField.Required.OPTIONAL, 1, 1));
        portalVendorFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        portalVendorFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        portalVendorFormFields.add(new FormField("website", FacilioField.FieldDisplayType.TEXTBOX, "Website", FormField.Required.OPTIONAL, 4, 1));
        portalVendorFormFields.add(new FormField("primaryContactName", FacilioField.FieldDisplayType.TEXTBOX, "Contact Name", FormField.Required.REQUIRED, 5, 1));
        portalVendorFormFields.add(new FormField("primaryContactEmail", FacilioField.FieldDisplayType.TEXTBOX, "Contact E-mail", FormField.Required.REQUIRED, 6, 1));
        portalVendorFormFields.add(new FormField("primaryContactPhone", FacilioField.FieldDisplayType.TEXTBOX, "Contact Phone", FormField.Required.REQUIRED, 7, 1));
        portalVendorFormFields.add(new FormField("address", FacilioField.FieldDisplayType.ADDRESS, "Address", FormField.Required.OPTIONAL, 8, 1));


        FormSection portalVendorFormSection = new FormSection("Default", 1, portalVendorFormFields, false);
        portalVendorFormSection.setSectionType(FormSection.SectionType.FIELDS);
        portalVendorForm.setSections(Collections.singletonList(portalVendorFormSection));
        portalVendorForm.setIsSystemForm(true);
        portalVendorForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> vendorsModuleForms = new ArrayList<>();
        vendorsModuleForms.add(vendorsForm);
        vendorsModuleForms.add(portalVendorForm);

        return vendorsModuleForms;
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("primaryContactName");
        fieldNames.add("primaryContactEmail");
        fieldNames.add("primaryContactPhone");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
