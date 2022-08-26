package com.facilio.bmsconsoleV3.signup.moduleconfig;

import java.util.ArrayList;
import java.util.List;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;

public class WorkOrderMultiResourceModule extends SignUpData {
	@Override
	public void addData() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	    
	    List<FacilioModule> modules = new ArrayList<>();
	    
	    FacilioModule workOrderMultiAssetModule = constructWorkOrderMultiResourceModule();
	    modules.add(workOrderMultiAssetModule);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
		addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
	}
	
	public FacilioModule constructWorkOrderMultiResourceModule() throws Exception{
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean",AccountUtil.getCurrentOrg().getOrgId());

		FacilioModule module = new FacilioModule(FacilioConstants.WorkOrderMultiResource.NAME,
				FacilioConstants.WorkOrderMultiResource.DISPLAY_NAME,
				FacilioConstants.WorkOrderMultiResource.TABLE_NAME,
                FacilioModule.ModuleType.BASE_ENTITY
                );

		List<FacilioField> fields = new ArrayList<>();
		
		NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);
				
		LookupField asset = FieldFactory.getDefaultField("asset","Asset","ASSET_ID",FieldType.LOOKUP);
		asset.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET));
		
		fields.add(asset);
		
		LookupField space = FieldFactory.getDefaultField("space","Space","SPACE_ID",FieldType.LOOKUP);
		space.setLookupModule(bean.getModule(FacilioConstants.ContextNames.SPACE));
		
		fields.add(space);
		
		LookupField workorderParent = FieldFactory.getDefaultField("parent","Parent Id","PARENT_ID",FieldType.LOOKUP);
		workorderParent.setLookupModule(bean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
		fields.add(workorderParent);	
		
		fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.STRING, FacilioField.FieldDisplayType.TEXTAREA));
		
		fields.add(FieldFactory.getDefaultField("sequence", "Sequence", "SEQUENCE", FieldType.NUMBER));
		
		module.setFields(fields);
		return module;
	}
}
