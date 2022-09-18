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

public class WorkOrderToolsModule extends BaseModuleConfig{
    public WorkOrderToolsModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.WORKORDER_TOOLS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> workOrderTools = new ArrayList<FacilioView>();
        workOrderTools.add(getAllWorkOrderTools().setOrder(order++));
        workOrderTools.add(getAllWorkOrderToolsDetailsView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", getModuleName());
        groupDetails.put("views", workOrderTools);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllWorkOrderTools() {

        FacilioModule workOrderToolsModule = ModuleFactory.getWorkOrderToolsModule();

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Work Order Tools");
        allView.setModuleName(workOrderToolsModule.getName());

        return allView;
    }

    private static FacilioView getAllWorkOrderToolsDetailsView() {
        FacilioModule workOrderToolsModule = ModuleFactory.getWorkOrderToolsModule();

        FacilioView detailsView = new FacilioView();
        detailsView.setName("details");
        detailsView.setDisplayName("Work Order Tools Details");
        detailsView.setModuleName(workOrderToolsModule.getName());

        return detailsView;
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workOrderToolsModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS);

        FacilioForm workOrderToolsModuleForm = new FacilioForm();
        workOrderToolsModuleForm.setDisplayName("New Work Order Tool");
        workOrderToolsModuleForm.setName("default_workorderTool_web");
        workOrderToolsModuleForm.setModule(workOrderToolsModule);
        workOrderToolsModuleForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        workOrderToolsModuleForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> workOrderToolsModuleFormFields = new ArrayList<>();
        workOrderToolsModuleFormFields.add(new FormField("toolType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Tool Type", FormField.Required.REQUIRED, "toolTypes", 1, 2,true));
        workOrderToolsModuleFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.REQUIRED, "storeRoom", 1, 3,true));
        workOrderToolsModuleFormFields.add(new FormField("issueTime", FacilioField.FieldDisplayType.DATETIME,"Issue Time", FormField.Required.OPTIONAL,2,2));
        workOrderToolsModuleFormFields.add(new FormField("returnTime", FacilioField.FieldDisplayType.DATETIME,"Return Time", FormField.Required.OPTIONAL,2,3));
        workOrderToolsModuleFormFields.add(new FormField("duration", FacilioField.FieldDisplayType.TEXTBOX,"Duration", FormField.Required.OPTIONAL,3,2));
        workOrderToolsModuleFormFields.add(new FormField("quantity", FacilioField.FieldDisplayType.TEXTBOX, "Quantity", FormField.Required.OPTIONAL, 3, 3));

        FormSection workOrderToolsModuleFormSection = new FormSection("Default", 1, workOrderToolsModuleFormFields, false);
        workOrderToolsModuleFormSection.setSectionType(FormSection.SectionType.FIELDS);
        workOrderToolsModuleForm.setSections(Collections.singletonList(workOrderToolsModuleFormSection));

        return Collections.singletonList(workOrderToolsModuleForm);
    }
}
