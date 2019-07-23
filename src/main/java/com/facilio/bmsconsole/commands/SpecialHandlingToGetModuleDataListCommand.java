package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class SpecialHandlingToGetModuleDataListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<AssetCategoryContext> records = (List<AssetCategoryContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(moduleName.equals("assetcategory") && records != null && !records.isEmpty()) {
			for(AssetCategoryContext record:records) {
				record.setModuleName(modBean.getModule(record.getAssetModuleID()).getName());
			}
		}
		return false;
	}

}
