package com.facilio.bmsconsole.commands;


import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GetViewGroupListCommand extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(GetViewGroupListCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		FacilioModule module = modBean.getModule(moduleName);

		Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1L);
		boolean fromBuilder = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_FROM_BUILDER, false);
		String requestedViewName = (String) context.getOrDefault(FacilioConstants.ContextNames.VIEW_NAME, null);

		ApplicationContext app;
		if (appId <= 0) {
			app = AccountUtil.getCurrentApp();
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			appId = app.getId();
		}
		User currentUser = AccountUtil.getCurrentUser();

		int groupTypeVal = ViewGroups.ViewGroupType.TABLE_GROUP.getIntVal();
		if (context.containsKey(FacilioConstants.ContextNames.VIEW_GROUP_TYPE) && (int) context.get(FacilioConstants.ContextNames.VIEW_GROUP_TYPE) > 0) {
			groupTypeVal = (int) context.get(FacilioConstants.ContextNames.VIEW_GROUP_TYPE);
		}

		ViewGroups.ViewGroupType groupType = ViewGroups.ViewGroupType.getGroupType(groupTypeVal);

		long currUserAppId = currentUser.getApplicationId();
		if (currUserAppId == -1) {
			currUserAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		}
		boolean isPrivilegedAccess = ViewAPI.isPrivilegedAccess(appId, currentUser, currUserAppId);


		List<FacilioView> dbViews = new ArrayList<>();
		List<ViewGroups> viewGroups = new ArrayList<>();
		if (module != null) {
			viewGroups = ViewAPI.getAllGroups(module.getModuleId(), appId, moduleName, groupType, true, fromBuilder);
			viewGroups = ViewAPI.filterAccessibleViewGroups(viewGroups, fromBuilder, isPrivilegedAccess);
		}

		if(!fromBuilder) {
			viewGroups = viewGroups.stream().filter(group -> group.getViewCount() > 0).collect(Collectors.toList());
		}

		if (CollectionUtils.isEmpty(viewGroups)) {
			context.put(FacilioConstants.ContextNames.GROUP_VIEWS, viewGroups);
			return false;
		}

		boolean isSpecialTypeModule = LookupSpecialTypeUtil.isSpecialType(moduleName);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();

		FacilioView requestedView = StringUtils.isNotEmpty(requestedViewName)
				? isSpecialTypeModule ? ViewAPI.getView(requestedViewName, moduleName, orgId, appId) : ViewAPI.getView(requestedViewName, module.getModuleId(), orgId, appId)
				: null;

		boolean requestedViewSet = false;
		if(requestedView != null) {
			dbViews = ViewAPI.getViewsForGroupId(requestedView.getGroupId(), fromBuilder);
			if(CollectionUtils.isNotEmpty(dbViews)) {
				dbViews = ViewAPI.filterAccessibleViews(dbViews, currentUser, isPrivilegedAccess);
				FacilioView existingView = dbViews.stream().filter(view -> view.getId() == requestedView.getId()).findFirst().orElse(null);
				int existingViewIndex = dbViews.indexOf(existingView);
				if (existingViewIndex != -1) {
					dbViews.set(existingViewIndex, requestedView);
					ViewAPI.setViewsForViewGroup(dbViews, viewGroups, requestedView.getGroupId());
					requestedViewSet = true;
				}
			}

		}
		if(!requestedViewSet){
			for(ViewGroups group : viewGroups) {
				dbViews = ViewAPI.getViewsForGroupId(group.getId(), fromBuilder);
				if(!CollectionUtils.isEmpty(dbViews)) {
					dbViews = ViewAPI.filterAccessibleViews(dbViews, currentUser, isPrivilegedAccess);
					if(CollectionUtils.isNotEmpty(dbViews)) {
						setFirstView(dbViews, viewGroups, group.getId());
						ViewAPI.setViewsForViewGroup(dbViews, viewGroups, group.getId());
						break;
					}
				}
			}
		}

		JSONArray viewGroupJsonArray = FieldUtil.getAsJSONArray(viewGroups, ViewGroups.class);
		context.put(FacilioConstants.ContextNames.GROUP_VIEWS, viewGroupJsonArray);
		return false;
	}

	private static void setFirstView(List<FacilioView> dbViews, List<ViewGroups> viewGroups, long groupId) throws Exception {
		FacilioView firstView = viewGroups.stream().anyMatch(viewGroup -> viewGroup.getId() == groupId)
				? ViewAPI.getView(dbViews.get(0).getId())
				: null;
		if (firstView != null) {
			dbViews.set(0, firstView);
		}
	}
}
