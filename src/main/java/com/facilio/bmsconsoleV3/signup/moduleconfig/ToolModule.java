package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.ToolTemplatePage;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

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
        stockedToolsFormFields.add(new FormField("costType", FacilioField.FieldDisplayType.SELECTBOX, "Cost Type", FormField.Required.OPTIONAL, 5, 1));


        FormSection section = new FormSection("Default", 1, stockedToolsFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        stockedToolsForm.setSections(Collections.singletonList(section));
        stockedToolsForm.setIsSystemForm(true);
        stockedToolsForm.setType(FacilioForm.Type.FORM);

        return Collections.singletonList(stockedToolsForm);
    }
    @Override
    public void addData() throws Exception {
        addSystemButton();
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        FacilioModule module= Constants.getModBean().getModule(FacilioConstants.ContextNames.TOOL);
        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            PagesContext nonRotatingToolsPage =  ToolTemplatePage.getNonRotatingToolsPage(app,module,false,true);
            PagesContext rotatingToolsPage = ToolTemplatePage.getRotatingToolsPage(app, module, false, false);
            List<PagesContext> pagesList = new ArrayList<>();
            pagesList.add(nonRotatingToolsPage);
            pagesList.add(rotatingToolsPage);
            appNameVsPage.put(appName,pagesList);
        }
        return appNameVsPage;
    }


    private static void addSystemButton() throws Exception{
        SystemButtonRuleContext editButton = new SystemButtonRuleContext();
        editButton.setName("Edit");
        editButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButton.setIdentifier("edit");
        editButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL,editButton);

        SystemButtonRuleContext stockButton = new SystemButtonRuleContext();
        stockButton.setName("Stock");
        stockButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        stockButton.setIdentifier(FacilioConstants.ContextNames.TOOL_STOCK_BUTTON);
        stockButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        stockButton.setPermission(AccountConstants.ModulePermission.CREATE.name());
        stockButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL,stockButton);

        SystemButtonRuleContext goToToolType = new SystemButtonRuleContext();
        goToToolType.setName("Go To Tool Type");
        goToToolType.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        goToToolType.setIdentifier(FacilioConstants.ContextNames.GO_TO_TOOL_TYPE_BUTTON);
        goToToolType.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL,goToToolType);

        SystemButtonRuleContext issueTools = new SystemButtonRuleContext();
        issueTools.setName("Issue Tools");
        issueTools.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        issueTools.setIdentifier(FacilioConstants.ContextNames.ISSUE_TOOLS_BUTTON);
        issueTools.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        issueTools.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        issueTools.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL,issueTools);

        SystemButtonRuleContext returnTools = new SystemButtonRuleContext();
        returnTools.setName("Return Tools");
        returnTools.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        returnTools.setIdentifier(FacilioConstants.ContextNames.RETURN_TOOLS_BUTTON);
        returnTools.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        returnTools.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        returnTools.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL,returnTools);

        SystemButtonRuleContext adjustToolBalanceButton = new SystemButtonRuleContext();
        adjustToolBalanceButton.setName("Adjust Balance");
        adjustToolBalanceButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        adjustToolBalanceButton.setIdentifier(FacilioConstants.ContextNames.ADJUST_TOOL_BALANCE_BUTTON);
        adjustToolBalanceButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        adjustToolBalanceButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        adjustToolBalanceButton.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL, adjustToolBalanceButton);

        SystemButtonRuleContext listEditButton = new SystemButtonRuleContext();
        listEditButton.setName("Edit");
        listEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        listEditButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listEditButton.setIdentifier("edit_list");
        listEditButton.setPermissionRequired(true);
        listEditButton.setPermission("UPDATE");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL,listEditButton);

        SystemButtonRuleContext exportAsCSVButton = new SystemButtonRuleContext();
        exportAsCSVButton.setName("Export As CSV");
        exportAsCSVButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsCSVButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCSVButton.setIdentifier("export_as_csv");
        exportAsCSVButton.setPermissionRequired(true);
        exportAsCSVButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL,exportAsCSVButton);

        SystemButtonRuleContext exportAsExcelButton = new SystemButtonRuleContext();
        exportAsExcelButton.setName("Export As Excel");
        exportAsExcelButton.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        exportAsExcelButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButton.setIdentifier("export_as_excel");
        exportAsExcelButton.setPermissionRequired(true);
        exportAsExcelButton.setPermission("EXPORT");
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.TOOL,exportAsExcelButton);
    }
}