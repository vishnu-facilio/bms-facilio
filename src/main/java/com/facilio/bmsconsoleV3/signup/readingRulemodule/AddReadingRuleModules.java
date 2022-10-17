package com.facilio.bmsconsoleV3.signup.readingRulemodule;

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
        FacilioModule readingRule=addReadingRuleModule();

        FacilioChain addModuleChain= TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(readingRule));
        addModuleChain.execute();

    }
    private FacilioModule addReadingRuleModule() throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule("newreadingrules",
                "NewReadingRules",
                "New_Reading_Rule",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
        fields.add(nameField);

        fields.add((FacilioField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));

        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY_ID", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);

        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY_ID", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);

        LookupField faultImpact = (LookupField) FieldFactory.getDefaultField("impact", "Impact", "IMPACT_ID", FieldType.LOOKUP);
        faultImpact.setLookupModule(modBean.getModule(FacilioConstants.FaultImpact.MODULE_NAME));
        fields.add(faultImpact);

        NumberField readingModuleId = (NumberField) FieldFactory.getDefaultField("readingModuleId", "Reading Module ID", "READING_MODULE_ID", FieldType.NUMBER);
        fields.add(readingModuleId);

        NumberField readingFieldId = (NumberField) FieldFactory.getDefaultField("readingFieldId", "Reading Field ID", "READING_FIELD_ID", FieldType.NUMBER);
        fields.add(readingFieldId);

        NumberField siteId = (NumberField) FieldFactory.getDefaultField("siteId", "Site ID", "SITE_ID", FieldType.NUMBER);
        fields.add(siteId);

        NumberField alarmType = (NumberField) FieldFactory.getDefaultField("alarmType", "Applied To", "ALARM_APPLIED_TO", FieldType.NUMBER);
        fields.add(alarmType);

        BooleanField status = (BooleanField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);

        BooleanField autoClear = (BooleanField) FieldFactory.getDefaultField("autoClear", "AutoClear", "AUTO_CLEAR", FieldType.BOOLEAN);
        fields.add(autoClear);

        NumberField sysCreatedTime = (NumberField) FieldFactory.getDefaultField("sysCreatedTime", "Created Time", "SYS_CREATED_TIME", FieldType.NUMBER);
        fields.add(sysCreatedTime);

        LookupField sysCreatedBy = (LookupField) FieldFactory.getField("sysCreatedBy", "Created By", "SYS_CREATED_BY", ModuleFactory.getUsersModule(), FieldType.LOOKUP);
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
