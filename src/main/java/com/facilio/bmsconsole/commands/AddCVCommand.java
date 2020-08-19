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
			if(view.getIncludeParentCriteria()) {
				FacilioView parentView = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
				if (viewCriteria == null) {
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
			
			addViewSharing(view);
			
			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
			
		}
		return false;
	}

	private void addViewSharing(FacilioView view) throws Exception {
		SharingContext<SingleSharingContext> viewSharing = view.getViewSharing();
		SharingAPI.deleteSharing(Collections.singletonList(view.getId()), ModuleFactory.getViewSharingModule());

		//temp handling for apptype sharing for custom views..can be removed after supporting apptype sharing in ui
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long modId = -1;
		if(view.getId() > 0) {
			FacilioView existingView = ViewAPI.getView(view.getId());
			modId = existingView.getModuleId();
		}
		if(viewSharing == null) {
			viewSharing = new SharingContext<>();
		}
		FacilioView defaultView = ViewFactory.getView(modBean.getModule(modId > 0 ? modId : view.getModuleId()), view.getName(), modBean);
		if(defaultView == null) {
			SingleSharingContext defaultAppSharing = SharingAPI.getCurrentAppTypeSharingForCustomViews();
			if (defaultAppSharing != null) {
				viewSharing.add(defaultAppSharing);
			}
		}
		else {
			SharingContext<SingleSharingContext> appSharing = SharingAPI.getDefaultAppTypeSharing(defaultView);
			if(CollectionUtils.isNotEmpty(appSharing)) {
				viewSharing.addAll(appSharing);
			}
		}

		if (viewSharing != null && !viewSharing.isEmpty()) {
			List<Long> orgUsersId = viewSharing.stream().filter(value -> value.getTypeEnum() == SharingType.USER)
					.map(val -> val.getUserId()).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(orgUsersId) && !orgUsersId.contains(AccountUtil.getCurrentUser().getId())) {
				SingleSharingContext newViewSharing = new SingleSharingContext(); 
				newViewSharing.setUserId(AccountUtil.getCurrentUser().getId());
				newViewSharing.setType(SharingType.USER);
				viewSharing.add(newViewSharing);	
			}
			SharingAPI.addSharing(viewSharing, view.getId(), ModuleFactory.getViewSharingModule());
		}
	}
}
