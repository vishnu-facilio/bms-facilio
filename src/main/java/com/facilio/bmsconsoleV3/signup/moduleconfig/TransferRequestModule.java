package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
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

public class TransferRequestModule extends BaseModuleConfig{
    public TransferRequestModule(){
        setModuleName(FacilioConstants.ContextNames.TRANSFER_REQUEST);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> transferRequest = new ArrayList<FacilioView>();
        transferRequest.add(getAllTransferRequestView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TRANSFER_REQUEST);
        groupDetails.put("views", transferRequest);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTransferRequestView() {

        FacilioModule module = ModuleFactory.getTransferRequestModule();

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Transfer Requests");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule transferRequestModule = modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST);

        FacilioForm transferRequestForm = new FacilioForm();
        transferRequestForm.setDisplayName("TRANSFER REQUEST");
        transferRequestForm.setName("default_transferrequest_web");
        transferRequestForm.setModule(transferRequestModule);
        transferRequestForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        transferRequestForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> transferRequestFormDefaultFields = new ArrayList<>();
        transferRequestFormDefaultFields.add(new FormField("requestSubject", FacilioField.FieldDisplayType.TEXTBOX, "Request Subject", FormField.Required.REQUIRED, 1, 1));
        transferRequestFormDefaultFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        transferRequestFormDefaultFields.add(new FormField("transferFromStore", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Transfer from Store", FormField.Required.REQUIRED, "storeRoom", 3, 2));
        transferRequestFormDefaultFields.add(new FormField("transferToStore", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Transfer to Store", FormField.Required.REQUIRED, "storeRoom", 3, 3));
        transferRequestFormDefaultFields.add(new FormField("transferInitiatedOn", FacilioField.FieldDisplayType.DATE, "Transfer Date", FormField.Required.OPTIONAL, 4, 2));
        transferRequestFormDefaultFields.add(new FormField("expectedCompletionDate", FacilioField.FieldDisplayType.DATE, "Expected Arrival Date", FormField.Required.OPTIONAL, 4, 3));
        transferRequestFormDefaultFields.add(new FormField("transferredBy", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Transferred By", FormField.Required.OPTIONAL, "people", 5, 2));
        transferRequestFormDefaultFields.add(new FormField("isShipmentTrackingNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Is Shipment Tracking Needed", FormField.Required.OPTIONAL, 5, 3));

        List<FormField> lineItemFields = new ArrayList<>();
        lineItemFields.add(new FormField("transferrequestlineitems", FacilioField.FieldDisplayType.INVREQUEST_LINE_ITEMS, "LINE ITEMS", FormField.Required.REQUIRED, 6, 1));

        List<FormField> transferRequestFormFields = new ArrayList<>();
        transferRequestFormFields.addAll(transferRequestFormDefaultFields);
        transferRequestFormFields.addAll(lineItemFields);


        FormSection defaultSection = new FormSection("Inventory Request", 1, transferRequestFormDefaultFields, true);
        defaultSection.setSectionType(FormSection.SectionType.FIELDS);

        FormSection lineItemSection = new FormSection("Line Items", 2, lineItemFields, true);
        lineItemSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> sections = new ArrayList<>();
        sections.add(defaultSection);
        sections.add(lineItemSection);

        transferRequestForm.setSections(sections);
        transferRequestForm.setIsSystemForm(true);
        transferRequestForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(transferRequestForm);
    }
}
