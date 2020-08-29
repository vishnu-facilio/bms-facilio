package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class CustomizeViewGroupsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ViewGroups> viewGroups = (List<ViewGroups>)context.get(FacilioConstants.ContextNames.GROUP_VIEWS);
		if(viewGroups != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module= modBean.getModule(moduleName);
			long moduleId = module.getModuleId();
			for(ViewGroups viewGroup : viewGroups) {
				if(viewGroup.getId() == -1) {
					viewGroup.setModuleId(moduleId);
					long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), module.getName());
					viewGroup.setId(groupId);
					List<FacilioView> views = viewGroup.getViews();
					if (views != null) {
						for(FacilioView view : views) {
							if(view.getId() == -1) {
								String viewName = view.getName();
								if (viewName != null && !viewName.isEmpty()) {
									long viewId = ViewAPI.checkAndAddView(viewName, moduleName, null, groupId);
									view.setId(viewId);
								} else {
									throw new IllegalArgumentException("viewId or viewName,moduleName is required");
								}
							}else if (view.getId() > 0) {
								view.setGroupId(groupId);
								Map<String, Object> viewProp = FieldUtil.getAsProperties(view);
								FacilioModule viewModule = ModuleFactory.getViewsModule();
								GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
																				.table(viewModule.getTableName())
																				.fields(FieldFactory.getViewFields())
																				.andCondition(CriteriaAPI.getIdCondition(view.getId(), viewModule));
								
								updateBuilder.update(viewProp);
								
							}
						}
						
					}
				}
			}
			
			ViewAPI.customizeViewGroups(viewGroups);
			
		}
		return false;
	}

	

}
