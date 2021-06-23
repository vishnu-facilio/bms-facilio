package com.facilio.controlaction.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControllableAssetCategoryContext.ControllableCategory;
import com.facilio.controlaction.context.ControllablePointContext.ControllablePoints;
import com.facilio.controlaction.util.ControlActionUtil;

public class GetExecuteControlActionCommandForSpaceList extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> spaceIncludeIds = (List<Long>) context.get(ControlActionUtil.SPACE_INCLUDE_LIST);
		ControllableCategory controllableCategory = (ControllableCategory) context.get(ControlActionUtil.CONTROLLABLE_CATEGORY);
		ControllablePoints controllablePoint = (ControllablePoints) context.get(ControlActionUtil.CONTROLLABLE_POINT);
		String value = (String) context.get(ControlActionUtil.VALUE);
		
		
		if(spaceIncludeIds != null  && !spaceIncludeIds.isEmpty()) {
			
			for(Long spaceId: spaceIncludeIds) {
				
				FacilioChain executeControlActionCommandChain = TransactionChainFactory.getExecuteControlActionCommandForSpaceChain();
				
				FacilioContext context1 = executeControlActionCommandChain.getContext();
				
				context1.put(ControlActionUtil.CONTROLLABLE_CATEGORY, controllableCategory);
				context1.put(ControlActionUtil.CONTROLLABLE_POINT, controllablePoint);
				context1.put(ControlActionUtil.VALUE, value);
				context1.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
				context1.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.MANUAL);
				
				executeControlActionCommandChain.execute();
			}
		}
		
		return false;
		
	}

}
