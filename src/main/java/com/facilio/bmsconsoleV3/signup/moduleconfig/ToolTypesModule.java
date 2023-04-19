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

public class ToolTypesModule extends BaseModuleConfig{
    public ToolTypesModule(){
        setModuleName(FacilioConstants.ContextNames.TOOL_TYPES);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> toolTypes = new ArrayList<FacilioView>();
        toolTypes.add(getAllToolTypes().setOrder(order++));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TOOL_TYPES);
        groupDetails.put("views", toolTypes);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllToolTypes() {

        FacilioModule itemsModule = ModuleFactory.getToolTypesModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("name");
        createdTime.setDataType(FieldType.STRING);
        createdTime.setColumnName("NAME");
        createdTime.setModule(itemsModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tool Types");
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
        FacilioModule toolTypesModule = modBean.getModule(FacilioConstants.ContextNames.TOOL_TYPES);

        FacilioForm toolTypesForm = new FacilioForm();
        toolTypesForm.setDisplayName("NEW TOOL TYPE");
        toolTypesForm.setName("default_toolTypes_web");
        toolTypesForm.setModule(toolTypesModule);
        toolTypesForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        toolTypesForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> toolTypesFormFields = new ArrayList<>();
        toolTypesFormFields.add(new FormField("photo", FacilioField.FieldDisplayType.IMAGE, "Photo", FormField.Required.OPTIONAL, 1, 1));
        toolTypesFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        toolTypesFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 3, 1));
        FormField field = new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.OPTIONAL, "inventoryCategory",4, 1);
        field.setAllowCreateOptions(true);
        field.addToConfig("canShowLookupWizard",true);
        toolTypesFormFields.add(field);
        toolTypesFormFields.add(new FormField("sellingPrice", FacilioField.FieldDisplayType.DECIMAL, "Selling Price Per Hour", FormField.Required.OPTIONAL,  5, 2));
        toolTypesFormFields.add(new FormField("minimumQuantity", FacilioField.FieldDisplayType.NUMBER, "Minimum Quantity", FormField.Required.OPTIONAL, 5, 3));
        toolTypesFormFields.add(new FormField("isRotating", FacilioField.FieldDisplayType.DECISION_BOX, "Is Rotating", FormField.Required.OPTIONAL, 6, 2));
        toolTypesFormFields.add(new FormField("isApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Approval Needed", FormField.Required.OPTIONAL, 6, 3));


        FormSection section = new FormSection("Default", 1, toolTypesFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        toolTypesForm.setSections(Collections.singletonList(section));
        toolTypesForm.setIsSystemForm(true);
        toolTypesForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(toolTypesForm);
    }

}
