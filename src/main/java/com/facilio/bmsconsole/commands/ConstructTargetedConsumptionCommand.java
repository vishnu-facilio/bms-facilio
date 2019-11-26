package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class ConstructTargetedConsumptionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		List<MVBaseline> baseLines = mvProjectWrapper.getBaselines();
		
		List<MVAdjustment> adjustments =  mvProjectWrapper.getAdjustments();
		
		for(MVBaseline baseLine :baseLines) {
			
		}
		
		return false;
	}

}
