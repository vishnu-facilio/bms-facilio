package com.facilio.alarms.sensor.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddSensorRuleModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        try {
            FacilioModule sensorRule = addSensorRuleModule();
            FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(sensorRule));
            addModuleChain.execute();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private FacilioModule addSensorRuleModule() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule("sensorrule",
                "SensorRule",
                "SensorRule",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField assetCategory = FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY_ID", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);

        SystemEnumField resourceType = FieldFactory.getDefaultField("resourceType", "Resource Type", "RESOURCE_TYPE", FieldType.SYSTEM_ENUM);
        resourceType.setEnumName("ConnectedResourceAssignmentType");
        fields.add(resourceType);

        NumberField categoryId = FieldFactory.getDefaultField("categoryId", "Category ID", "CATEGORY_ID", FieldType.NUMBER);
        fields.add(categoryId);

        NumberField sensorModuleId = FieldFactory.getDefaultField("sensorModuleId", "Sensor Module ID", "MODULE_ID", FieldType.NUMBER);
        fields.add(sensorModuleId);

        NumberField sensorFieldId = FieldFactory.getDefaultField("sensorFieldId", "Sensor Field ID", "READING_FIELD_ID", FieldType.NUMBER);
        fields.add(sensorFieldId);

        NumberField readingModuleId = FieldFactory.getDefaultField("recordModuleId", "Module ID", "RECORD_MODULE_ID", FieldType.NUMBER);
        fields.add(readingModuleId);

        NumberField readingFieldId = FieldFactory.getDefaultField("recordFieldId", "Field ID", "RECORD_FIELD_ID", FieldType.NUMBER);
        fields.add(readingFieldId);


        BooleanField status = FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);

        fields.add(FieldFactory.getSystemField("sysCreatedTime",module));
        fields.add(FieldFactory.getSystemField("sysCreatedBy",module));
        fields.add(FieldFactory.getSystemField("sysModifiedTime",module));
        fields.add(FieldFactory.getSystemField("sysModifiedBy",module));

        module.setFields(fields);
        return module;

    }

}
