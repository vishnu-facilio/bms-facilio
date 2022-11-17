package com.facilio.bmsconsoleV3.signup.jobPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.v3.context.Constants;

public class AddJobPlanToolsModule extends BaseModuleConfig {

	
	public AddJobPlanToolsModule(){
		 setModuleName(FacilioConstants.ContextNames.JOB_PLAN_TOOLS);
	 }
	 
	 @Override
	    public void addData() throws Exception {

	    	ModuleBean bean = Constants.getModBean();
	        FacilioModule parentModule = bean.getModule(FacilioConstants.ContextNames.JOB_PLAN);

	        Objects.requireNonNull(parentModule,"JobPlan module doesn't exists.");

	        FacilioModule jobPlanToolsModule = constructJobPlanToolsModule(parentModule,bean);

	        List<FacilioModule> modules = new ArrayList<>();
	        modules.add(jobPlanToolsModule);

	        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
	        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
	        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
	        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,parentModule.getName());
	        addModuleChain.execute();
	        
	        bean.addSubModule(parentModule.getModuleId(), jobPlanToolsModule.getModuleId());
	    }
	    
	    private FacilioModule constructJobPlanToolsModule(FacilioModule jobPlanModule,ModuleBean bean) throws Exception {
	        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.JOB_PLAN_TOOLS, "Job Plan Tools", "Job_Plan_Tools", FacilioModule.ModuleType.SUB_ENTITY);

	        List<FacilioField> fields = new ArrayList<>();

	        LookupField parent = FieldFactory.getDefaultField("jobPlan","Job Plan","JOB_PLAN_ID",FieldType.LOOKUP);
	        parent.setLookupModule(jobPlanModule);
	        fields.add(parent);

	        LookupField tool = FieldFactory.getDefaultField("toolType","Tool Type","TOOL_TYPE",FieldType.LOOKUP,true);
	        tool.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ModuleNames.TOOL_TYPES),"Tool module doesn't exists."));
	        fields.add(tool);
	        
	        fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING));

	        
	        fields.add(FieldFactory.getDefaultField("rate","Rate","RATE",FieldType.DECIMAL));
	        fields.add(FieldFactory.getDefaultField("quantity","Quantity","QUANTITY",FieldType.DECIMAL,FieldDisplayType.DECIMAL));
	        fields.add(FieldFactory.getDefaultField("duration","Duration","DURATION",FieldType.NUMBER, FieldDisplayType.DURATION));
	        fields.add(FieldFactory.getDefaultField("totalCost","Total Cost","TOTAL_COST",FieldType.DECIMAL,FieldDisplayType.DECIMAL));
	        
	        LookupField storeRoom = FieldFactory.getDefaultField("storeRoom","Storeroom","STOREROOM",FieldType.LOOKUP);
	        storeRoom.setLookupModule(Objects.requireNonNull(bean.getModule(FacilioConstants.ContextNames.STORE_ROOM),"Store room module doesn't exists."));
	        fields.add(storeRoom);

	        module.setFields(fields);

	        return module;
	    }
}
