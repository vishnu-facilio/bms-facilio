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

public class AddMultiResourceModule extends SignUpData {
	@Override
	public void addData() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	    
	    List<FacilioModule> modules = new ArrayList<>();
	    
	    FacilioModule multiResourceModule = constructMultiResourceModule();
	    modules.add(multiResourceModule);
        
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
		addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
	}
	
	public FacilioModule constructMultiResourceModule() throws Exception{
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean",AccountUtil.getCurrentOrg().getOrgId());

		FacilioModule module = new FacilioModule(FacilioConstants.MultiResource.NAME,
				FacilioConstants.MultiResource.DISPLAY_NAME,
				FacilioConstants.MultiResource.TABLE_NAME,
                FacilioModule.ModuleType.BASE_ENTITY
                );

		List<FacilioField> fields = new ArrayList<>();
		
		NumberField siteField = (NumberField) FieldFactory.getDefaultField("siteId", "Site", "SITE_ID", FieldType.NUMBER);
        fields.add(siteField);
				
		LookupField asset = FieldFactory.getDefaultField("asset","Asset","ASSET_ID",FieldType.LOOKUP);
		asset.setLookupModule(bean.getModule(FacilioConstants.ContextNames.ASSET));
		
		fields.add(asset);
		
		LookupField space = FieldFactory.getDefaultField("space","Space","SPACE_ID",FieldType.LOOKUP);
		space.setLookupModule(bean.getModule(FacilioConstants.ContextNames.BASE_SPACE));
		
		fields.add(space);	
		
		NumberField parentModuleIdField = (NumberField) FieldFactory.getDefaultField("parentModuleId", "Parent_Module_Id", "PARENT_MODULE_ID", FieldType.NUMBER,true);
        fields.add(parentModuleIdField);
        
        NumberField parentRecordIdField = (NumberField) FieldFactory.getDefaultField("parentRecordId", "Parent_Record_Id", "PARENT_RECORD_ID", FieldType.NUMBER);
        fields.add(parentRecordIdField);
		
		fields.add(FieldFactory.getDefaultField("description", "Description", "DESCRIPTION", FieldType.BIG_STRING, FacilioField.FieldDisplayType.TEXTAREA));
		
		fields.add(FieldFactory.getDefaultField("sequence", "Sequence", "SEQUENCE", FieldType.NUMBER));
		
		module.setFields(fields);
		return module;
	}
}
