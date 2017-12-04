package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

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
				viewCriteria = new Criteria();
				viewCriteria.setPattern(parentView.getCriteria().getPattern());
				viewCriteria.setConditions(parentView.getCriteria().getConditions());
				viewCriteria.addAndConditions(conditions);
			}
			else if(conditions != null && !conditions.isEmpty()) {
				viewCriteria = new Criteria();
				viewCriteria.addAndConditions(conditions);
			}
			view.setCriteria(viewCriteria);
			if(view.getTypeEnum() == null)
			{
				view.setType(ViewType.TABLE_LIST);
			}
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule moduleObj = modBean.getModule(moduleName);
			
			view.setModuleId(moduleObj.getModuleId());
			if(view.getName() == null)
			{
				view.setName(view.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
			}
			
			long viewId = ViewAPI.addView(view, AccountUtil.getCurrentOrg().getOrgId());
			view.setId(viewId);
		}
		return false;
	}

}
