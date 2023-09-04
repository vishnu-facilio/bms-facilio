package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class TerritoryModule extends BaseModuleConfig {
    public static List<String> territorySupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);
    public TerritoryModule() {
        setModuleName(FacilioConstants.Territory.TERRITORY);
    }

    public void addData() throws Exception {
        addTerritoryModule();
//        addTerritoryLookupInPeople();
        addTerritoryLookupInSite();
        addActivityModuleForTerritory();
        addPeopleTerritoryModule();
        addTerritoriesField();
        SignupUtil.addNotesAndAttachmentModule(Constants.getModBean().getModule(FacilioConstants.Territory.TERRITORY));
    }

    private void addTerritoryModule() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule territoryModule = new FacilioModule(FacilioConstants.Territory.TERRITORY, "Territory", "TERRITORY", FacilioModule.ModuleType.BASE_ENTITY, true);
        List<FacilioField> territoryFields = new ArrayList<>();

        NumberField localId = new NumberField(territoryModule,"localId","Id", FacilioField.FieldDisplayType.NUMBER,"LOCAL_ID",FieldType.NUMBER,false,false,true,false);
        territoryFields.add(localId);
        ModuleLocalIdUtil.insertModuleLocalId(FacilioConstants.Territory.TERRITORY);

        territoryFields.add(new StringField(territoryModule, "name", "Name", FacilioField.FieldDisplayType.TEXTBOX, "NAME", FieldType.STRING, true, false, true, true));
        territoryFields.add(new StringField(territoryModule, "description", "Description", FacilioField.FieldDisplayType.TEXTAREA, "DESCRIPTION", FieldType.STRING, false, false, true, false));
        territoryFields.add(new StringField(territoryModule, "color", "Territory Color", FacilioField.FieldDisplayType.COLOR_PICKER, "COLOR", FieldType.STRING, true, false, true, false));
        territoryFields.add(new StringField(territoryModule, "geography", "Geography", FacilioField.FieldDisplayType.TEXTAREA, "GEO_JSON", FieldType.STRING, true, false, true, false));

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        territoryFields.add(moduleStateField);

        NumberField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        territoryFields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        territoryFields.add(approvalStateField);

        NumberField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        territoryFields.add(approvalFlowIdField);

        territoryModule.setFields(territoryFields);
        modules.add(territoryModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }

