package com.facilio.bmsconsoleV3.signup.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddKPIModules extends SignUpData {

    @Override
    public void addData() throws Exception {
        // TODO Auto-generated method stub

        FacilioModule kpiModule = addReadingKPIModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(kpiModule));
        addModuleChain.execute();

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

        LookupField spaceCategory = FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);

        NumberField kpiCategoryId = FieldFactory.getDefaultField("kpiCategory", "KPI Category ID", "KPI_CATEGORY_ID", FieldType.NUMBER);
        fields.add(kpiCategoryId);

        NumberField readingModuleId = FieldFactory.getDefaultField("readingModuleId", "Reading Module ID", "READING_MODULE_ID", FieldType.NUMBER);
        fields.add(readingModuleId);

        NumberField readingFieldId = FieldFactory.getDefaultField("readingFieldId", "Reading Field ID", "READING_FIELD_ID", FieldType.NUMBER);
        fields.add(readingFieldId);

        SystemEnumField frequency = FieldFactory.getDefaultField("frequency", "Frequency", "FREQUENCY", FieldType.SYSTEM_ENUM);
        frequency.setEnumName("NamespaceFrequency");
        fields.add(frequency);

        NumberField siteId = FieldFactory.getDefaultField("siteId", "Site ID", "SITE_ID", FieldType.NUMBER);
        fields.add(siteId);

        NumberField unitId = FieldFactory.getDefaultField("unitId", "Unit ID", "UNIT_ID", FieldType.NUMBER);
        fields.add(unitId);

        FacilioField customUnit = FieldFactory.getDefaultField("customUnit", "Custom Unit", "CUSTOM_UNIT", FieldType.STRING);
        fields.add(customUnit);

        NumberField metric = FieldFactory.getDefaultField("metricId", "Metric", "METRIC", FieldType.NUMBER);
        fields.add(metric);

        BooleanField status = FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);

        SystemEnumField resourceType = FieldFactory.getDefaultField("resourceType", "Resource Type", "RESOURCE_TYPE", FieldType.SYSTEM_ENUM);
        resourceType.setEnumName("MultiResourceAssignmentType");
        fields.add(resourceType);

        NumberField sysCreatedTime = FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.NUMBER);
        fields.add(sysCreatedTime);

        LookupField sysCreatedBy = FieldFactory.getField("sysCreatedBy", "Created By", "SYS_CREATED_BY",ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysCreatedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysCreatedBy);


        NumberField sysModifiedTime = FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.NUMBER);
        fields.add(sysModifiedTime);

        LookupField sysModifiedBy = FieldFactory.getField("sysModifiedBy", "Modified By", "SYS_MODIFIED_BY",ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysModifiedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysModifiedBy);

        module.setFields(fields);
        return module;
    }
}
