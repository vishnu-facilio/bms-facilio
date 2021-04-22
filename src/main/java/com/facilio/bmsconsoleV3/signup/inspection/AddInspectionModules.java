package com.facilio.bmsconsoleV3.signup.inspection;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.ArrayList;
import java.util.List;

public class AddInspectionModules extends SignUpData {
    @Override
    public void addData() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        List<FacilioModule> modules = new ArrayList<>();
        
        modules.add(constructInspectionCategory());
        modules.add(constructInspectionPriority());
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        
        
        modules = new ArrayList<>();
        
        FacilioModule inspection = constructInspection(modBean);
        modules.add(inspection);
        modules.add(constructInspectionResponse(modBean, inspection));
        
        
        addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.execute();
        
        List<FacilioModule> modules1 = new ArrayList<>();
        
        modules1.add(constructInspectionTriggers(modBean, inspection));
        
        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();
        
        modules1 = new ArrayList<>();
        
        modules1.add(constructInspectionTriggersInclExcl(modBean));
        
        addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules1);
        addModuleChain1.execute();
    }


	public FacilioModule constructInspectionTriggersInclExcl(ModuleBean modBean) throws Exception {

		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL,
                "Inspection Triggers Incl Excl",
                "Inspection_Trigger_Include_Exclude_Resource",
                FacilioModule.ModuleType.SUB_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();
        
        LookupField template = (LookupField) FieldFactory.getDefaultField("inspectionTemplate", "Inspection Template", "INSPECTION_ID", FieldType.LOOKUP);
        template.setLookupModule(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE));
        fields.add(template);
        
        LookupField trigger = (LookupField) FieldFactory.getDefaultField("inspectionTrigger", "Inspection Trigger", "TRIGGER_ID", FieldType.LOOKUP);
        trigger.setLookupModule(modBean.getModule(FacilioConstants.Inspection.INSPECTION_TRIGGER));
        fields.add(trigger);
        
        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Resource", "RESOURCE_ID", FieldType.LOOKUP);
        resource.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(resource);
        
        BooleanField isIncl = (BooleanField) FieldFactory.getDefaultField("isInclude", "Is Include", "IS_INCLUDE", FieldType.BOOLEAN);
        fields.add(isIncl);
        
        module.setFields(fields);
        
        return module;
	}


	public FacilioModule constructInspectionPriority() {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_PRIORITY,
                "Inspection Priority",
                "Inspection_Priority",
                FacilioModule.ModuleType.BASE_ENTITY
                );

		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("priority", "Priority", "PRIORITY", FieldType.STRING, true);
		fields.add(nameField);
		
		fields.add((FacilioField) FieldFactory.getDefaultField("displayName", "Display Name", "DISPLAY_NAME", FieldType.STRING));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("colour", "Colour", "COLOUR", FieldType.STRING));
		
		fields.add((NumberField) FieldFactory.getDefaultField("sequenceNumber", "Sequence", "SEQUENCE_NUMBER", FieldType.NUMBER));
		
		fields.add((BooleanField) FieldFactory.getDefaultField("isDefault", "Is Default", "ISDEFAULT", FieldType.BOOLEAN));
		
		module.setFields(fields);
		return module;
	}


	public FacilioModule constructInspectionCategory() throws Exception {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_CATEGORY,
                "Inspection Category",
                "Inspection_Category",
                FacilioModule.ModuleType.BASE_ENTITY
                );

		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
		fields.add(nameField);
		
		fields.add((FacilioField) FieldFactory.getDefaultField("displayName", "Display Name", "DISPLAY_NAME", FieldType.STRING));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));
		
		module.setFields(fields);
		return module;
	}


	private FacilioModule constructInspection(ModuleBean modBean) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TEMPLATE,
                                                "Inspection Templates",
                                                "Inspection_Templates",
                                                FacilioModule.ModuleType.Q_AND_A,
                                                modBean.getModule(FacilioConstants.QAndA.Q_AND_A_TEMPLATE)
                                                );

        List<FacilioField> fields = new ArrayList<>();
        
        LookupField siteField = (LookupField) FieldFactory.getDefaultField("site", "Site", "SITE_ID", FieldType.LOOKUP);
        siteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        fields.add(siteField);
        
        SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("creationType", "Creation Type", "CREATION_TYPE", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("InspectionCreationType");
        
        fields.add(creationType);
        
        SystemEnumField assignmentType = (SystemEnumField) FieldFactory.getDefaultField("assignmentType", "Assigment Type", "ASSIGNMENT_TYPE", FieldType.SYSTEM_ENUM);
        assignmentType.setEnumName("MultiResourceAssignmentType");
        
        fields.add(assignmentType);
        
        LookupField baseSpace = (LookupField) FieldFactory.getDefaultField("baseSpace", "Base Space", "BASE_SPACE", FieldType.LOOKUP);
        baseSpace.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE));
        fields.add(baseSpace);
        
        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);
        
        fields.addAll(getInspectionCommonFieldList(modBean));
        
        module.setFields(fields);
        return module;
    }
	
	private FacilioModule constructInspectionTriggers(ModuleBean modBean, FacilioModule inspection) throws Exception {
		
		FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_TRIGGER,
                "Inspection Triggers",
                "Inspection_Triggers",
                FacilioModule.ModuleType.SUB_ENTITY,
                true
        );

        List<FacilioField> fields = new ArrayList<>();
        
        FacilioField nameField = FieldFactory.getNameField(module);
        fields.add(nameField);
        
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(inspection);
        fields.add(parentField);
        
        NumberField type = (NumberField) FieldFactory.getDefaultField("type", "Trigger Type", "TRIGGER_TYPE", FieldType.NUMBER);
        fields.add(type);
        
        NumberField scheduleID = (NumberField) FieldFactory.getDefaultField("scheduleId", "Schedule", "SCHEDULE_ID", FieldType.NUMBER);
        fields.add(scheduleID);

        module.setFields(fields);
        return module;
	}

    private FacilioModule constructInspectionResponse (ModuleBean modBean, FacilioModule inspection) throws Exception {
        FacilioModule module = new FacilioModule(FacilioConstants.Inspection.INSPECTION_RESPONSE,
                "Inspections",
                "Inspection_Responses",
                FacilioModule.ModuleType.Q_AND_A_RESPONSE,
                modBean.getModule(FacilioConstants.QAndA.RESPONSE)
        );

        List<FacilioField> fields = new ArrayList<>();
        LookupField siteField = (LookupField) FieldFactory.getDefaultField("site", "Site", "SITE_ID", FieldType.LOOKUP);
        siteField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SITE));
        fields.add(siteField);
        LookupField parentField = (LookupField) FieldFactory.getDefaultField("parent", "Parent", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(inspection);
        fields.add(parentField);
        
        FacilioField createdTime = (FacilioField) FieldFactory.getDefaultField("createdTime", "Created Time", "CREATED_TIME", FieldType.DATE_TIME);
        fields.add(createdTime);
        
        SystemEnumField status = (SystemEnumField) FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.SYSTEM_ENUM);
        status.setEnumName("InspectionResponseStatus");
        fields.add(status);
        
        SystemEnumField sourceType = (SystemEnumField) FieldFactory.getDefaultField("sourceType", "Source", "SOURCE_TYPE", FieldType.SYSTEM_ENUM);
        sourceType.setEnumName("InspectionResponseSourceType");
        fields.add(sourceType);
        
        fields.addAll(getInspectionCommonFieldList(modBean));

        module.setFields(fields);
        return module;
    }

    public List<FacilioField> getInspectionCommonFieldList(ModuleBean modBean) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		
        LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Resource", "RESOURCE", FieldType.LOOKUP);
        resource.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(resource);
        
        LookupField vendor = (LookupField) FieldFactory.getDefaultField("vendor", "Vendor", "VENDOR", FieldType.LOOKUP);
        vendor.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
        fields.add(vendor);
        
        LookupField tenant = (LookupField) FieldFactory.getDefaultField("tenant", "Tenant", "TENANT", FieldType.LOOKUP);
        tenant.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.TENANT));
        fields.add(tenant);
        
        LookupField category = (LookupField) FieldFactory.getDefaultField("category", "Category", "CATEGORY", FieldType.LOOKUP);
        category.setLookupModule(modBean.getModule(FacilioConstants.Inspection.INSPECTION_CATEGORY));
        fields.add(category);
        
        LookupField priority = (LookupField) FieldFactory.getDefaultField("priority", "Priority", "PRIORITY", FieldType.LOOKUP);
        priority.setLookupModule(modBean.getModule(FacilioConstants.Inspection.INSPECTION_PRIORITY));
        fields.add(priority);
        
        LookupField assignedTo = (LookupField) FieldFactory.getDefaultField("assignedTo", "Assigned To", "ASSIGNED_TO", FieldType.LOOKUP);
        assignedTo.setSpecialType("users");
        fields.add(assignedTo);
        
        LookupField assignmentGroup = (LookupField) FieldFactory.getDefaultField("assignmentGroup", "Assignment Group", "ASSIGNMENT_GROUP", FieldType.LOOKUP);
        assignmentGroup.setSpecialType("groups");
        fields.add(assignmentGroup);
        
        return fields;
	}
}
