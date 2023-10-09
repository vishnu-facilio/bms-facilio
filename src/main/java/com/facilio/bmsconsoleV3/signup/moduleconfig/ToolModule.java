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
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class ToolModule extends BaseModuleConfig{
    public ToolModule(){
        setModuleName(FacilioConstants.ContextNames.TOOL);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> tool = new ArrayList<FacilioView>();
        tool.add(getAllTools().setOrder(order++));



        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TOOL);
        groupDetails.put("views", tool);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllTools() {

        FacilioModule toolmodule = ModuleFactory.getToolModule();

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tools");

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
        FacilioModule toolModule = modBean.getModule(FacilioConstants.ContextNames.TOOL);

        FacilioForm stockedToolsForm = new FacilioForm();
        stockedToolsForm.setDisplayName("UPDATE TOOL ATTRIBUTES");
        stockedToolsForm.setName("web_default");
        stockedToolsForm.setModule(toolModule);
        stockedToolsForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        stockedToolsForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> stockedToolsFormFields = new ArrayList<>();
        stockedToolsFormFields.add(new FormField("toolType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tool Type", FormField.Required.REQUIRED, "toolTypes", 1, 1));
        stockedToolsFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Store Room", FormField.Required.REQUIRED, "storeRoom", 2, 1));
        stockedToolsFormFields.add(new FormField("minimumQuantity", FacilioField.FieldDisplayType.DECIMAL, "Minimum Quantity", FormField.Required.OPTIONAL, 3, 1));
        stockedToolsFormFields.add(new FormField("rate", FacilioField.FieldDisplayType.DECIMAL, "Rate/Hour", FormField.Required.OPTIONAL, 4, 1));


        FormSection section = new FormSection("Default", 1, stockedToolsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        stockedToolsForm.setSections(Collections.singletonList(section));
        stockedToolsForm.setIsSystemForm(true);
        stockedToolsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(stockedToolsForm);
    }
}