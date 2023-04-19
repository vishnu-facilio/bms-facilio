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

public class ItemTypesModule extends BaseModuleConfig{
    public ItemTypesModule(){
        setModuleName(FacilioConstants.ContextNames.ITEM_TYPES);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> itemTypes = new ArrayList<FacilioView>();
        itemTypes.add(getAllItemTypes().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.ITEM_TYPES);
        groupDetails.put("views", itemTypes);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllItemTypes() {

        FacilioModule itemsModule = ModuleFactory.getItemTypesModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("name");
        createdTime.setDataType(FieldType.STRING);
        createdTime.setColumnName("NAME");
        createdTime.setModule(itemsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Item Types");
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
        FacilioModule itemTypesModule = modBean.getModule(FacilioConstants.ContextNames.ITEM_TYPES);

        FacilioForm itemTypesForm = new FacilioForm();
        itemTypesForm.setDisplayName("NEW ITEM TYPE");
        itemTypesForm.setName("default_itemTypes_web");
        itemTypesForm.setModule(itemTypesModule);
        itemTypesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        itemTypesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> itemTypesFormFields = new ArrayList<>();
        itemTypesFormFields.add(new FormField("photo", FacilioField.FieldDisplayType.IMAGE, "Photo", FormField.Required.OPTIONAL, 1, 1));
        itemTypesFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        itemTypesFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        FormField field = new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "inventoryCategory",4, 2);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        itemTypesFormFields.add(field);
        itemTypesFormFields.add(new FormField("sellingPrice", FacilioField.FieldDisplayType.DECIMAL, "Selling Price", FormField.Required.OPTIONAL,  5, 2));
        itemTypesFormFields.add(new FormField("minimumQuantity", FacilioField.FieldDisplayType.NUMBER, "Minimum Quantity", FormField.Required.OPTIONAL, 5, 3));
        itemTypesFormFields.add(new FormField("isRotating", FacilioField.FieldDisplayType.DECISION_BOX, "Is Rotating", FormField.Required.OPTIONAL, 6, 2));
        itemTypesFormFields.add(new FormField("isApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Approval Needed", FormField.Required.OPTIONAL, 7, 3));
        itemTypesFormFields.add(new FormField("isConsumable", FacilioField.FieldDisplayType.DECISION_BOX, "To Be Issued", FormField.Required.OPTIONAL, 8, 2));
        FormField currentQuantity = new FormField("currentQuantity", FacilioField.FieldDisplayType.NUMBER, "Current Quantity", FormField.Required.OPTIONAL, 9, 2);
        currentQuantity.setHideField(true);
        itemTypesFormFields.add(currentQuantity);

//        itemTypesForm.setFields(itemTypesFormFields);

        FormSection section = new FormSection("Default", 1, itemTypesFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        itemTypesForm.setSections(Collections.singletonList(section));
        itemTypesForm.setIsSystemForm(true);
        itemTypesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(itemTypesForm);
    }
}