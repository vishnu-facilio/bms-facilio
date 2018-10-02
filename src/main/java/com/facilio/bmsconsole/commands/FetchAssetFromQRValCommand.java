package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class FetchAssetFromQRValCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String qrValue = (String) context.get(FacilioConstants.ContextNames.QR_VALUE);
		if (qrValue != null && !qrValue.isEmpty()) {
			String id = (String) context.remove(FacilioConstants.ContextNames.ID);
			AssetContext asset = null;
			if (id != null && !id.isEmpty()) {
				asset = AssetsAPI.getAssetInfo(Long.parseLong(id));
			}
			else {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField qrValField = modBean.getField("qrVal", FacilioConstants.ContextNames.ASSET);
				SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET))
																		.moduleName(FacilioConstants.ContextNames.ASSET)
																		.beanClass(AssetContext.class)
																		.andCondition(CriteriaAPI.getCondition(qrValField, qrValue, StringOperators.IS))
																		;
				
				List<AssetContext> assets = selectBuilder.get();
				if (assets != null && !assets.isEmpty()) {
					asset = assets.get(0);
				}
			}
			
			if (asset != null) {
				if (asset.getCategory() != null && asset.getCategory().getId() > 0) {
					context.put(FacilioConstants.ContextNames.ID, asset.getId());
					context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, asset.getCategory().getId());
				}
				else {
					context.put(FacilioConstants.ContextNames.ASSET, asset);
				}
			}
		}
		return false;
	}

}
