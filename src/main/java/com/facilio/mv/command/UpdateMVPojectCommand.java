package com.facilio.mv.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.mv.context.MVProjectWrapper;
import com.facilio.mv.util.MVUtil;

public class UpdateMVPojectCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		MVProjectWrapper mvProjectWrapper = (MVProjectWrapper) context.get(MVUtil.MV_PROJECT_WRAPPER);
		
		mvProjectWrapper.getMvProject().setIsLocked(Boolean.TRUE);
		MVUtil.updateMVProject(mvProjectWrapper.getMvProject());
		
		return false;
	}


}