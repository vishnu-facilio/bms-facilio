package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetAssetDetailCommand extends GenericGetModuleDataDetailCommand {

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
			assetContext.setModuleName(modBean.getModule(assetContext.getModuleId()).getName());
			context.put(FacilioConstants.ContextNames.ASSET, assetContext);
		}
		return false;
	}
	
}
