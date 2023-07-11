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

public class InventoryCategoryModule extends BaseModuleConfig{
    public InventoryCategoryModule(){
        setModuleName(FacilioConstants.ContextNames.INVENTORY_CATEGORY);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> inventoryCategory = new ArrayList<FacilioView>();
        inventoryCategory.add(getAllInventoryCategory().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.INVENTORY_CATEGORY);
        groupDetails.put("views", inventoryCategory);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllInventoryCategory() {

        FacilioModule inventoryCategoryModule = ModuleFactory.getInventoryCategoryModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("name");
        createdTime.setDataType(FieldType.STRING);
        createdTime.setColumnName("NAME");
        createdTime.setModule(inventoryCategoryModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Inventory Category");
        allView.setSortFields(sortFields);

        return allView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inventoryCategoryModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_CATEGORY);

        FacilioForm inventoryCategoryForm = new FacilioForm();
        inventoryCategoryForm.setDisplayName("NEW CATEGORY");
        inventoryCategoryForm.setName("web_default");
        inventoryCategoryForm.setModule(inventoryCategoryModule);
        inventoryCategoryForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        inventoryCategoryForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> inventoryCategoryFormFields = new ArrayList<>();
        inventoryCategoryFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        inventoryCategoryFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
//        inventoryCategoryForm.setFields(inventoryCategoryFormFields);

        FormSection section = new FormSection("Default", 1, inventoryCategoryFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        inventoryCategoryForm.setSections(Collections.singletonList(section));
        inventoryCategoryForm.setIsSystemForm(true);
        inventoryCategoryForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(inventoryCategoryForm);
    }
}
