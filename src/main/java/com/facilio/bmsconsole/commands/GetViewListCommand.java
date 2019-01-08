package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetViewListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleObj = null;
		Map<String,FacilioView> viewMap = ViewFactory.getModuleViews(moduleName);
		List<FacilioView> dbViews = null;
		if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			dbViews = ViewAPI.getAllViews(moduleName);
		} else {
			if (!moduleName.equals("approval")) {
				moduleObj = modBean.getModule(moduleName);
				dbViews = ViewAPI.getAllViews(moduleObj.getModuleId());
			}
		}
		
		if (dbViews != null) {
			for(FacilioView view: dbViews) {
				viewMap.put(view.getName(), view);
			}
		}
		
		viewMap.entrySet().removeIf(enrty -> enrty.getValue().isHidden());
		
		List<FacilioView> allViews = new ArrayList<>(viewMap.values());
		
		Boolean fetchByGroup = (Boolean) context.get(FacilioConstants.ContextNames.GROUP_STATUS);
		if (fetchByGroup != null && fetchByGroup) {
			// Temp...Needs to change in web client also
			if (AccountUtil.getCurrentAccount().isFromMobile()) {
				List<Map<String, Object>> groupViews = ViewFactory.getGroupVsViews(moduleName);
				if (groupViews != null && !groupViews.isEmpty()) {
					for(int i = 0, size = groupViews.size(); i < size; i++) {
						Map<String, Object> group = groupViews.get(i);
						Map<String, Object> mutatedDetail = new HashMap<>(group);
						List<FacilioView> views = ((List<String>)group.get("views")).stream().map(view -> viewMap.get(view)).collect(Collectors.toList());
						views.sort(Comparator.comparing(FacilioView::getSequenceNumber, (s1, s2) -> {
							if(s1 == s2){
						         return 0;
						    }
						    return s1 < s2 ? -1 : 1;
						}));
						mutatedDetail.put("views", views);
						groupViews.set(i, mutatedDetail);
					}
				}
				
				List<FacilioView> customViews = allViews.stream().filter(view -> view.getIsDefault() != null && !view.getIsDefault()).collect(Collectors.toList());
				if (!customViews.isEmpty()) {
					Map<String, Object> customViewGroup = new HashMap<>();
					customViewGroup.put("name", "customworkorders");
					customViewGroup.put("displayName", "Custom Work Orders");
					customViewGroup.put("views", customViews);
					groupViews.add(customViewGroup);
				}
				
				context.put(FacilioConstants.ContextNames.VIEW_LIST, groupViews);
			}
			// TODO remove
			else {
				allViews.sort(Comparator.comparing(FacilioView::getSequenceNumber, (s1, s2) -> {
					if(s1 == s2){
				         return 0;
				    }
				    return s1 < s2 ? -1 : 1;
				}));
				context.put(FacilioConstants.ContextNames.VIEW_LIST, allViews);
				context.put(FacilioConstants.ContextNames.GROUP_VIEWS, ViewFactory.getGroupViews(moduleName));
			}
		}
		else {
			allViews.sort(Comparator.comparing(FacilioView::getSequenceNumber, (s1, s2) -> {
				if(s1 == s2){
			         return 0;
			    }
			    return s1 < s2 ? -1 : 1;
			}));
			context.put(FacilioConstants.ContextNames.VIEW_LIST, allViews);
		}
		
		
		return false;
	}

}
