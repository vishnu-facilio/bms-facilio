package com.facilio.bmsconsoleV3.signup.Point;

import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddPointModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        FacilioModule module = getPointModule();
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();
    }
    private FacilioModule getPointModule() throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = new FacilioModule(AgentConstants.POINT,
                "Point",
                "Point",
                FacilioModule.ModuleType.BASE_ENTITY);

        List<FacilioField> fields = new ArrayList<>();

        StringField name =  FieldFactory.getDefaultField(AgentConstants.NAME, "Link Name", "Name", module, FieldType.STRING);
        name.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(name);

        StringField description =  FieldFactory.getDefaultField(AgentConstants.DESCRIPTION, "Description", "DESCRIPTION", module, FieldType.STRING);
        description.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(description);

        NumberField dataType = FieldFactory.getDefaultField(AgentConstants.DATA_TYPE, "Data Type", "DATA_TYPE", module, FieldType.NUMBER);
        dataType.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(dataType);

        NumberField pointType = FieldFactory.getDefaultField(AgentConstants.POINT_TYPE, "Point Type", "POINT_TYPE", module, FieldType.NUMBER);
        pointType.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(pointType);

        StringField deviceName = FieldFactory.getDefaultField(AgentConstants.DEVICE_NAME, "Device Name", "DEVICE_NAME", module, FieldType.STRING);
        deviceName.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(deviceName);

        BooleanField logical = FieldFactory.getDefaultField(AgentConstants.LOGICAL, "Logical", "LOGICAL", module, FieldType.BOOLEAN);
        logical.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(logical);

        NumberField controllerId = FieldFactory.getDefaultField(AgentConstants.CONTROLLER_ID, "Controller ID", "CONTROLLER_ID", module, FieldType.NUMBER);
        controllerId.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(controllerId);

        LookupField assetCategoryId = (LookupField) FieldFactory.getDefaultField(AgentConstants.ASSET_CATEGORY_ID, "Category", "ASSET_CATEGORY_ID", module, FieldType.LOOKUP);
        assetCategoryId.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        assetCategoryId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        assetCategoryId.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(assetCategoryId);

        LookupField resourceId = (LookupField) FieldFactory.getDefaultField(AgentConstants.RESOURCE_ID, "Asset", "RESOURCE_ID", module, FieldType.LOOKUP);
        resourceId.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        resourceId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        resourceId.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(resourceId);

        FacilioField fieldId = FieldFactory.getPointFieldIdField(module);
        fieldId.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(fieldId);

        BooleanField writable =  FieldFactory.getDefaultField(AgentConstants.WRITABLE, "Writable", "WRITABLE", module, FieldType.BOOLEAN);
        writable.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(writable);

        BooleanField agentWritable = FieldFactory.getDefaultField(AgentConstants.AGENT_WRITABLE, "Agent Writable", "AGENT_WRITABLE", module, FieldType.BOOLEAN);
        agentWritable.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(agentWritable);

        StringField thresholdJSON = FieldFactory.getDefaultField(AgentConstants.THRESHOLD_JSON, "Threshold JSON", "THRESHOLD_JSON", module, FieldType.STRING);
        thresholdJSON.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(thresholdJSON);

        DateField createdTIme = FieldFactory.getDefaultField(AgentConstants.CREATED_TIME, "Created Time", "CREATED_TIME", module, FieldType.DATE_TIME);
        createdTIme.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(createdTIme);

        DateField mappedTime = FieldFactory.getDefaultField(AgentConstants.MAPPED_TIME, "Mapped Time", "MAPPED_TIME", module, FieldType.DATE_TIME);
        mappedTime.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(mappedTime);

        DateField lastRecordedTime = FieldFactory.getDefaultField(AgentConstants.LAST_RECORDED_TIME, "Last Recorded Time", "LAST_RECORDED_TIME", module, FieldType.DATE_TIME);
        lastRecordedTime.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(lastRecordedTime);

        StringField lastRecordedValue = FieldFactory.getDefaultField(AgentConstants.LAST_RECORDED_VALUE, "Last Recorded Value", "LAST_RECORDED_VALUE", module, FieldType.STRING);
        lastRecordedValue.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(lastRecordedValue);

        NumberField unit = FieldFactory.getDefaultField(AgentConstants.UNIT, "Unit", "UNIT", module, FieldType.NUMBER);
        unit.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(unit);

        NumberField agentId = FieldFactory.getDefaultField(AgentConstants.AGENT_ID, "Agent ID", "AGENT_ID", module, FieldType.NUMBER);
        agentId.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(agentId);

        NumberField dataInterval= FieldFactory.getDefaultField(AgentConstants.DATA_INTERVAL, "Data Interval", "DATA_INTERVAL", module, FieldType.NUMBER);
        dataInterval.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(dataInterval);

        BooleanField dataMissing = FieldFactory.getDefaultField(AgentConstants.DATA_MISSING, "Data Missing", "DATA_MISSING", module, FieldType.BOOLEAN);
        dataMissing.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(dataMissing);

        SystemEnumField configureStatusfield = (SystemEnumField) FieldFactory.getDefaultField(AgentConstants.CONFIGURE_STATUS, "Configure Status", "CONFIGURE_STATUS", module, FieldType.SYSTEM_ENUM);
        configureStatusfield.setEnumName("ConfigureStatus");
        configureStatusfield.setValues(FacilioEnum.getEnumValues(configureStatusfield.getEnumName()));
        configureStatusfield.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(configureStatusfield);

        SystemEnumField subscribeStatusfield = (SystemEnumField) FieldFactory.getDefaultField(AgentConstants.SUBSCRIBE_STATUS, "Subscribe Status", "SUBSCRIBE_STATUS", module, FieldType.SYSTEM_ENUM);
        subscribeStatusfield.setEnumName("SubscribeStatus");
        subscribeStatusfield.setValues(FacilioEnum.getEnumValues(subscribeStatusfield.getEnumName()));
        subscribeStatusfield.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(subscribeStatusfield);

        SystemEnumField mappedTypeField = FieldFactory.getDefaultField(AgentConstants.MAPPED_TYPE, "Mapped Type", "MAPPED_TYPE", module, FieldType.SYSTEM_ENUM);
        mappedTypeField.setEnumName("MappedType");
        mappedTypeField.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(mappedTypeField);

        FacilioField displayName = FieldFactory.getDefaultField(AgentConstants.DISPLAY_NAME, "Name", "DISPLAY_NAME", module, FieldType.STRING);
        displayName.setMainField(true);
        displayName.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(displayName);

        module.setFields(fields);
        return module;
    }
}
