package com.facilio.bmsconsoleV3.signup.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.TemplatePageUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigChain;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class PagesUtil {

    public static void addTemplatePage(String moduleName, Map<String, List<PagesContext>> appNameVsTemplatePages) throws Exception {
        for (Map.Entry<String, List<PagesContext>> entry : appNameVsTemplatePages.entrySet()) {
            String appName = entry.getKey();
            if (appName.equals("newapp") && SignupUtil.maintenanceAppSignup()) {
                continue;
            }
            LOGGER.info("Started adding template page for orgId --" + AccountUtil.getCurrentOrg().getOrgId() + " for module --" + moduleName + " and appName" + appName);

            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            Objects.requireNonNull(app, "App doesn't exists for appName-- " + appName);
            long appId = app.getId();

            for (PagesContext templatePage : entry.getValue()) {
                templatePage.setIsTemplate(true);
                templatePage.setIsDefaultPage(false);
                templatePage.setAppId(appId);
                templatePage.setSequenceNumber(0D);
                templatePage.setStatus(false);
                templatePage.setModuleName(moduleName);

                addPage(templatePage.getAppId(), moduleName, templatePage, true);
            }
            LOGGER.info("Completed adding template page for orgId --" + AccountUtil.getCurrentOrg().getOrgId() + " for module --" + moduleName);
        }
    }

    public static void addSystemPages(String moduleName, Map<String, List<PagesContext>> appNameVsPages) throws Exception {
        for (Map.Entry<String, List<PagesContext>> entry : appNameVsPages.entrySet()) {
            String appName = entry.getKey();
            if (appName.equals("newapp") && SignupUtil.maintenanceAppSignup()) {
                continue;
            }
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            Objects.requireNonNull(app, "App doesn't exists for appName -- " + appName);
            long appId = app.getId();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            boolean isDefaultPageExists = CustomPageAPI.getDefaultPage(appId, module.getModuleId()) != null;

            if (!isDefaultPageExists) {
                PagesContext defaultPage = null;
                for (PagesContext page : entry.getValue()) {
                    if (page.getIsDefaultPage() != null && page.getIsDefaultPage()) {
                        defaultPage = page;
                        break;
                    }
                }

                if (defaultPage != null) {
                    entry.getValue().remove(defaultPage);
                    LOGGER.info("Started adding default page for orgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " for module -- " + moduleName);
                    defaultPage.setIsDefaultPage(true);
                    defaultPage.setIsTemplate(false);
                    defaultPage.setAppId(appId);
                    defaultPage.setStatus(true);
                    defaultPage.setModuleName(moduleName);
                    isDefaultPageExists = true;
                    addPage(defaultPage.getAppId(), moduleName, defaultPage, false);
                    LOGGER.info("Completed adding default page for orgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " for module -- " + moduleName + " and appName -- " + appName);
                }
            }

            for (PagesContext page : entry.getValue()) {
                LOGGER.info("Started adding page for orgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " for module -- " + moduleName + " and appName -- " + appName);
                if (!isDefaultPageExists) {
                    page.setIsDefaultPage(true);
                    isDefaultPageExists = true;
                    LOGGER.info("Page is set as default page as default page doesn't exists in the list");
                } else {
                    page.setIsDefaultPage(false);
                }
                page.setIsTemplate(false);
                page.setAppId(appId);
                page.setStatus(true);
                page.setModuleName(moduleName);
                addPage(page.getAppId(), moduleName, page, false);
                LOGGER.info("Completed adding page for orgId -- " + AccountUtil.getCurrentOrg().getOrgId() + " for module -- " + moduleName + " and appName -- " + appName);
            }
        }
    }

    private static void addPage(long appId, String moduleName, PagesContext page, boolean isTemplate) throws Exception {

        FacilioChain pageChain = TransactionChainFactory.getCreateCustomPageChain();
        FacilioContext pageChainContext = pageChain.getContext();
        pageChainContext.put(FacilioConstants.CustomPage.CUSTOM_PAGE, page);
        pageChainContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        pageChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, true);
        pageChainContext.put(FacilioConstants.CustomPage.IS_TEMPLATE, isTemplate);

        ApplicationContext app = ApplicationApi.getApplicationForId(appId);
        Objects.requireNonNull(app, "Invalid appId");
        pageChainContext.put(FacilioConstants.ContextNames.APP_ID, appId);
        pageChain.execute();
        long pageId = (long) pageChainContext.get(FacilioConstants.CustomPage.PAGE_ID);
        Objects.requireNonNull(page.getLayouts(), "Layout(s) should be defined for page - "+ page.getName());
        LOGGER.info("| Started adding Layouts for page -- "+ page.getName());
        addLayouts(appId, moduleName, true, pageId, page.getLayouts());
        LOGGER.info("| Completed adding Layouts for page -- "+ page.getName()+"\n");

    }

    public static void addLayouts(long appId, String moduleName, boolean isSystem, long pageId, Map<String, List<PageTabContext>> layouts) throws Exception {
        for (Map.Entry<String, List<PageTabContext>> layout : layouts.entrySet()) {
            Map<Long, List<PageTabContext>> layoutTabsMap = new HashMap<>();
            PagesContext.PageLayoutType layoutType = PagesContext.PageLayoutType.valueOf(layout.getKey());
            long layoutId = CustomPageAPI.getLayoutIdForPageId(pageId, layoutType);
            layoutTabsMap.put(layoutId, layout.getValue());
            FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(layout.getValue()), "Tab(s) should be defined for Layout - "+layoutType.name());
            LOGGER.info("|| Started adding Tab(s) for "+layoutType.name());
            addTabs(appId, moduleName, isSystem, layoutType, layoutTabsMap);
            LOGGER.info("|| Completed adding Tab(s) for "+layoutType.name());
        }
    }

    private static void addTabs(long appId, String moduleName, boolean isSystem, PagesContext.PageLayoutType layoutType, Map<Long, List<PageTabContext>> layoutTabsMap) throws Exception {
        FacilioChain tabChain = TransactionChainFactory.getAddPageTabsChain();
        FacilioContext tabChainContext = tabChain.getContext();
        tabChainContext.put(FacilioConstants.CustomPage.LAYOUT_TABS_MAP, layoutTabsMap);
        tabChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        tabChain.execute();

        Map<Long, List<PageColumnContext>> tabColumnsMap = new HashMap<>();
        for (PageTabContext tab : layoutTabsMap.values().stream().flatMap(List::stream).collect(Collectors.toList())) {
            FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(tab.getColumns()), "Column(s) should be defined for Tab - "+tab.getName());
            tabColumnsMap.put(tab.getId(), tab.getColumns());
        }
        LOGGER.info("||| Started adding columns for Tabs");
        addColumns(appId, moduleName, isSystem, layoutType, tabColumnsMap);
        LOGGER.info("||| Completed adding columns for Tabs");
    }

    private static void addColumns(long appId, String moduleName, boolean isSystem, PagesContext.PageLayoutType layoutType, Map<Long, List<PageColumnContext>> tabColumnsMap) throws Exception {
        FacilioChain columnsChain = TransactionChainFactory.getAddPageColumnsChain();
        FacilioContext columnsChainContext = columnsChain.getContext();
        columnsChainContext.put(FacilioConstants.CustomPage.TAB_COLUMNS_MAP, tabColumnsMap);
        columnsChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        columnsChain.execute();

        Map<Long, List<PageSectionContext>> columnSectionsMap = new HashMap<>();
        for (PageColumnContext column : tabColumnsMap.values().stream().flatMap(List::stream).collect(Collectors.toList())) {
            FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(column.getSections()), "Section(s) should be defined for Column - "+column.getName());
            columnSectionsMap.put(column.getId(), column.getSections());
        }

        LOGGER.info("|||| Started adding sections for columns");
        addSections(appId, moduleName, isSystem, layoutType, columnSectionsMap);
        LOGGER.info("|||| Completed adding sections for columns");
    }

    private static void addSections(long appId, String moduleName, boolean isSystem, PagesContext.PageLayoutType layoutType, Map<Long, List<PageSectionContext>> columnSectionsMap) throws Exception {
        FacilioChain sectionChain = TransactionChainFactory.getAddPageSectionChain();
        FacilioContext sectionChainContext = sectionChain.getContext();
        sectionChainContext.put(FacilioConstants.CustomPage.COLUMN_SECTIONS_MAP, columnSectionsMap);
        sectionChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        sectionChain.execute();

        Map<Long, List<PageSectionWidgetContext>> sectionWidgetsMap = new HashMap<>();
        for (PageSectionContext section : columnSectionsMap.values().stream().flatMap(List::stream).collect(Collectors.toList())) {
            FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(section.getWidgets()), "Widget(s) should be defined for Section - "+section.getName());
            sectionWidgetsMap.put(section.getId(), section.getWidgets());
        }

        LOGGER.info("||||| Started adding widgets for sections");
        addWidgets(appId, moduleName, isSystem, layoutType, sectionWidgetsMap);
        LOGGER.info("||||| Completed adding widgets for sections");

    }

    private static void addWidgets(long appId, String moduleName, boolean isSystem, PagesContext.PageLayoutType layoutType, Map<Long, List<PageSectionWidgetContext>> sectionWidgetsMap) throws Exception {
        if(MapUtils.isNotEmpty(sectionWidgetsMap)) {
            FacilioChain chain = TransactionChainFactory.getAddPageSectionWidgetsChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.APP_ID, appId);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, WidgetWrapperType.DEFAULT);
            context.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
            context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
            context.put(FacilioConstants.CustomPage.SECTION_WIDGETS_MAP, sectionWidgetsMap);
            chain.execute();
        }
    }

    public static void cloneTemplateToPage(long appId, long moduleId, long pageId, PagesContext.PageLayoutType layoutType) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleId);
        ApplicationContext app = ApplicationApi.getApplicationForId(appId);
        Objects.requireNonNull(app, "Invalid appId to getTemplatePage");

        PagesContext templatePage = CustomPageAPI.getTemplatePageFromDB(app, moduleId, layoutType != null ? layoutType.name() : null);
        if (templatePage == null) {
            templatePage = TemplatePageUtil.getTemplatePageFromFactory(module, app);
            if (templatePage != null) {
                addLayouts(appId, module.getName(), false, pageId, templatePage.getLayouts());
            }

        } else {
            Map<String, List<PageTabContext>> layouts = new HashMap<>();
            fetchPageLayouts(null, layouts, module, appId);
            addLayouts(appId, module.getName(), false, pageId, layouts);
        }
    }


    public static long clonePage(FacilioModule module, long appId, long pageId) throws Exception {
        PagesContext page = CustomPageAPI.getCustomPage(pageId);
        if (page != null && page.getModuleId() == module.getModuleId() && page.getAppId() == appId) {
            page.setName("cloned" + page.getName());
            if (page.getCriteriaId() != -1 && page.getCriteria() == null) {
                Criteria criteria = CriteriaAPI.getCriteria(page.getCriteriaId());
                page.setCriteria(criteria);
            }
            page.setPageSharing(SharingAPI.getSharing(pageId, ModuleFactory.getPageSharingModule(),
                    SingleSharingContext.class, FieldFactory.getPageSharingFields()));

            fetchPageLayouts(page, null, module, appId);

            FacilioChain pageChain = TransactionChainFactory.getCreateCustomPageChain();
            FacilioContext pageChainContext = pageChain.getContext();
            pageChainContext.put(FacilioConstants.CustomPage.CUSTOM_PAGE, page);
            pageChainContext.put(FacilioConstants.ContextNames.APP_ID, appId);
            pageChainContext.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
            pageChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, false);
            pageChainContext.put(FacilioConstants.CustomPage.IS_CLONE_PAGE, true);
            pageChainContext.put(FacilioConstants.CustomPage.IS_TEMPLATE, false);
            pageChain.execute();
            long clonedPageId = (long) pageChainContext.get(FacilioConstants.CustomPage.PAGE_ID);

            addLayouts(appId, module.getName(), false, clonedPageId, page.getLayouts());

            return clonedPageId;
        }
        return -1;
    }

    private static void fetchPageLayouts(PagesContext page, Map<String, List<PageTabContext>> layouts, FacilioModule module, long appId) throws Exception {
        if (layouts == null) {
            layouts = new HashMap<>();
        }
        for (PagesContext.PageLayoutType type : PagesContext.PageLayoutType.values()) {
            layouts.put(type.name(), fetchLayoutTabsToClone(module, appId, type, page));
        }
        if (page != null && MapUtils.isNotEmpty(layouts)) {
            page.setLayouts(layouts);
        }
    }

    private static List<PageTabContext> fetchLayoutTabsToClone(FacilioModule module, long appId, PagesContext.PageLayoutType layoutType, PagesContext pageToClone) throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.fetchPageTabsForLayoutChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        context.put(FacilioConstants.ContextNames.PAGE_ID, pageToClone.getId());
        context.put(FacilioConstants.CustomPage.CUSTOM_PAGE, pageToClone);
        context.put(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, true);
        context.put(FacilioConstants.CustomPage.IS_BUILDER_REQUEST, true);
        context.put(FacilioConstants.CustomPage.EXCLUDE_COLUMNS, false);
        context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
        chain.execute();
        return (List<PageTabContext>) context.get(FacilioConstants.CustomPage.PAGE_TABS);
    }

    public static List<PageTabContext> addConnectedAppSummaryTabs(long pageId, String moduleName) throws Exception {
        return addConnectedAppSummaryTabs(pageId, moduleName, null, null, false);
    }
    public static PageTabContext addConnectedAppSummaryTabs(long pageId, String moduleName, String widgetName) throws Exception {
        List<PageTabContext> tabs = addConnectedAppSummaryTabs(pageId, moduleName, -1L,widgetName, false);
        return tabs.get(0);
    }

    public static List<PageTabContext> addConnectedAppSummaryTabs(long pageId, long moduleId) throws Exception {
        return addConnectedAppSummaryTabs(pageId, null, moduleId, null, false);
    }
    public static PageTabContext addConnectedAppSummaryTabs(long pageId, long moduleId, String widgetName) throws Exception {
        List<PageTabContext> tabs = addConnectedAppSummaryTabs(pageId, null,moduleId, widgetName, false);
        return tabs.get(0);
    }
    public static List<PageTabContext> addConnectedAppSummaryTabs(long pageId, String moduleName, Long moduleId, String widgetName, boolean isSystem) throws Exception {
        if(moduleId == null || moduleId <= 0) {
            FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Invalid params to add connectedAppSummaryWidget");
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            moduleId = module.getModuleId();
        }
        List<ConnectedAppWidgetContext> connectedAppSummaryWidgets = ConnectedAppAPI.getConnectedAppWidgets(ConnectedAppWidgetContext.EntityType.SUMMARY_PAGE, String.valueOf(moduleId));
        if(CollectionUtils.isNotEmpty(connectedAppSummaryWidgets)) {
            List<PageTabContext> tabs = new ArrayList<>();
            for (ConnectedAppWidgetContext connectedAppSummaryWidget : connectedAppSummaryWidgets) {
                if(connectedAppSummaryWidget != null && (StringUtils.isBlank(widgetName) || widgetName.equals(connectedAppSummaryWidget.getLinkName()))) {
                    JSONObject widgetParam = new JSONObject();
                    widgetParam.put("widgetId", connectedAppSummaryWidget.getId());
                    PageTabContext tab = new PageTabContext(connectedAppSummaryWidget.getLinkName() + "tab", connectedAppSummaryWidget.getWidgetName(), -1D, PageTabContext.TabType.CONNECTED_TAB, true, AccountUtil.FeatureLicense.CONNECTEDAPPS.getFeatureId())
                            .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                            .addSection("section", "", null)
                            .addWidget(connectedAppSummaryWidget.getLinkName(), connectedAppSummaryWidget.getWidgetName(), PageWidget.WidgetType.CONNNECTED_APP, "flexiblewebconnectedapp_30", 0, 0, widgetParam, null)
                            .widgetDone()
                            .sectionDone()
                            .columnDone();
                    tabs.add(tab);
                }
            }
            if(CollectionUtils.isNotEmpty(tabs)) {
                Map<Long, List<PageTabContext>> layoutTabsMap = new HashMap<>();
                long layoutId = CustomPageAPI.getLayoutIdForPageId(pageId, PagesContext.PageLayoutType.WEB);
                layoutTabsMap.put(layoutId, tabs);
                addTabs(-1, moduleName, isSystem, PagesContext.PageLayoutType.WEB, layoutTabsMap);
            }
            return tabs;
        }
        return null;
    }
}
