package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
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
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.delegate.context.DelegationType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GetViewListCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GetViewListCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
        Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1l);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		int groupTypeVal = ViewGroups.ViewGroupType.TABLE_GROUP.getIntVal();
		if(context.containsKey(ContextNames.VIEW_GROUP_TYPE) && (int) context.get(ContextNames.VIEW_GROUP_TYPE)>0) {
			groupTypeVal = (int) context.get(ContextNames.VIEW_GROUP_TYPE);
		}
		int viewTypeVal = FacilioView.ViewType.TABLE_LIST.getIntVal();
		if(context.containsKey(ContextNames.VIEW_TYPE) && (int) context.get(ContextNames.VIEW_TYPE)>0) {
			viewTypeVal = (int) context.get(ContextNames.VIEW_TYPE);
		}
		boolean getOnlyBasicViewDetails = (context.containsKey(ContextNames.GET_ONLY_BASIC_VIEW_DETAILS)) ? (boolean)context.get(ContextNames.GET_ONLY_BASIC_VIEW_DETAILS) : false;
		boolean fromBuilder = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_FROM_BUILDER, false);

		FacilioView.ViewType viewType= FacilioView.ViewType.getViewType(viewTypeVal);
		ViewGroups.ViewGroupType groupType = ViewGroups.ViewGroupType.getGroupType(groupTypeVal);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleObj = modBean.getModule(moduleName);
		ApplicationContext currentApp = AccountUtil.getCurrentApp();
		ApplicationContext app = null;

		if (appId > 0) {
			 app = ApplicationApi.getApplicationForId(appId);
		} else {
			app = AccountUtil.getCurrentApp();
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			appId = app.getId();
		}

		Map<String,FacilioView> factoryViewsMap = new HashMap();

		// ViewFactory views
		if (((app != null && app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) ||
				(app == null && currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP))) &&
						(viewType == FacilioView.ViewType.TABLE_LIST))  {
			 factoryViewsMap = ViewFactory.getModuleViews(moduleName, moduleObj);
		}
		
		//db group views
		List<ViewGroups> viewGroups = new ArrayList<>();
		if (moduleObj != null) {
			viewGroups = ViewAPI.getAllGroups(moduleObj.getModuleId(), appId, moduleName,groupType);
		}
		
		//db views
		List<FacilioView> dbViews = new ArrayList<>();	

		if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			dbViews = ViewAPI.getAllViews(appId, moduleName,viewType, getOnlyBasicViewDetails);
		} else {
			if (!moduleName.equals("approval")) {
				dbViews = ViewAPI.getAllViews(appId, moduleObj.getModuleId(), viewType, getOnlyBasicViewDetails);
			}
			// Temp...till mobile alarm module is split
			if (AccountUtil.getCurrentAccount().isFromMobile() && moduleName.equals(ContextNames.NEW_READING_ALARM)) {
				addMobilelarmViews(appId, modBean, dbViews, factoryViewsMap, viewGroups,viewType,groupType, getOnlyBasicViewDetails);
			}
		}

			factoryViewsMap.entrySet().removeIf(enrty -> {
				try {
					return (enrty.getValue().isHidden() ||
							(enrty.getValue().getViewSharing() != null && !enrty.getValue().getViewSharing().isAllowed(AccountUtil.getCurrentUser(),DelegationType.LIST_VIEWS)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			});

		if (CollectionUtils.isNotEmpty(dbViews)) {
			dbViews = filterAccessibleViews(appId, dbViews, fromBuilder);
		}

		if (!dbViews.isEmpty() && !viewGroups.isEmpty() && dbViews != null && viewGroups != null) {
			for(ViewGroups viewGroup : viewGroups) {
				List<FacilioView> groupBasedViews = dbViews.stream().filter(view -> view.getGroupId() == viewGroup.getId()).collect(Collectors.toList());
				if (groupBasedViews != null && groupBasedViews.size() > 0) {
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
		
		
				
		List<FacilioView> allViews = new ArrayList<>(factoryViewsMap.values());
		Boolean fetchByGroup = (Boolean) context.get(FacilioConstants.ContextNames.GROUP_STATUS);
		
		boolean isMainApp = (app != null && app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) || 
		(app == null && currentApp != null && currentApp.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));
		
		
		if (fetchByGroup != null && fetchByGroup) {
			
			List<FacilioView> customViews = new ArrayList<>();
			
			Optional<FacilioView> upcomingView = allViews.stream()
					.filter(view -> view.getIsDefault() != null && view.getIsDefault() && view.getName() != null && view.getName().equals("upcoming")).findFirst();

			Optional<FacilioView> myupcomingView = allViews.stream()
					.filter(view -> view.getIsDefault() != null && view.getIsDefault() && view.getName() != null && view.getName().equals("myupcoming")).findFirst();
			
			
			// groupViews from ViewFactory
			List<Map<String, Object>> factoryViewGroups = new ArrayList<>();
			if (isMainApp && (groupType == ViewGroups.ViewGroupType.TABLE_GROUP)) {
				factoryViewGroups = new ArrayList<>(ViewFactory.getGroupVsViews(moduleName));
			
				if (!factoryViewGroups.isEmpty()) {
						
					addChildModuleViews(appId, moduleName, factoryViewGroups, customViews,viewType, getOnlyBasicViewDetails);
					
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
						factoryViewGroups.add(4, groupDetails1);
					}
						
					int customGroupIdx = getCustomGroupIdx(factoryViewGroups);
					if (customGroupIdx != -1) {
						if (!customViews.isEmpty()) {
							Map<String, Object> mutatedDetail = new HashMap<>(factoryViewGroups.get(customGroupIdx));
							mutatedDetail.remove("type");
							mutatedDetail.put("views", customViews);
							factoryViewGroups.set(customGroupIdx, mutatedDetail);
						}
						else {
							factoryViewGroups.remove(customGroupIdx);
						}
					}
					
					if (upcomingView.isPresent()) {
						Map<String, Object> groupDetails1 = new HashMap<>();
						groupDetails1.put("name", "upcoming");
						groupDetails1.put("displayName", "Upcoming Work Orders");
						groupDetails1.put("views", Arrays.asList(upcomingView.get()));
						factoryViewGroups.add(groupDetails1);
					}
	
					if (myupcomingView.isPresent()) {
						Map<String, Object> groupDetails1 = new HashMap<>();
						groupDetails1.put("name", "myupcoming");
						groupDetails1.put("displayName", "My Upcoming Work Orders");
						groupDetails1.put("views", Arrays.asList(myupcomingView.get()));
						factoryViewGroups.add(groupDetails1);
					}
					
				}
				
				
				else {
					factoryViewGroups = new ArrayList<>();
					Map<String, Object> groupDetails = new HashMap<>();
					if (moduleObj != null && moduleObj.isCustom()) {
						groupDetails.put("name", "allViews");
						groupDetails.put("displayName", "All Views");
						groupDetails.put("views", allViews);
						factoryViewGroups.add(groupDetails);
					}
					else {
						groupDetails.put("name", "systemviews");
						groupDetails.put("displayName", "System Views");
						groupDetails.put("views", allViews.stream().filter(view -> view.getIsDefault() == null || view.getIsDefault()).collect(Collectors.toList()));
						factoryViewGroups.add(groupDetails);
						if (!customViews.isEmpty() ) {
							groupDetails = new HashMap<>();
							groupDetails.put("name", "customviews");
							groupDetails.put("displayName", "Custom Views");
							groupDetails.put("views", customViews);
							factoryViewGroups.add(groupDetails);
						}
					}
				}
			}
			
			sortGroupViews(factoryViewGroups, factoryViewsMap, viewGroups, moduleName);
			// TODO remove 
			
			if (AccountUtil.getCurrentAccount().isFromMobile()) {
				context.put(FacilioConstants.ContextNames.VIEW_LIST, viewGroups);
			}else {
				context.put(FacilioConstants.ContextNames.GROUP_VIEWS, viewGroups);
			}
						
			
		}
		else {
			if (moduleObj != null) { //moduleObj check is for approval module. Needs to handle in a better way
				if (!viewGroups.isEmpty() && viewGroups != null) {
					allViews = new ArrayList<>();
					for(ViewGroups viewGroup : viewGroups) {
						if (viewGroup.getViews() != null && !viewGroup.getViews().isEmpty()) {
							allViews.addAll(viewGroup.getViews());
						}
					}
				}
				else if (!isMainApp) {
					allViews = new ArrayList<>();
				}
			}
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
	
	private void sortGroupViews(List<Map<String, Object>> groupViews, Map<String, FacilioView> viewMap, List<ViewGroups> viewGroups, String moduleName) throws Exception {
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
								ApplicationContext applicationContext = AccountUtil.getCurrentApp();
								if (applicationContext == null) {
									applicationContext = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
								}
								LOGGER.info(String.format("ViewFactoryTracking - GetViewListCommand.sortGroupViews() - ModuleName - %s GroupName - %s ViewName - %s AppId - %d AppName - %s", moduleName, group.get("name"), viewName, applicationContext.getId(), applicationContext.getLinkName()));
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
	
	private void addChildModuleViews(Long appId, String moduleName, List<Map<String, Object>> groupViews, List<FacilioView> customViews, FacilioView.ViewType viewType, boolean getOnlyBasicViewDetails) throws Exception {
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

				List<FacilioView> childDbViews = ViewAPI.getAllViews(appId, childModule.getModuleId(),viewType, getOnlyBasicViewDetails);
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
	
	private void addMobilelarmViews(Long appId, ModuleBean modBean, List<FacilioView> dbViews, Map<String,FacilioView> viewMap, List<ViewGroups> viewGroups, FacilioView.ViewType viewType, ViewGroups.ViewGroupType groupType, boolean getOnlyBasicViewDetails) throws Exception {
		List<String> alarmModules = Arrays.asList(new String[] {ContextNames.BMS_ALARM, ContextNames.AGENT_ALARM, ContextNames.SENSOR_ROLLUP_ALARM});
		for(String moduleName: alarmModules) {
			FacilioModule alarmModule = modBean.getModule(moduleName);
			dbViews.addAll(ViewAPI.getAllViews(appId, alarmModule.getModuleId(), viewType, getOnlyBasicViewDetails));
			
			viewMap.putAll(ViewFactory.getModuleViews(moduleName, alarmModule));
			
			viewGroups.addAll(ViewAPI.getAllGroups(alarmModule.getModuleId(), -1, moduleName, groupType));
		}
		
	}

	private void addEditableAccess(List<FacilioView> dbViews, Boolean isPrivilegedAccess, Long currentUserId, Long superAdminUserId) {
		for (FacilioView view : dbViews) {
			boolean isLocked = view.getIsLocked() != null ? view.getIsLocked() : false;
			Long ownerId = view.getOwnerId() != -1 ? view.getOwnerId() : superAdminUserId;
			view.setEditable(isPrivilegedAccess || !isLocked || (isLocked && (ownerId.equals(currentUserId))));
		}
	}

	public List<FacilioView> filterAccessibleViews(long currAppId, List<FacilioView> dbViews, boolean fromBuilder) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		List<FacilioView> resultViews = new ArrayList<>();

		User currentUser = AccountUtil.getCurrentUser();
		long currUserAppId = currentUser.getApplicationId();
		if (currUserAppId == -1) {
			currUserAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		}
		Long currentUserId = currentUser.getId();
		Long currentUserRoleId = currentUser.getRoleId();
		Boolean isSuperAdmin = currentUser.isSuperAdmin();
		Long superAdminUserId = AccountUtil.getOrgBean().getSuperAdmin(orgId).getOuid();
		// Admin/CAFMAdmin Role has equal privileges as SuperAdmin Role
		Criteria roleNameCriteria = new Criteria();
		String[] roleNames = { FacilioConstants.DefaultRoleNames.ADMIN, FacilioConstants.DefaultRoleNames.MAINTENANCE_ADMIN, FacilioConstants.DefaultRoleNames.CAFM_ADMIN };
		roleNameCriteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", StringUtils.join(roleNames, ","), StringOperators.IS));

		List<Role> adminRoles = AccountUtil.getRoleBean().getRoles(roleNameCriteria);
		List<Long> adminRoleIds = CollectionUtils.isNotEmpty(adminRoles) ? adminRoles.stream().map(Role::getId).collect(Collectors.toList()) : new ArrayList<>();

		boolean isPrivilegedRole = currentUser.getRole().isPrevileged() && (currAppId == currUserAppId);
		boolean isAdmin = adminRoleIds.contains(currentUserRoleId);
		boolean isPrivilegedAccess = isSuperAdmin || isAdmin || isPrivilegedRole;

		if (!isPrivilegedAccess) {
			List<Long> viewIds = new ArrayList<>();
			viewIds = dbViews.stream().map(FacilioView::getId).collect(Collectors.toList());

			Map<Long, SharingContext<SingleSharingContext>> sharingMap = SharingAPI.getSharing(viewIds, ModuleFactory.getViewSharingModule(), SingleSharingContext.class,
					FieldFactory.getSharingFields(ModuleFactory.getViewSharingModule()));

			List<Long> viewIdsWithPermission = new ArrayList<>();
			if (MapUtils.isNotEmpty(sharingMap)) {
				viewIdsWithPermission = SharingAPI.getAllowedParentIds(sharingMap, DelegationType.LIST_VIEWS);
			}

			for (FacilioView view : dbViews) {
				if (!view.isHidden()) {
					Long ownerId = view.getOwnerId() != -1 ? view.getOwnerId() : superAdminUserId;
					if (ownerId.equals(currentUserId) || !sharingMap.containsKey(view.getId()) || viewIdsWithPermission.contains(view.getId())) {
						resultViews.add(view);
					}
				}
			}
		} else {
			resultViews = dbViews.stream().filter(facilioView -> !facilioView.isHidden()).collect(Collectors.toList());
		}
		if(!fromBuilder){
			resultViews = resultViews.stream().filter(view -> view.getStatus()).collect(Collectors.toList());
		}

		// removing all views which are neither list view nor calendar view
		resultViews.removeIf(view -> !(view.isListView() || view.isCalendarView()));

		addEditableAccess(dbViews, isPrivilegedAccess, currentUserId, superAdminUserId);

		return resultViews;
	}
	
}
