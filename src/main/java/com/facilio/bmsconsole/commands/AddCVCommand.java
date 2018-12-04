package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewSharingContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddCVCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.NEW_CV);
//		String name = (String) context.get(FacilioConstants.ContextNames.VIEW_NAME);
		if(view != null) {
			long viewId = view.getId();
			Criteria viewCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
			view.setCriteria(viewCriteria);
			if(viewId == -1) {
				if(view.getTypeEnum() == null)
				{
					view.setType(ViewType.TABLE_LIST);
				}
				String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule moduleObj = modBean.getModule(moduleName);
				if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
					view.setModuleName(moduleName);
				} 
				view.setModuleId(moduleObj.getModuleId());
				
				ActivityType activityType =  (ActivityType) context.get(FacilioConstants.ContextNames.ACTIVITY_TYPE);
				if (( activityType == null ) || ( activityType == ActivityType.CREATE )) {
					if(view.getName() == null)
					{
						view.setName(view.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
					}
					if (ViewFactory.getView(moduleName, view.getName()) != null) {
						throw new IllegalArgumentException("Name already taken");
					}
					view.setDefault(false);
				}
				
				else {
					if(viewCriteria == null) {
						FacilioView view1 = ViewFactory.getView(moduleName, view.getName());
						Criteria viewCriteria2 = view1.getCriteria();
						view.setCriteria(viewCriteria2);
					}
					view.setDefault(true);
				}
				
				viewId = ViewAPI.addView(view, AccountUtil.getCurrentOrg().getOrgId());
			}
			else {
				long viewId1 = ViewAPI.updateView(viewId,view);	
			}
			
			List<ViewSharingContext> viewSharingList = (List<ViewSharingContext>) context.get(FacilioConstants.ContextNames.VIEW_SHARING_LIST);
			if (viewSharingList != null) {
				ViewAPI.applyViewSharing(viewId, viewSharingList);
			}
			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
			view.setId(viewId);
		}
		return false;
	}

}
