package com.facilio.bmsconsoleV3.signup.readingkpi;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;

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

        FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
        fields.add(nameField);

        fields.add((FacilioField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));

        SystemEnumField kpiType = (SystemEnumField) FieldFactory.getDefaultField("kpiType", "KPI Type", "KPI_TYPE", FieldType.SYSTEM_ENUM);
        kpiType.setEnumName("KPIType");
        fields.add(kpiType);

        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);

        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);

        NumberField kpiCategoryId = (NumberField) FieldFactory.getDefaultField("kpiCategory", "KPI Category ID", "KPI_CATEGORY_ID", FieldType.NUMBER);
        fields.add(kpiCategoryId);

        NumberField readingModuleId = (NumberField) FieldFactory.getDefaultField("readingModuleId", "Reading Module ID", "READING_MODULE_ID", FieldType.NUMBER);
        fields.add(readingModuleId);

        NumberField readingFieldId = (NumberField) FieldFactory.getDefaultField("readingFieldId", "Reading Field ID", "READING_FIELD_ID", FieldType.NUMBER);
        fields.add(readingFieldId);

        SystemEnumField frequency = (SystemEnumField) FieldFactory.getDefaultField("frequency", "Frequency", "FREQUENCY", FieldType.SYSTEM_ENUM);
        frequency.setEnumName("NamespaceFrequency");
        fields.add(frequency);

        NumberField siteId = (NumberField) FieldFactory.getDefaultField("siteId", "Site ID", "SITE_ID", FieldType.NUMBER);
        fields.add(siteId);

        BooleanField status = (BooleanField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);

        SystemEnumField resourceType = (SystemEnumField) FieldFactory.getDefaultField("resourceType", "Resource Type", "RESOURCE_TYPE", FieldType.SYSTEM_ENUM);
        resourceType.setEnumName("MultiResourceAssignmentType");
        fields.add(resourceType);

        NumberField sysCreatedTime = (NumberField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.NUMBER);
        fields.add(sysCreatedTime);

        LookupField sysCreatedBy = (LookupField) FieldFactory.getField("sysCreatedBy", "Created By", "SYS_CREATED_BY",ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysCreatedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysCreatedBy);


        NumberField sysModifiedTime = (NumberField) FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.NUMBER);
        fields.add(sysModifiedTime);

        LookupField sysModifiedBy = (LookupField) FieldFactory.getField("sysModifiedBy", "Modified By", "SYS_MODIFIED_BY",ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysModifiedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysModifiedBy);

        module.setFields(fields);
        return module;
    }
}
