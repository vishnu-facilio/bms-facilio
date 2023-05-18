package com.facilio.bmsconsole.commands;


import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class GetViewsForGroupIdCommand extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(GetViewsForGroupIdCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long viewGroupId = (Long) context.getOrDefault(FacilioConstants.ContextNames.VIEW_GROUP_ID, -1L);
		boolean fromBuilder = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_FROM_BUILDER, false);
		Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1L);

		ApplicationContext app;
		if (appId <= 0) {
			app = AccountUtil.getCurrentApp();
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			appId = app.getId();
		}
		User currentUser = AccountUtil.getCurrentUser();
		long currUserAppId = currentUser.getApplicationId();
		if (currUserAppId == -1) {
			currUserAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		}
		boolean isPrivilegedAccess = ViewAPI.isPrivilegedAccess(appId, currentUser, currUserAppId);

		List<FacilioView> viewList = new ArrayList<>();

		if(viewGroupId > 0) {
			viewList = ViewAPI.getViewsForGroupId(viewGroupId, fromBuilder);
			if(CollectionUtils.isNotEmpty(viewList)) {
				viewList = ViewAPI.filterAccessibleViews(viewList, currentUser, isPrivilegedAccess);
				if(CollectionUtils.isNotEmpty(viewList) && !fromBuilder) {
					viewList.set(0, ViewAPI.getView(viewList.get(0).getId()));
				}
			}
		}
		if(CollectionUtils.isEmpty(viewList)) viewList = new ArrayList<>();

		JSONArray viewsJsonArray = FieldUtil.getAsJSONArray(viewList, FacilioView.class);
		context.put(FacilioConstants.ContextNames.VIEW_LIST, viewsJsonArray);
		return false;
	}
}
