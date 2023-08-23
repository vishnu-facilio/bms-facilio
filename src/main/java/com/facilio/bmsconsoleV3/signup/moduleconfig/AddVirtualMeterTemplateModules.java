package com.facilio.bmsconsoleV3.signup.moduleconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;

public class AddVirtualMeterTemplateModules extends SignUpData {

	@Override
	public void addData() throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long orgId = Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId();
        
		FacilioModule virtualMeterTemplateModule = getVirtualMeterTemplateModule();
		SignupUtil.addModules(virtualMeterTemplateModule);
        
        FacilioModule virtualMeterTemplateSites = constructvirtualMeterTemplateSiteModule(modBean, orgId, virtualMeterTemplateModule);
        SignupUtil.addModules(virtualMeterTemplateSites);
        
        addMultilookupFieldIntoTemplateModule("sites",modBean, orgId, virtualMeterTemplateModule, virtualMeterTemplateSites);

        FacilioModule virtualMeterTemplateBuildings = constructvirtualMeterTemplateBuildingModule(modBean, orgId, virtualMeterTemplateModule);
        SignupUtil.addModules(virtualMeterTemplateBuildings);

        addMultilookupFieldIntoTemplateModule("buildings",modBean, orgId, virtualMeterTemplateModule, virtualMeterTemplateBuildings);
        
        FacilioModule virtualMeterReadingModule = constructVirtualMeterReadingModule(modBean, orgId, virtualMeterTemplateModule);
        SignupUtil.addModules(virtualMeterReadingModule);
        
        FacilioModule virtualMeterResourcesModule = constructVirtualMeterResourcesModule(modBean, orgId, virtualMeterTemplateModule);
        SignupUtil.addModules(virtualMeterResourcesModule);
        
