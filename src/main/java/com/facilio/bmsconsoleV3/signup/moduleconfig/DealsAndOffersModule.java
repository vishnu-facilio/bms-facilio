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

public class DealsAndOffersModule extends BaseModuleConfig{
    public DealsAndOffersModule(){
        setModuleName(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> dealsAndOffers = new ArrayList<FacilioView>();
        dealsAndOffers.add(getAllDealsAndOffersView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS);
        groupDetails.put("views", dealsAndOffers);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllDealsAndOffersView() {

        FacilioModule module = ModuleFactory.getDealsAndOffersModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Deals and Offers");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule dealsAndOffersModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS);

        FacilioForm dealsAndOffersForm = new FacilioForm();
        dealsAndOffersForm.setDisplayName("Deals and Offers");
        dealsAndOffersForm.setName("default_"+ FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS +"_web");
        dealsAndOffersForm.setModule(dealsAndOffersModule);
        dealsAndOffersForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        dealsAndOffersForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> dealsAndOffersFormFields = new ArrayList<>();
        dealsAndOffersFormFields.add(new FormField("title", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 1, 1));
        FormField descField = new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1);
        descField.addToConfig("richText", true);
        dealsAndOffersFormFields.add(descField);
        dealsAndOffersFormFields.add(new FormField("expiryDate", FacilioField.FieldDisplayType.DATE, "Expiry Date", FormField.Required.OPTIONAL, 3, 2));
        dealsAndOffersFormFields.add(new FormField("dealer", FacilioField.FieldDisplayType.TEXTBOX, "Dealer", FormField.Required.REQUIRED, 4, 1));
        dealsAndOffersFormFields.add(new FormField("neighbourhood", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Neighbourhood", FormField.Required.REQUIRED, "neighbourhood",5, 1));
        FormField attachment = new FormField("dealsandoffersattachments", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, 6, 1);
        attachment.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
        dealsAndOffersFormFields.add(attachment);
        FormField field = new FormField("audience", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Audience", FormField.Required.REQUIRED, "audience",7, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        dealsAndOffersFormFields.add(field);
//        dealsAndOffersForm.setFields(dealsAndOffersFormFields);

        FormSection dealsAndOffersFormSection = new FormSection("Default", 1, dealsAndOffersFormFields, false);
        dealsAndOffersFormSection.setSectionType(FormSection.SectionType.FIELDS);
        dealsAndOffersForm.setSections(Collections.singletonList(dealsAndOffersFormSection));
        dealsAndOffersForm.setIsSystemForm(true);
        dealsAndOffersForm.setType(FacilioForm.Type.FORM);

        FacilioForm dealsAndOffersPortalForm = new FacilioForm();
        dealsAndOffersPortalForm.setDisplayName("Deals and Offers");
        dealsAndOffersPortalForm.setName("default_"+ FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS +"_portal");
        dealsAndOffersPortalForm.setModule(dealsAndOffersModule);
        dealsAndOffersPortalForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        dealsAndOffersPortalForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));

        List<FormField> dealsAndOffersPortalFormFields = new ArrayList<>();
        dealsAndOffersPortalFormFields.add(new FormField("title", FacilioField.FieldDisplayType.TEXTBOX, "Title", FormField.Required.REQUIRED, 1, 1));
        FormField descriptionField = new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1);
        descriptionField.addToConfig("richText", true);
        dealsAndOffersPortalFormFields.add(descriptionField);
        dealsAndOffersPortalFormFields.add(new FormField("expiryDate", FacilioField.FieldDisplayType.DATE, "Expiry Date", FormField.Required.OPTIONAL, 3, 2));
        dealsAndOffersPortalFormFields.add(new FormField("dealer", FacilioField.FieldDisplayType.TEXTBOX, "Dealer", FormField.Required.REQUIRED, 4, 1));
        dealsAndOffersPortalFormFields.add(new FormField("neighbourhood", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Neighbourhood", FormField.Required.REQUIRED, "neighbourhood",5, 1));
        FormField attachments = new FormField("dealsandoffersattachments", FacilioField.FieldDisplayType.ATTACHMENT, "Attachments", FormField.Required.OPTIONAL, 6, 1);
        attachments.addToConfig("fileTypes", "image/*,.pdf,.doc,.docx");
        dealsAndOffersPortalFormFields.add(attachments);
//        dealsAndOffersPortalForm.setFields(dealsAndOffersPortalFormFields);

        FormSection dealsAndOffersPortalFormSection = new FormSection("Default", 1, dealsAndOffersPortalFormFields, false);
        dealsAndOffersPortalFormSection.setSectionType(FormSection.SectionType.FIELDS);
        dealsAndOffersPortalForm.setSections(Collections.singletonList(dealsAndOffersPortalFormSection));
        dealsAndOffersPortalForm.setIsSystemForm(true);
        dealsAndOffersPortalForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> dealsAndOffersModuleForms = new ArrayList<>();
        dealsAndOffersModuleForms.add(dealsAndOffersForm);
        dealsAndOffersModuleForms.add(dealsAndOffersPortalForm);

        return dealsAndOffersModuleForms;
    }
}
