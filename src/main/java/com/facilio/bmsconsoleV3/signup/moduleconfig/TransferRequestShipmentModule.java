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

public class TransferRequestShipmentModule extends BaseModuleConfig{
    public TransferRequestShipmentModule(){
        setModuleName(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> transferRequestShipment = new ArrayList<FacilioView>();
        transferRequestShipment.add(getAllTransferRequestShipmentView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT);
        groupDetails.put("views", transferRequestShipment);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTransferRequestShipmentView() {

        FacilioModule module = ModuleFactory.getTransferRequestShipmentModule();
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Transfer Request Shipment");
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
        FacilioModule transferRequestShipmentModule = modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST_SHIPMENT);

        FacilioForm transferRequestShipmentForm = new FacilioForm();
        transferRequestShipmentForm.setDisplayName("TRANSFER REQUEST SHIPMENT");
        transferRequestShipmentForm.setName("default_trshipment_web");
        transferRequestShipmentForm.setModule(transferRequestShipmentModule);
        transferRequestShipmentForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        transferRequestShipmentForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> transferRequestShipmentFormFields = new ArrayList<>();
        transferRequestShipmentFormFields.add(new FormField("expectedCompletionDate", FacilioField.FieldDisplayType.DATE, "Expected Completion Date", FormField.Required.OPTIONAL, 1, 1));
        transferRequestShipmentForm.setFields(transferRequestShipmentFormFields);

        FormSection section = new FormSection("Default", 1, transferRequestShipmentFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        transferRequestShipmentForm.setSections(Collections.singletonList(section));
        transferRequestShipmentForm.setIsSystemForm(true);
        transferRequestShipmentForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(transferRequestShipmentForm);
    }
}