//    private void addTerritoryLookupInPeople() throws Exception {
//        ModuleBean modBean = Constants.getModBean();
//        LookupField territoryLookup = new LookupField(modBean.getModule(FacilioConstants.ContextNames.PEOPLE), FacilioConstants.Territory.TERRITORY, "Territory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "TERRITORY_ID", FieldType.LOOKUP, false, false, true, false, "Territory", modBean.getModule(FacilioConstants.Territory.TERRITORY));
//        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
//        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PEOPLE);
//        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, Collections.singletonList(territoryLookup));
//        chain.execute();
//    }

    private void addTerritoryLookupInSite() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        LookupField territoryLookup = new LookupField(modBean.getModule(FacilioConstants.ContextNames.SITE), FacilioConstants.Territory.TERRITORY, "Territory", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "TERRITORY_ID", FieldType.LOOKUP, false, false, true, false, "Territory", modBean.getModule(FacilioConstants.Territory.TERRITORY));
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.SITE);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, Collections.singletonList(territoryLookup));
        chain.execute();
    }

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule territoryModule = modBean.getModule(FacilioConstants.Territory.TERRITORY);
        FacilioForm territoryForm = new FacilioForm();
        territoryForm.setDisplayName("Standard");
        territoryForm.setName("default_asset_web");
        territoryForm.setModule(territoryModule);
        territoryForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        territoryForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));

        List<FormField> territoryGeneralInfoFormFields = new ArrayList<>();
        territoryGeneralInfoFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        territoryGeneralInfoFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));

        List<FormField> territoryGeographyFormFields=new ArrayList<>();
        territoryGeographyFormFields.add(new FormField("geography", FacilioField.FieldDisplayType.TERRITORY, "Geography", FormField.Required.REQUIRED, 3, 1));

        FormSection generalInfosection = new FormSection("General Information", 1, territoryGeneralInfoFormFields, false);
        FormSection geographySection = new FormSection("Geography", 2, territoryGeographyFormFields, false);

        generalInfosection.setSectionType(FormSection.SectionType.FIELDS);

        geographySection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> webTerritoryFormSection=new ArrayList<>();
        webTerritoryFormSection.add(generalInfosection);
        webTerritoryFormSection.add(geographySection);

        territoryForm.setSections(webTerritoryFormSection);

        territoryForm.setIsSystemForm(true);
        territoryForm.setType(FacilioForm.Type.FORM);
        List<FacilioForm> territoryModuleForms = new ArrayList<>();
        territoryModuleForms.add(territoryForm);
        return territoryModuleForms;
    }

    public void addActivityModuleForTerritory() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule territory = modBean.getModule(FacilioConstants.Territory.TERRITORY);

        FacilioModule module = new FacilioModule(FacilioConstants.Territory.TERRITORY_ACTIVITY,
                "Territory Activity",
                "Territory_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );


        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);

        fields.add(timefield);

        NumberField type = FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);

        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING);

        fields.add(info);


        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();

        modBean.addSubModule(territory.getModuleId(), module.getModuleId());
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.Territory.TERRITORY);

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createTerritoryPage(app, module, false, true));
        }
        return appNameVsPage;
    }

    private static List<PagesContext> createTerritoryPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.Territory.TERRITORY_ACTIVITY);

        return new ModulePages()
                .addPage("territory", "Territory","", null, isTemplate, isDefault, false)
                .addWebTab("territorysummary", "Summary", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("territorysummaryfields", null, null)
                .addWidget("territorysummaryfieldswidget", "Territory", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.Territory.TERRITORY))
                .widgetDone()
                .sectionDone()
                .addSection("territorygeograpy", null, null)
                .addWidget("territorygeograpywidget", "Geography", PageWidget.WidgetType.GEOGRAPHY, "fixedterritorygeography_22_6", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("territorysites","Sites",true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("sites", null, null)
                .addWidget("sites", "Sites", PageWidget.WidgetType.TERRITORY_SITES, "flexiblewebterritorysites_50", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("territoryfieldagent","Field Agents",true,null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("fieldagents", null, null)
                .addWidget("fieldagents", "Field Agents", PageWidget.WidgetType.FIELD_AGENTS, "flexibleterritoryfieldagents_50", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("territoryhistory", "History", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .pageDone().getCustomPages();




    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup GeneralInfoWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(GeneralInfoWidgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(GeneralInfoWidgetGroup, descriptionField, 1, 2, 3);

        SummaryWidgetGroup sysDetailsWidgetGroup=new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysCreatedByField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysCreatedTimeField, 2, 2, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysModifiedByField, 2, 3, 1);
        addSummaryFieldInWidgetGroup(sysDetailsWidgetGroup, sysModifiedTimeField, 2, 4, 1);


        GeneralInfoWidgetGroup.setName("generalInformation");
        GeneralInfoWidgetGroup.setDisplayName("General Information");
        GeneralInfoWidgetGroup.setColumns(4);

        sysDetailsWidgetGroup.setName("systemDetails");
        sysDetailsWidgetGroup.setDisplayName("System Details");
        sysDetailsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(GeneralInfoWidgetGroup);
        widgetGroupList.add(sysDetailsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.Territory.TERRITORY_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.Territory.TERRITORY_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;
        int order = 1;

        ArrayList<FacilioView> all = new ArrayList<FacilioView>();
        all.add(getAllTerritories().setOrder(order++));
        groupDetails = new HashMap<>();
        groupDetails.put("name", "default");
        groupDetails.put("displayName", "Default");
        groupDetails.put("views", all);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getAllTerritories() {
        FacilioModule territoryModule = new FacilioModule(FacilioConstants.Territory.TERRITORY,"Territory","TERRITORY", FacilioModule.ModuleType.BASE_ENTITY,true);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id","ID",territoryModule,FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Territories");
        allView.setModuleName(FacilioConstants.Territory.TERRITORY);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(TerritoryModule.territorySupportedApps);

        List<ViewField> territoryViewFields = new ArrayList<>();

        territoryViewFields.add(new ViewField("name","Name"));
        territoryViewFields.add(new ViewField("moduleState","Status"));
        territoryViewFields.add(new ViewField("sysCreatedBy","Created By"));
        territoryViewFields.add(new ViewField("sysCreatedTime","Created Time"));
        territoryViewFields.add(new ViewField("sysModifiedBy","Modified By"));
        territoryViewFields.add(new ViewField("sysModifiedTime","Modified Time"));

        allView.setFields(territoryViewFields);

        return allView;
    }

    private void addPeopleTerritoryModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule territoryModule = modBean.getModule(FacilioConstants.Territory.TERRITORY);
        FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        FacilioModule peopleTerritoryModule = new FacilioModule(FacilioConstants.Territory.PEOPLE_TERRITORY,"People Territories","PEOPLE_TERRITORY_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField peopleField = new LookupField(peopleTerritoryModule,"left","People",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"PEOPLE_ID",FieldType.LOOKUP,true,false,true,true,"People",peopleModule);
        fields.add(peopleField);
        LookupField territoryField = new LookupField(peopleTerritoryModule,"right","Territories", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"TERRITORY_ID",FieldType.LOOKUP,true,false,true,false,"Territories",territoryModule);
        fields.add(territoryField);
        peopleTerritoryModule.setFields(fields);
        modules.add(peopleTerritoryModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private void addTerritoriesField() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField>fields = new ArrayList<>();
        MultiLookupField multiLookupTerritoryField = FieldFactory.getDefaultField("territories", "Territories", null, FieldType.MULTI_LOOKUP);
        multiLookupTerritoryField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupTerritoryField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupTerritoryField.setLookupModule( modBean.getModule(FacilioConstants.Territory.TERRITORY));
        multiLookupTerritoryField.setRelModule(modBean.getModule(FacilioConstants.Territory.PEOPLE_TERRITORY));
        multiLookupTerritoryField.setRelModuleId(modBean.getModule(FacilioConstants.Territory.PEOPLE_TERRITORY).getModuleId());
        fields.add(multiLookupTerritoryField);
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.PEOPLE);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }

}
