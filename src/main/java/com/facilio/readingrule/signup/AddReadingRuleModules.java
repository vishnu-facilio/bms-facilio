package com.facilio.readingrule.signup;

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

public class AddReadingRuleModules extends SignUpData {

    @Override
    public void addData() throws Exception {
        try {
            FacilioModule readingRule = addReadingRuleModule();

            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(readingRule));
            addModuleChain.execute();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private FacilioModule addReadingRuleModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule("newreadingrules",
                "NewReadingRules",
                "New_Reading_Rule",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        FacilioField nameField = FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
        fields.add(nameField);

        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));

        FacilioField linkNameField = FieldFactory.getDefaultField("linkName", "Link Name", "LINK_NAME", FieldType.STRING);
        fields.add(linkNameField);

        LookupField assetCategory = FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY_ID", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);

        SystemEnumField resourceType = FieldFactory.getDefaultField("resourceType", "Resource Type", "RESOURCE_TYPE", FieldType.SYSTEM_ENUM);
        resourceType.setEnumName("ConnectedResourceAssignmentType");
        fields.add(resourceType);

        NumberField categoryId = FieldFactory.getDefaultField("categoryId", "Category ID", "CATEGORY_ID", FieldType.NUMBER);
        fields.add(categoryId);

        LookupField faultImpact = FieldFactory.getDefaultField("impact", "Impact", "IMPACT_ID", FieldType.LOOKUP);
        faultImpact.setLookupModule(modBean.getModule(FacilioConstants.FaultImpact.MODULE_NAME));
        fields.add(faultImpact);

        NumberField readingModuleId = FieldFactory.getDefaultField("readingModuleId", "Reading Module ID", "READING_MODULE_ID", FieldType.NUMBER);
        fields.add(readingModuleId);

        NumberField readingFieldId = FieldFactory.getDefaultField("readingFieldId", "Reading Field ID", "READING_FIELD_ID", FieldType.NUMBER);
        fields.add(readingFieldId);

        NumberField siteId = FieldFactory.getDefaultField("siteId", "Site ID", "SITE_ID", FieldType.NUMBER);
        fields.add(siteId);

        BooleanField status = FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);

        BooleanField autoClear = FieldFactory.getDefaultField("autoClear", "AutoClear", "AUTO_CLEAR", FieldType.BOOLEAN);
        fields.add(autoClear);

        DateField sysCreatedTime = FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.DATE_TIME);
        fields.add(sysCreatedTime);

        LookupField sysCreatedBy = FieldFactory.getField("sysCreatedBy", "Created By", "SYS_CREATED_BY", ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysCreatedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysCreatedBy);

        DateField sysModifiedTime = FieldFactory.getDefaultField("sysModifiedTime", "Modified Time", "SYS_MODIFIED_TIME", FieldType.DATE_TIME);
        fields.add(sysModifiedTime);

        LookupField sysModifiedBy = FieldFactory.getField("sysModifiedBy", "Modified By", "SYS_MODIFIED_BY", ModuleFactory.getUsersModule(), FieldType.LOOKUP);
        sysModifiedBy.setSpecialType(FacilioConstants.ContextNames.USERS);
        fields.add(sysModifiedBy);

        module.setFields(fields);
        return module;

    }

}
