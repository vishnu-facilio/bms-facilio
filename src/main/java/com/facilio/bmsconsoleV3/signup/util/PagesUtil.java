package com.facilio.bmsconsoleV3.signup.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigChain;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
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

        addLayouts(appId,moduleName, pageId, page.getLayouts());

    }
    public static void addLayouts(long appId, String moduleName, long pageId, Map<String,List<PageTabContext>> layouts) throws Exception {
        for (Map.Entry<String, List<PageTabContext>> layout : layouts.entrySet()) {
            Objects.requireNonNull(layout.getValue(), "Tabs can't be empty for layout "+layout.getKey()+"of page "+pageId);

            LOGGER.info("---- Started adding tabs for layout "+layout.getKey()+" for pageId "+pageId);
            for(PageTabContext tab : layout.getValue()) {
                tab.setStatus(true);
                addTab(appId, moduleName, pageId, PagesContext.PageLayoutType.valueOf(layout.getKey()), tab);
            }
            LOGGER.info("---- Completed adding tabs for layout "+layout.getKey()+" for pageId "+pageId);
        }
    }

    private static void addTab(long appId, String moduleName, long pageId, PagesContext.PageLayoutType layoutType, PageTabContext tab) throws Exception {
        FacilioChain tabChain = TransactionChainFactory.getAddPageTabsChain();
        FacilioContext tabChainContext = tabChain.getContext();
        tabChainContext.put(FacilioConstants.CustomPage.TAB, tab);
        tabChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, true);
        tabChainContext.put(FacilioConstants.CustomPage.PAGE_ID, pageId);
        tabChainContext.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
        tabChain.execute();
        long tabId = (long) tabChainContext.get(FacilioConstants.CustomPage.TAB_ID);

        List<Long> columnWidths = new ArrayList<>();
        Objects.requireNonNull(tab.getColumns(), "Columns should be defined for Tabs ---signUp error");
        for(PageColumnContext column : tab.getColumns()) {
            columnWidths.add(column.getWidth());
        }
        LOGGER.info("------ Started adding columns for tab ------ "+tabId);
        addColumns(appId, moduleName, tabId, columnWidths, tab.getColumns());
        LOGGER.info("------ Completed adding columns for tab ------ "+tabId);
    }

    private static void addColumns(long appId, String moduleName, long tabId, List<Long> widths, List<PageColumnContext> columns) throws Exception {
        FacilioChain columnsChain = TransactionChainFactory.getAddPageColumnsChain();
        FacilioContext columnsChainContext = columnsChain.getContext();
        columnsChainContext.put(FacilioConstants.CustomPage.COLUMN_WIDTHS,widths);
        columnsChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, true);
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
                addSection(appId, moduleName, widthVsColumnIdMap.get(column.getWidth()), section);
                LOGGER.info("-------- Completed adding columns for column -------- " + widthVsColumnIdMap.get(column.getWidth()));
            }
        }
    }

    private static void addSection(long appId, String moduleName, long columnId, PageSectionContext section) throws Exception{
        FacilioChain sectionChain = TransactionChainFactory.getAddPageSectionChain();
        FacilioContext sectionChainContext = sectionChain.getContext();
        sectionChainContext.put(FacilioConstants.CustomPage.SECTION,section);
        sectionChainContext.put(FacilioConstants.CustomPage.IS_SYSTEM, true);
        sectionChainContext.put(FacilioConstants.CustomPage.COLUMN_ID, columnId);
        sectionChain.execute();
        long sectionId = (Long) sectionChainContext.get(FacilioConstants.CustomPage.SECTION_ID);

        Objects.requireNonNull(section.getWidgets(), "Widgets should be defined for section ---signUp error");
        for(PageSectionWidgetContext widget :section.getWidgets()) {
            LOGGER.info("-------- Started adding widget for section -------- " + sectionId);
            addWidget(appId, moduleName, sectionId, widget);
            LOGGER.info("-------- Completed adding widget for section -------- " + sectionId);
        }
    }
    private static void addWidget(long appId, String moduleName, long sectionId, PageSectionWidgetContext widget) throws Exception {
        FacilioChain createChain = WidgetConfigChain.getCreateChain(widget.getWidgetType().name());
        FacilioContext context = createChain.getContext();

        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.IS_SYSTEM, true);
        context.put(FacilioConstants.CustomPage.SECTION_ID, sectionId);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, widget);

        createChain.execute();
    }

    public static void cloneTemplateToPage(long appId, long moduleId, long pageId, PagesContext.PageLayoutType layoutType) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleId);

        PagesContext templatePage = CustomPageAPI.getTemplatePage(appId, moduleId, layoutType.name());

        if(templatePage != null) {
            FacilioChain chain = ReadOnlyChainFactory.fetchPageTabsForLayoutChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.PAGE_ID, templatePage.getId());
            context.put(FacilioConstants.CustomPage.CUSTOM_PAGE, templatePage);
            context.put(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, true);
            context.put(FacilioConstants.CustomPage.IS_BUILDER_REQUEST, true);
            context.put(FacilioConstants.CustomPage.LAYOUT_TYPE, layoutType);
            context.put(FacilioConstants.CustomPage.EXCLUDE_COLUMNS, false);
            chain.execute();
            List<PageTabContext> tabs = (List<PageTabContext>) context.get(FacilioConstants.CustomPage.PAGE_TABS);

            addLayouts(appId, module.getName(), pageId, new HashMap<String, List<PageTabContext>>() {{
                put(layoutType.name(), tabs);
            }});
        }
    }
}
