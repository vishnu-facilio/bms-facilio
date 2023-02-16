package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.*;

public class AssetModule extends BaseModuleConfig{
    public AssetModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.ASSET);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails = new HashMap<>();

        int order = 1;
        ArrayList<FacilioView> asset = new ArrayList<FacilioView>();
        asset.add(getAllAssetsView().setOrder(order++));
        asset.add(getAssets("Energy").setOrder(order++));
        asset.add(getAssets("HVAC").setOrder(order++));
        asset.add(getAssetsByState("Active").setOrder(order++));
        asset.add(getAssetsByState("Retired").setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "assetviews");
        groupDetails.put("displayName", "Asset");
        groupDetails.put("views", asset);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAssetsView() {
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Assets");
        allView.setSortFields(getSortFields(FacilioConstants.ContextNames.ASSET));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAssets(String category) {

        FacilioView assetView = new FacilioView();
        if (category.equals("Energy")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getAssetCategoryCondition(category));

            assetView.setName("energy");
            assetView.setDisplayName("Energy Assets");
            assetView.setCriteria(criteria);
        } else if (category.equals("HVAC")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getAssetCategoryCondition(category));

            assetView.setName("hvac");
            assetView.setDisplayName("HVAC Assets");
            assetView.setCriteria(criteria);
        }

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getAssetsModule());

        assetView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        assetView.setAppLinkNames(appLinkNames);

        return assetView;
    }

    private static FacilioView getAssetsByState(String state) {

        FacilioView assetView = new FacilioView();
        Criteria criteria = getAssetStatusCriteria(state);
        if (state.equals("Active")) {
            assetView.setName("active");
            assetView.setDisplayName("Active Assets");
            assetView.setCriteria(criteria);
        } else if (state.equals("Retired")) {
            assetView.setName("retired");
            assetView.setDisplayName("Retired Assets");
            assetView.setCriteria(criteria);
        }

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getAssetsModule());

        assetView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        assetView.setAppLinkNames(appLinkNames);

        return assetView;
    }

    public static List<SortField> getSortFields(String moduleName, FacilioModule...module) {
        List<SortField> fields = new ArrayList<>();
        switch (moduleName) {
            case FacilioConstants.ContextNames.ASSET:
                FacilioField localId = new FacilioField();
                localId.setName("localId");
                localId.setColumnName("LOCAL_ID");
                localId.setDataType(FieldType.NUMBER);
                localId.setModule(ModuleFactory.getAssetsModule());

                fields = Arrays.asList(new SortField(localId, false));
                break;
            default:
                if (module.length > 0) {
                    FacilioField createdTime = new FacilioField();
                    createdTime.setName("sysCreatedTime");
                    createdTime.setDataType(FieldType.NUMBER);
                    createdTime.setColumnName("CREATED_TIME");
                    createdTime.setModule(module[0]);

                    fields = Arrays.asList(new SortField(createdTime, false));
                }
                break;
        }
        return fields;
    }

    private static Condition getAssetCategoryCondition(String category) {
        FacilioModule module = ModuleFactory.getAssetsModule();
        LookupField statusField = new LookupField();
        statusField.setName("category");
        statusField.setColumnName("CATEGORY");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getAssetCategoryModule());

        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getAssetCategoryCriteria(category));

        return open;
    }

    private static Criteria getAssetStatusCriteria(String status) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("status");
        statusTypeField.setColumnName("STATUS");
        statusTypeField.setDataType(FieldType.STRING);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusActive = new Condition();
        statusActive.setField(statusTypeField);
        statusActive.setOperator(StringOperators.IS);
        statusActive.setValue(status);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(statusActive);

        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(ModuleFactory.getAssetsModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition ticketActive = new Condition();
        ticketActive.setField(statusField);
        ticketActive.setOperator(LookupOperator.LOOKUP);
        ticketActive.setCriteriaValue(statusCriteria);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(ticketActive);
        return criteria;
    }

    private static Criteria getAssetCategoryCriteria(String category) {
        FacilioField categoryType = new FacilioField();
        categoryType.setName("categoryType");
        categoryType.setColumnName("CATEGORY_TYPE");
        categoryType.setDataType(FieldType.NUMBER);
        categoryType.setModule(ModuleFactory.getAssetCategoryModule());

        Condition statusOpen = new Condition();
        statusOpen.setField(categoryType);
        statusOpen.setOperator(NumberOperators.EQUALS);
        if (category.equals("Energy")) {
            statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.ENERGY.getIntVal()));
        } else if (category.equals("HVAC")) {
            statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.HVAC.getIntVal()));
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusOpen);

        return criteria;
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);

        FacilioForm assetForm =new FacilioForm();
        assetForm.setDisplayName("Standard");
        assetForm.setName("default_asset_web");
        assetForm.setModule(assetModule);
        assetForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        assetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> assetFormFields = new ArrayList<>();
        assetFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        assetFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        assetFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        FormField categoryField = new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.REQUIRED, "assetcategory", 4, 2);
        categoryField.setIsDisabled(true);
        assetFormFields.add(categoryField);
        assetFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.OPTIONAL,"assetdepartment", 4, 3));
        assetFormFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Asset Location", FormField.Required.OPTIONAL, 5, 2));
        assetFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Type", FormField.Required.OPTIONAL,"assettype", 5, 3));
        assetFormFields.add(new FormField("manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "Manufacturer", FormField.Required.OPTIONAL, 6, 2));
        assetFormFields.add(new FormField("supplier", FacilioField.FieldDisplayType.TEXTBOX, "Supplier", FormField.Required.OPTIONAL, 6, 3));
        assetFormFields.add(new FormField("model", FacilioField.FieldDisplayType.TEXTBOX, "Model", FormField.Required.OPTIONAL, 7, 2));
        assetFormFields.add(new FormField("serialNumber", FacilioField.FieldDisplayType.TEXTBOX, "Serial Number", FormField.Required.OPTIONAL, 7, 3));
        assetFormFields.add(new FormField("tagNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tag", FormField.Required.OPTIONAL, 8, 2));
        assetFormFields.add(new FormField("partNumber", FacilioField.FieldDisplayType.TEXTBOX, "Part No.", FormField.Required.OPTIONAL, 8, 3));
        assetFormFields.add(new FormField("purchasedDate", FacilioField.FieldDisplayType.DATETIME, "Purchased Date", FormField.Required.OPTIONAL, 9, 2));
        assetFormFields.add(new FormField("retireDate", FacilioField.FieldDisplayType.DATETIME, "Retire Date", FormField.Required.OPTIONAL, 9, 3));
        assetFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 10, 2));
        assetFormFields.add(new FormField("warrantyExpiryDate", FacilioField.FieldDisplayType.DATETIME, "Warranty Expiry Date", FormField.Required.OPTIONAL, 10, 3));
        assetFormFields.add(new FormField("qrVal", FacilioField.FieldDisplayType.TEXTBOX, "QR Value", FormField.Required.OPTIONAL, 11, 2));
        // new fields
        assetFormFields.add(new FormField("rotatingItem", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Rotating Item", FormField.Required.OPTIONAL, "item", 12,2));
        assetFormFields.add(new FormField("rotatingTool", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Rotating Tool", FormField.Required.OPTIONAL, "tool", 12,3));
        assetFormFields.add(new FormField("geoLocationEnabled", FacilioField.FieldDisplayType.DECISION_BOX, "Is Movable", FormField.Required.OPTIONAL, 13,2));
        assetFormFields.add(new FormField("moveApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Is Move Approval Needed", FormField.Required.OPTIONAL, 13,2));
        assetFormFields.add(new FormField("boundaryRadius", FacilioField.FieldDisplayType.NUMBER, "Boundary Radius", FormField.Required.OPTIONAL, 14, 2));
        assetFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));

        FormSection section = new FormSection("Default", 1, assetFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        assetForm.setSections(Collections.singletonList(section));
        assetForm.setIsSystemForm(true);
        assetForm.setType(FacilioForm.Type.FORM);


        FacilioForm mobileAssetForm = new FacilioForm();
        mobileAssetForm.setDisplayName("Asset");
        mobileAssetForm.setName("default_asset_mobile");
        mobileAssetForm.setModule(assetModule);
        mobileAssetForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        mobileAssetForm.setShowInMobile(true);
        mobileAssetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> mobileAssetFormFields = new ArrayList<>();
        mobileAssetFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        mobileAssetFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Type", FormField.Required.OPTIONAL,"assettype", 2, 1));
        mobileAssetFormFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Asset Location", FormField.Required.OPTIONAL, 3, 1 ));
        mobileAssetFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.OPTIONAL,"assetdepartment", 4, 1));
        mobileAssetFormFields.add(new FormField("manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "Manufacturer", FormField.Required.OPTIONAL, 5, 1));
        mobileAssetFormFields.add(new FormField("model", FacilioField.FieldDisplayType.TEXTBOX, "Model", FormField.Required.OPTIONAL, 6, 1));
        mobileAssetFormFields.add(new FormField("serialNumber", FacilioField.FieldDisplayType.TEXTBOX, "Serial Number", FormField.Required.OPTIONAL, 7, 1));
        mobileAssetFormFields.add(new FormField("tagNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tag", FormField.Required.OPTIONAL, 8, 1));
        mobileAssetFormFields.add(new FormField("partNumber", FacilioField.FieldDisplayType.TEXTBOX, "Part No.", FormField.Required.OPTIONAL, 9, 1));
        mobileAssetFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 10, 1));
        mobileAssetFormFields.add(new FormField("supplier", FacilioField.FieldDisplayType.TEXTBOX, "Supplier", FormField.Required.OPTIONAL, 11, 1));
        mobileAssetFormFields.add(new FormField("purchasedDate", FacilioField.FieldDisplayType.DATE, "Purchased Date", FormField.Required.OPTIONAL, 12, 1));
        mobileAssetFormFields.add(new FormField("retireDate", FacilioField.FieldDisplayType.DATE, "Retire Date", FormField.Required.OPTIONAL, 13, 1));
        mobileAssetFormFields.add(new FormField("warrantyExpiryDate", FacilioField.FieldDisplayType.DATE, "Warranty Expiry Date", FormField.Required.OPTIONAL, 14, 1));
        mobileAssetFormFields.add(new FormField("qrVal", FacilioField.FieldDisplayType.TEXTBOX, "QR Value", FormField.Required.OPTIONAL, 15, 1));

        FormSection mobileAssetFormSection = new FormSection("Default", 1, mobileAssetFormFields, false);
        mobileAssetFormSection.setSectionType(FormSection.SectionType.FIELDS);
        mobileAssetForm.setSections(Collections.singletonList(mobileAssetFormSection));
        mobileAssetForm.setIsSystemForm(true);
        mobileAssetForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> assetModuleForms = new ArrayList<>();
        assetModuleForms.add(assetForm);
        assetModuleForms.add(mobileAssetForm);

        return assetModuleForms;
    }


    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields maintenanceApp = new ScopeVariableModulesFields();
        maintenanceApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_maintenance_site"));
        maintenanceApp.setModuleId(module.getModuleId());
        maintenanceApp.setFieldName("siteId");

        scopeConfigList = Arrays.asList(maintenanceApp);
        return scopeConfigList;
    }

    @Override
    public void addClassificationDataModule() throws Exception {
        String tableName="Assets_Classification_Data";
        ClassificationUtil.addClassificationDataModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.ASSET), tableName);
    }
}
