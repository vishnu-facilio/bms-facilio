package com.facilio.bmsconsoleV3.signup.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.TemplatePages.TemplatePageUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
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
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class PagesUtil {

    public static void addTemplatePage(String moduleName, Map<String, List<PagesContext>> appNameVsTemplatePages) throws Exception {
        for(Map.Entry<String, List<PagesContext>> entry : appNameVsTemplatePages.entrySet()) {
            String appName = entry.getKey();
            if(appName.equals("newapp") && SignupUtil.maintenanceAppSignup()) {
                    continue;
            }
            LOGGER.info("Started adding template page for orgId --" + AccountUtil.getCurrentOrg().getOrgId() + " for module --" + moduleName + " and appName" +appName);

            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            Objects.requireNonNull(app, "App doesn't exists for appName-- "+appName);
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
            if(appName.equals("newapp") && SignupUtil.maintenanceAppSignup()) {
                continue;
            }
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            Objects.requireNonNull(app, "App doesn't exists for appName-- "+appName);
            long appId = app.getId();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            boolean isDefaultPageExists = CustomPageAPI.getDefaultPage(appId, module.getModuleId()) != null;

            if(!isDefaultPageExists) {
                PagesContext defaultPage = null;
                for (PagesContext page : entry.getValue()) {
                    if (page.getIsDefaultPage() != null && page.getIsDefaultPage()) {
                        defaultPage = page;
                        break;
                    }
                }

                if (defaultPage != null) {
                    entry.getValue().remove(defaultPage);
                    LOGGER.info("Started adding default page for orgId --" + AccountUtil.getCurrentOrg().getOrgId() + " for module --" + moduleName);
                    defaultPage.setIsDefaultPage(true);
                    defaultPage.setIsTemplate(false);
                    defaultPage.setAppId(appId);
                    defaultPage.setStatus(true);
                    defaultPage.setModuleName(moduleName);
                    isDefaultPageExists = true;
                    addPage(defaultPage.getAppId(), moduleName, defaultPage, false);
                    LOGGER.info("Completed adding default page for orgId --" + AccountUtil.getCurrentOrg().getOrgId() + " for module --" + moduleName + " and appName" + appName);
                }
            }

            for (PagesContext page : entry.getValue()) {
                LOGGER.info("Started adding page for orgId --" + AccountUtil.getCurrentOrg().getOrgId() + " for module --" + moduleName+" and appName"+appName);
                if(!isDefaultPageExists) {
                    page.setIsDefaultPage(true);
                    isDefaultPageExists = true;
                    LOGGER.info("Page is set as default page as there default page doesn't exists");
                }
                else {
                    page.setIsDefaultPage(false);
                }
                page.setIsTemplate(false);
                page.setAppId(appId);
                page.setStatus(true);
                page.setModuleName(moduleName);
                addPage(page.getAppId(), moduleName, page, false);
                LOGGER.info("Completed adding page for orgId --" + AccountUtil.getCurrentOrg().getOrgId() + " for module --" + moduleName+" and appName"+appName);
            }
        }
    }
    private static void addPage(long appId, String moduleName, PagesContext page, boolean isTemplate) throws Exception {

        FacilioChain pageChain = TransactionChainFactory.getCreateCustomPageChain();
        FacilioContext pageChainContext = pageChain.getContext();
        pageChainContext.put(FacilioConstants.CustomPage.CUSTOM_PAGE, page);
        pageChainContext.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        pageChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, true);
        pageChainContext.put(FacilioConstants.CustomPage.IS_TEMPLATE,isTemplate);

        ApplicationContext app = ApplicationApi.getApplicationForId(appId);
        Objects.requireNonNull(app, "Invalid appId");
        pageChainContext.put(FacilioConstants.ContextNames.APP_ID,appId);
        pageChain.execute();
        long pageId = (long) pageChainContext.get(FacilioConstants.CustomPage.PAGE_ID);
        Objects.requireNonNull(page.getLayouts(), "Layouts should be defined for pages ---signUp error");

        addLayouts(appId,moduleName, true, pageId, page.getLayouts());

    }
    public static void addLayouts(long appId, String moduleName, boolean isSystem, long pageId, Map<String,List<PageTabContext>> layouts) throws Exception {
        for (Map.Entry<String, List<PageTabContext>> layout : layouts.entrySet()) {
            Objects.requireNonNull(layout.getValue(), "Tabs can't be empty for layout "+layout.getKey()+"of page --"+pageId);

            LOGGER.info("---- Started adding tabs for layout "+layout.getKey()+" for pageId "+pageId);
            for(PageTabContext tab : layout.getValue()) {
                tab.setStatus(true);
                addTab(appId, moduleName, isSystem, pageId, PagesContext.PageLayoutType.valueOf(layout.getKey()), tab);
            }
            LOGGER.info("---- Completed adding tabs for layout "+layout.getKey()+" for pageId "+pageId);
        }
    }

    private static void addTab(long appId, String moduleName, boolean isSystem, long pageId, PagesContext.PageLayoutType layoutType, PageTabContext tab) throws Exception {
        FacilioChain tabChain = TransactionChainFactory.getAddPageTabsChain();
        FacilioContext tabChainContext = tabChain.getContext();
        tabChainContext.put(FacilioConstants.CustomPage.TAB, tab);
        tabChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        tabChainContext.put(FacilioConstants.CustomPage.PAGE_ID, pageId);
        tabChainContext.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
        tabChain.execute();
        long tabId = (long) tabChainContext.get(FacilioConstants.CustomPage.TAB_ID);

        List<Long> columnWidths = new ArrayList<>();
        Objects.requireNonNull(tab.getColumns(), "Columns should be defined for Tabs ---signUp error");
        for(PageColumnContext column : tab.getColumns()) {
            columnWidths.add(column.getWidth());
        }
        LOGGER.info("------ Started adding columns for tab ------ "+tab.getName());
        addColumns(appId, moduleName, isSystem, tabId, layoutType, columnWidths, tab.getColumns());
        LOGGER.info("------ Completed adding columns for tab ------ "+tab.getName());
    }

    private static void addColumns(long appId, String moduleName, boolean isSystem, long tabId,  PagesContext.PageLayoutType layoutType, List<Long> widths, List<PageColumnContext> columns) throws Exception {
        FacilioChain columnsChain = TransactionChainFactory.getAddPageColumnsChain();
        FacilioContext columnsChainContext = columnsChain.getContext();
        columnsChainContext.put(FacilioConstants.CustomPage.COLUMN_WIDTHS,widths);
        columnsChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        columnsChainContext.put(FacilioConstants.CustomPage.TAB_ID, tabId);
        columnsChain.execute();

        Map<Long, List<PageColumnContext>> columnsMap = CustomPageAPI.fetchColumns(Collections.singletonList(tabId));
        FacilioUtil.throwIllegalArgumentException(MapUtils.isEmpty(columnsMap), "Columns(s) should not be empty");

        Map<Long, Long> widthVsColumnIdMap = columnsMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(PageColumnContext::getWidth,PageColumnContext::getId));

        for (PageColumnContext column : columns) {
            Objects.requireNonNull(column.getSections(), "Sections should be defined for column ---signUp error");
            for(PageSectionContext section : column.getSections()) {
                LOGGER.info("-------- Started adding section for column -------- " + widthVsColumnIdMap.get(column.getWidth()));
                addSection(appId, moduleName,isSystem, widthVsColumnIdMap.get(column.getWidth()), layoutType, section);
                LOGGER.info("-------- Completed adding columns for column -------- " + widthVsColumnIdMap.get(column.getWidth()));
            }
        }
    }

    private static void addSection(long appId, String moduleName,boolean isSystem, long columnId,  PagesContext.PageLayoutType layoutType, PageSectionContext section) throws Exception{
        FacilioChain sectionChain = TransactionChainFactory.getAddPageSectionChain();
        FacilioContext sectionChainContext = sectionChain.getContext();
        sectionChainContext.put(FacilioConstants.CustomPage.SECTION,section);
        sectionChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        sectionChainContext.put(FacilioConstants.CustomPage.COLUMN_ID, columnId);
        sectionChain.execute();
        long sectionId = (Long) sectionChainContext.get(FacilioConstants.CustomPage.SECTION_ID);

        Objects.requireNonNull(section.getWidgets(), "Widgets should be defined for section ---signUp error");
        for(PageSectionWidgetContext widget :section.getWidgets()) {
            LOGGER.info("-------- Started adding widget for section -------- " + section.getName());
            addWidget(appId, moduleName, isSystem, sectionId, layoutType, widget);
            LOGGER.info("-------- Completed adding widget for section -------- " + section.getName());
        }
    }
    private static void addWidget(long appId, String moduleName,boolean isSystem, long sectionId,  PagesContext.PageLayoutType layoutType, PageSectionWidgetContext widget) throws Exception {
        FacilioChain createChain = WidgetConfigChain.getCreateChain(widget.getWidgetType().name());
        FacilioContext context = createChain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE, WidgetWrapperType.DEFAULT);
        context.put(FacilioConstants.CustomPage.IS_SYSTEM, isSystem);
        context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
        context.put(FacilioConstants.CustomPage.SECTION_ID, sectionId);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, widget);

        createChain.execute();
    }

    public static void cloneTemplateToPage(long appId, long moduleId, long pageId, PagesContext.PageLayoutType layoutType) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleId);
        ApplicationContext app = ApplicationApi.getApplicationForId(appId);
        Objects.requireNonNull(app, "Invalid appId to getTemplatePage");

        PagesContext templatePage = CustomPageAPI.getTemplatePageFromDB(app, moduleId, layoutType!=null?layoutType.name() : null);
        if (templatePage == null) {
            templatePage = TemplatePageUtil.getTemplatePageFromFactory(module, app);
            if(templatePage != null) {addLayouts(appId, module.getName(), false, pageId, templatePage.getLayouts());}

        } else {
            Map<String, List<PageTabContext>> layouts = new HashMap<>();
            fetchPageLayouts(null, layouts, module, appId);
            addLayouts(appId, module.getName(), false, pageId, layouts);
        }
    }


    public static long clonePage(FacilioModule module, long appId, long pageId) throws Exception {
        PagesContext page = CustomPageAPI.getCustomPage(pageId);
        if (page != null && page.getModuleId() == module.getModuleId() && page.getAppId() == appId) {
            page.setName("cloned"+page.getName());
            if(page.getCriteriaId()!=-1 && page.getCriteria() == null) {
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
        if(layouts == null) {
            layouts = new HashMap<>();
        }
        for(PagesContext.PageLayoutType type : PagesContext.PageLayoutType.values()) {
            layouts.put(type.name(), fetchLayoutTabsToClone(module, appId, type, page));
        }
        if(page != null && MapUtils.isNotEmpty(layouts)) {
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
}
