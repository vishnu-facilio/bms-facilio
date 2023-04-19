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
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class VendorDocumentsModule extends BaseModuleConfig{
    public VendorDocumentsModule(){
        setModuleName(FacilioConstants.ContextNames.VENDOR_DOCUMENTS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> vendorDocuments = new ArrayList<FacilioView>();
        vendorDocuments.add(getAllDocumentsView().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VENDOR_DOCUMENTS);
        groupDetails.put("views", vendorDocuments);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllDocumentsView() {

        FacilioField sysCreatedtimeField = new FacilioField();
        sysCreatedtimeField.setName("sysCreatedTime");
        sysCreatedtimeField.setColumnName("SYS_CREATED_TIME");
        sysCreatedtimeField.setDataType(FieldType.DATE_TIME);
        sysCreatedtimeField.setModule(ModuleFactory.getVendorDocumentsModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(sysCreatedtimeField, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorDocumentsModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_DOCUMENTS);

        FacilioForm vendorDocumentForm = new FacilioForm();
        vendorDocumentForm.setDisplayName("Vendor Document");
        vendorDocumentForm.setName("default_vendorDocuments_web");
        vendorDocumentForm.setModule(vendorDocumentsModule);
        vendorDocumentForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        vendorDocumentForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> vendorDocumentFormFields = new ArrayList<>();
        vendorDocumentFormFields.add(new FormField("documentName", FacilioField.FieldDisplayType.TEXTBOX, "Document Name", FormField.Required.REQUIRED, 1, 1));
        FormField type = new FormField("documentType", FacilioField.FieldDisplayType.SELECTBOX, "Document Type", FormField.Required.OPTIONAL, 2, 1);
        type.setAllowCreateOptions(true);
        vendorDocumentFormFields.add(type);
        vendorDocumentFormFields.add(new FormField("document", FacilioField.FieldDisplayType.FILE, "Document", FormField.Required.REQUIRED,3, 1));

        FormSection vendorDocumentFormSection = new FormSection("Default", 1, vendorDocumentFormFields, false);
        vendorDocumentFormSection.setSectionType(FormSection.SectionType.FIELDS);
        vendorDocumentForm.setSections(Collections.singletonList(vendorDocumentFormSection));
        vendorDocumentForm.setIsSystemForm(true);
        vendorDocumentForm.setType(FacilioForm.Type.FORM);

        FacilioForm portalVendorDocumentForm = new FacilioForm();
        portalVendorDocumentForm.setDisplayName("Vendor Document");
        portalVendorDocumentForm.setName("default_vendorDocuments_portal");
        portalVendorDocumentForm.setModule(vendorDocumentsModule);
        portalVendorDocumentForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        portalVendorDocumentForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> portalVendorDocumentFormFields = new ArrayList<>();
        portalVendorDocumentFormFields.add(new FormField("documentName", FacilioField.FieldDisplayType.TEXTBOX, "Document Name", FormField.Required.REQUIRED, 1, 1));
        FormField typePortal = new FormField("documentType", FacilioField.FieldDisplayType.SELECTBOX, "Document Type", FormField.Required.OPTIONAL, 2, 1);
        typePortal.setAllowCreateOptions(true);
        portalVendorDocumentFormFields.add(typePortal);
        portalVendorDocumentFormFields.add(new FormField("document", FacilioField.FieldDisplayType.FILE, "Document", FormField.Required.REQUIRED,3, 1));

        FormSection portalVendorDocumentFormSection = new FormSection("Default", 1, portalVendorDocumentFormFields, false);
        portalVendorDocumentFormSection.setSectionType(FormSection.SectionType.FIELDS);
        portalVendorDocumentForm.setSections(Collections.singletonList(portalVendorDocumentFormSection));
        portalVendorDocumentForm.setIsSystemForm(true);
        portalVendorDocumentForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> vendorDocumentForms = new ArrayList<>();
        vendorDocumentForms.add(vendorDocumentForm);
        vendorDocumentForms.add(portalVendorDocumentForm);

        return vendorDocumentForms;
    }

}
