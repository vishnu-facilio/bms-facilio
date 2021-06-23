package com.facilio.controlaction.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
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
		
		List<Integer> categoryIncludeIds = (List<Integer>) context.get(ControlActionUtil.CATEGORY_INCLUDE_LIST);
		List<Integer> categoryExcludeIds = (List<Integer>) context.get(ControlActionUtil.CATEGORY_EXCLUDE_LIST);
		
		Map<Long,Map<Long,ControllableAssetCategoryContext>> result = new HashMap<Long, Map<Long,ControllableAssetCategoryContext>>();
		
		if(spaceIncludeIds != null  && !spaceIncludeIds.isEmpty()) {
			
			for(Long spaceId: spaceIncludeIds) {
				
				FacilioChain getControllableCategoryChain = ReadOnlyChainFactory.getControllableCategoryFromSpaceIdChain();
				
				FacilioContext context1 = getControllableCategoryChain.getContext();
				
				context1.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
				
				context1.put(ControlActionUtil.CATEGORY_INCLUDE_LIST, categoryIncludeIds);
				context1.put(ControlActionUtil.CATEGORY_EXCLUDE_LIST, categoryExcludeIds);
				
				getControllableCategoryChain.execute();
				
				Map<Long,ControllableAssetCategoryContext> controllableAssetCategoryMap = (Map<Long,ControllableAssetCategoryContext>) context1.get(ControlActionUtil.CONTROLLABLE_CATEGORIES);
				
				if(controllableAssetCategoryMap != null) {
					result.put(spaceId, controllableAssetCategoryMap);
				}
			}
		}
		
		context.put(ControlActionUtil.SPACE_CONTROLLABLE_CATEGORIES_MAP, result);
		
		return false;
	}

}
