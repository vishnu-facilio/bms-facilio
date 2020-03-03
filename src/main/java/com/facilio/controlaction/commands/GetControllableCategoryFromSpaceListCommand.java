package com.facilio.controlaction.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControllableAssetCategoryContext;
import com.facilio.controlaction.util.ControlActionUtil;

public class GetControllableCategoryFromSpaceListCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> spaceIncludeIds = (List<Long>) context.get(ControlActionUtil.SPACE_INCLUDE_LIST);
		
		Map<Long,Map<Long,ControllableAssetCategoryContext>> result = new HashMap<Long, Map<Long,ControllableAssetCategoryContext>>();
		
		if(spaceIncludeIds != null  && !spaceIncludeIds.isEmpty()) {
			
			for(Long spaceId: spaceIncludeIds) {
				
				FacilioChain getControllableCategoryChain = ReadOnlyChainFactory.getControllableCategoryFromSpaceIdChain();
				
				FacilioContext context1 = getControllableCategoryChain.getContext();
				
				context1.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
				
				getControllableCategoryChain.execute();
				
				Map<Long,ControllableAssetCategoryContext> controllableAssetCategoryMap = (Map<Long,ControllableAssetCategoryContext>) context1.get(ControlActionUtil.CONTROLLABLE_CATEGORIES);
				
				result.put(spaceId, controllableAssetCategoryMap);
			}
		}
		
		context.put(ControlActionUtil.SPACE_CONTROLLABLE_CATEGORIES_MAP, result);
		
		return false;
	}

}
