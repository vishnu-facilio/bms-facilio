package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewSharingContext;
import com.facilio.bmsconsole.context.ViewSharingContext.SharingType;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericDeleteRecordBuilder;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.stream.Collectors;

public class AddCVCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
					if (ViewFactory.getView(moduleName, view.getName()) != null) {
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
			List<ViewSharingContext> viewSharingList = (List<ViewSharingContext>) context.get(FacilioConstants.ContextNames.VIEW_SHARING_LIST);
			
			FacilioModule module = ModuleFactory.getViewSharingModule();
			List<FacilioField> fields =  FieldFactory.getViewSharingFields();
			if (viewSharingList != null && !viewSharingList.isEmpty()) {
				List<Long> orgUsersId = (List<Long>) viewSharingList.stream().filter(value -> (value.getSharingType() == SharingType.USER.getIntVal())).map(val -> val.getOrgUserId()).collect(Collectors.toList());
				if (!orgUsersId.contains(AccountUtil.getCurrentUser().getId())) {
					ViewSharingContext newViewSharingContext = new ViewSharingContext();
					((ViewSharingContext) newViewSharingContext).setOrgUserId(AccountUtil.getCurrentUser().getId());
					((ViewSharingContext) newViewSharingContext).setSharingType(SharingType.USER.getIntVal());;
					viewSharingList.add(newViewSharingContext);	
				}
				ViewAPI.applyViewSharing(viewId, viewSharingList);
				view.setSharingType(ViewAPI.getViewSharingDetail(viewId));
				
			}
			else if (viewSharingList == null || viewSharingList.isEmpty()) {
				GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
						.table(module.getTableName())
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
						.andCondition(CriteriaAPI.getCondition("VIEWID", "viewId", String.valueOf(viewId), NumberOperators.EQUALS));
				
				int count = builder.delete();
			}						
			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
			view.setId(viewId);
		}
		return false;
	}

}
