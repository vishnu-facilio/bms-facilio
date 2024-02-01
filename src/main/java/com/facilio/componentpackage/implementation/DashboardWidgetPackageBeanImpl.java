package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.bmsconsoleV3.context.dashboard.WidgetDashboardFilterContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.PackageConstants.DashboardConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingrule.util.ConnectedRuleUtil;
import com.facilio.report.context.ReportTemplateContext;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.componentpackage.utils.PackageBeanUtil.getNewRecordIds;

public class DashboardWidgetPackageBeanImpl implements PackageBean<DashboardWidgetContext>{
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return DashboardUtil.getDashboardWidgetsId();
    }

    @Override
    public Map<Long, DashboardWidgetContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long,String> dashboardIdsVsLinkName = DashboardUtil.getIdVsNameMap(ModuleFactory.getDashboardModule());
        Map<Long,String> filterIdVsName = DashboardUtil.getIdVsNameMap(ModuleFactory.getDashboardFilterModule());
        Map<Long,String> tabIdsVsLinkName = DashboardUtil.getIdVsNameMap(ModuleFactory.getDashboardTabModule());
        List<Long> fieldIds = DashboardUtil.getFilterFieldIds();
        Map<Long,Map<String, String>> fieldMap = DashboardUtil.getFieldIdVsLinkName(fieldIds);
        Map<Long,String> reportIdVsName = DashboardUtil.getIdVsNameMap(ModuleFactory.getReportModule());
        Map<Long,String> kpiIdVsName = DashboardUtil.getIdVsNameMap(ModuleFactory.getKpiModule());
        List<KPICategoryContext> categoryContexts = KPIUtil.getAllKPICategories();
        Map<Long,String> categoryIdVsName = new HashMap<>();
        if(categoryContexts!=null && !categoryContexts.isEmpty()){
            for(KPICategoryContext context : categoryContexts){
                categoryIdVsName.put(context.getId(),context.getName());
            }
        }
        return DashboardUtil.getWidgetWithIds(ids,dashboardIdsVsLinkName,tabIdsVsLinkName,fieldMap,filterIdVsName,reportIdVsName,kpiIdVsName,categoryIdVsName);
    }

    @Override
    public void convertToXMLComponent(DashboardWidgetContext component, XMLBuilder widgetElement) throws Exception {

        if(component.getWidgetType().equals(DashboardWidgetContext.WidgetType.SECTION)){
            createBaseWidget(component,widgetElement);
            WidgetSectionContext sectionContext = (WidgetSectionContext) component;
            widgetElement.element(DashboardConstants.NAME).text(sectionContext.getName());
            widgetElement.element(DashboardConstants.DESCRIPTION).text(sectionContext.getDesc());
            widgetElement.element(DashboardConstants.BANNER_DETAILS).cData(sectionContext.getBanner_meta());
            widgetElement.element(DashboardConstants.IS_COLLAPSED).text(String.valueOf(sectionContext.getCollapsed()));
            if(sectionContext.getNoResize()!=null){
                widgetElement.element(DashboardConstants.IS_RESIZE_ENABLED).text(String.valueOf(sectionContext.getNoResize()));
            }
            List<DashboardWidgetContext> sectionWidgets = sectionContext.getWidgets_in_section();
            if(sectionWidgets!=null){
                XMLBuilder widgetList = widgetElement.element(DashboardConstants.SECTION_WIDGETS);
                for(DashboardWidgetContext widgetContext : sectionWidgets){
                    XMLBuilder dashboardWidget = widgetList.element(DashboardConstants.SECTION_DASHBOARD_WIDGET);
                    createWidgetBuilder(widgetContext,dashboardWidget);
                }
            }
        }
        else{
            createWidgetBuilder(component,widgetElement);
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {

        Map<String,Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, Long> dashboardNameVsIds = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardModule());
        Map<String,Long> filterNameVsIds =DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardFilterModule());
        Map<String, Long> tabNameVsIds = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardTabModule());
        Map<String,Long> reportNameVsId = DashboardUtil.getNameVsIdMap(ModuleFactory.getReportModule());

        Map<String,Long> kpiNameVsId = new HashMap<>();
        List<KPICategoryContext> categoryContexts = KPIUtil.getAllKPICategories();
        Map<String,Long> categoryNameVsId = new HashMap<>();
        if(categoryContexts!=null && !categoryContexts.isEmpty()){
            for(KPICategoryContext context : categoryContexts){
                categoryNameVsId.put(context.getName(),context.getId());
            }
        }
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()){
            XMLBuilder widgetElement = idVsData.getValue();
            DashboardWidgetContext  widgetContext = createWidgetContext(widgetElement,dashboardNameVsIds,tabNameVsIds,filterNameVsIds,reportNameVsId,kpiNameVsId,categoryNameVsId);
            FacilioChain createWidgetChain = TransactionChainFactoryV3.addOrUpdateWidgetChain();
            FacilioContext context = createWidgetChain.getContext();
            context.put(DashboardConstants.WIDGET,widgetContext);
            createWidgetChain.execute();
            DashboardWidgetContext widget = (DashboardWidgetContext) context.get(DashboardConstants.WIDGET);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(),widget.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        Map<String, Long> dashboardNameVsIds = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardModule());
        Map<String,Long> filterNameVsIds = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardFilterModule());
        Map<String, Long> tabNameVsIds = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardTabModule());
        Map<String,Long> reportNameVsId = DashboardUtil.getNameVsIdMap(ModuleFactory.getReportModule());
        Map<String,Long> kpiNameVsId = DashboardUtil.getNameVsIdMap( ModuleFactory.getKpiModule());

        List<KPICategoryContext> categoryContexts = KPIUtil.getAllKPICategories();
        Map<String,Long> categoryNameVsId = new HashMap<>();
        if(categoryContexts!=null && !categoryContexts.isEmpty()){
            for(KPICategoryContext context : categoryContexts){
                categoryNameVsId.put(context.getName(),context.getId());
            }
        }

        for(Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()){
            XMLBuilder widgetElement = idVsComponent.getValue();
            DashboardWidgetContext widgetContext = createWidgetContext(widgetElement,dashboardNameVsIds,tabNameVsIds,filterNameVsIds,reportNameVsId,kpiNameVsId,categoryNameVsId);
            widgetContext.setId(idVsComponent.getKey());
            FacilioChain createWidgetChain = TransactionChainFactoryV3.addOrUpdateWidgetChain();
            FacilioContext context = createWidgetChain.getContext();
            context.put(DashboardConstants.WIDGET,widgetContext);
            createWidgetChain.execute();
        }

    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        Map<String, Long> dashboardNameVsId = DashboardUtil.getNameVsIdMap(ModuleFactory.getDashboardModule());
        Map<String,Long> reportNameVsId = DashboardUtil.getNameVsIdMap(ModuleFactory.getReportModule());
        Map<String,Long> kpiNameVsId = DashboardUtil.getNameVsIdMap(ModuleFactory.getKpiModule());

        List<KPICategoryContext> categoryContexts = KPIUtil.getAllKPICategories();
        Map<String,Long> categoryNameVsId = new HashMap<>();
        if(categoryContexts!=null && !categoryContexts.isEmpty()){
            for(KPICategoryContext context : categoryContexts){
                categoryNameVsId.put(context.getName(),context.getId());
            }
        }
        
        Map<String,FacilioField> staticWidgetFields = FieldFactory.getAsMap(FieldFactory.getWidgetStaticFields());
        Map<String,FacilioField> filterWidgetFields = FieldFactory.getAsMap(FieldFactory.getDashboardUserFilterFields());
        Map<String,FacilioField> cardWidgetFields = FieldFactory.getAsMap(FieldFactory.getWidgetCardFields());
        Map<String,FacilioField> chartWidgetFields = FieldFactory.getAsMap(FieldFactory.getWidgetChartFields());

        List<GenericUpdateRecordBuilder.BatchUpdateContext> staticBatchUpdateList = new ArrayList<>();
        List<GenericUpdateRecordBuilder.BatchUpdateContext> filterBatchUpdateList = new ArrayList<>();
        List<GenericUpdateRecordBuilder.BatchUpdateContext> cardBatchUpdateList = new ArrayList<>();
        List<GenericUpdateRecordBuilder.BatchUpdateContext> chartBatchUpdateList = new ArrayList<>();

        for(Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()){
            XMLBuilder widgetElement = idVsComponent.getValue();
            Long widgetId = idVsComponent.getKey();
            int type = Integer.parseInt(widgetElement.getElement(DashboardConstants.TYPE).getText());
            if(type == DashboardWidgetContext.WidgetType.STATIC.getValue()){
                XMLBuilder photoElement = widgetElement.getElement(DashboardConstants.PHOTO_ELEMENT);
                FileContext fileContext = null;
                if(widgetElement.getElement(DashboardConstants.PHOTO_ELEMENT)!=null){
                    fileContext = PackageFileUtil.addMetaFileAndGetContext(photoElement);
                }
                String paramJson = widgetElement.getElement(DashboardConstants.PARAMS_JSON).getCData();
                String metaJson = widgetElement.getElement(DashboardConstants.META_JSON_WIDGET).getCData();
                GenericUpdateRecordBuilder.BatchUpdateContext staticUpdateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                staticUpdateVal.addWhereValue(DashboardConstants.ID,widgetId);
                try{
                    JSONParser parser = new JSONParser();
                    JSONObject params  = (JSONObject) parser.parse(paramJson);
                    JSONObject meta  = (JSONObject) parser.parse(metaJson);
                    if(params !=null && params.containsKey("photoId")){
                        params.put("photoId",fileContext.getFileId());
                    }
                    staticUpdateVal.addUpdateValue("params",params.toString());
                    if(meta !=null && meta.containsKey("photoId")){
                        meta.put("photoId",fileContext.getFileId());
                        constructStaticMetaJson(meta,dashboardNameVsId,reportNameVsId);
                    }
                    staticUpdateVal.addUpdateValue("metaJson",meta.toString());
                }
                catch(Exception e){
                    staticUpdateVal.addUpdateValue("params",paramJson);
                    staticUpdateVal.addUpdateValue("metaJson",metaJson);
                }
                staticBatchUpdateList.add(staticUpdateVal);
            }
            else if (type == DashboardWidgetContext.WidgetType.FILTER.getValue()){
                String userFilterJson = widgetElement.getElement(DashboardConstants.DASHBOARD_USER_FILTER_JSON).getCData();
                String moduleName = widgetElement.getElement(DashboardConstants.MODULE_NAME).getText();
                GenericUpdateRecordBuilder.BatchUpdateContext filterUpdateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                filterUpdateVal.addWhereValue("widget_id",widgetId);
                if(moduleName!=null && userFilterJson!=null){
                     String userFilter = constructUserFilterJson(widgetElement,moduleName,userFilterJson);
                     filterUpdateVal.addUpdateValue("dashboardUserFilterJson",userFilter);
                }
                else{
                    filterUpdateVal.addUpdateValue("dashboardUserFilterJson",userFilterJson);
                }
                filterBatchUpdateList.add(filterUpdateVal);

            }
            else if (type == DashboardWidgetContext.WidgetType.CARD.getValue()){
                String cardLayout = widgetElement.getElement(DashboardConstants.CARD_LAYOUT).getText();
                GenericUpdateRecordBuilder.BatchUpdateContext cardUpdateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                cardUpdateVal.addWhereValue(DashboardConstants.ID,widgetId);
                if(widgetElement.getElement(DashboardConstants.CARD_DRILL_DOWN)!=null){
                    String cardDrillDown = widgetElement.getElement(DashboardConstants.CARD_DRILL_DOWN).getCData();
                    String newDrillDown = drillDownConversion(cardDrillDown,cardLayout,reportNameVsId);
                    cardUpdateVal.addUpdateValue("cardDrilldownJSON",newDrillDown);
                }
                String cardParams = widgetElement.getElement(DashboardConstants.CARD_PARAMS).getCData();
                List<String> cardList = Arrays.asList(DashboardConstants.MAP_CARD_LAYOUT_1,DashboardConstants.PHOTOS_LAYOUT_1);
                if(cardList.contains(cardLayout)){
                   cardUpdateVal.addUpdateValue("cardParamsJSON",cardParams);
                }
                else{
                    String newCardParams = convertCardParams(cardParams,cardLayout,kpiNameVsId,categoryNameVsId);
                    cardUpdateVal.addUpdateValue("cardParamsJSON",newCardParams);
                }
                cardBatchUpdateList.add(cardUpdateVal);
            }
            else if (type == DashboardWidgetContext.WidgetType.CHART.getValue()) {
                GenericUpdateRecordBuilder.BatchUpdateContext chartUpdateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
                chartUpdateVal.addWhereValue(DashboardConstants.ID,widgetId);
                if(widgetElement.getElement(PackageConstants.ReportsConstants.REPORT_TEMPLATE_ELEMENT)!=null){
                    Map<String, String> assetCategoryNameVsId = PackageUtil.getNameVsRecordIdForPicklistModule("assetcategory");
                    XMLBuilder reportTemplateElement = widgetElement.getElement(PackageConstants.ReportsConstants.REPORT_TEMPLATE_ELEMENT);
                    String reportTemplate = ReportsPackageBeanImpl.convertReportTemplate(reportTemplateElement,assetCategoryNameVsId);
                    chartUpdateVal.addUpdateValue("reportTemplate",reportTemplate);
                }
                else{
                    chartUpdateVal.addUpdateValue("reportTemplate",null);
                }
                chartBatchUpdateList.add(chartUpdateVal);
            }
        }
        if(CollectionUtils.isNotEmpty(staticBatchUpdateList)){
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(Collections.singletonList(staticWidgetFields.get("metaJson")))
                    .table(ModuleFactory.getWidgetStaticModule().getTableName());
            updateRecordBuilder.batchUpdate(Collections.singletonList(staticWidgetFields.get(DashboardConstants.ID)),staticBatchUpdateList);
        }
        if(CollectionUtils.isNotEmpty(filterBatchUpdateList)){
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(Collections.singletonList(filterWidgetFields.get("dashboardUserFilterJson")))
                    .table(ModuleFactory.getDashboardUserFilterModule().getTableName());
            updateRecordBuilder.batchUpdate(Collections.singletonList(filterWidgetFields.get("widget_id")),filterBatchUpdateList);
        }
        List<FacilioField> cardUpdateFields = Arrays.asList(cardWidgetFields.get("cardDrilldownJSON"),cardWidgetFields.get("cardParamsJSON"));
        if(CollectionUtils.isNotEmpty(cardBatchUpdateList)){
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(cardUpdateFields)
                    .table(ModuleFactory.getWidgetCardModule().getTableName());
            updateRecordBuilder.batchUpdate(Collections.singletonList(cardWidgetFields.get(DashboardConstants.ID)),cardBatchUpdateList);
        }
        if(CollectionUtils.isNotEmpty(chartBatchUpdateList)){
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(Collections.singletonList(chartWidgetFields.get("reportTemplate")))
                    .table(ModuleFactory.getWidgetChartModule().getTableName());
            updateRecordBuilder.batchUpdate(Collections.singletonList(chartWidgetFields.get(DashboardConstants.ID)),chartBatchUpdateList);
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        FacilioModule module =  ModuleFactory.getWidgetModule();
        DashboardUtil.deleteRecords(ids,module);
    }

    private static DashboardWidgetContext createWidgetContext(XMLBuilder widgetElement,Map<String,Long> dashboardNameVsId ,Map<String,Long> tabNameVsId,Map<String,Long> filterNameVsId,Map<String,Long> reportNameVsId,Map<String,Long> kpiNameVsId,Map<String,Long> categoryNameVsId) throws Exception {

        String type = widgetElement.getElement(DashboardConstants.TYPE).getText();
        DashboardWidgetContext widgetContext = null;
        if(Integer.parseInt(type)==DashboardWidgetContext.WidgetType.SECTION.getValue()){
            widgetContext = createBaseWidgetContext(widgetElement, dashboardNameVsId, tabNameVsId);
            WidgetSectionContext sectionContext = (WidgetSectionContext) widgetContext;
            String name = widgetElement.getElement(DashboardConstants.NAME).getText();
            String description = widgetElement.getElement(DashboardConstants.DESCRIPTION).getText();
            String banner_details = widgetElement.getElement(DashboardConstants.BANNER_DETAILS).getText();
            Boolean isCollapsed  = Boolean.parseBoolean(widgetElement.getElement(DashboardConstants.IS_COLLAPSED).getText());
            if(widgetElement.getElement(DashboardConstants.IS_RESIZE_ENABLED)!=null){
                Boolean isResizeEnabled = Boolean.parseBoolean(widgetElement.getElement(DashboardConstants.IS_RESIZE_ENABLED).getText());
                sectionContext.setNoResize(isResizeEnabled);
            }
            sectionContext.setSectionId(sectionContext.getId());
            sectionContext.setName(name);
            sectionContext.setDesc(description);
            sectionContext.setBanner_meta(banner_details);
            sectionContext.setCollapsed(isCollapsed);
            XMLBuilder widgetListBuilder = widgetElement.getElement(DashboardConstants.SECTION_WIDGETS);
            if(widgetListBuilder!=null){
                List<DashboardWidgetContext> widgets = new ArrayList<>();
                List<XMLBuilder> widgetsList = widgetListBuilder.getElementList(DashboardConstants.SECTION_DASHBOARD_WIDGET);
                for(XMLBuilder sectionWidgetElement: widgetsList){
                    widgetContext = getWidgetContext(sectionWidgetElement,dashboardNameVsId,tabNameVsId,filterNameVsId,reportNameVsId,kpiNameVsId,categoryNameVsId);
                    widgets.add(widgetContext);
                }
                sectionContext.setWidgets_in_section(widgets);
            }
            return sectionContext;
        }
        else{
            widgetContext = getWidgetContext(widgetElement,dashboardNameVsId,tabNameVsId,filterNameVsId,reportNameVsId,kpiNameVsId,categoryNameVsId);
        }
        return widgetContext;
    }
    private static DashboardWidgetContext getWidgetContext(XMLBuilder widgetElement,Map<String,Long> dashboardNameVsId,Map<String,Long> tabNameVsId,Map<String,Long> filterNameVsId,Map<String,Long> reportNameVsId,Map<String,Long> kpiNameVsId, Map<String,Long> categoryNameVsId) throws Exception {
        JSONParser parser = new JSONParser();
        DashboardWidgetContext dashboardWidget = null;
        dashboardWidget = createBaseWidgetContext(widgetElement, dashboardNameVsId, tabNameVsId);
        int type = Integer.parseInt(widgetElement.getElement(DashboardConstants.TYPE).getText());
        if(type == DashboardWidgetContext.WidgetType.STATIC.getValue()){
            WidgetStaticContext staticContext = (WidgetStaticContext) dashboardWidget;
           String staticKey = widgetElement.getElement(DashboardConstants.STATIC_KEY).getText();
           String params = widgetElement.getElement(DashboardConstants.PARAMS_JSON).getCData();
           String metaJson = widgetElement.getElement(DashboardConstants.META_JSON_WIDGET).getCData();
           staticContext.setStaticKey(staticKey);
           if(params!=null){
               staticContext.setParams(params);
           }
           staticContext.setMetaJson(metaJson);
           return staticContext;
        }
        else if (type == DashboardWidgetContext.WidgetType.LIST_VIEW.getValue()) {
            WidgetListViewContext listViewContext = (WidgetListViewContext) dashboardWidget;
            String moduleName = widgetElement.getElement(DashboardConstants.MODULE_NAME).getText();
            String viewName = widgetElement.getElement(DashboardConstants.VIEW_NAME).getText();
            listViewContext.setModuleName(moduleName);
            listViewContext.setViewName(viewName);
            return listViewContext;
        }
        else if (type == DashboardWidgetContext.WidgetType.WEB.getValue()) {
            WidgetWebContext webContext = (WidgetWebContext) dashboardWidget;
            String webUrl = widgetElement.getElement(DashboardConstants.WEB_URL).getText();
            webContext.setWebUrl(webUrl);
            return webContext;
        }
        else if (type == DashboardWidgetContext.WidgetType.FILTER.getValue()) {
            WidgetDashboardFilterContext dashboardUserFilterContext = (WidgetDashboardFilterContext) dashboardWidget;

            if(widgetElement.getElement(DashboardConstants.FILTER_LINK_NAME)!=null){
                long filterId = filterNameVsId.get(widgetElement.getElement(DashboardConstants.FILTER_LINK_NAME).getText());
                dashboardUserFilterContext.setDashboardFilterId(filterId);
            }
            int componentType = Integer.parseInt(widgetElement.getElement(DashboardConstants.COMPONENT_TYPE).getText());
            int optionType = Integer.parseInt(widgetElement.getElement(DashboardConstants.OPTION_TYPE).getText());
            Boolean isOthersOptionEnabled = Boolean.parseBoolean(widgetElement.getElement(DashboardConstants.IS_OTHERS_OPTION_ENABLED).getText());
            Boolean isAllOtherOptionEnabled = Boolean.parseBoolean(widgetElement.getElement(DashboardConstants.IS_ALL_OPTION_ENABLED).getText());
            if(widgetElement.getElement(DashboardConstants.SHOW_ONLY_RELEVANT_VALUES)!=null){
                Boolean showOnlyRelevantValues = Boolean.parseBoolean(widgetElement.getElement(DashboardConstants.SHOW_ONLY_RELEVANT_VALUES).getText());
                dashboardUserFilterContext.setShowOnlyRelevantValues(showOnlyRelevantValues);
            }
            String label = widgetElement.getElement(DashboardConstants.LABEL).getText();
           // String userFilterJson = widgetElement.getElement(DashboardConstants.DASHBOARD_USER_FILTER_JSON).getCData();
            int filterOrder = Integer.parseInt(widgetElement.getElement(DashboardConstants.FILTER_ORDER).getText());
            String moduleName = widgetElement.getElement(DashboardConstants.MODULE_NAME).getText();
            //check
            if( widgetElement.getElement(DashboardConstants.FIELD_MODULE_NAME)!=null && widgetElement.getElement(DashboardConstants.FIELD_LINK_NAME)!=null){
                String fieldModuleName = widgetElement.getElement(DashboardConstants.FIELD_MODULE_NAME).getText();
                String fieldName = widgetElement.getElement(DashboardConstants.FIELD_LINK_NAME).getText();
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField field = modBean.getField(fieldName,fieldModuleName);
                dashboardUserFilterContext.setFieldId(field.getFieldId());
            }
            XMLBuilder criteriaElement = widgetElement.getElement(PackageConstants.CriteriaConstants.CRITERIA);

            dashboardUserFilterContext.setComponentType(componentType);
            dashboardUserFilterContext.setOptionType(optionType);
            dashboardUserFilterContext.setIsOthersOptionEnabled(isOthersOptionEnabled);
            dashboardUserFilterContext.setIsAllOptionEnabled(isAllOtherOptionEnabled);
            dashboardUserFilterContext.setLabel(label);
            dashboardUserFilterContext.setFilterOrder(filterOrder);
            dashboardUserFilterContext.setModuleName(moduleName);
            if (criteriaElement != null) {
                Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                long criteriaId = CriteriaAPI.addCriteria(criteria);
                dashboardUserFilterContext.setCriteriaId(criteriaId);
            }
            return dashboardUserFilterContext;
        }
        else if(type == DashboardWidgetContext.WidgetType.CARD.getValue()){
            WidgetCardContext cardContext = (WidgetCardContext) dashboardWidget;
            String cardLayout = widgetElement.getElement(DashboardConstants.CARD_LAYOUT).getText();
            int scriptMode = Integer.parseInt(widgetElement.getElement(DashboardConstants.SCRIPT_MODE_INT).getText());
            if(widgetElement.getElement(DashboardConstants.CUSTOM_SCRIPT)!=null){
                String customScript = widgetElement.getElement(DashboardConstants.CUSTOM_SCRIPT).getText();
                Long workflowId = WorkflowUtil.addWorkflow(customScript);
                cardContext.setCustomScriptId(workflowId);
            }
            if(widgetElement.getElement(DashboardConstants.PARENT_ID)!=null){
                Long parentId = Long.valueOf(widgetElement.getElement(DashboardConstants.PARENT_ID).getText());
                Long newParentId = resourcesIdReplacement(parentId);
                cardContext.setParentId(newParentId);
            }
            String conditionalFormatting = widgetElement.getElement(DashboardConstants.CONDITIONAL_FORMATTING).getCData();
            cardContext.setConditionalFormattingJSON(conditionalFormatting);
            String cardState = widgetElement.getElement(DashboardConstants.CARD_STATE).getCData();
            cardContext.setCardLayout(cardLayout);
            cardContext.setScriptModeInt(scriptMode);
            cardContext.setCardStateJSON(cardState);
        }
        else if(type == DashboardWidgetContext.WidgetType.CHART.getValue()){
            WidgetChartContext chartContext = (WidgetChartContext) dashboardWidget;
            if(widgetElement.getElement(DashboardConstants.REPORT_NAME)!=null){
                String reportName = widgetElement.getElement(DashboardConstants.REPORT_NAME).getText();
                chartContext.setReportId(reportNameVsId.get(reportName));
            }
            else{
                String newReportName = widgetElement.getElement(DashboardConstants.NEW_REPORT_NAME).getText();
                chartContext.setNewReportId(reportNameVsId.get(newReportName));
            }
            if(widgetElement.getElement(DashboardConstants.DATE_FILTER_ID)!=null){
                Long dateFilterId = Long.valueOf(widgetElement.getElement(DashboardConstants.DATE_FILTER_ID).getText());
                chartContext.setDateFilterId(dateFilterId);
            }
            if(widgetElement.getElement(DashboardConstants.CHART_TYPE)!=null){
                Integer chartType = Integer.valueOf(widgetElement.getElement(DashboardConstants.CHART_TYPE).getText());
                chartContext.setChartType(chartType);
            }
        }
        return dashboardWidget;
    }
    private static DashboardWidgetContext createBaseWidgetContext(XMLBuilder widgetElement,Map<String,Long>  dashboardNameVsId,Map<String,Long>  tabNameVsId) throws Exception {

        DashboardWidgetContext widgetContext = new DashboardWidgetContext() {
            @Override
            public JSONObject widgetJsonObject(boolean optimize) {
                return null;
            }

            @Override
            public JSONObject widgetMobileJsonObject(boolean optimize, int index) {
                return null;
            }
        };
        String widgetName = widgetElement.getElement(DashboardConstants.WIDGET_NAME).getText();
        Integer type = Integer.parseInt(widgetElement.getElement(DashboardConstants.TYPE).getText());
        String widgetUrl = widgetElement.getElement(DashboardConstants.WIDGET_URL).getText();
        if (widgetElement.getElement(DashboardConstants.DATA_REFRESH_INTERTVEL) != null) {
            int dataRefreshInterval = Integer.parseInt(widgetElement.getElement(DashboardConstants.DATA_REFRESH_INTERTVEL).getText());
            widgetContext.setDataRefreshIntervel(dataRefreshInterval);
        }
        String headerText = widgetElement.getElement(DashboardConstants.HEADER_TEXT).getText();
        String headerSubText = widgetElement.getElement(DashboardConstants.HEADER_SUB_TEXT).getText();
        Boolean headerIsExport = Boolean.parseBoolean(widgetElement.getElement(DashboardConstants.HEADER_IS_EXPORT).getText());
        if (widgetElement.getElement(DashboardConstants.DASHBOARD_LINK_NAME) != null) {
            Long dashboardId = dashboardNameVsId.get(widgetElement.getElement(DashboardConstants.DASHBOARD_LINK_NAME).getText());
            widgetContext.setDashboardId(dashboardId);
        }

        if (widgetElement.getElement(DashboardConstants.TAB_LINK_NAME) != null) {
            Long dashboardTabId = tabNameVsId.get(widgetElement.getElement(DashboardConstants.TAB_LINK_NAME).getText());
            widgetContext.setDashboardTabId(dashboardTabId);
        }

        String metaJson = widgetElement.getElement(DashboardConstants.META_JSON).getCData();
        String widgetSettingsJson = widgetElement.getElement(DashboardConstants.WIDGET_SETTINGS_JSON).getCData();

        String linkName = widgetElement.getElement(DashboardConstants.LINK_NAME).getText();
        String helpText = widgetElement.getElement(DashboardConstants.HELP_TEXT).getText();
        widgetContext.setWidgetName(widgetName);
        widgetContext.setType(type);
        widgetContext.setWidgetUrl(widgetUrl);
        widgetContext.setHeaderText(headerText);
        widgetContext.setHeaderSubText(headerSubText);
        widgetContext.setHeaderIsExport(headerIsExport);
        widgetContext.setLinkName(linkName);
        widgetContext.setMetaJSONString(metaJson);
        widgetContext.setWidgetSettingsJsonString(widgetSettingsJson);
        widgetContext.setHelpText(helpText);

        Map<String,Object> prop = FieldUtil.getAsProperties(widgetContext);
        DashboardWidgetContext.WidgetType widgetType = DashboardWidgetContext.WidgetType.getWidgetType(type);
        DashboardWidgetContext dashboardWidgetContext = (DashboardWidgetContext) FieldUtil.getAsBeanFromMap(prop, widgetType.getWidgetContextClass());
        if(widgetSettingsJson!=null){
            JSONParser parser = new JSONParser();
            JSONObject widgetSettings = (JSONObject) parser.parse(widgetSettingsJson);
            dashboardWidgetContext.setWidgetSettings(widgetSettings);
        }
        return dashboardWidgetContext;
    }

    public static void createWidgetBuilder(DashboardWidgetContext component, XMLBuilder dashboardWidget) throws Exception {
        JSONParser parser = new JSONParser();
        createBaseWidget(component,dashboardWidget);
        if(component.getWidgetType().equals(DashboardWidgetContext.WidgetType.STATIC)) {
            WidgetStaticContext context = (WidgetStaticContext) component;
            dashboardWidget.element(DashboardConstants.STATIC_KEY).text(context.getStaticKey());
            dashboardWidget.element(DashboardConstants.PARAMS_JSON).cData(context.getParams());
            dashboardWidget.element(DashboardConstants.META_JSON_WIDGET).cData(context.getMetaJson());
            String metaJson = context.getMetaJson();
            try{
                JSONObject meta = (JSONObject) parser.parse(metaJson);
                Long photoId = (Long) meta.get("photoId");
                if(photoId!=null){
                    XMLBuilder photoElement = dashboardWidget.element(DashboardConstants.PHOTO_ELEMENT);
                    FileStore fs = FacilioFactory.getFileStore();
                    FileInfo fileInfo = fs.getFileInfo(photoId);
                    photoElement.addElement(PackageFileUtil.constructMetaAttachmentXMLBuilder(ComponentType.DASHBOARD_WIDGET , component.getId(), photoElement, fileInfo));
                }
            }
            catch(Exception e){
            }
        }
        if(component.getWidgetType().equals(DashboardWidgetContext.WidgetType.LIST_VIEW)){
            WidgetListViewContext context = (WidgetListViewContext) component;
            dashboardWidget.element(DashboardConstants.MODULE_NAME).text(context.getModuleName());
            dashboardWidget.element(DashboardConstants.VIEW_NAME).text(context.getViewName());
        }
        if(component.getWidgetType().equals(DashboardWidgetContext.WidgetType.WEB)){
            WidgetWebContext context = (WidgetWebContext) component;
            dashboardWidget.element(DashboardConstants.WEB_URL).text(context.getWebUrl());
        }
        if(component.getWidgetType().equals(DashboardWidgetContext.WidgetType.FILTER)){
            WidgetDashboardFilterContext context  = (WidgetDashboardFilterContext) component;
            dashboardWidget.element(DashboardConstants.FILTER_LINK_NAME).text(context.getFilterLinkName());
            dashboardWidget.element(DashboardConstants.COMPONENT_TYPE).text(String.valueOf(context.getComponentType()));
            dashboardWidget.element(DashboardConstants.OPTION_TYPE).text(String.valueOf(context.getOptionType()));
            dashboardWidget.element(DashboardConstants.IS_OTHERS_OPTION_ENABLED).text(String.valueOf(context.getIsOthersOptionEnabled()));
            dashboardWidget.element(DashboardConstants.IS_ALL_OPTION_ENABLED).text(String.valueOf(context.getIsAllOptionEnabled()));
            if(context.getShowOnlyRelevantValues()!=null){
                dashboardWidget.element(DashboardConstants.SHOW_ONLY_RELEVANT_VALUES).text(String.valueOf(context.getShowOnlyRelevantValues()));
            }
            dashboardWidget.element(DashboardConstants.LABEL).text(context.getLabel());
            if(context.getLinkNameMap()!=null){
                dashboardWidget.element(DashboardConstants.FIELD_MODULE_NAME).text(context.getLinkNameMap().get(FacilioConstants.ContextNames.MODULE_NAME));
                dashboardWidget.element(DashboardConstants.FIELD_LINK_NAME).text(context.getLinkNameMap().get(DashboardConstants.FIELD_NAME));
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = context.getModuleName();
            FacilioModule module = moduleName !=null ? modBean.getModule(moduleName) : null;
            if(moduleName !=null && moduleName.equals("ticketstatus")){
                JSONObject userFilterJson = (JSONObject) parser.parse(context.getDashboardUserFilterJson());
                String parentModuleName = userFilterJson !=null ? (String) userFilterJson.get("parentModuleName") : "";
                if(userFilterJson.containsKey("selectedOptions")){
                    XMLBuilder SelectedOption = dashboardWidget.element("selectedOptions");
                    List selectedIds = (List) userFilterJson.get("selectedOptions");
                    String[] idsAsStringArray = CollectionUtils.isNotEmpty(selectedIds) ? (String[]) selectedIds.toArray(new String[selectedIds.size()]) : new String[]{};
                    PackageBeanUtil.pickListXMLBuilder(SelectedOption,parentModuleName, idsAsStringArray,true);
                }
                if(userFilterJson.containsKey("defaultValues")){
                    XMLBuilder defaultOption = dashboardWidget.element("defaultOptions");
                    List defaultIds = (List) userFilterJson.get("defaultValues");
                    String[] idsAsStringArray = CollectionUtils.isNotEmpty(defaultIds) ? (String[]) defaultIds.toArray(new String[defaultIds.size()]) : new String[]{};
                    PackageBeanUtil.pickListXMLBuilder(defaultOption,parentModuleName,idsAsStringArray,true);
                }
                context.setDashboardUserFilterJson(userFilterJson.toString());
            }else if(module!=null && module.getType()==2){
                JSONObject userFilterJson = (JSONObject) parser.parse(context.getDashboardUserFilterJson());
                if(userFilterJson.containsKey("selectedOptions")){
                    List<String> selectedIds = optionSerialize(userFilterJson,"selectedOptions",moduleName);
                    userFilterJson.put("selectedOptions",selectedIds);
                }
                if(userFilterJson.containsKey("defaultValues")){
                    List<String> defaultIds = optionSerialize(userFilterJson,"defaultValues",moduleName);
                    userFilterJson.put("defaultValues",defaultIds);
                }
                context.setDashboardUserFilterJson(userFilterJson.toString());
            }
            dashboardWidget.element(DashboardConstants.DASHBOARD_USER_FILTER_JSON).cData(context.getDashboardUserFilterJson());
            dashboardWidget.element(DashboardConstants.FILTER_ORDER).text(String.valueOf(context.getFilterOrder()));
            dashboardWidget.element(DashboardConstants.MODULE_NAME).text(context.getModuleName());
            if (context.getCriteria() != null && !context.getCriteria().isEmpty()) {
                dashboardWidget.addElement(PackageBeanUtil.constructBuilderFromCriteria(context.getCriteria(), dashboardWidget.element(PackageConstants.CriteriaConstants.CRITERIA), context.getModuleName()));
            }
        }
        if(component.getWidgetType().equals(DashboardWidgetContext.WidgetType.CHART)){
            WidgetChartContext context = (WidgetChartContext) component;
            if(context.getReportName()!=null){
                dashboardWidget.element(DashboardConstants.REPORT_NAME).text(context.getReportName());
            }
            else{
                dashboardWidget.element(DashboardConstants.NEW_REPORT_NAME).text(context.getNewReportName());
            }
            if(context.getChartType()!=null){
                dashboardWidget.element(DashboardConstants.CHART_TYPE).text(String.valueOf(context.getChartType()));
            }
            if(context.getDateFilterId()!=null){
                dashboardWidget.element(DashboardConstants.DATE_FILTER_ID).text(String.valueOf(context.getDateFilterId()));
            }
            if(context.getReportTemplate()!=null){
                JSONObject templateJson = (JSONObject) parser.parse(context.getReportTemplate());
                if(templateJson!=null){
                    XMLBuilder templateElement = dashboardWidget.element(PackageConstants.ReportsConstants.REPORT_TEMPLATE_ELEMENT);
                    ReportTemplateContext reportTemplateContext = FieldUtil.getAsBeanFromJson(templateJson,ReportTemplateContext.class);
                    if(reportTemplateContext.getCriteria()!=null && !reportTemplateContext.getCriteria().isEmpty()){
                        templateElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(reportTemplateContext.getCriteria(),templateElement.element(PackageConstants.CriteriaConstants.CRITERIA), "asset"));
                        templateJson.put("criteria",null);
                    }
                    templateElement.element(PackageConstants.ReportsConstants.REPORT_TEMPLATE).cData(templateJson.toString());
                }
            }
        }
        if(component.getWidgetType().equals(DashboardWidgetContext.WidgetType.CARD)){
            WidgetCardContext context = (WidgetCardContext) component;
            String cardLayout = context.getCardLayout();
            dashboardWidget.element(DashboardConstants.CARD_LAYOUT).text(cardLayout);
            dashboardWidget.element(DashboardConstants.SCRIPT_MODE_INT).text(String.valueOf(context.getScriptModeInt()));
            if(context.getCustomScript()!=null){
                dashboardWidget.element(DashboardConstants.CUSTOM_SCRIPT).text(context.getCustomScript());
            }
            dashboardWidget.element(DashboardConstants.CARD_STATE).cData(context.getCardStateJSON());
            dashboardWidget.element(DashboardConstants.CONDITIONAL_FORMATTING).cData(context.getConditionalFormattingJSON());
            if(context.getParentId()!=null){
                dashboardWidget.element(DashboardConstants.PARENT_ID).text(String.valueOf(context.getParentId()));
            }
            String cardParams = context.getCardParamsJSON();
            dashboardWidget.element(DashboardConstants.CARD_PARAMS).cData(cardParams);
            if(context.getCardDrilldownJSON()!=null){
                String cardDrillDown = context.getCardDrilldownJSON();
                dashboardWidget.element(DashboardConstants.CARD_DRILL_DOWN).cData(cardDrillDown);
            }
        }
    }

    public static void createBaseWidget(DashboardWidgetContext component, XMLBuilder dashboardWidget) throws Exception {
        dashboardWidget.element(DashboardConstants.WIDGET_NAME).text(component.getWidgetName());
        if(component.getDataRefreshIntervel()>=0){
            dashboardWidget.element(DashboardConstants.DATA_REFRESH_INTERTVEL).text(String.valueOf(component.getDataRefreshIntervel()));
        }
        dashboardWidget.element(DashboardConstants.HEADER_IS_EXPORT).text(String.valueOf(component.isHeaderIsExport()));
        dashboardWidget.element(DashboardConstants.DASHBOARD_LINK_NAME).text(component.getDashboardLinkName());
        dashboardWidget.element(DashboardConstants.TAB_LINK_NAME).text(component.getTabLinkName());
        dashboardWidget.element(DashboardConstants.TYPE).text(String.valueOf(component.getType()));
        dashboardWidget.element(DashboardConstants.WIDGET_URL).text(component.getWidgetUrl());
        dashboardWidget.element(DashboardConstants.HEADER_TEXT).text(component.getHeaderText());
        dashboardWidget.element(DashboardConstants.HEADER_SUB_TEXT).text(component.getHeaderSubText());
        dashboardWidget.element(DashboardConstants.META_JSON).cData(String.valueOf(component.getMetaJSONString()));
        dashboardWidget.element(DashboardConstants.WIDGET_SETTINGS_JSON).cData(component.getWidgetSettingJsonAsString());
        dashboardWidget.element(DashboardConstants.LINK_NAME).text(component.getLinkName());
        dashboardWidget.element(DashboardConstants.HELP_TEXT).text(component.getHelpText());
    }
    public static String drillDownConversion(String cardDrillDown,String cardLayout,Map<String,Long> reportNameVsId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONParser parser  = new JSONParser();
        JSONObject drillDown = (JSONObject) parser.parse(cardDrillDown);
        JSONObject defaultObj = (JSONObject) (cardLayout.equals("controlcard_layout_1") ? drillDown.get("set-reading-button"): drillDown.get("default"));
        String actionType = (String) defaultObj.get("actionType");
        JSONObject data = (JSONObject) defaultObj.get("data");
        if(actionType.equals("showReport")){
                String reportLinkName = (String) data.get("reportLinkName");
                data.put("reportId",reportNameVsId.get(reportLinkName));
                data.remove("reportLinkName");
            defaultObj.put("data",data);
        }
        else if(actionType.equals("controlAction")){
            Long assetId = (Long) data.get("assetId");
            Long newAssetId = resourcesIdReplacement(assetId);
            String fieldName = (String) data.get("fieldName");
            String moduleName = (String) data.get("moduleName");
            FacilioField field = modBean.getField(fieldName,moduleName);
            data.remove("fieldName");
            data.remove("moduleName");
            data.put("fieldId",field.getFieldId());
            data.put("assetId",newAssetId);
            //change controlPointId
            defaultObj.put("data",data);
        }
        return drillDown.toString();
    }
    public static String convertCardParams(String params, String cardLayout,Map<String,Long> kpiNameVsId,Map<String,Long> categoryNameVsId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONParser parser = new JSONParser();
        JSONObject cardParams = (JSONObject) parser.parse(params);
        List<String> kpiCards = Arrays.asList(DashboardConstants.KPI_CARD_LAYOUT_1,DashboardConstants.KPI_CARD_LAYOUT_2);
        if(kpiCards.contains(cardLayout)){
            JSONObject kpi = (JSONObject) cardParams.get("kpi");
            String kpiType = (String) cardParams.get("kpiType");
            replaceKpiNameWithId(kpi,kpiType,kpiNameVsId,categoryNameVsId);
        }
        else if(cardLayout.equals(DashboardConstants.GAUGE_LAYOUT_7)){
            String centerKpiType = cardParams.get("centerTextType")!=null ? (String) cardParams.get("centerTextType") : "";
            String maxSafeLimitType = cardParams.get("maxSafeLimitType") !=null ? (String) cardParams.get("maxSafeLimitType") : "";
            String kpiType = (String) cardParams.get("kpiType");
            List<JSONObject> kpiList = (List<JSONObject>) cardParams.get("kpis");
            if(centerKpiType.equals("kpi")){
                JSONObject centerTextKpi = (JSONObject) cardParams.get("centerTextKpi");
                replaceKpiNameWithId(centerTextKpi,kpiType,kpiNameVsId,categoryNameVsId);
            }
            if(maxSafeLimitType.equals("kpi")){
                JSONObject maxSafeLimitKpi = (JSONObject) cardParams.get("maxSafeLimitKpi");
                replaceKpiNameWithId(maxSafeLimitKpi,kpiType,kpiNameVsId,categoryNameVsId);
            }
            for(JSONObject kpi : kpiList){
                replaceKpiNameWithId(kpi,kpiType,kpiNameVsId,categoryNameVsId);
            }
        }
        else if(cardLayout.equals(DashboardConstants.WEATHER_CARD_LAYOUT_1)){
            Long baseSpaceId = (Long) cardParams.get("baseSpaceId");
            Long newBaseSpaceId = resourcesIdReplacement(baseSpaceId);
            cardParams.put("baseSpaceId",newBaseSpaceId);
        }
        else if(cardLayout.equals(DashboardConstants.PM_READINGS_LAYOUT)){
            //check module name is correct
            Long PMId = (Long) cardParams.get("pmId");
            List<Long> PPIds = Arrays.asList(PMId);
            Map<Long,Long> oldIdVsNewIds = PackageBeanUtil.getNewRecordIds("plannedmaintenance",PPIds);
            Long newPMId = oldIdVsNewIds.get(PMId);
            cardParams.put("pmId",newPMId);
        }
        else if(cardLayout.equals(DashboardConstants.GRAPHICAL_CARD_LAYOUT_1)){
            List<JSONObject> readings = (List<JSONObject>) cardParams.get("readings");
            for(JSONObject reading : readings){
                JSONObject readingObj = constructReading((JSONObject) reading.get("reading"),cardLayout);
                reading.put("reading",readingObj);
            }
            cardParams.put("readings",readings);
        }
        else if(cardLayout.equals(DashboardConstants.WEB_LAYOUT_1)){
            // replace connectedAppID;
        }
        else if (cardLayout.equals(DashboardConstants.TABLE_LAYOUT_1)){
            String assetCategoryName = (String) cardParams.get("assetCategoryName");
            Long buildingId = (Long) cardParams.get("buildingId");
            String newCategoryId = PackageUtil.getNameVsRecordIdForPicklistModule("assetcategory").get(assetCategoryName);
            Long newBuildingId = resourcesIdReplacement(buildingId);
            if(newCategoryId!=null){
                cardParams.put("assetCategoryId",Long.valueOf(newCategoryId));
            }
            cardParams.put("buildingId",newBuildingId);
            cardParams.remove("assetCategoryName");
            List<JSONObject> columns = (List<JSONObject>) cardParams.get("columns");
            for(JSONObject column : columns){
                if(column.containsKey("fieldObj")){
                    String fieldName = (String) column.get("fieldName");
                    String moduleName = (String) column.get("moduleName");
                    FacilioField field = modBean.getField(fieldName,moduleName);
                    StringBuilder fieldObj = new StringBuilder()
                            .append(field.getName())
                            .append("_")
                            .append(field.getFieldId());
                    column.put("fieldObj",fieldObj.toString());
                }
            }
            cardParams.put("columns",columns);
        }
        else{
            if(cardLayout.equals("energycard_layout_1")){
                Long baseSpaceId = resourcesIdReplacement((Long) cardParams.get("imageSpaceId"));
                cardParams.put("imageSpaceId",baseSpaceId);
            }
            String minSafeLimitType = cardParams.containsKey("minSafeLimitType") ? (String) cardParams.get("minSafeLimitType") : "" ;
            String maxSafeLimitType = cardParams.containsKey("maxSafeLimitType") ? (String) cardParams.get("maxSafeLimitType") : "" ;;
            if(minSafeLimitType.equals("reading")){
                JSONObject minSafeLimit = (JSONObject) cardParams.get("minSafeLimitReading");
                JSONObject newMinSafeLimit = constructReading(minSafeLimit,cardLayout);
                cardParams.put("minSafeLimitReading",newMinSafeLimit);
            }
            if(maxSafeLimitType.equals("reading")){
                JSONObject maxSafeLimit = (JSONObject) cardParams.get("maxSafeLimitReading");
                JSONObject newMaxSafeLimit = constructReading(maxSafeLimit,cardLayout);
                cardParams.put("maxSafeLimitReading",newMaxSafeLimit);
            }
            JSONObject reading = (JSONObject) cardParams.get("reading");
            JSONObject newReading = constructReading(reading,cardLayout);
            cardParams.put("reading",newReading);
        }
        return cardParams.toString();
    }
    public static JSONObject constructReading(JSONObject readingObject, String cardLayout) throws Exception{
        JSONParser parser = new JSONParser();
        String kpiType = readingObject.containsKey("kpiType") ? (String) readingObject.get("kpiType") : "";
        String selectedField = readingObject.containsKey("selectedFieldId") ? (String) readingObject.get("selectedFieldId") : null;
        if(!kpiType.equals("DYNAMIC")){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String fieldName = (String) readingObject.get("fieldName");
            String moduleName = (String) readingObject.get("moduleName");
            if(readingObject.containsKey("fieldId")){
                FacilioField field = modBean.getField(fieldName,moduleName);
                readingObject.put("fieldId",field.getFieldId());
                if(selectedField!=null){
                    JSONObject selectedFieldObj = (JSONObject) parser.parse(selectedField);
                    selectedFieldObj.put("id",field.getFieldId());
                    readingObject.put("selectedFieldId",selectedFieldObj.toString());
                }
            }
        }
        else {
            String kpiLinkName  = (String) readingObject.get("kpiLinkName");
            Long kpiId = ConnectedRuleUtil.getConnectedRuleIdWithLinkName(kpiLinkName,FacilioConstants.ReadingKpi.READING_KPI);
            readingObject.put("fieldId",kpiId);
            if(selectedField !=null){
                JSONObject selectedFieldObj = (JSONObject) parser.parse(selectedField);
                selectedFieldObj.put("id",kpiId);
                readingObject.put("selectedFieldId",selectedFieldObj.toString());
            }
        }
        if(cardLayout.equals("readingcard_layout_5")){
            List<Long> parentIds = (List<Long>) readingObject.get("parentId");
            List<String> parentIdsList = parentIds.stream().map(id-> String.valueOf(id)).collect(Collectors.toList());
            List<String> newParentIds = replaceOldIds("asset",parentIdsList);
            List<Long> parentIdAsLong = newParentIds.stream().map(id->Long.valueOf(id)).collect(Collectors.toList());
            readingObject.put("parentId",parentIdAsLong);
        }
        else{
           Long parentId = (Long) readingObject.get("parentId");
           Long newParentId = resourcesIdReplacement(parentId);
           readingObject.put("parentId",newParentId);
        }
        return readingObject;
    }

    public static boolean filterIntegerValue(String value,List<String> stringsList){
        if(NumberUtils.isNumber(value)){
            return true;
        }
        else{
            stringsList.add(value);
            return false;
        }
    }
    public static String constructUserFilterJson(XMLBuilder widgetElement,String moduleName,String userFilterJson) throws Exception{
        JSONParser parser = new JSONParser();
        JSONObject userFilter = (JSONObject) parser.parse(userFilterJson);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long moduleType = -1;
        if(moduleName!=null){
            FacilioModule module = modBean.getModule(moduleName);
            moduleType = module !=null ? module.getType() : -1;
        }
        if(moduleName.equals("ticketstatus")){
            XMLBuilder defaultOptions = widgetElement.getElement("defaultOptions");
            XMLBuilder selectedOptions = widgetElement.getElement("selectedOptions");
            if(defaultOptions!=null){
                String values = PackageBeanUtil.pickListValueBuilder(defaultOptions);
                String[] valueArray = values.split(",");
                userFilter.put("defaultValues",Arrays.asList(valueArray));
            }
            if(selectedOptions!=null){
                String values = PackageBeanUtil.pickListValueBuilder(selectedOptions);
                String[] valueArray = values.split(",");
                userFilter.put("selectedOptions",Arrays.asList(valueArray));
            }
        } else if (moduleType == 2) {
            if(userFilter.containsKey("selectedOptions")){
                List<String> selectedIds = deserializeFilter(userFilter,"selectedOptions",moduleName);
                userFilter.put("selectedOptions",selectedIds);
            }
            if(userFilter.containsKey("defaultValues")){
                List<String> defaultIds = deserializeFilter(userFilter,"defaultValues",moduleName);
                userFilter.put("defaultValues",defaultIds);
            }
        } else if(StringUtils.isNotEmpty(moduleName)){
            List<String> selectedOptionIds = userFilter.containsKey("selectedOptions") ? (List<String>) userFilter.get("selectedOptions") : new ArrayList<>();
            List<String> ids = userFilter.containsKey("defaultValues") ? (List<String>) userFilter.get("defaultValues") : new ArrayList<>();
            if (CollectionUtils.isNotEmpty(ids)) {
                List<String> newIds = replaceOldIds(moduleName, ids);
                userFilter.put("defaultValues", newIds);
            }
            if (CollectionUtils.isNotEmpty(selectedOptionIds)) {
                List<String> newIds = replaceOldIds(moduleName, ids);
                userFilter.put("selectedOptions", newIds);
            }

            if (userFilter.containsKey("widget_field_mapping")) {
                List<JSONObject> mappingList = new ArrayList<>();
                for (JSONObject map : mappingList) {
                    String modName = (String) map.get("moduleName");
                    String fieldName = (String) map.get("fieldName");
                    FacilioField field = modBean.getField(fieldName, modName);
                    map.put("fieldId", field.getFieldId());
                }
                userFilter.put("widget_field_mapping", mappingList);
            }
        }

        return userFilter.toString();
    }
    public static List<String> replaceOldIds(String moduleName, List<String> ids) throws Exception{
        List<String> stringValues = new ArrayList<>();
        List<String> integerValues = ids.stream().filter(id-> filterIntegerValue(id,stringValues)).collect(Collectors.toList());
        List<Long> newIntValues =  integerValues.stream().map(id->Long.parseLong(id)).collect(Collectors.toList());
        Map<Long,Long> oldIdVsNewId =   getNewRecordIds(moduleName,newIntValues);
        if(newIntValues!=null && oldIdVsNewId!=null){
            List<Long> newIds =  newIntValues.stream().map(id->oldIdVsNewId.get(id)).collect(Collectors.toList());
            for(Long id : newIds){
                stringValues.add(String.valueOf(id));
            }
        }
        return stringValues;
    }
    public static Long resourcesIdReplacement(Long id) throws Exception {
        if(id!=null && id>0){
            List<Long> oldId = new ArrayList<>();
            oldId.add(id);
            Map<Long,Long> oldIdVsNewId =  PackageBeanUtil.getNewRecordIds("resource",oldId);
            return oldIdVsNewId.get(id);
        }
        return -1l;
    }
    public static void replaceKpiNameWithId(JSONObject kpi,String kpiType, Map<String,Long> kpiNameVsId,Map<String, Long> categoryNameVsId) throws Exception{
        if(kpiType.equals("reading")){
            String categoryName = (String) kpi.get("categoryName");
            Long categoryId = categoryNameVsId.get(categoryName);
            kpi.put("categoryId",categoryId);
            kpi.remove("categoryName");
            boolean isNewKpi = kpi.containsKey("isNewKpi") ? (boolean) kpi.get("isNewKpi") : false;
            if(isNewKpi){
                String  kpiName = (String) kpi.get("kpiLinkName");
                Long kpiId = ConnectedRuleUtil.getConnectedRuleIdWithLinkName(kpiName,FacilioConstants.ReadingKpi.READING_KPI);
                kpi.put("kpiId",kpiId);
                kpi.remove("kpiLinkName");
                Long parentId = resourcesIdReplacement((Long) kpi.get("parentId"));
                kpi.put("parentId",parentId);
            }
        } else{
            String kpiName  = (String) kpi.get("moduleKpiName");
            Long kpiId = kpiNameVsId.get(kpiName);
            kpi.put("kpiId",kpiId);
            kpi.remove("moduleKpiName");
        }
    }

    public static void constructStaticMetaJson(JSONObject metaJson, Map<String,Long> dashboardNameVsId, Map<String,Long> reportNameVsId) throws Exception {
        JSONObject option = (JSONObject) metaJson.get("options");
        List<JSONObject> areaList = (List<JSONObject>) option.get("areas");
        for(JSONObject area : areaList){
            JSONObject link = (JSONObject) area.get("link");
            String dashboardLinkName = (String) link.get("dashboardLinkName");
            String reportLinkName = (String) link.get("reportLinkName");
            if(!dashboardLinkName.isEmpty()){
                String dashboardId = String.valueOf(dashboardNameVsId.get(dashboardLinkName));
                link.put("dashboardId",dashboardId);
            }
            if(!reportLinkName.isEmpty()){
                String reportId = String.valueOf(reportNameVsId.get(reportLinkName));
                link.put("reportId",reportId);
            }
            String assetId = (String) link.get("assetId");
            if(!assetId.isEmpty()){
                Long newAssetId = resourcesIdReplacement(Long.valueOf(assetId));
                link.put("assetId",String.valueOf(newAssetId));
            }
            area.put("link",link);
        }
        option.put("areas",areaList);
        metaJson.put("options",option);
    }
    public static List<String> optionSerialize(JSONObject userFilterJson,String option, String moduleName){
        List selectedOption = (List) userFilterJson.get(option);
        List<String> selectedStrings = new ArrayList<>();
        List<String> selectedIds = (List<String>) selectedOption.stream().filter(id-> filterIntegerValue((String) id,selectedStrings)).collect(Collectors.toList());
        Map<String, String> idMap = PackageUtil.getRecordIdVsNameForPicklistModule(moduleName);
        List<String> pickListNames = selectedIds.stream().map(id-> idMap.get(id)).collect(Collectors.toList());
        return pickListNames;
    }
    public static List<String> deserializeFilter(JSONObject userFilterJson, String option, String moduleName){
        List<String> selectedStrings = (List<String>) userFilterJson.get(option);
        Map<String, String> idMap = PackageUtil.getNameVsRecordIdForPicklistModule(moduleName);
        List<String> selectedIds = new ArrayList<>();
        for(String name : selectedStrings){
            if(name.equals("all") || name.equals("others")){
                selectedIds.add(name);
            }else{
                selectedIds.add(idMap.get(name));
            }
        }
        return selectedIds;
    }
}

