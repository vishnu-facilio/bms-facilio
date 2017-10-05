package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class AddCVCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.NEW_CV);
		
		if(view != null) {
			FacilioView parentView = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
			Criteria viewCriteria = null;
			List<Condition> conditions = (List<Condition>) context.get(FacilioConstants.ContextNames.FILTER_CONDITIONS);
			if(parentView != null && parentView.getCriteria() != null) {
				viewCriteria = parentView.getCriteria();
				viewCriteria.addAndConditions(conditions);
			}
			else if(conditions != null && !conditions.isEmpty()) {
				viewCriteria = new Criteria();
				viewCriteria.addAndConditions(conditions);
			}
			view.setCriteria(viewCriteria);
			long viewId = ViewAPI.addView(view, OrgInfo.getCurrentOrgInfo().getOrgid());
			view.setId(viewId);
		}
		return false;
	}

}
