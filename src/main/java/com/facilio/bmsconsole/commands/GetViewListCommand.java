package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetViewListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
 		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleObj = null;
		
		List<FacilioView> dbViews = null;
		if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			dbViews = ViewAPI.getAllViews(moduleName);
		} else {
			if (!moduleName.equals("approval")) {
				moduleObj = modBean.getModule(moduleName);
				dbViews = ViewAPI.getAllViews(moduleObj.getModuleId());
			}
		}
		Map<String,FacilioView> viewMap = ViewFactory.getModuleViews(moduleName, moduleObj);
		
		if (dbViews != null) {
			for(FacilioView view: dbViews) {
				viewMap.put(view.getName(), view);
			}
		}
		
		viewMap.entrySet().removeIf(enrty -> enrty.getValue().isHidden());
		
		List<FacilioView> allViews = new ArrayList<>(viewMap.values());
		Boolean fetchByGroup = (Boolean) context.get(FacilioConstants.ContextNames.GROUP_STATUS);
		if (fetchByGroup != null && fetchByGroup) {
			List<FacilioView> customViews = allViews.stream().filter(view -> view.getIsDefault() != null && !view.getIsDefault()).collect(Collectors.toList());
			Optional<FacilioView> upcomingView = allViews.stream()
					.filter(view -> view.getIsDefault() != null && view.getIsDefault() && view.getName() != null && view.getName().equals("upcoming")).findFirst();

			Optional<FacilioView> myupcomingView = allViews.stream()
					.filter(view -> view.getIsDefault() != null && view.getIsDefault() && view.getName() != null && view.getName().equals("myupcoming")).findFirst();

				List<Map<String, Object>> groupViews = new ArrayList<>(ViewFactory.getGroupVsViews(moduleName));
				if (!groupViews.isEmpty()) {
					
					if (moduleName.equals("asset")) {
						ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						List<FacilioModule> modules = bean.getChildModules(bean.getModule(moduleName));
						List<Map<String, Object>> childViews = new ArrayList<>();
						Map<String,FacilioView> childViewMap = null;
						Map<String, Object> groupDetails = new HashMap<>();
						for(FacilioModule childModule: modules) {
							if (!childModule.isShowAsView()) {
								continue;
							}
							FacilioView childView = ViewFactory.getModuleView(childModule, moduleName);
							childViewMap = new HashMap<>();
							childViewMap.put(childView.getName(), childView);
							
							List<FacilioView> childDbViews = ViewAPI.getAllViews(childModule.getModuleId());
							if (childDbViews != null) {
								for(FacilioView view: childDbViews) {
									childViewMap.put(view.getName(), view);
								}
							}
							
							groupDetails = new HashMap<>();
							groupDetails.put("name", childModule.getName());
							groupDetails.put("displayName", childModule.getDisplayName());
							groupDetails.put("views",  new ArrayList<>(childViewMap.values()));
							childViews.add(groupDetails);
						}
						Map<String, Object> group = new HashMap<>(groupViews.get(0));
						if (!customViews.isEmpty() ) {
							if (!childViews.isEmpty()) {
								List<String> viewList = new ArrayList<>((List)group.get("views"));
								viewList.addAll(customViews.stream().map(view -> view.getName()).collect(Collectors.toList()));
								group.put("views", viewList);
							}
							else {
								group.put("displayName", "System Views");
								groupDetails = new HashMap<>();
								groupDetails.put("name", "customviews");
								groupDetails.put("displayName", "Custom Views");
								groupDetails.put("views", customViews);
								groupViews.add(groupDetails);
							}
						}
						if (!childViews.isEmpty()) {
							groupViews.addAll(childViews);
						}
						else if (customViews.isEmpty()){
							group.put("displayName", "System Views");
						}
						groupViews.set(0, group);
					}
						
					int groupSize = groupViews.size();
					Map<String, Object> group1 = groupViews.get(groupSize - 1);
					if (group1.containsKey("type") && group1.get("type").equals("custom") && !customViews.isEmpty()) {
						Map<String, Object> mutatedDetail = new HashMap<>(group1);
						mutatedDetail.remove("type");
						mutatedDetail.put("views", customViews);
						groupViews.set(groupSize - 1, mutatedDetail);
					}

					if (upcomingView.isPresent()) {
						Map<String, Object> groupDetails1 = new HashMap<>();
						groupDetails1.put("name", "upcoming");
						groupDetails1.put("displayName", "Upcoming Work Orders");
						groupDetails1.put("views", Arrays.asList(upcomingView.get()));
						groupViews.add(groupDetails1);
					}

					if (myupcomingView.isPresent()) {
						Map<String, Object> groupDetails1 = new HashMap<>();
						groupDetails1.put("name", "myupcoming");
						groupDetails1.put("displayName", "My Upcoming Work Orders");
						groupDetails1.put("views", Arrays.asList(myupcomingView.get()));
						groupViews.add(groupDetails1);
					}
				}
				else {
					groupViews = new ArrayList<>();
					Map<String, Object> groupDetails = new HashMap<>();
					groupDetails.put("name", "systemviews");
					groupDetails.put("displayName", "System Views");
					groupDetails.put("views", allViews.stream().filter(view -> view.getIsDefault() == null || view.getIsDefault()).collect(Collectors.toList()));
					groupViews.add(groupDetails);
					if (!customViews.isEmpty() ) {
						groupDetails = new HashMap<>();
						groupDetails.put("name", "customviews");
						groupDetails.put("displayName", "Custom Views");
						groupDetails.put("views", customViews);
						groupViews.add(groupDetails);
					}
				}
				
				for(int i = 0, size = groupViews.size(); i < size; i++) {
					Map<String, Object> group = groupViews.get(i);
					Map<String, Object> mutatedDetail = new HashMap<>(group);
					if (group.get("views") != null) {
						List<FacilioView> views;
						if (!(((List<?>)group.get("views")).isEmpty()) && ((List<?>)group.get("views")).get(0) instanceof FacilioView) {
							views = (List<FacilioView>) group.get("views");
						}
						else {
							views = new ArrayList<>();
							for(String view: ((List<String>)group.get("views"))) {
								views.add(viewMap.get(view));
							}
//							views = ((List<String>)group.get("views")).stream().map(view -> viewMap.get(view)).collect(Collectors.toList());
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
