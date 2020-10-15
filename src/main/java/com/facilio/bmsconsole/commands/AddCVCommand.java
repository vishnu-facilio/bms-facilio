package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext.SharingType;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.collections4.CollectionUtils;

public class AddCVCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.NEW_CV);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
//		String name = (String) context.get(FacilioConstants.ContextNames.VIEW_NAME);
		if(view != null) {
			long viewId = view.getId();
			Criteria viewCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
			if (view.getCriteria() != null) {
				viewCriteria = view.getCriteria();
			}
			if(view.getIncludeParentCriteria()) {
				FacilioView parentView = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
				if (viewCriteria == null && parentView != null) {
					viewCriteria = parentView.getCriteria();
				}
				else if (parentView != null && parentView.getCriteria() != null) {
					viewCriteria.andCriteria(parentView.getCriteria());
				}				
			}
			view.setCriteria(viewCriteria);
			if(viewId == -1) {
				if(view.getTypeEnum() == null)
				{
					view.setType(ViewType.TABLE_LIST);
				}
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule moduleObj = modBean.getModule(moduleName);
				String extendedModName = null;
				if (moduleObj != null && moduleObj.getExtendModule() != null) {
					extendedModName = moduleObj.getExtendModule().getName();
				}
				if (extendedModName != null && extendedModName.contains("asset")) {	
					view.setModuleName(moduleName);
				}
				if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
					view.setModuleName(moduleName);
				} 
				view.setModuleId(moduleObj.getModuleId());
				
				EventType activityType =  (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
				if (( activityType == null ) || ( activityType == EventType.CREATE )) {
					if(view.getName() == null)
					{
						view.setName(view.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+",""));
					}
					if (ViewFactory.getView(moduleObj, view.getName(), modBean) != null) {
						throw new IllegalArgumentException("Name already taken");
					}
					view.setDefault(false);
				}
				
				else {
					view.setDefault(true);          
				}
				
				viewId = ViewAPI.addView(view, AccountUtil.getCurrentOrg().getOrgId());
			}
			else {
				long viewId1 = ViewAPI.updateView(viewId,view);	
			}
			view.setId(viewId);
			
			ViewAPI.addViewSharing(view);
			
			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
			
		}
		return false;
	}

}
