package com.facilio.bmsconsoleV3.signup.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;

import java.util.*;

public class AddKPIModules extends BaseModuleConfig {

    public AddKPIModules() throws Exception {
        setModuleName(FacilioConstants.ReadingKpi.READING_KPI);
    }

    @Override
    public void addData() throws Exception {
        // TODO Auto-generated method stub

        FacilioModule kpiModule = addReadingKPIModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(kpiModule));
        addModuleChain.execute();
        addSystemButtonsForKPI();

    }

    private FacilioModule addReadingKPIModule() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule("readingkpi",
                "ReadingKPI",
                "ReadingKPI",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        FacilioField nameField = FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
        fields.add(nameField);

        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));

        FacilioField linkNameField = FieldFactory.getDefaultField("linkName", "Link Name", "LINK_NAME", FieldType.STRING);
        fields.add(linkNameField);

        SystemEnumField kpiType = FieldFactory.getDefaultField("kpiType", "KPI Type", "KPI_TYPE", FieldType.SYSTEM_ENUM);
        kpiType.setEnumName("KPIType");
        fields.add(kpiType);

        LookupField assetCategory = FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);

        NumberField categoryId = FieldFactory.getDefaultField("categoryId", "Category", "CATEGORY_ID", FieldType.NUMBER);
        fields.add(categoryId);

        NumberField kpiCategoryId = FieldFactory.getDefaultField("kpiCategory", "KPI Category", "KPI_CATEGORY_ID", FieldType.NUMBER);
        fields.add(kpiCategoryId);

        NumberField readingModuleId = FieldFactory.getDefaultField("readingModuleId", "Reading Module ID", "READING_MODULE_ID", FieldType.NUMBER);
        fields.add(readingModuleId);

        NumberField readingFieldId = FieldFactory.getDefaultField("readingFieldId", "Reading Field ID", "READING_FIELD_ID", FieldType.NUMBER);
        fields.add(readingFieldId);

        SystemEnumField frequency = FieldFactory.getDefaultField("frequency", "Frequency", "FREQUENCY", FieldType.SYSTEM_ENUM);
        frequency.setEnumName("NamespaceFrequency");
        fields.add(frequency);

        NumberField siteId = FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteId);

        SystemEnumField unitId = FieldFactory.getDefaultField("unitId", "Unit", "UNIT_ID", FieldType.SYSTEM_ENUM);
        unitId.setEnumName("FacilioUnit");
        fields.add(unitId);

        FacilioField customUnit = FieldFactory.getDefaultField("customUnit", "Custom Unit", "CUSTOM_UNIT", FieldType.STRING);
        fields.add(customUnit);

        SystemEnumField metric = FieldFactory.getDefaultField("metricId", "Metric", "METRIC", FieldType.SYSTEM_ENUM);
        metric.setEnumName("FacilioMetric");
        fields.add(metric);

        BooleanField status = FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);

        SystemEnumField resourceType = FieldFactory.getDefaultField("resourceType", "Resource Type", "RESOURCE_TYPE", FieldType.SYSTEM_ENUM);
        resourceType.setEnumName("ConnectedResourceAssignmentType");
        fields.add(resourceType);

        DateField sysCreatedTime = FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME);
        fields.add(sysCreatedTime);

        LookupField sysCreatedBy = FieldFactory.getField("sysCreatedBy", "Created By", "SYS_CREATED_BY",ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysCreatedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysCreatedBy);


        DateField sysModifiedTime = FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME);
        fields.add(sysModifiedTime);

        LookupField sysModifiedBy = FieldFactory.getField("sysModifiedBy", "Modified By", "SYS_MODIFIED_BY",ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysModifiedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysModifiedBy);

        module.setFields(fields);
        return module;
    }

    private void addSystemButtonsForKPI() throws Exception {
        SystemButtonApi.addCreateButtonWithCustomName(FacilioConstants.ReadingKpi.READING_KPI, "New KPI");
        SystemButtonApi.addListEditButton(FacilioConstants.ReadingKpi.READING_KPI);
        SystemButtonApi.addListDeleteButton(FacilioConstants.ReadingKpi.READING_KPI);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> readingKpi = new ArrayList<FacilioView>();
        readingKpi.add(getReadingKpi("all", "All KPI").setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "readingKpiViews");
        groupDetails.put("displayName", "Reading KPI");
        groupDetails.put("moduleName", FacilioConstants.ReadingKpi.READING_KPI);
        groupDetails.put("views", readingKpi);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private static FacilioView getReadingKpi(String name, String displayName) {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getReadingKpiModule());

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setModuleName(FacilioConstants.ReadingKpi.READING_KPI);
        view.setSortFields(Arrays.asList(new SortField(createdTime, false)));
        view.setDefault(true);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }
}
