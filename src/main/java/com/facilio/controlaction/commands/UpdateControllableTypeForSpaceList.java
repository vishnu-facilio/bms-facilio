package com.facilio.controlaction.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingDataMeta.ControlActionMode;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.util.ControlActionUtil;

public class UpdateControllableTypeForSpaceList extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> spaceIncludeIds = (List<Long>) context.get(ControlActionUtil.SPACE_INCLUDE_LIST);
		ControlActionMode controlActionMode = (ControlActionMode)context.get(ControlActionUtil.CONTROL_MODE);
		
		
		if(spaceIncludeIds != null  && !spaceIncludeIds.isEmpty()) {
			
			for(Long spaceId: spaceIncludeIds) {
				
				FacilioChain getControllableCategoryChain = TransactionChainFactory.updateControllableTypeForSpace();
				
				FacilioContext context1 = getControllableCategoryChain.getContext();
				
				context1.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
				context1.put(ControlActionUtil.CONTROL_MODE, controlActionMode);
				
				getControllableCategoryChain.execute();
			}
		}
		
		
		return false;
	}

}
