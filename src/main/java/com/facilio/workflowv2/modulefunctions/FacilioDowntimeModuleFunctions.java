package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class FacilioDowntimeModuleFunctions extends FacilioModuleFunctionImpl {
	
	@Override
	public void add(Map<String, Object> globalParams, List<Object> objects) throws Exception {
		Map<String, Object> downtimeObj = (Map<String, Object>) objects.get(1);
		
		AssetBDSourceDetailsContext breakdownContext =  FieldUtil.getAsBeanFromMap(downtimeObj, AssetBDSourceDetailsContext.class);
		
		FacilioChain chain = TransactionChainFactory.getAddAssetDowntimeChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.ASSET_ACTIVITY);
		context.put(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS, breakdownContext);
		chain.execute();
		
	}

}
