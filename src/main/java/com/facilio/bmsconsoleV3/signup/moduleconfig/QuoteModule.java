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

public class QuoteModule extends BaseModuleConfig{
    public QuoteModule(){
        setModuleName(FacilioConstants.ContextNames.QUOTE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> quote = new ArrayList<FacilioView>();
        quote.add(getAllQuotations().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.QUOTE);
        groupDetails.put("views", quote);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllQuotations() {

        FacilioModule module = ModuleFactory.getQuotationModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("parentId", "PARENT_ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Quotes");
        allView.setModuleName(module.getName());
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
        FacilioModule quoteModule = modBean.getModule(FacilioConstants.ContextNames.QUOTE);

        FacilioForm quotationForm = new FacilioForm();
        quotationForm.setDisplayName("Quote");
        quotationForm.setName("default_quote_web");
        quotationForm.setModule(quoteModule);
        quotationForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        quotationForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> quotationFormFields = new ArrayList<>();
        quotationFormFields.add(new FormField("subject", FacilioField.FieldDisplayType.TEXTBOX, "Subject", FormField.Required.REQUIRED, 1, 1));
        quotationFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        quotationFormFields.add(new FormField("billDate", FacilioField.FieldDisplayType.DATE, "Bill Date", FormField.Required.OPTIONAL, 3, 2));
        quotationFormFields.add(new FormField("expiryDate", FacilioField.FieldDisplayType.DATE, "Expiry Date", FormField.Required.REQUIRED, 3, 3));
        quotationFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED,"site", 4, 2));
        quotationFormFields.add(new FormField("tenant", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tenant", FormField.Required.REQUIRED,"tenant", 4, 3));
        quotationFormFields.add(new FormField("contact", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Contact", FormField.Required.OPTIONAL,"people", 5, 3));
        quotationFormFields.add(new FormField("workorder", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Workorder", FormField.Required.OPTIONAL,"workorder", 6, 1));
        quotationFormFields.add(new FormField("signature", FacilioField.FieldDisplayType.SIGNATURE, "Signature", FormField.Required.OPTIONAL, 14, 1));

        List<FormField> billingAddressFields = new ArrayList<>();
        billingAddressFields.add(new FormField("billToAddress", FacilioField.FieldDisplayType.QUOTE_ADDRESS, "Bill To Address", FormField.Required.OPTIONAL, 7, 1));

        List<FormField> lineItemFields = new ArrayList<>();
        FormField lineItemField  =new FormField("lineItems", FacilioField.FieldDisplayType.QUOTE_LINE_ITEMS, "Line Items", FormField.Required.REQUIRED, 9, 1);
        lineItemField.addToConfig("hideTaxField",false);
        lineItemFields.add(lineItemField);

        List<FormField> signatureFields = new ArrayList<>();
        signatureFields.add(new FormField("notes", FacilioField.FieldDisplayType.TEXTAREA, "Customer Notes", FormField.Required.OPTIONAL, 13, 1));

        List<FormField> requestForQuotationModuleFormFields = new ArrayList<>();
        requestForQuotationModuleFormFields.addAll(quotationFormFields);
        requestForQuotationModuleFormFields.addAll(billingAddressFields);
        requestForQuotationModuleFormFields.addAll(lineItemFields);
        requestForQuotationModuleFormFields.addAll(signatureFields);
//        quotationForm.setFields(requestForQuotationModuleFormFields);

        FormSection defaultSection = new FormSection("QUOTE INFORMATION", 1, quotationFormFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection billingSection = new FormSection("Billing Address", 2, billingAddressFields, false);
        billingSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("QUOTE ITEMS", 3, lineItemFields, true);
        lineItemSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection notesSection = new FormSection("NOTES", 4, signatureFields, false);
        notesSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(billingSection);
        sections.add(lineItemSection);
        sections.add(notesSection);

        quotationForm.setSections(sections);
        quotationForm.setIsSystemForm(true);
        quotationForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(quotationForm);
    }
}
