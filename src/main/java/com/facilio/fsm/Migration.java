//package com.facilio.fsm;
//
//import com.facilio.accounts.util.AccountUtil;
//import com.facilio.beans.ModuleBean;
//import com.facilio.bmsconsole.ModuleWidget.APIModuleWidgets;
//import com.facilio.bmsconsole.commands.TransactionChainFactory;
//import com.facilio.bmsconsole.context.*;
//import com.facilio.bmsconsole.forms.FacilioForm;
//import com.facilio.bmsconsole.util.*;
//import com.facilio.bmsconsole.view.FacilioView;
//import com.facilio.bmsconsoleV3.signup.util.AddModuleViewsAndGroups;
//import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
//import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
//import com.facilio.chain.FacilioChain;
//import com.facilio.chain.FacilioContext;
//import com.facilio.command.FacilioCommand;
//import com.facilio.constants.FacilioConstants;
//import com.facilio.fsm.signup.*;
//import com.facilio.fw.BeanFactory;
//import com.facilio.modules.FacilioModule;
//import com.facilio.modules.FieldFactory;
//import com.facilio.modules.FieldType;
//import com.facilio.modules.fields.FacilioField;
//import com.facilio.modules.fields.LookupField;
//import com.facilio.v3.context.Constants;
//import lombok.extern.log4j.Log4j;
//import org.apache.commons.chain.Context;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections4.MapUtils;
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//@Log4j
//public class Migration extends FacilioCommand {
//    @Override
//    public boolean executeCommand(Context context) throws Exception {
//        // forms, views, widgets and pages
//        // Have migration commands for each org
//        // Transaction is only org level. If failed, have to continue from the last failed org and not from first
//        long orgId = AccountUtil.getCurrentOrg().getId();
//        printMsg("Started For -- "+AccountUtil.getCurrentOrg().getId());
//        // write code here
//        printMsg("Started adding tabs and tab groups-- "+AccountUtil.getCurrentOrg().getId());
////                addTabsAndTabGroups();
//        printMsg("Successfully added tabs and tab groups-- "+AccountUtil.getCurrentOrg().getId());
//
//        ModuleBean modBean = Constants.getModBean();
//        printMsg("Modules and field addition started-- "+AccountUtil.getCurrentOrg().getId());
//        FacilioModule servicePMTriggerMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
//        if(servicePMTriggerMod==null){
//            ServicePMTriggerModule servicePMTriggerModule = new ServicePMTriggerModule();
//            servicePMTriggerModule.addData();
//        }
//        printMsg("Successfully added Modules and fields for servicePMTriggerModule -- "+AccountUtil.getCurrentOrg().getId());
//        FacilioModule servicePlannedMaintenanceMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
//        if(servicePlannedMaintenanceMod==null){
//            ServicePlannedMaintenanceModule servicePlannedMaintenanceModule = new ServicePlannedMaintenanceModule();
//            servicePlannedMaintenanceModule.addData();
//        }
//        printMsg("Successfully added Modules and fields for servicePlannedMaintenanceModule -- "+AccountUtil.getCurrentOrg().getId());
//        FacilioModule servicePlanMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
//        if(servicePlanMod==null) {
//            ServicePlanModule servicePlanModule = new ServicePlanModule();
//            servicePlanModule.addData();
//
//            LookupField servicePlan = FieldFactory.getDefaultField("servicePlan","Job Plan","SERVICE_PLAN", FieldType.LOOKUP);
//            servicePlan.setRequired(true);
//            servicePlan.setLookupModule(servicePlanMod);
//            servicePlan.setModule(servicePlannedMaintenanceMod);
//            modBean.addField(servicePlan);
//        }
//        printMsg("Successfully added Modules and fields for servicePlanModule -- "+AccountUtil.getCurrentOrg().getId());
//        FacilioModule serviceTaskTemplateMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE);
//        if(serviceTaskTemplateMod==null) {
//            ServiceTaskTemplateModule serviceTaskTemplateModule = new ServiceTaskTemplateModule();
//            serviceTaskTemplateModule.addData();
//        }
//        printMsg("Successfully added Modules and fields for serviceTaskTemplateModule -- "+AccountUtil.getCurrentOrg().getId());
//        FacilioModule servicePlanItemsMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ITEMS);
//        if(servicePlanItemsMod==null) {
//            ServicePlanItemsModule servicePlanItemsModule = new ServicePlanItemsModule();
//            servicePlanItemsModule.addData();
//        }
//        printMsg("Successfully added Modules and fields for servicePlanItemsModule -- "+AccountUtil.getCurrentOrg().getId());
//        FacilioModule servicePlanToolsMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_TOOLS);
//        if(servicePlanToolsMod==null) {
//            ServicePlanToolsModule servicePlanToolsModule = new ServicePlanToolsModule();
//            servicePlanToolsModule.addData();
//        }
//        printMsg("Successfully added Modules and fields for servicePlanToolsModule -- "+AccountUtil.getCurrentOrg().getId());
//        FacilioModule servicePlanServicesMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_SERVICES);
//        if(servicePlanServicesMod==null) {
//            ServicePlanServicesModule servicePlanServicesModule = new ServicePlanServicesModule();
//            servicePlanServicesModule.addData();
//        }
//        printMsg("Successfully added Modules and fields for servicePlanServicesModule -- "+AccountUtil.getCurrentOrg().getId());
//        FacilioModule servicePMTemplateMod = modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
//        if(servicePMTemplateMod==null) {
//            ServicePMTemplateModule servicePMTemplateModule = new ServicePMTemplateModule();
//            servicePMTemplateModule.addData();
//        }
//        printMsg("Successfully added Modules and fields for servicePMTemplateModule -- "+AccountUtil.getCurrentOrg().getId());
//
//        printMsg("Modules and fields addition completed-- "+AccountUtil.getCurrentOrg().getId());
//
//        ApplicationContext fsmApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);
//        if(fsmApp!=null){
//            long fsmAppId = fsmApp.getId();
//            printMsg("Views Migration started--------->");
//            //service pm
//            List<FacilioView> servicePMViews = ViewAPI.getAllViews(fsmAppId,FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE, FacilioView.ViewType.TABLE_LIST,true);
//            if(CollectionUtils.isEmpty(servicePMViews)){
//                ServicePlannedMaintenanceModule servicePlannedMaintenanceModule = new ServicePlannedMaintenanceModule();
//                List<Map<String, Object>> servicePMViewGroup = servicePlannedMaintenanceModule.getViewsAndGroups();
//                servicePMViewGroup = getFsmAppViewGroup(servicePMViewGroup);
//                if(CollectionUtils.isNotEmpty(servicePMViewGroup)){
//                    addViews(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE, servicePMViewGroup, Arrays.asList(fsmApp));
//                    printMsg("Added Service PM Views--------->");
//                }
//            }
//            // master pm
//            List<FacilioView> masterPMViews = ViewAPI.getAllViews(fsmAppId,FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE, FacilioView.ViewType.TABLE_LIST,true);
//            if(CollectionUtils.isEmpty(masterPMViews)){
//                ServicePMTemplateModule servicePMTemplateModule = new ServicePMTemplateModule();
//                List<Map<String, Object>> servicePMTemplateViewGroup = servicePMTemplateModule.getViewsAndGroups();
//                servicePMTemplateViewGroup = getFsmAppViewGroup(servicePMTemplateViewGroup);
//                if(CollectionUtils.isNotEmpty(servicePMTemplateViewGroup)){
//                    addViews(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE, servicePMTemplateViewGroup, Arrays.asList(fsmApp));
//                    printMsg("Added Service PM Template Views--------->");
//                }
//            }
//            // service plan
//            List<FacilioView> servicePlanViews = ViewAPI.getAllViews(fsmAppId,FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN, FacilioView.ViewType.TABLE_LIST,true);
//            if(CollectionUtils.isEmpty(servicePlanViews)){
//                ServicePlanModule servicePlanModule = new ServicePlanModule();
//                List<Map<String, Object>> servicePlanViewGroup = servicePlanModule.getViewsAndGroups();
//                servicePlanViewGroup = getFsmAppViewGroup(servicePlanViewGroup);
//                if(CollectionUtils.isNotEmpty(servicePlanViewGroup)){
//                    addViews(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN, servicePlanViewGroup, Arrays.asList(fsmApp));
//                    printMsg("Added Service Plan Views--------->");
//                }
//            }
//            printMsg("Views Migration completed--------->");
//
//            printMsg("Forms Migration started--------->");
//            //servicePM
//            List<FacilioForm> servicePMForms = FormsAPI.getDBFormList(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,null,null,false,false,false,fsmAppId);
//            if(CollectionUtils.isEmpty(servicePMForms)){
//                ServicePlannedMaintenanceModule servicePlannedMaintenanceModule = new ServicePlannedMaintenanceModule();
//                List<FacilioForm> servicePMModForms  = servicePlannedMaintenanceModule.getModuleForms();
//                servicePMModForms = getFsmAppFormForModule(servicePMModForms);
//                if(CollectionUtils.isNotEmpty(servicePMModForms)){
//                    SignupUtil.addFormForModules(servicePMModForms, Arrays.asList(fsmApp), FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
//                    printMsg("Added Service PM Forms--------->");
//                }
//            }
//            // master pm
//            List<FacilioForm> servicePMTemplateForms = FormsAPI.getDBFormList(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,null,null,false,false,false,fsmAppId);
//            if(CollectionUtils.isEmpty(servicePMTemplateForms)){
//                ServicePMTemplateModule servicePMTemplateModule = new ServicePMTemplateModule();
//                List<FacilioForm> servicePMTemplateModForms  = servicePMTemplateModule.getModuleForms();
//                servicePMTemplateModForms = getFsmAppFormForModule(servicePMTemplateModForms);
//                if(CollectionUtils.isNotEmpty(servicePMTemplateModForms)){
//                    SignupUtil.addFormForModules(servicePMTemplateModForms, Arrays.asList(fsmApp), FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE);
//                    printMsg("Added Service PM Template Forms--------->");
//                }
//            }
//            //service plan
//            List<FacilioForm> servicePlanForms = FormsAPI.getDBFormList(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,null,null,false,false,false,fsmAppId);
//            if(CollectionUtils.isEmpty(servicePlanForms)){
//                ServicePlanModule servicePlanModule = new ServicePlanModule();
//                List<FacilioForm> servicePlanModForms  = servicePlanModule.getModuleForms();
//                servicePlanModForms = getFsmAppFormForModule(servicePlanModForms);
//                if(CollectionUtils.isNotEmpty(servicePlanModForms)){
//                    SignupUtil.addFormForModules(servicePlanModForms, Arrays.asList(fsmApp), FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN);
//                    printMsg("Added Service Plan Forms--------->");
//                }
//            }
//            printMsg("Forms Migration Completed--------->");
//
//            printMsg("Pages and widgets Migration Started--------->");
//
//            String[] moduleNames = new String[]{FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN,FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE};
//            for(String moduleName : moduleNames){
//                printMsg("Adding widgets started for "+moduleName);
//                addUniqueWidgetConfig(moduleName);  //widgetAdding
//                printMsg("Adding widgets completed for "+moduleName);
//
//                printMsg("Page adding started for "+moduleName);
//                addPage(moduleName);   //page adding
//                printMsg("Page adding completed for "+moduleName);
//            }
//            printMsg("Pages and widgets Migration Completed--------->");
//        }
//
//
//        printMsg("Completed For -- "+AccountUtil.getCurrentOrg().getId());
//
//
//        return false;
//    }
//    public void addUniqueWidgetConfig(String moduleName) throws Exception {
//        ModuleWidgets widgetList = new ModuleWidgets();
//
//        if (moduleName.equals(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE)) {
//            widgetList = APIModuleWidgets.getServicePlannedMaintenanceWidgets().get();
//        } else if (moduleName.equals(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE)) {
//            widgetList = APIModuleWidgets.getServicePMTemplateWidgets().get();
//        } else {
//            return;
//        }
//
//        if (widgetList != null) {
//            List<WidgetContext> widgets = widgetList.getWidgets();
//            if (CollectionUtils.isNotEmpty(widgets)) {
//                for (WidgetContext widget : widgets) {
//                    if (widget != null) {
//                        List<WidgetConfigContext> widgetConfigs = widget.getWidgetConfigs();
//
//                        if (CollectionUtils.isNotEmpty(widgetConfigs)) {
//                            List<WidgetConfigContext> widgetConfigsToRemove = new ArrayList<>();
//                            for (WidgetConfigContext widgetConfig : widgetConfigs) {
//                                WidgetConfigContext existingWidgetConfig = WidgetAPI.getWidgetConfiguration(widget.getWidgetType(), -1L, widgetConfig.getName(), widgetConfig.getLayoutType());
//
//                                if (existingWidgetConfig != null) {
//                                    printMsg("Widget Config already exists  config name:"+existingWidgetConfig.getName());
//                                    widgetConfigsToRemove.add(widgetConfig);
//                                }
//
//                            }
//                            if (CollectionUtils.isNotEmpty(widgetConfigsToRemove)) {
//                                widget.getWidgetConfigs().removeAll(widgetConfigsToRemove);
//                            }
//                        }
//                    }
//                }
//            }
//            if (CollectionUtils.isNotEmpty(widgets)) {
//                WidgetAPI.addWidgets(moduleName, widgets);
//            }
//        }
//
//    }
//
//    public void addPage(String moduleName) throws Exception {
//        Map<String, List<PagesContext>> appNameVsSystemPages = null;
//        List<String> appNameToRemovePage = new ArrayList<>();
//
//        if (moduleName.equals(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE)) {
//            ServicePlannedMaintenanceModule servicePMModule = new ServicePlannedMaintenanceModule();
//            appNameVsSystemPages = servicePMModule.fetchSystemPageConfigs();
//        } else if (moduleName.equals(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE)) {
//            ServicePMTemplateModule servicePMTemplateModule = new ServicePMTemplateModule();
//            appNameVsSystemPages = servicePMTemplateModule.fetchSystemPageConfigs();
//        } else if (moduleName.equals(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN)) {
//            ServicePlanModule servicePlanModule = new ServicePlanModule();
//            appNameVsSystemPages = servicePlanModule.fetchSystemPageConfigs();
//        }
//
//
//        FacilioModule module = Constants.getModBean().getModule(moduleName);
//        for (Map.Entry<String, List<PagesContext>> entry : appNameVsSystemPages.entrySet()) {
//            String appName = entry.getKey();
//            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
//            if (app == null) {
//                printMsg("App not found for appName:"+appName);
//                continue;
//            }
//            PagesContext dbDefaultPage = CustomPageAPI.getDefaultPage(app.getId(), module.getModuleId());
//            if (dbDefaultPage != null) {
//                printMsg("Default Page for "+moduleName+" already exists in appName:"+appName);
//                appNameToRemovePage.add(appName);
//            }
//        }
//
//        for (String appName : appNameToRemovePage) {
//            appNameVsSystemPages.remove(appName);
//        }
//
//        if (MapUtils.isNotEmpty(appNameVsSystemPages)) {
//            PagesUtil.addSystemPages(moduleName, appNameVsSystemPages);
//        }
//    }
//
//    public  List<FacilioForm> getFsmAppFormForModule(List<FacilioForm> forms) throws Exception{
//        List<FacilioForm> formsList = new ArrayList<>();
//        if(CollectionUtils.isNotEmpty(forms)){
//            for(FacilioForm form : forms){
//                List<String> appNames = form.getAppLinkNamesForForm();
//                boolean check = false;
//                for(String app : appNames){
//                    if(app.equals(FacilioConstants.ApplicationLinkNames.FSM_APP)){
//                        check = true;
//                        break;
//                    }
//                }
//                if(check){
//                    form.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP));
//                    formsList.add(form);
//                }
//            }
//        }
//        return formsList;
//    }
//    public  List<Map<String,Object>> getFsmAppViewGroup(List<Map<String,Object>> viewGroupList){
//        if(CollectionUtils.isNotEmpty(viewGroupList)){
//            for(Map<String,Object> viewGroup : viewGroupList){
//                viewGroup.put("appLinkNames", Arrays.asList(FacilioConstants.ApplicationLinkNames.FSM_APP));
//                ArrayList<FacilioView> views = (ArrayList<FacilioView>) viewGroup.get("views");
//                List<String> appLinkNames = new ArrayList<>();
//                appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
//                for(FacilioView view : views){
//                    view.setAppLinkNames(appLinkNames);
//                }
//            }
//        }
//        return viewGroupList;
//    }
//    public void addViews(String moduleName, List<Map<String, Object>> groupVsViews, List<ApplicationContext> allApplications) throws Exception {
//        printMsg("Started adding Views and Groups for module - " + moduleName);
//        long orgId = AccountUtil.getCurrentOrg().getOrgId();
//        long orgUserId = AccountUtil.getOrgBean().getSuperAdmin(orgId).getOuid();
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        FacilioModule module = modBean.getModule(moduleName);
//        printMsg("1");
//        Map<String, ApplicationContext> allApplicationMap = new HashMap<>();
//        if (CollectionUtils.isNotEmpty(allApplications)) {
//            allApplicationMap = allApplications.stream().collect(Collectors.toMap(ApplicationContext::getLinkName, Function.identity()));
//        }
//        printMsg("allApplicationMap -- "+allApplicationMap);
//        List<Long> allApplicationIds = allApplications.stream().map(ApplicationContext::getId).collect(Collectors.toList());
//        printMsg("allApplicationIds -- "+allApplicationIds);
//        printMsg("1");
//        Map<Long, Map<String, ViewGroups>> allViewGroupsMap = new HashMap<>();
//        allApplicationIds.forEach(appId -> allViewGroupsMap.put(appId, new HashMap<>()));
//        List<FacilioField> allModuleFields = modBean.getAllFields(moduleName);
//        // For "hidden-all" & "pendingapproval"
//        FacilioView allView = new FacilioView();
//        boolean containsAll = false;
//        boolean containsPendingApproval = false;
//        printMsg("1");
//        try {
//            for (Map<String, Object> group : groupVsViews) {
//                //Add ViewGroups
//                String groupName = (String) group.get("name");
//                ArrayList<FacilioView> views = (ArrayList<FacilioView>) group.get("views");
//                List<String> groupAppLinkNamesFromMap = (List<String>) group.get("appLinkNames");
//                printMsg("1");
//                List<String> groupAppLinkNames = new ArrayList<>();
//                if (CollectionUtils.isNotEmpty(groupAppLinkNamesFromMap)){
//                    groupAppLinkNames.addAll(groupAppLinkNamesFromMap);
//                    if(SignupUtil.maintenanceAppSignup()) {
//                        groupAppLinkNames.remove(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
//                    }
//                } else{
//                    groupAppLinkNames.add(SignupUtil.getSignupApplicationLinkName());
//                }
//                for (String groupAppLinkName : groupAppLinkNames) {
//                    long appId = allApplicationMap.get(groupAppLinkName).getId();
//                    long groupId = addViewGroup(orgId, appId, moduleName, group);
//                    allViewGroupsMap.get(appId).put(groupName, ViewAPI.getViewGroup(groupId));
//                }
//                printMsg("1");
//                for (FacilioView view : views) {
//                    List<String> viewAppLinkNames = new ArrayList<>();
//                    if (CollectionUtils.isNotEmpty(view.getAppLinkNames())) {
//                        viewAppLinkNames.addAll(view.getAppLinkNames());
//                    } else {
//                        viewAppLinkNames.add(SignupUtil.getSignupApplicationLinkName());
//                    }
//                    if(SignupUtil.maintenanceAppSignup()) {
//                        viewAppLinkNames.remove(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
//                    }
//                    FacilioView newViewObj = view;
//                    for (String viewAppLinkName : viewAppLinkNames) {
//                        view = new FacilioView(newViewObj);
//                        long groupId = -1;
//                        String viewName = view.getName();
//                        long appId = allApplicationMap.get(viewAppLinkName).getId();
//
//                        //Add/ Use ViewGroups
//                        if (allViewGroupsMap.get(appId).containsKey(groupName)) {
//                            groupId = allViewGroupsMap.get(appId).get(groupName).getId();
//                        } else {
//                            groupId = addViewGroup(orgId, appId, moduleName, group);
//                            allViewGroupsMap.get(appId).put(groupName, ViewAPI.getViewGroup(groupId));
//                        }
//
//                        //Add Views
//                        view.setId(-1);
//                        view.setAppId(appId);
//                        view.setDefault(true);
//                        view.setLocked(false);
//                        view.setIsListView(true);
//                        view.setGroupId(groupId);
//                        view.setOwnerId(orgUserId);
//                        view.setModuleName(moduleName);
//                        view.setModuleId(module.getModuleId());
//                        view.setType(FacilioView.ViewType.TABLE_LIST);
//                        view.setStatus(true);
//                        printMsg("view name -- "+view.getName());
//                        long viewId = AddModuleViewsAndGroups.addView(view, orgId);
//                        view.setId(viewId);
//
//                        //Add Columns And SortFields
//                        AddModuleViewsAndGroups.addColumnsAndSortField(moduleName, modBean, view, allModuleFields);
//
//                        if (viewName.equals("all")){
//                            containsAll = true;
//                            allView = view;
//                        }
//                        if (viewName.equals("pendingapproval")) {
//                            containsPendingApproval = true;
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.info("Error occurred in ORGID - " + orgId + " ModuleId - " + module.getModuleId() + " ModuleName - " + moduleName + " Error - " + e.getMessage());
//            throw e;
//        }
//    }
//    public long addViewGroup(long orgId, long appId, String moduleName, Map<String, Object> group) throws Exception {
//        long groupId = -1;
//        ViewGroups viewGroup = new ViewGroups();
//        String groupName = (String) group.get("name");
//        String groupDisplayName = (String) group.get("displayName");
//
//        viewGroup.setName(groupName);
//        viewGroup.setDisplayName(groupDisplayName);
//        viewGroup.setAppId(appId);
//        groupId = ViewAPI.addViewGroup(viewGroup, orgId, moduleName);
//        return groupId;
//    }
//    private void addTabsAndTabGroups()throws Exception{
//
//        ApplicationContext rmApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FSM_APP);
//        List<ApplicationLayoutContext> layouts = ApplicationApi.getLayoutsForAppId(rmApp.getId());
//        ApplicationLayoutContext layout = null;
//        for (ApplicationLayoutContext appLayout : layouts) {
//            if (appLayout.getLayoutDeviceTypeEnum() == ApplicationLayoutContext.LayoutDeviceType.WEB) {
//                layout = appLayout;
//            }
//        }
//        long webGroupId = 0l;
//        FacilioChain chain;
//        FacilioContext chainContext;
//        for (WebTabGroupContext webTabGroupContext : getWebTabGroups(layout.getApplicationId(), layout.getId())) {
//            if (!webTabGroupContext.getName().equals("ONLY_TABS")) {
//                chain = TransactionChainFactory.getAddOrUpdateTabGroup();
//                chainContext = chain.getContext();
//                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, webTabGroupContext);
//                chain.execute();
//                webGroupId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
//            }
//            webTabGroupContext.setId(webGroupId);
//            List<WebTabContext> tabs = getGroupNameVsTabsMap(layout.getApplicationId(), layout.getId())
//                    .get(webTabGroupContext.getRoute());
//            for (WebTabContext webTabContext : tabs) {
//                WebTabContext webtab = ApplicationApi.getWebTabForApplication(layout.getApplicationId(), webTabContext.getRoute());
//                long tabId = 0l;
//                if (webtab != null) {
//                    tabId = webtab.getId();
//                } else {
//                    chain = TransactionChainFactory.getAddOrUpdateTabChain();
//                    chainContext = chain.getContext();
//                    chainContext.put(FacilioConstants.ContextNames.WEB_TAB, webTabContext);
//                    chain.execute();
//                    tabId = (long) chainContext.get(FacilioConstants.ContextNames.WEB_TAB_ID);
//                }
//                webTabContext.setId(tabId);
//            }
//            if (CollectionUtils.isNotEmpty(tabs) && !webTabGroupContext.getName().equals("ONLY_TABS")) {
//                chain = TransactionChainFactory.getCreateAndAssociateTabGroupChain();
//                chainContext = chain.getContext();
//                chainContext.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
//                chainContext.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, webGroupId);
//                chain.execute();
//            }
//        }
//    }
//    private Map<String, WebTabContext> getWebTabs(long appId) throws Exception {
//        Map<String, WebTabContext> tabsMap = new HashMap<String, WebTabContext>();
//
//        List<WebTabContext> tabs = Arrays.asList(
//                new WebTabContext("Planned Maintenance","servicePlannedMaintenance",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE)), null, null,null,appId),
//                new WebTabContext("Master PM","servicePMTemplate",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE)), null, null,null,appId),
//                new WebTabContext("Job Plan","servicePlan",WebTabContext.Type.MODULE,getModuleIdsListFromModuleNames(Arrays.asList(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN)), null, null,null,appId)
//        );
//        for (WebTabContext webTab : tabs) {
//            tabsMap.put(webTab.getRoute(), webTab);
//        }
//        return tabsMap;
//    }
//    private List<Long> getModuleIdsListFromModuleNames(List<String> moduleNames) throws Exception {
//        List<Long> moduleIds = new ArrayList<>();
//        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        if(CollectionUtils.isNotEmpty(moduleNames)){
//            for (String moduleName : moduleNames) {
//                moduleIds.add(modBean.getModule(moduleName).getModuleId());
//            }
//        }
//        return moduleIds;
//    }
//    public List<WebTabGroupContext> getWebTabGroups(long appId, long layoutId) throws Exception {
//        Map<String,WebTabContext> tabsMap = getWebTabs(appId);
//        List<WebTabGroupContext> webTabGroups = Arrays.asList(
//                new WebTabGroupContext(Arrays.asList(tabsMap.get("servicePMTemplate"),tabsMap.get("servicePlannedMaintenance"),tabsMap.get("servicePlan")), "Planned Maintenance", "plannedMaintenance", 6, 4, null,layoutId,IconType.service_pm)
//        );
//        return webTabGroups;
//    }
//    public Map<String, List<WebTabContext>> getGroupNameVsTabsMap(long appId,long layoutId) throws Exception {
//        Map<String, List<WebTabContext>> groupNameVsTabsMap = new HashMap<>();
//        for(WebTabGroupContext webTabGroup : getWebTabGroups(appId,layoutId)){
//            groupNameVsTabsMap.put(webTabGroup.getRoute(),webTabGroup.getWebTabs());
//        }
//        return groupNameVsTabsMap;
//    }
//    // to print in both logs and web
//    private void printMsg(String message) throws Exception {
//        LOGGER.info(message);
//
//    }
//}
