package com.facilio.remotemonitoring.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.StringField;
import com.facilio.remotemonitoring.RemoteMonitorConstants;

public class AlarmAssetTaggingModule extends SignUpData {

    public static final String MODULE_NAME = "alarmAssetMapping";
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = new FacilioModule();
        module.setName(MODULE_NAME);
        module.setDisplayName("Alarm Asset Mapping");
        module.setDescription("Alarm Asset Mapping");
        module.setTableName("Alarm_Asset_Tagging");
        module.setCustom(false);
        module.setType(FacilioModule.ModuleType.BASE_ENTITY);
        module.setTrashEnabled(true);
        modBean.addModule(module);

        FacilioModule mod = modBean.getModule(MODULE_NAME);

        LookupField clientField = new LookupField();
        clientField.setDefault(true);
        clientField.setName("client");
        clientField.setDisplayName("Client");
        clientField.setModule(mod);
        clientField.setDataType(FieldType.LOOKUP);
        clientField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        clientField.setColumnName("CLIENT_ID");
        clientField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CLIENT));
        modBean.addField(clientField);

        LookupField alarmDefinition = new LookupField();
        alarmDefinition.setDefault(true);
        alarmDefinition.setName("alarmDefinition");
        alarmDefinition.setDisplayName("Alarm Definition");
        alarmDefinition.setModule(mod);
        alarmDefinition.setDataType(FieldType.LOOKUP);
        alarmDefinition.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        alarmDefinition.setColumnName("ALARM_DEFINITION_ID");
        alarmDefinition.setLookupModule(modBean.getModule(AlarmDefinitionModule.MODULE_NAME));
        alarmDefinition.setMainField(true);
        modBean.addField(alarmDefinition);

        LookupField controller = new LookupField();
        controller.setDefault(true);
        controller.setName("controller");
        controller.setDisplayName("Controller");
        controller.setModule(mod);
        controller.setDataType(FieldType.LOOKUP);
        controller.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        controller.setColumnName("CONTROLLER_ID");
        controller.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.CONTROLLER));
        modBean.addField(controller);

        LookupField asset = new LookupField();
        asset.setDefault(true);
        asset.setName("asset");
        asset.setDisplayName("Asset");
        asset.setModule(mod);
        asset.setDataType(FieldType.LOOKUP);
        asset.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        asset.setColumnName("ASSET_ID");
        asset.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET));
        modBean.addField(asset);

        modBean.addField(FieldFactory.getSystemField("sysCreatedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysCreatedByPeople", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedTime", mod));
        modBean.addField(FieldFactory.getSystemField("sysModifiedByPeople", mod));
    }
}