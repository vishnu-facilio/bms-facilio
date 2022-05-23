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
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class VendorQuotesModule extends BaseModuleConfig{
    public VendorQuotesModule(){
        setModuleName(FacilioConstants.ContextNames.VENDOR_QUOTES);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> vendorQuote = new ArrayList<FacilioView>();
        vendorQuote.add(getAllVendorQuotesView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.VENDOR_QUOTES);
        groupDetails.put("views", vendorQuote);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllVendorQuotesView() {

        FacilioModule module = ModuleFactory.getVendorQuotesModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Vendor Quotes");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule vendorQuotes = modBean.getModule(FacilioConstants.ContextNames.VENDOR_QUOTES);

        FacilioForm vendorQuotesForm = new FacilioForm();
        vendorQuotesForm.setDisplayName("VENDOR QUOTES");
        vendorQuotesForm.setName("default_vendorQuotes_web");
        vendorQuotesForm.setModule(vendorQuotes);
        vendorQuotesForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        vendorQuotesForm.setAppLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        vendorQuotesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> vendorQuotesFormFields = new ArrayList<>();

        FacilioField expRepDateField = null;
        try {
            expRepDateField = modBean.getField("expectedReplyDate", FacilioConstants.ContextNames.VENDOR_QUOTES);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("expectedReplyDate field not found");
        }
        FormField vendorField = new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED, "vendors", 1, 1).setAllowCreateOptions(true).setCreateFormName("vendors_form");
        vendorField.setIsDisabled(true);
        vendorQuotesFormFields.add(vendorField);
        vendorQuotesFormFields.add(new FormField("replyDate", FacilioField.FieldDisplayType.DATE, "Reply Date", FormField.Required.OPTIONAL, 2, 1));
        vendorQuotesFormFields.add(new FormField(expRepDateField.getId(),"expectedReplyDate", FacilioField.FieldDisplayType.DATE,"Expected Reply Date", FormField.Required.OPTIONAL,2,2,true));
//        vendorQuotesForm.setFields(vendorQuotesFormFields);

        FormSection vendorQuotesFormSection = new FormSection("Default", 1, vendorQuotesFormFields, false);
        vendorQuotesFormSection.setSectionType(FormSection.SectionType.FIELDS);
        vendorQuotesForm.setSections(Collections.singletonList(vendorQuotesFormSection));

        return Collections.singletonList(vendorQuotesForm);
    }
}
