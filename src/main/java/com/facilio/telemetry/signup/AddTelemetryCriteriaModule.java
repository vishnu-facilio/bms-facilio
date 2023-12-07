package com.facilio.telemetry.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;

public class AddTelemetryCriteriaModule extends SignUpData {
    public static final String MODULE_NAME = "telemetryCriteria";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Telemetry Criteria");
        module.setDescription("Alarms");
        module.setTableName("Telemetry_Criteria");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.READING_RULE);
        module.setTrashEnabled(true);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);


        StringField name = new StringField();
        name.setName("name");
        name.setModule(mod);
        name.setDisplayName("Name");
        name.setColumnName("NAME");
        name.setDataType(FieldType.STRING);
        name.setDisplayType(FacilioField.FieldDisplayType.TEXTBOX);
        name.setDefault(true);
        name.setMainField(true);
        modBean.addField(name);

        StringField description = new StringField();
        description.setName("description");
        description.setModule(mod);
        description.setDisplayName("Description");
        description.setColumnName("DESCRIPTION");
        description.setDataType(FieldType.BIG_STRING);
        description.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        description.setDefault(true);
        description.setMainField(true);
        modBean.addField(description);

        LookupField assetCategory = new LookupField();
        assetCategory.setDefault(true);
        assetCategory.setName("assetCategory");
        assetCategory.setDisplayName("Asset Category");
        assetCategory.setModule(mod);
        assetCategory.setDataType(FieldType.LOOKUP);
        assetCategory.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        assetCategory.setColumnName("ASSET_CATEGORY");
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        modBean.addField(assetCategory);

        NumberField namespaceId = new NumberField();
        namespaceId.setDefault(true);
        namespaceId.setName("namespaceId");
        namespaceId.setDisplayName("Namespace Id");
        namespaceId.setModule(mod);
        namespaceId.setDataType(FieldType.NUMBER);
        namespaceId.setColumnName("NAMESPACE_ID");
        modBean.addField(namespaceId);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));

    }
}