        addMultilookupFieldIntoTemplateModule("resources",modBean, orgId, virtualMeterTemplateModule, virtualMeterResourcesModule);
	}
	
	private FacilioModule constructVirtualMeterReadingModule(ModuleBean moduleBean, long orgId, FacilioModule virtualMeterTemplateModule) throws Exception {
		
		
		FacilioModule module = new FacilioModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_READING, "Virtual Meter Template Readings",
                "Virtual_Meter_Template_Readings", FacilioModule.ModuleType.SUB_ENTITY, true);
        module.setOrgId(orgId);

        List<FacilioField> fields = new ArrayList<>();
        
        LookupField virtualMeterTemplate = SignupUtil.getLookupField(module, virtualMeterTemplateModule, "virtualMeterTemplate", "Virtual Meter Template", "VIRTUAL_METER_TEMPLATE_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(virtualMeterTemplate);
        
        NumberField readingFieldId = (NumberField) FieldFactory.getDefaultField("readingFieldId", "Field ID", "FIELD_ID", FieldType.NUMBER);
        fields.add(readingFieldId);
        
        SystemEnumField frequency = (SystemEnumField) FieldFactory.getDefaultField("frequency", "Frequency", "FREQUENCY", FieldType.SYSTEM_ENUM);
        frequency.setEnumName("NamespaceFrequency");
        fields.add(frequency);

        BooleanField status = (BooleanField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);
        
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
	
	
	private FacilioModule constructVirtualMeterResourcesModule(ModuleBean moduleBean, long orgId, FacilioModule virtualMeterTemplateModule) throws Exception {

        FacilioModule module = new FacilioModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_RESOURCE, "Virtual Meter Template Resource",
                "Virtual_Meter_Template_Resource", FacilioModule.ModuleType.SUB_ENTITY);
        module.setOrgId(orgId);

        /**
         * Adding fields plannedMaintenanceSite Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* left Field */
        LookupField leftField = SignupUtil.getLookupField(module, virtualMeterTemplateModule, "left", "Virtual Meter Template", "LEFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        /* right Field */
        LookupField rightField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE),
                "right", "Resource", "RIGHT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);
        module.setFields(fields);

        return module;
    }
	
	private FacilioModule constructvirtualMeterTemplateSiteModule(ModuleBean moduleBean, long orgId, FacilioModule virtualMeterTemplateModule) throws Exception {

        FacilioModule module = new FacilioModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_SITES, "Virtual Meter Template Sites",
                "Virtual_Meter_Template_Sites", FacilioModule.ModuleType.SUB_ENTITY);
        module.setOrgId(orgId);

        /**
         * Adding fields virtualMeterTemplateSites Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* left Field */
        LookupField leftField = SignupUtil.getLookupField(module, virtualMeterTemplateModule, "left", "Virtual Meter Template", "LEFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        /* right Field */
        LookupField rightField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.SITE),
                "right", "Site", "RIGHT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);
        module.setFields(fields);

        return module;
    }

    private FacilioModule constructvirtualMeterTemplateBuildingModule(ModuleBean moduleBean, long orgId, FacilioModule virtualMeterTemplateModule) throws Exception {

        FacilioModule module = new FacilioModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE_BUILDINGS, "Virtual Meter Template Buildings",
                "Virtual_Meter_Template_Buildings", FacilioModule.ModuleType.SUB_ENTITY);
        module.setOrgId(orgId);

        /**
         * Adding fields virtualMeterTemplateBuildings Module
         */
        List<FacilioField> fields = new ArrayList<>();
        /* left Field */
        LookupField leftField = SignupUtil.getLookupField(module, virtualMeterTemplateModule, "left", "Virtual Meter Template", "LEFT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(leftField);

        /* right Field */
        LookupField rightField = SignupUtil.getLookupField(module, moduleBean.getModule(FacilioConstants.ContextNames.BUILDING),
                "right", "Building", "RIGHT_ID", null,
                FacilioField.FieldDisplayType.LOOKUP_SIMPLE, false, false, true, orgId);
        fields.add(rightField);
        module.setFields(fields);

        return module;
    }

	private FacilioModule getVirtualMeterTemplateModule()  throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE,
                "Virtual Meter Template",
                "Virtual_Meter_Template",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();

        FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
        fields.add(nameField);

        fields.add((FacilioField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));

        LookupField utilityType = (LookupField) FieldFactory.getDefaultField("utilityType", "Utility Type", "UTILITY_TYPE_ID", FieldType.LOOKUP);
        utilityType.setLookupModule(modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE));
        fields.add(utilityType);
        
        NumberField readingModuleId = (NumberField) FieldFactory.getDefaultField("readingModuleId", "Reading Module ID", "READING_MODULE_ID", FieldType.NUMBER);
        fields.add(readingModuleId);
        
        SystemEnumField resourceType = (SystemEnumField) FieldFactory.getDefaultField("scope", "Scope", "SCOPE", FieldType.SYSTEM_ENUM);
        resourceType.setEnumName("MultiResourceAssignmentType");
        fields.add(resourceType);

        SystemEnumField vmTemplateStatus = (SystemEnumField) FieldFactory.getDefaultField("vmTemplateStatus", "VM Template Status", "VM_TEMPLATE_STATUS", FieldType.SYSTEM_ENUM);
        vmTemplateStatus.setEnumName("VirtualMeterTemplateStatus");

        fields.add(vmTemplateStatus);

        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);

        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);
        
        BooleanField status = (BooleanField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.BOOLEAN);
        fields.add(status);
        
        FacilioField meterNameField = (FacilioField) FieldFactory.getDefaultField("meterName", "Meter Name", "METER_NAME", FieldType.STRING);
        fields.add(meterNameField);

        fields.add((FacilioField) FieldFactory.getDefaultField("meterDescription", "Meter Description", "METER_DESCRIPTION", FieldType.STRING));

        NumberField relationShipId = (NumberField) FieldFactory.getDefaultField("relationShipId", "Relationship Id", "RELATIONSHIP_ID", FieldType.NUMBER);
        fields.add(relationShipId);

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
	
	
	private void addMultilookupFieldIntoTemplateModule(String name,ModuleBean moduleBean, long orgId, FacilioModule templateModule,FacilioModule site) throws Exception {

		MultiLookupField sitesField = FieldFactory.getDefaultField(name, null, null, FieldType.MULTI_LOOKUP);
		sitesField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
		sitesField.setRequired(true);
		sitesField.setDisabled(false);
		sitesField.setDefault(true);
		sitesField.setMainField(false);
		sitesField.setOrgId(orgId);
		sitesField.setModule(templateModule);
		sitesField.setLookupModuleId(site.getModuleId());
		sitesField.setRelModuleId(site.getModuleId());
		sitesField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
		moduleBean.addField(sitesField);
	}

}
