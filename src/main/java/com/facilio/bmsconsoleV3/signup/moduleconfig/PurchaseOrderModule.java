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
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class PurchaseOrderModule extends BaseModuleConfig{
    public PurchaseOrderModule(){
        setModuleName(FacilioConstants.ContextNames.PURCHASE_ORDER);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> purchaseOrder = new ArrayList<FacilioView>();
        purchaseOrder.add(getAllPurchaseOrderView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.PURCHASE_ORDER);
        groupDetails.put("views", purchaseOrder);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPurchaseOrderView() {
        FacilioField localId = new FacilioField();
        localId.setName("id");
        localId.setColumnName("ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getPurchaseOrderModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule purchaseOrderModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);

        FacilioForm purchaseOrderForm = new FacilioForm();
        purchaseOrderForm.setDisplayName("PURCHASE ORDER");
        purchaseOrderForm.setName("default_purchaseorder_web");
        purchaseOrderForm.setModule(purchaseOrderModule);
        purchaseOrderForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        purchaseOrderForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> purchaseOrderFormFields = new ArrayList<>();
        purchaseOrderFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        purchaseOrderFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        purchaseOrderFormFields.add(new FormField("vendor", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Vendor", FormField.Required.REQUIRED, "vendors", 3, 2).setAllowCreateOptions(true).setCreateFormName("vendors_form"));
        purchaseOrderFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 3, 3));
        purchaseOrderFormFields.add(new FormField("orderedTime", FacilioField.FieldDisplayType.DATE, "Ordered Date", FormField.Required.OPTIONAL, 4, 2));
        purchaseOrderFormFields.add(new FormField("requiredTime", FacilioField.FieldDisplayType.DATE, "Expected Delivery Date", FormField.Required.OPTIONAL, 4, 3));
        purchaseOrderFormFields.add(new FormField("requestedBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Requested By", FormField.Required.OPTIONAL, "user", 5, 1));
//        purchaseOrderForm.setFields(purchaseOrderFormFields);

        List<FormField> billingAddressFields = new ArrayList<>();
        billingAddressFields.add(new FormField("billToAddress", FacilioField.FieldDisplayType.SADDRESS, "BILLING ADDRESS", FormField.Required.OPTIONAL, 6, 1));

        List<FormField> shippingAddressFields = new ArrayList<>();
        shippingAddressFields.add(new FormField("shipToAddress", FacilioField.FieldDisplayType.SADDRESS, "SHIPPING ADDRESS", FormField.Required.OPTIONAL, 7, 1));

        List<FormField> lineItemFields = new ArrayList<>();
        FormField lineItemField = new FormField("lineItems", FacilioField.FieldDisplayType.LINEITEMS, "LINE ITEMS", FormField.Required.REQUIRED, 8, 1);
        lineItemField.addToConfig("hideTaxField",false);
        lineItemFields.add(lineItemField);

        List<FormField> requestForQuotationModuleFormFields = new ArrayList<>();
        requestForQuotationModuleFormFields.addAll(purchaseOrderFormFields);
        requestForQuotationModuleFormFields.addAll(billingAddressFields);
        requestForQuotationModuleFormFields.addAll(shippingAddressFields);
        requestForQuotationModuleFormFields.addAll(lineItemFields);
//        purchaseOrderForm.setFields(requestForQuotationModuleFormFields);

        FormSection defaultSection = new FormSection("Purchase Order", 1, purchaseOrderFormFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection billingSection = new FormSection("Billing Address", 2, billingAddressFields, true);
        billingSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection shippingSection = new FormSection("Shipping Address", 3, shippingAddressFields, true);
        shippingSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("Line Items", 4, lineItemFields, true);
        shippingSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(billingSection);
        sections.add(shippingSection);
        sections.add(lineItemSection);

        purchaseOrderForm.setSections(sections);
        purchaseOrderForm.setIsSystemForm(true);
        purchaseOrderForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(purchaseOrderForm);
    }

    @Override
    public List<GlimpseContext> getModuleGlimpse() throws Exception{

        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("storeRoom");
        fieldNames.add("vendor");
        fieldNames.add("orderedTime");
        fieldNames.add("requiredTime");

        GlimpseContext glimpse = GlimpseUtil.getNewGlimpse(fieldNames,getModuleName());

        List<GlimpseContext> glimpseList = new ArrayList<>();
        glimpseList.add(glimpse);

        return glimpseList;

    }

}
