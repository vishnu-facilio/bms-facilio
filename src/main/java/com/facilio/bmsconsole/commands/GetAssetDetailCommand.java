package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;

public class GetAssetDetailCommand extends GenericGetModuleDataDetailCommand {
	
	private static final Logger LOGGER = LogManager.getLogger(GetAssetDetailCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(moduleName);
			if (beanClassName == null) {
				beanClassName = AssetContext.class;
			}
			super.execute(context);
			AssetContext assetContext = (AssetContext) context.get(FacilioConstants.ContextNames.RECORD);
			if (assetContext != null && assetContext.getId() > 0) {
				AssetsAPI.loadAssetsLookups(Collections.singletonList(assetContext));
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (assetContext.getModuleId() != -1) {
				FacilioModule module = modBean.getModule(assetContext.getModuleId());
				if (module != null) {
					assetContext.setModuleName(module.getName());
				}
				else {
					assetContext.setModuleName(FacilioConstants.ContextNames.ASSET);
					LOGGER.info("No module for asset" + assetContext.getId() + ", moduleId:" + assetContext.getModuleId());
				}
			}
			else {
				assetContext.setModuleName(FacilioConstants.ContextNames.ASSET);
				LOGGER.info("No module ID for asset" + assetContext.getId());
			}
			context.put(FacilioConstants.ContextNames.ASSET, assetContext);
		}
		return false;
	}
	
}
