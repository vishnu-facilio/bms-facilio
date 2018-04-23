package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewSharingContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.ColumnFactory;
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
			Criteria viewCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
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
			if (ColumnFactory.getColumns(moduleName, view.getName()) != null) {
				throw new Exception("Name already taken");
			}
			view.setDefault(false);
			
			long viewId = ViewAPI.addView(view, AccountUtil.getCurrentOrg().getOrgId());
			
			List<ViewSharingContext> viewSharingList = (List<ViewSharingContext>) context.get(FacilioConstants.ContextNames.VIEW_SHARING_LIST);
			ViewAPI.applyViewSharing(viewId, viewSharingList);
			context.put(FacilioConstants.ContextNames.VIEWID, viewId);
			view.setId(viewId);
		}
		return false;
	}

}
