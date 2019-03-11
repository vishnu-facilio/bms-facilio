package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.AssetsAPI;
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
		if ((fetchByGroup != null && fetchByGroup) || moduleName.equals("asset")) {
			List<FacilioView> customViews = allViews.stream().filter(view -> view.getIsDefault() != null && !view.getIsDefault()).collect(Collectors.toList());
			
			// Temp...Needs to change in web client also
			if ((AccountUtil.getCurrentAccount().isFromMobile()) || moduleName.equals("asset")) {
				List<Map<String, Object>> groupViews = ViewFactory.getGroupVsViews(moduleName);
				if (groupViews != null && !groupViews.isEmpty()) {
					int groupSize = groupViews.size();
					Map<String, Object> group = groupViews.get(groupSize - 1);
					if (group.containsKey("type") && group.get("type").equals("custom") && !customViews.isEmpty()) {
						Map<String, Object> mutatedDetail = new HashMap<>(group);
						mutatedDetail.remove("type");
						mutatedDetail.put("views", customViews);
						groupViews.set(groupSize - 1, mutatedDetail);
					}
				}
				else {
					groupViews = new ArrayList<>();
					Map<String, Object> groupDetails = new HashMap<>();
					if (moduleName.equals("asset")) {
					groupDetails.put("name", "systemviews");
					groupDetails.put("displayName", "System Views");
					groupDetails.put("views", allViews.stream().filter(view -> view.getIsDefault() == null || view.getIsDefault()).collect(Collectors.toList()));
					groupViews.add(groupDetails);
//					groupViews.addAll(customViews);
					}
					else {
						groupDetails.put("name", "systemviews");
						groupDetails.put("displayName", "System Views");
						groupDetails.put("views", allViews.stream().filter(view -> view.getIsDefault() == null || view.getIsDefault()).collect(Collectors.toList()));
						groupViews.add(groupDetails);
					}
					if (!customViews.isEmpty() && moduleName.equals("asset")) {
						groupDetails = new HashMap<>();
						groupDetails.put("name", "customviews");
						groupDetails.put("displayName", "Custom Views");
						groupDetails.put("views", customViews);
						groupViews.add(groupDetails);
					}
					if (moduleName.equals("asset")) {
						ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						List<AssetCategoryContext> assetCategoryLists = AssetsAPI.getCategoryList();
						for(AssetCategoryContext category: assetCategoryLists) {
							FacilioView categoryView = new FacilioView();
							categoryView.setName(category.getName().toLowerCase());
							categoryView.setDisplayName(category.getName());
							categoryView.setModuleId(category.getAssetModuleID());;
							categoryView.setModuleName(bean.getModule(category.getAssetModuleID()).getName());
							List<FacilioView> dbViews1 = ViewAPI.getAllViews(categoryView.getModuleId());
							dbViews1.add(categoryView);
							viewMap.put(categoryView.getName(), categoryView);
							if (!viewMap.isEmpty()) {
								groupDetails = new HashMap<>();
								groupDetails.put("name", categoryView.getName());
								groupDetails.put("displayName", categoryView.getName().toUpperCase());
								groupDetails.put("views", dbViews1);
								groupViews.add(groupDetails);
							}
//							if (!dbViews1.isEmpty()) {
//								groupDetails = new HashMap<>();
//								groupDetails.put("name", "customviews");
//								groupDetails.put("displayName", categoryView.getName().toUpperCase() + " Custom Views");
//								groupDetails.put("views", dbViews1);
//								groupViews.add(groupDetails);
//							}
						}		
					}
				}
				
				for(int i = 0, size = groupViews.size(); i < size; i++) {
					Map<String, Object> group = groupViews.get(i);
					Map<String, Object> mutatedDetail = new HashMap<>(group);
					if (group.get("views") != null) {
						List<FacilioView> views;
						if (((List<?>)group.get("views")).get(0) instanceof FacilioView) {
							views = (List<FacilioView>) group.get("views");
						}
						else {
							views = ((List<String>)group.get("views")).stream().map(view -> viewMap.get(view)).collect(Collectors.toList());
						}
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
				
				context.put(FacilioConstants.ContextNames.VIEW_LIST, groupViews);
			}
//			 TODO remove
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
