package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class WorkOrderItemsModule extends BaseModuleConfig{
    public WorkOrderItemsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WORKORDER_ITEMS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderItems = new ArrayList<FacilioView>();
        workOrderItems.add(getAllWorkOrderItems().setOrder(order++));
        workOrderItems.add(getAllWorkOrderItemsDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderItems);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderItems() {

        FacilioModule workOrderItemsModule = ModuleFactory.getWorkOrderItemsModule();
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Items");
        allView.setModuleName(workOrderItemsModule.getName());

        return allView;
    }
    private static FacilioView getAllWorkOrderItemsDetailsView() {
        FacilioModule workOrderItemsModule = ModuleFactory.getWorkOrderItemsModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Items Details");
        detailsView.setModuleName(workOrderItemsModule.getName());

        return detailsView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderItemsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS);

        FacilioForm workOrderItemForm = new FacilioForm();
        workOrderItemForm.setDisplayName("New Work Order Item");
        workOrderItemForm.setName("default_workorderItem_web");
        workOrderItemForm.setModule(workOrderItemsModule);
        workOrderItemForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        workOrderItemForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));


        List<FormField> workOrderItemFormFields = new ArrayList<>();
        workOrderItemFormFields.add(new FormField("itemType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Item Type", FormField.Required.REQUIRED, "itemTypes", 1, 2,true));
        workOrderItemFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.REQUIRED, "storeRoom", 1, 3,true));
        workOrderItemFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.TEXTBOX, "Quantity", FormField.Required.OPTIONAL, 2, 2));

        FormSection section = new FormSection("Default", 1, workOrderItemFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        workOrderItemForm.setSections(Collections.singletonList(section));
        workOrderItemForm.setIsSystemForm(true);
        workOrderItemForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(workOrderItemForm);
    }
}
