package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.v3.context.Constants;

import java.util.*;

public class VendorContactModule extends BaseModuleConfig{
    public VendorContactModule(){
        setModuleName(FacilioConstants.ContextNames.VENDOR_CONTACT);
    }

    public void addData() throws Exception {
        addActivityModuleForVendorContacts();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.VENDOR_CONTACT));
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> vendorContact = new ArrayList<FacilioView>();
        vendorContact.add(getAllHiddenVendorContacts().setOrder(order++));
        vendorContact.add(getAllVendorContacts().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VENDOR_CONTACT);
        groupDetails.put("views", vendorContact);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllHiddenVendorContacts() {

        FacilioModule vendorContactModule = ModuleFactory.getVendorContactModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME", FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Vendor Contacts");
        allView.setModuleName(vendorContactModule.getName());
        allView.setSortFields(sortFields);

        allView.setHidden(true);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAllVendorContacts() {

        FacilioModule vendorContactModule = ModuleFactory.getVendorContactModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("name","NAME",FieldType.STRING), true));

        FacilioView allView = new FacilioView();
        allView.setName("all-contacts");
        allView.setDisplayName("All Vendor Contacts");
        allView.setModuleName(vendorContactModule.getName());
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
        FacilioModule vendorContactModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);

        FacilioForm newVendorContactForm = new FacilioForm();
        newVendorContactForm.setDisplayName("NEW VENDOR CONTACT");
        newVendorContactForm.setName("default_vendorcontact_web");
        newVendorContactForm.setModule(vendorContactModule);
        newVendorContactForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        newVendorContactForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> newVendorContactFormFields = new ArrayList<>();
        newVendorContactFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        newVendorContactFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 2, 1));
        newVendorContactFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.OPTIONAL, 3, 1));
        newVendorContactFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED, "vendors", 4, 1));
        newVendorContactFormFields.add(new FormField("isPrimaryContact", FacilioField.FieldDisplayType.DECISION_BOX, "Primary Contact", FormField.Required.OPTIONAL, 5, 1));
//        newVendorContactForm.setFields(newVendorContactFormFields);

        FormSection section = new FormSection("Default", 1, newVendorContactFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        newVendorContactForm.setSections(Collections.singletonList(section));
        newVendorContactForm.setIsSystemForm(true);
        newVendorContactForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(newVendorContactForm);
    }

    public void addActivityModuleForVendorContacts() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule vc = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);

        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.VENDOR_CONTACTS_ACTIVITY,
                "Vendor Contact Activity",
                "Vendor_Contacts_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );


        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);

        fields.add(timefield);

        NumberField type = FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);

        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING);

        fields.add(info);


        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();

        modBean.addSubModule(vc.getModuleId(), module.getModuleId());
    }
}
