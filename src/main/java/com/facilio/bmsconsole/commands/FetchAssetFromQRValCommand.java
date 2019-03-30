package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FetchAssetFromQRValCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String qrValue = (String) context.get(FacilioConstants.ContextNames.QR_VALUE);
		if (qrValue != null && !qrValue.isEmpty()) {
			String id = (String) context.remove(FacilioConstants.ContextNames.ID);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField qrValField = modBean.getField("qrVal", FacilioConstants.ContextNames.ASSET);
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
//			LookupFieldMeta fetchLookupMeta = new LookupFieldMeta((LookupField) modBean.getField("identifiedLocation", FacilioConstants.ContextNames.ASSET));
//			fetchLookupMeta.addChildLookupFIeld((LookupField) modBean.getField("location", FacilioConstants.ContextNames.SITE));
			
			SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
																	.select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET))
																	.module(module)
																	.beanClass(AssetContext.class)
//																	.fetchLookup(fetchLookupMeta)
																	;
			
			if (StringUtils.isNotEmpty(id)) {
				selectBuilder.andCondition(CriteriaAPI.getIdCondition(id, module));
			}
			else {
				selectBuilder.andCondition(CriteriaAPI.getCondition(qrValField, qrValue, StringOperators.IS));
			}
			
			List<AssetContext> assets = selectBuilder.get();
			if (assets != null && !assets.isEmpty()) {
				AssetContext asset = assets.get(0);
				
				if (asset.getCategory() != null && asset.getCategory().getId() > 0) {
					context.put(FacilioConstants.ContextNames.ID, asset.getId());
					context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, asset.getCategory().getId());
//					context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, Collections.singletonList(fetchLookupMeta));
				}
				else {
					context.put(FacilioConstants.ContextNames.ASSET, asset);
				}
			}
		}
		return false;
	}

}
