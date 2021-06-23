package com.facilio.controlaction.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.util.ControlActionUtil;

public class GetIncludedSpaceListCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> spaceIncludeIds = (List<Long>) context.get(ControlActionUtil.SPACE_INCLUDE_LIST);
		
		List<Long> spaceExcludeIds = (List<Long>) context.get(ControlActionUtil.SPACE_EXCLUDE_LIST);
		
		Long floorId = (Long) context.get(FacilioConstants.ContextNames.FLOOR_ID);
		
		if(spaceIncludeIds == null || spaceIncludeIds.isEmpty()) {
			List<SpaceContext> spaces = SpaceAPI.getAllSpaces(floorId);
			
			if(spaces != null && !spaces.isEmpty()) {
				
				spaceIncludeIds = new ArrayList<Long>();
				for(SpaceContext space : spaces) {
					spaceIncludeIds.add(space.getId());
				}
			}
		}
		
		if(spaceIncludeIds != null && spaceExcludeIds != null && !spaceExcludeIds.isEmpty()) {
			spaceIncludeIds.removeAll(spaceExcludeIds);
		}
		
		context.put(ControlActionUtil.SPACE_INCLUDE_LIST, spaceIncludeIds);
		return false;
	}

}
