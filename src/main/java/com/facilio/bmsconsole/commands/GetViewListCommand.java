package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class GetViewListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
        Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleObj = modBean.getModule(moduleName);
		ApplicationContext app = null;
		ApplicationContext currentApp = AccountUtil.getCurrentApp();


		if (appId > 0) {
			 app = ApplicationApi.getApplicationForId(appId);
		}

		Map<String,FacilioView> viewMap = new HashMap();

		// ViewFactory views
		if ((app != null && app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) || 
				(app == null && currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)))  {
			 viewMap = ViewFactory.getModuleViews(moduleName, moduleObj);
		}
		
		//db group views
		List<ViewGroups> viewGroups = new ArrayList<>();
		if (moduleObj != null) {
			viewGroups = ViewAPI.getAllGroups(moduleObj.getModuleId(), appId);
		}
		
		//db views
		List<FacilioView> dbViews = new ArrayList<>();	

		if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			dbViews = ViewAPI.getAllViews(moduleName);
		} else {
			if (!moduleName.equals("approval")) {
				dbViews = ViewAPI.getAllViews(moduleObj.getModuleId());
			}
			// Temp...till mobile alarm module is split
			if (AccountUtil.getCurrentAccount().isFromMobile() && moduleName.equals(ContextNames.NEW_READING_ALARM)) {
				addMobilelarmViews(modBean, dbViews, viewMap, viewGroups);
			}
		}
		
		
		Map<Long, SharingContext<SingleSharingContext>> sharingMap = SharingAPI.getSharingMap(ModuleFactory.getViewSharingModule(), SingleSharingContext.class);
		if (!dbViews.isEmpty() && dbViews != null) {
			for(FacilioView view: dbViews) {
					viewMap.put(view.getName(), view);
					if (sharingMap != null && sharingMap.containsKey(view.getId())) {
						view.setViewSharing(sharingMap.get(view.getId()));
					}
			}
		}
		viewMap.entrySet().removeIf(enrty -> {
			try {
				return enrty.getValue().isHidden() || (enrty.getValue().getViewSharing() != null && !enrty.getValue().getViewSharing().isAllowed());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		});
		
		
		if (!dbViews.isEmpty() && !viewGroups.isEmpty() && dbViews != null && viewGroups != null) {
			for(ViewGroups viewGroup : viewGroups) {
				List<FacilioView> groupBasedViews = dbViews.stream().filter(view -> view.getGroupId() == viewGroup.getId()).collect(Collectors.toList());
				if (groupBasedViews != null && groupBasedViews.size() > 0) {
				for(FacilioView view: groupBasedViews) {
						if (sharingMap != null && sharingMap.containsKey(view.getId())) {
							view.setViewSharing(sharingMap.get(view.getId()));
						}
				}
				groupBasedViews.removeIf(view -> {
					try {
						return view.isHidden() || (view.getViewSharing() != null && !view.getViewSharing().isAllowed());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				});
				groupBasedViews.sort(Comparator.comparing(FacilioView::getSequenceNumber, (s1, s2) -> {
					if(s1 == s2){
						return 0;
					}
					return s1 < s2 ? -1 : 1;
				}));
				}
				viewGroup.setViews(groupBasedViews);
			}
		}
		
		
				
		List<FacilioView> allViews = new ArrayList<>(viewMap.values());
		Boolean fetchByGroup = (Boolean) context.get(FacilioConstants.ContextNames.GROUP_STATUS);
		
		
		
		if (fetchByGroup != null && fetchByGroup) {
			
			List<FacilioView> customViews = new ArrayList<>();
			
			Optional<FacilioView> upcomingView = allViews.stream()
					.filter(view -> view.getIsDefault() != null && view.getIsDefault() && view.getName() != null && view.getName().equals("upcoming")).findFirst();

			Optional<FacilioView> myupcomingView = allViews.stream()
					.filter(view -> view.getIsDefault() != null && view.getIsDefault() && view.getName() != null && view.getName().equals("myupcoming")).findFirst();
			
			
			// groupViews from ViewFactory
			List<Map<String, Object>> groupViews = new ArrayList<>();
			if ((app != null && app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) || 
					(app == null && currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP))) {
				 groupViews = new ArrayList<>(ViewFactory.getGroupVsViews(moduleName));
			
			if (!groupViews.isEmpty()) {
					
				addChildModuleViews(moduleName, groupViews, customViews);
				
				// Temp handling for qualityfm
				if (moduleName.equals("workorder") && (AccountUtil.getCurrentOrg().getOrgId() == 320 || FacilioProperties.isDevelopment())) {
					List cadViews = new ArrayList<>();
					if (customViews != null) {
						List<FacilioView> tempCustom = new ArrayList<>();
						for(FacilioView cv: customViews) {
							if (cv.getName().endsWith("cadview")) {
								cadViews.add(cv);
							}
							else {
								tempCustom.add(cv);
							}
						}
						customViews = tempCustom;
					}
					Map<String, Object> groupDetails1 = new HashMap<>();
					groupDetails1.put("name", "cleaning");
					groupDetails1.put("displayName", "Cleaning and Disinfection");
					groupDetails1.put("views", cadViews);
					groupViews.add(4, groupDetails1);
				}
					
				int customGroupIdx = getCustomGroupIdx(groupViews);
				if (customGroupIdx != -1) {
					if (!customViews.isEmpty()) {
						Map<String, Object> mutatedDetail = new HashMap<>(groupViews.get(customGroupIdx));
						mutatedDetail.remove("type");
						mutatedDetail.put("views", customViews);
						groupViews.set(customGroupIdx, mutatedDetail);
					}
					else {
						groupViews.remove(customGroupIdx);
					}
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
				if (moduleObj != null && moduleObj.isCustom()) {
					groupDetails.put("name", "allViews");
					groupDetails.put("displayName", "All Views");
					groupDetails.put("views", allViews);
					groupViews.add(groupDetails);
				}
				else {
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
			}
		}
			
			sortGroupViews(groupViews, viewMap, viewGroups);
			// TODO remove 
			
			if (AccountUtil.getCurrentAccount().isFromMobile()) {
				context.put(FacilioConstants.ContextNames.VIEW_LIST, viewGroups);
			}else {
				context.put(FacilioConstants.ContextNames.GROUP_VIEWS, viewGroups);
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
	
	private void sortGroupViews(List<Map<String, Object>> groupViews, Map<String, FacilioView> viewMap, List<ViewGroups> viewGroups) {
		if (groupViews != null) {
			for(Map<String, Object> group : groupViews) {
				List<ViewGroups> sysAndCusViewGroup = viewGroups.stream().filter(viewGroup -> viewGroup.getName().equals(group.get("name"))).collect(Collectors.toList());
				if ((sysAndCusViewGroup.isEmpty() || sysAndCusViewGroup == null)) {
					ViewGroups viewGroup = new ViewGroups();
					List<FacilioView> viewsList;
					if (group.get("views") != null) {
					if (!(((List<?>)group.get("views")).isEmpty()) && ((List<?>)group.get("views")).get(0) instanceof FacilioView) {
						viewsList = (List<FacilioView>) group.get("views");
					}
					else {
						viewsList = new ArrayList<>();
						for(String viewName: ((List<String>)group.get("views"))) {
							FacilioView view = viewMap.get(viewName);
							if (view != null ) {
								viewsList.add(view);
							}
						}
					}	
					viewsList.sort(Comparator.comparing(FacilioView::getSequenceNumber, (s1, s2) -> {
						if(s1 == s2){
							return 0;
						}
						return s1 < s2 ? -1 : 1;
					}));
					
					
					viewGroup.setViews((List<FacilioView>) viewsList);
					viewGroup.setDisplayName((String) group.get("displayName"));
					viewGroup.setName((String) group.get("name"));
					viewGroups.add(viewGroup);
				}
				}
			}
			
			
		}
		
		if (viewGroups != null) {
			viewGroups.sort(Comparator.comparing(ViewGroups::getSequenceNumber, (s1, s2) -> {
				if(s1 == s2){
					return 0;
				}
				return s1 < s2 ? -1 : 1;
			}));
		}

	}

	private int getCustomGroupIdx(List<Map<String, Object>> groupViews ) {
		return IntStream.range(0, groupViews.size())
		.filter(i -> groupViews.get(i).containsKey("type") && groupViews.get(i).get("type").equals("custom"))
		.findFirst()
		.orElse(-1);
	}
	
	private void addChildModuleViews(String moduleName, List<Map<String, Object>> groupViews, List<FacilioView> customViews) throws Exception {
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
				} else {
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
			} else if (customViews.isEmpty()) {
				group.put("displayName", "System Views");
			}
			groupViews.set(0, group);

		}
	}
	
	private void addMobilelarmViews(ModuleBean modBean, List<FacilioView> dbViews, Map<String,FacilioView> viewMap, List<ViewGroups> viewGroups) throws Exception {
		List<String> alarmModules = Arrays.asList(new String[] {ContextNames.BMS_ALARM, ContextNames.AGENT_ALARM, ContextNames.SENSOR_ROLLUP_ALARM});
		for(String moduleName: alarmModules) {
			FacilioModule alarmModule = modBean.getModule(moduleName);
			dbViews.addAll(ViewAPI.getAllViews(alarmModule.getModuleId()));
			
			viewMap.putAll(ViewFactory.getModuleViews(moduleName, alarmModule));
			
			viewGroups.addAll(ViewAPI.getAllGroups(alarmModule.getModuleId(), -1));
		}
		
	}
	
}
