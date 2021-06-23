package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class ModuleBasedSpecificCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		if (module != null && 
				(module.getName() == "asset" || (module.getExtendModule() != null && module.getExtendModule().getName().equals(FacilioConstants.ContextNames.ASSET)))) {
			AssetContext record = (AssetContext) context
					.get(FacilioConstants.ContextNames.RECORD);
			if (record != null) {
			AssetCategoryContext assetCategory= record.getCategory();
			long categoryId=-1;
			if(assetCategory!=null && assetCategory.getId() != 0) {
				categoryId=assetCategory.getId();
			}
			context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, categoryId);
			
			context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
			FacilioChain moduleBasedChain = TransactionChainFactory.insertReadingDataMetaForBulkResourceDuplication();
			moduleBasedChain.execute(context);
			}
		}
		return false;
	}

}
