package com.facilio.readingrule.signup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

public class AddFaultImpactModules extends SignUpData {

	@Override
	public void addData() throws Exception {
		// TODO Auto-generated method stub
		
        FacilioModule faultImpactModule = addFaultImpactModule();
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(faultImpactModule));
        addModuleChain.execute();
        
        addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(addFaultImpactFieldsModule(faultImpactModule)));
        addModuleChain.execute();
	}
	
	private FacilioModule addFaultImpactFieldsModule(FacilioModule faultImpactModule) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		// TODO Auto-generated method stub
		FacilioModule module = new FacilioModule(FacilioConstants.FaultImpact.FAULT_IMPACT_FIELDS_MODULE_NAME,
                "Fault Impact Namespace Fields",
                "Fault_Impact_Namespace_Fields",
                FacilioModule.ModuleType.SUB_ENTITY);

		List<FacilioField> fields = new ArrayList<>();
		
		LookupField resource = (LookupField) FieldFactory.getDefaultField("resource", "Resource", "RESOURCE_ID", FieldType.LOOKUP);
		resource.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        fields.add(resource);
        
        NumberField impact = (NumberField) FieldFactory.getDefaultField("impact", "Impact", "IMPACT_ID", FieldType.NUMBER);
        fields.add(impact);
        
        NumberField fieldId = (NumberField) FieldFactory.getDefaultField("fieldId", "Field Id", "FIELD_ID", FieldType.NUMBER);
		fields.add(fieldId);

		NumberField dataInterval = (NumberField) FieldFactory.getDefaultField("dataInterval", "Data Interval", "DATA_INTERVAL", FieldType.NUMBER);
		fields.add(dataInterval);
		
		FacilioField varName = (FacilioField) FieldFactory.getDefaultField("varName", "Var Name", "VAR_NAME", FieldType.STRING, true);
		fields.add(varName);
		
		BooleanField enabledCompaction = (BooleanField) FieldFactory.getDefaultField("enabledCompaction", "Enable Compaction", "ENABLED_COMPACTION", FieldType.BOOLEAN);
        fields.add(enabledCompaction);
		
		SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("aggregationTypeEnum", "Aggregation Type", "AGGREGATION_TYPE", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("AggregationType");
		fields.add(creationType);

		SystemEnumField nsFieldType = (SystemEnumField) FieldFactory.getDefaultField("nsFieldTypeEnum", "NS Field Type", "NS_FIELD_TYPE", FieldType.SYSTEM_ENUM);
        nsFieldType.setEnumName("NsFieldType");
		fields.add(nsFieldType);
		
		module.setFields(fields);
		return module;
	}

	private FacilioModule addFaultImpactModule() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		// TODO Auto-generated method stub
		FacilioModule module = new FacilioModule(FacilioConstants.FaultImpact.MODULE_NAME,
                "Fault Impact",
                "Fault_Impact",
                FacilioModule.ModuleType.BASE_ENTITY,
                true
                );

		List<FacilioField> fields = new ArrayList<>();
		
		FacilioField nameField = (FacilioField) FieldFactory.getDefaultField("name", "Name", "NAME", FieldType.STRING, true);
		fields.add(nameField);
		
		fields.add((FacilioField) FieldFactory.getDefaultField("linkName", "Link Name", "LINK_NAME", FieldType.STRING));
		
		fields.add((FacilioField) FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));
		
		SystemEnumField creationType = (SystemEnumField) FieldFactory.getDefaultField("type", "Scope", "TYPE", FieldType.SYSTEM_ENUM);
        creationType.setEnumName("MultiResourceAssignmentType");
        
        fields.add(creationType);
        
        LookupField assetCategory = (LookupField) FieldFactory.getDefaultField("assetCategory", "Asset Category", "ASSET_CATEGORY", FieldType.LOOKUP);
        assetCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ASSET_CATEGORY));
        fields.add(assetCategory);
        
        LookupField spaceCategory = (LookupField) FieldFactory.getDefaultField("spaceCategory", "Space Category", "SPACE_CATEGORY", FieldType.LOOKUP);
        spaceCategory.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY));
        fields.add(spaceCategory);
        
        NumberField workflowId = (NumberField) FieldFactory.getDefaultField("workflowId", "Workflow Id", "WORKFLOW", FieldType.NUMBER);
		fields.add(workflowId);
        
		module.setFields(fields);
		return module;
	}

}
