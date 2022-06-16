package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteViewCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.EXISTING_CV);
		if (view.getIsDefault()){
			throw new IllegalArgumentException("System View cannot be deleted");
		}
		ViewAPI.deleteView(view);
		return false;
	}

}
