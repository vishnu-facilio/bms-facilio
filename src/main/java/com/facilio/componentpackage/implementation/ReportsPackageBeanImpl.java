package com.facilio.componentpackage.implementation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.report.ReportDynamicKpiContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.readingrule.util.ConnectedRuleUtil;
import com.facilio.report.context.*;
import com.facilio.report.util.ReportUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class ReportsPackageBeanImpl implements PackageBean<ReportContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        Map<Long,Long> reportIds = new HashMap<>();
        FacilioModule module = ModuleFactory.getReportModule();
        List<Map<String, Object>> props = DashboardUtil.getIdAndLinkNameAsProp(module);
        if(CollectionUtils.isNotEmpty(props)) {
            props.forEach(prop -> reportIds.put((Long) prop.get(PackageConstants.DashboardConstants.ID),-1L));
        }
        return reportIds;
    }

    @Override
    public Map<Long, ReportContext> fetchComponents(List<Long> ids) throws Exception {
        Map<Long,String> folderIdVsLinkName = DashboardUtil.getIdVsNameMap(ModuleFactory.getReportFolderModule());
        return ReportUtil.getReportsWithId(ids,folderIdVsLinkName);
    }

    @Override
    public void convertToXMLComponent(ReportContext component, XMLBuilder reportElement) throws Exception {
        reportElement.element(PackageConstants.DashboardConstants.NAME).text(component.getName());
        reportElement.element(PackageConstants.DashboardConstants.APP_NAME).text(component.getAppName());
        reportElement.element(PackageConstants.ReportsConstants.SITE_ID).text(String.valueOf(component.getSiteId()));
        if(component.getModuleName()!=null){
            reportElement.element(PackageConstants.DashboardConstants.MODULE_NAME).text(component.getModuleName());

        }
        reportElement.element(PackageConstants.DashboardConstants.FOLDER_LINK_NAME).text(component.getFolderName());
        reportElement.element(PackageConstants.DashboardConstants.DESCRIPTION).text(component.getDescription());
        if(component.getWorkFlowString()!=null){
            reportElement.element(PackageConstants.FunctionConstants.WORKFLOW_STRING).cData(component.getWorkFlowString());
        }
        reportElement.element(PackageConstants.ReportsConstants.COMMON_STATE_JSON).cData(component.getCommonState());
        reportElement.element(PackageConstants.ReportsConstants.REPORT_STATE_JSON).cData(component.getReportStateJson());
        reportElement.element(PackageConstants.ReportsConstants.X_AGGR).text(String.valueOf(component.getxAggr()));
        reportElement.element(PackageConstants.DashboardConstants.DATE_OPERATOR).text(String.valueOf(component.getDateOperator()));
        reportElement.element(PackageConstants.DashboardConstants.DATE_VALUE).text(component.getDateValue());
        if(component.getDateRangeJson()!=null){
            reportElement.element(PackageConstants.ReportsConstants.DATE_RANGE_JSON).text(component.getDateRangeJson());
        }
        if(component.getBaselineJson()!=null){
            reportElement.element(PackageConstants.ReportsConstants.BASELINE_JSON).cData(component.getBaselineJson());
        }
        reportElement.element(PackageConstants.ReportsConstants.ANALYTICS_TYPE).text(String.valueOf(component.getAnalyticsType()));
        reportElement.element(PackageConstants.ReportsConstants.REPORT_TYPE).text(String.valueOf(component.getType()));
        reportElement.element(PackageConstants.ReportsConstants.BOOLEAN_SETTINGS).text(String.valueOf(component.getBooleanSetting()));
        if(component.getTransformClass()!=null){
            reportElement.element(PackageConstants.ReportsConstants.TRANSFORM_CLASS).text(component.getTransformClass());
        }
        reportElement.element(PackageConstants.ReportsConstants.MODULE_TYPE).text(String.valueOf(component.getModuleType()));
        if(component.getTimeFilter()!=null){
            reportElement.element(PackageConstants.ReportsConstants.TIME_FILTER).cData(component.getTimeFilter());
        }
        if(component.getDataFilter()!=null){
            reportElement.element(PackageConstants.ReportsConstants.DATA_FILTER).cData(component.getDataFilter());
        }
        reportElement.element(PackageConstants.ReportsConstants.DRILL_DOWN_PATH_JSON).cData(component.getReportDrilldownJson());
        reportElement.element(PackageConstants.ReportsConstants.REPORT_SETTINGS_JSON).cData(component.getReportSettingsJson());
        if(component.getType()==2){
            reportElement.element(PackageConstants.ReportsConstants.CHART_STATE_JSON).cData(component.getChartState());
            reportElement.element(PackageConstants.ReportsConstants.DATA_POINT_JSON).cData(component.getDataPointJson());
            reportElement.element(PackageConstants.ReportsConstants.FILTERS_JSON).text(component.getFiltersJson());
            reportElement.element(PackageConstants.ReportsConstants.REPORT_TEMPLATE).text(component.getTemplate());
            JSONParser parser = new JSONParser();
            JSONObject chartState  = (JSONObject) parser.parse(component.getChartState());
            JSONObject initialConfig = (JSONObject) chartState.get("initialConfig");
            List<JSONObject> userFilterList = initialConfig!=null ?(List<JSONObject>) initialConfig.get("userFilters") :null;
            if(userFilterList !=null){
                for(JSONObject userFilter : userFilterList){
                    XMLBuilder userFilterBuilder = reportElement.element("userFilter");
                    userFilterBuilder.element(PackageConstants.ReportsConstants.USER_FILTER_JSON).cData(userFilter.toString());
                    String parentModule = (String) userFilter.get("moduleName");
                    if(userFilter.containsKey("userFilterChosenValues")){
                        XMLBuilder pickList = userFilterBuilder.element(PackageConstants.ReportsConstants.USER_FILTER_CHOSEN_LIST);
                        List<String> chosenValues = (List<String>) userFilter.get("userFilterChosenValues");
                        String[] arrayString = chosenValues.toArray(new String[chosenValues.size()]);
                        PackageBeanUtil.pickListXMLBuilder(pickList,parentModule,arrayString,true);
                    }
                    if(userFilter.containsKey("userFilterDefaultValues")){
                        XMLBuilder pickList = userFilterBuilder.element(PackageConstants.ReportsConstants.USER_FILTER_DEFAULT_LIST);
                        List<String> defaultValues = (List<String>) userFilter.get("userFilterDefaultValues");
                        String[] arrayString = defaultValues.toArray(new String[defaultValues.size()]);
                        PackageBeanUtil.pickListXMLBuilder(pickList,parentModule,arrayString,true);
                    }
                    if(userFilter.containsKey("userFilterAllValues")){
                        XMLBuilder pickList = userFilterBuilder.element(PackageConstants.ReportsConstants.USER_FILTER_ALL_LIST);
                        List<String> allValues = (List<String>) userFilter.get("userFilterAllValues");
                        String[] arrayString = allValues.toArray(new String[allValues.size()]);
                        PackageBeanUtil.pickListXMLBuilder(pickList,parentModule,arrayString,true);
                    }

                }
                initialConfig.put("userFilters",null);
            }
            if(chartState.containsKey("configCriteria")){
                Criteria criteria = FieldUtil.getAsBeanFromJson((JSONObject) chartState.get("configCriteria"),Criteria.class);
                if (criteria != null && !criteria.isEmpty()) {
                    reportElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(criteria, reportElement.element(PackageConstants.ReportsConstants.CONFIG_CRITERIA),component.getModuleName()));
                }
            }
            if(chartState.containsKey("groupByPickListIds")){
                XMLBuilder groupByPickListElement = reportElement.element(PackageConstants.ReportsConstants.GROUP_BY_PICK_LIST);
                String parentModule = (String) chartState.get("groupByParentModule");
                JSONArray keys = (JSONArray) chartState.get("groupByPickListIds");
                String[] oldPickListIds = new String[keys.size()];
                for (int i = 0; i < keys.size(); i++) {
                    oldPickListIds[i] = keys.get(i).toString();
                }
                PackageBeanUtil.pickListXMLBuilder(groupByPickListElement,parentModule, oldPickListIds,true);
            }
            if(chartState.containsKey("dimensionSelectedId")){
                XMLBuilder pickList = reportElement.element(PackageConstants.ReportsConstants.DIMENSION_PICK_LIST);
                String parentModule = (String) chartState.get("dimensionPickListName");
                List<Long> keys = (List<Long>) chartState.get("dimensionSelectedId");
                String[] selectedIds  = keys.toArray(new String[keys.size()]);
                PackageBeanUtil.pickListXMLBuilder(pickList,parentModule,selectedIds,true);
            }
        }
        else{
            reportElement.element(PackageConstants.ReportsConstants.CHART_STATE_JSON).cData(component.getChartState());
            List<ReportDataPointContext> dataPointContexts = component.getDataPoints();
            XMLBuilder dataPointElementsList = reportElement.element(PackageConstants.ReportsConstants.DATA_POINTS);
            for(ReportDataPointContext dataPoint : dataPointContexts){
                XMLBuilder dataPointElement = dataPointElementsList.element(PackageConstants.ReportsConstants.DATA_POINT);
                JSONObject dataPointJson = FieldUtil.getAsJSON(dataPoint);
                if (dataPoint.getCriteria() != null && !dataPoint.getCriteria().isEmpty()){
                    dataPointElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(dataPoint.getCriteria(), dataPointElement.element(PackageConstants.CriteriaConstants.CRITERIA), "asset"));
                    dataPointJson.put("criteria",null);
                }
                if (dataPoint.getOtherCriteria() != null && !dataPoint.getOtherCriteria().isEmpty()){
                    dataPointElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(dataPoint.getOtherCriteria(), dataPointElement.element(PackageConstants.ReportsConstants.OTHER_CRITERIA), component.getModuleName()));
                    dataPointJson.put("otherCriteria",null);
                }
                if(dataPoint.getParentCriteriaFilter()!=null && !dataPoint.getParentCriteriaFilter().isEmpty()){
                    dataPointElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(dataPoint.getParentCriteriaFilter(),dataPointElement.element(PackageConstants.ReportsConstants.PARENT_CRITERIA_FILTER),component.getModuleName()));
                    dataPointJson.put("parentCriteriaFilter",null);
                }
                dataPointElement.element(PackageConstants.ReportsConstants.DATA_POINT_JSON).cData(dataPointJson.toString());
            }
            List<ReportFilterContext> filterContexts = component.getFilters();
            if(filterContexts!=null){
                XMLBuilder filterElementsList = reportElement.element(PackageConstants.ReportsConstants.FILTERS);
                for(ReportFilterContext filter : filterContexts){
                    XMLBuilder filterElement = filterElementsList.element(PackageConstants.ReportsConstants.FILTER_ELEMENT);
                    JSONObject filterJson = FieldUtil.getAsJSON(filter);
                    if (filter.getCriteria() != null && !filter.getCriteria().isEmpty()) {
                        filterElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(filter.getCriteria(), filterElement.element(PackageConstants.CriteriaConstants.CRITERIA), filter.getModuleName()));
                        filterJson.put("criteria",null);
                    }
                    filterElement.element(PackageConstants.ReportsConstants.FILTERS_JSON).cData(filterJson.toString());
                }
            }
            ReportTemplateContext templateContext = component.getReportTemplate();
            if(templateContext!=null){
                XMLBuilder templateElement = reportElement.element(PackageConstants.ReportsConstants.REPORT_TEMPLATE_ELEMENT);
                JSONObject template = FieldUtil.getAsJSON(templateContext);
                if (templateContext.getCriteria() != null && !templateContext.getCriteria().isEmpty()) {
                    templateElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(templateContext.getCriteria(), templateElement.element(PackageConstants.CriteriaConstants.CRITERIA), "asset"));
                    template.put("criteria",null);
                }
                templateElement.element(PackageConstants.ReportsConstants.REPORT_TEMPLATE).cData(template.toString());
            }

            if(component.getTabularState()!=null){
                JSONParser parser = new JSONParser();
                String tabularState = component.getTabularState();
                JSONObject tabularStateJson = (JSONObject) parser.parse(tabularState);
                String moduleName = (String) tabularStateJson.get("moduleName");
                XMLBuilder tabularStateElement = reportElement.element(PackageConstants.ReportsConstants.TABULAR_STATE_ELEMENT);
                JSONObject criteria = (JSONObject) tabularStateJson.get("criteria");
                if(criteria!=null){
                    tabularStateJson.put("criteria",null);
                    Criteria criteriaObj = FieldUtil.getAsBeanFromJson(criteria,Criteria.class);
                    tabularStateElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(criteriaObj, tabularStateElement.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));
                }
                JSONObject templateJson = (JSONObject) tabularStateJson.get("templateJSON");
                if(templateJson!=null){
                    templateJsonXMLConversion(templateJson,tabularStateElement,moduleName);
                }
                tabularStateJson.put("templateJSON",null);
                tabularStateElement.element(PackageConstants.ReportsConstants.TABULAR_STATE_JSON).cData(tabularStateJson.toString());
            }
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
        Map<String,Long> linkNameVsFolderId =DashboardUtil.getNameVsIdMap(ModuleFactory.getReportFolderModule());
        Map<String, String> assetCategoryNameVsId = PackageUtil.getNameVsRecordIdForPicklistModule("assetcategory");
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()){
            XMLBuilder reportElement = idVsData.getValue();
            ReportContext reportContext = createReportContext(reportElement,linkNameVsFolderId,appNameVsAppId,assetCategoryNameVsId);
            FacilioChain chain = TransactionChainFactoryV3.getAddOrUpdateModuleReportChain();
            FacilioContext context = chain.getContext();
            context.put(PackageConstants.ReportsConstants.REPORT, reportContext);
            chain.execute(context);
            ReportContext report = (ReportContext) context.get(PackageConstants.ReportsConstants.REPORT);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(),report.getId());
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        Map<String,Long> linkNameVsFolderId = DashboardUtil.getNameVsIdMap(ModuleFactory.getReportModule());
        Map<String, String> assetCategoryNameVsId = PackageUtil.getNameVsRecordIdForPicklistModule("assetcategory");
        Map<String, Long> appNameVsAppId = PackageBeanUtil.getAppNameVsAppId();
        for(Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()){
            XMLBuilder reportElement = idVsComponent.getValue();
            ReportContext report = createReportContext(reportElement,linkNameVsFolderId,appNameVsAppId,assetCategoryNameVsId);
            report.setId(idVsComponent.getKey());
            FacilioChain updateReportChain = TransactionChainFactoryV3.getAddOrUpdateModuleReportChain();
            FacilioContext context = updateReportChain.getContext();
            context.put(PackageConstants.ReportsConstants.REPORT, report);
            updateReportChain.execute(context);
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        Map<String, String> assetCategoryNameVsId = PackageUtil.getNameVsRecordIdForPicklistModule("assetcategory");
        List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = new ArrayList<>();
        Map<String,FacilioField> reportFields = FieldFactory.getAsMap(FieldFactory.getReport1Fields());
        List<FacilioField> updateReportFields = new ArrayList<>();
        updateReportFields.add(reportFields.get("template"));
        updateReportFields.add(reportFields.get("chartState"));
        updateReportFields.add(reportFields.get("filtersJson"));
        updateReportFields.add(reportFields.get("userFiltersJson"));
        updateReportFields.add(reportFields.get("dataFilter"));
        updateReportFields.add(reportFields.get("dataPointJson"));
        JSONParser parser = new JSONParser();

        for (Map.Entry<Long, XMLBuilder> idVsComponent : idVsXMLComponents.entrySet()) {
            XMLBuilder reportElement = idVsComponent.getValue();
            LOGGER.info("reportName--"+reportElement.getElement("Name").getText());
            Long reportId = idVsComponent.getKey();
            ReportContext reportContext = new ReportContext();
            String moduleName = reportElement.getElement(PackageConstants.DashboardConstants.MODULE_NAME).getText();
            int reportType = Integer.parseInt(reportElement.getElement(PackageConstants.ReportsConstants.REPORT_TYPE).getText());
            GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
            updateVal.addWhereValue(PackageConstants.DashboardConstants.ID,reportId);
            if(reportElement.getElement(PackageConstants.ReportsConstants.REPORT_TEMPLATE_ELEMENT)!=null){
                XMLBuilder reportTemplateElement = reportElement.getElement(PackageConstants.ReportsConstants.REPORT_TEMPLATE_ELEMENT);
                String reportTemplate = convertReportTemplate(reportTemplateElement,assetCategoryNameVsId);
                reportContext.setTemplate(reportTemplate);
                updateVal.addUpdateValue("template",reportTemplate);
            }
            String chartState = reportElement.getElement(PackageConstants.ReportsConstants.CHART_STATE_JSON).getCData();
            String newChartState = null;
            List<XMLBuilder> userFilterBuilder = reportElement.getElementList("userFilter");
            if(chartState !=null && reportElement.getElementList("userFilter")!=null){
                List<JSONObject> userFilterList = new ArrayList<>();
                for(XMLBuilder userFilterElement : userFilterBuilder){
                    XMLBuilder filterElement = userFilterElement.getElement(PackageConstants.ReportsConstants.USER_FILTER_JSON);
                    if(filterElement!=null){
                        String userFilterString = filterElement.getCData();
                        JSONObject userFilter = deserializeUserFilter(userFilterString,userFilterElement);
                        userFilterList.add(userFilter);
                    }
                }
                JSONObject chartStateJson = (JSONObject) parser.parse(chartState);
                JSONObject initialConfig = (JSONObject) chartStateJson.get("initialConfig");
                if(initialConfig!=null){
                    initialConfig.put("userFilters",userFilterList);
                    chartStateJson.put("initialConfig",initialConfig);
                    chartState = chartStateJson.toString();
                    //remove all values in useFilter
                    String newUserFilterJson = FieldUtil.getAsJSONArray(userFilterList,ReportUserFilterContext.class).toString();
                    updateVal.addUpdateValue("userFiltersJson",newUserFilterJson);
                }
            }
            if(chartState!=null){
                newChartState = chartStateConversion(reportElement,chartState,moduleName,reportType,assetCategoryNameVsId,reportContext);
                updateVal.addUpdateValue("chartState",newChartState);
            }
            if(reportElement.getElement(PackageConstants.ReportsConstants.FILTERS)!=null){
                XMLBuilder filters = reportElement.getElement(PackageConstants.ReportsConstants.FILTERS);
                List<XMLBuilder> filterElementList = filters.getElementList(PackageConstants.ReportsConstants.FILTER_ELEMENT);
                List<ReportFilterContext> filtersContext = new ArrayList<>();
                for(XMLBuilder filter : filterElementList){
                    ReportFilterContext context = convertFilterContext(filter);
                    filtersContext.add(context);
                }
                reportContext.setFilters(filtersContext);
                updateVal.addUpdateValue("filtersJson",reportContext.getFiltersJson());
            }
            if(reportElement.getElement(PackageConstants.ReportsConstants.DATA_FILTER)!=null){
                String dataFilter = reportElement.getElement(PackageConstants.ReportsConstants.DATA_FILTER).getCData();
                JSONObject dataFilterJson = (JSONObject) parser.parse(dataFilter);
                JSONObject newDataFilter = dataFilterConversion(dataFilterJson);
                updateVal.addUpdateValue("dataFilter",newDataFilter);
            }
            String dataPoint = dataPointConstruction(reportElement,reportType,newChartState,reportContext);
            updateVal.addUpdateValue("dataPointJson",dataPoint);
            batchUpdateList.add(updateVal);
        }
        if (CollectionUtils.isNotEmpty(batchUpdateList)) {
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .fields(updateReportFields)
                    .table(ModuleFactory.getReportModule().getTableName());
            updateRecordBuilder.batchUpdate(Collections.singletonList(reportFields.get(PackageConstants.DashboardConstants.ID)), batchUpdateList);
        }

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        for(Long id: ids){
            FacilioChain chain = TransactionChainFactoryV3.getDeleteReportChain();
            FacilioContext context = chain.getContext();
            context.put(PackageConstants.ReportsConstants.REPORT_ID,id);
            chain.execute(context);
        }
    }

    public static ReportContext createReportContext(XMLBuilder reportElement,Map<String,Long> linkNameVsFolderId,Map<String,Long> appNameVsAppId, Map<String,String> assetCategoryNameVsId) throws Exception {

        ReportContext reportContext = new ReportContext();
        String name = reportElement.getElement(PackageConstants.DashboardConstants.NAME).getText();
        String appName = reportElement.getElement(PackageConstants.DashboardConstants.APP_NAME).getText();
        long siteId = Long.parseLong(reportElement.getElement(PackageConstants.ReportsConstants.SITE_ID).getText());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if(reportElement.getElement(PackageConstants.DashboardConstants.MODULE_NAME)!=null){
            String moduleName = reportElement.getElement(PackageConstants.DashboardConstants.MODULE_NAME).getText();
            FacilioModule module = modBean.getModule(moduleName);
            reportContext.setModuleId(module.getModuleId());
        }
        String reportFolderName = reportElement.getElement(PackageConstants.DashboardConstants.FOLDER_LINK_NAME).getText();
        long reportFolderId = linkNameVsFolderId.get(reportFolderName);
        String description = reportElement.getElement(PackageConstants.DashboardConstants.DESCRIPTION).getText();

        if(reportElement.getElement(PackageConstants.FunctionConstants.WORKFLOW_STRING)!=null){
            String workFlowString= reportElement.getElement(PackageConstants.FunctionConstants.WORKFLOW_STRING).getCData();
            Long workflowId = WorkflowUtil.addWorkflow(workFlowString);
            reportContext.setWorkflowId(workflowId);
        }

        int reportType = Integer.parseInt(reportElement.getElement(PackageConstants.ReportsConstants.REPORT_TYPE).getText());
        String commonState = reportElement.getElement(PackageConstants.ReportsConstants.COMMON_STATE_JSON).getCData();
        String reportState = reportElement.getElement(PackageConstants.ReportsConstants.REPORT_STATE_JSON).getCData();
        int xAggr = Integer.parseInt(reportElement.getElement(PackageConstants.ReportsConstants.X_AGGR).getText());
        int dateOperator = Integer.parseInt(reportElement.getElement(PackageConstants.DashboardConstants.DATE_OPERATOR).getText());
        String dateValue = reportElement.getElement(PackageConstants.DashboardConstants.DATE_VALUE).getText();

        if(reportElement.getElement(PackageConstants.ReportsConstants.DATE_RANGE_JSON)!=null){
            String dateRangeJson = reportElement.getElement(PackageConstants.ReportsConstants.DATE_RANGE_JSON).getText();
            reportContext.setDateRangeJson(dateRangeJson);
        }

        if(reportElement.getElement(PackageConstants.ReportsConstants.BASELINE_JSON)!=null){
            String baseLineJson = reportElement.getElement(PackageConstants.ReportsConstants.BASELINE_JSON).getCData();
            reportContext.setBaselineJson(baseLineJson);
        }

        int analyticsType = Integer.parseInt(reportElement.getElement(PackageConstants.ReportsConstants.ANALYTICS_TYPE).getText());
        int booleanSettings = Integer.parseInt(reportElement.getElement(PackageConstants.ReportsConstants.BOOLEAN_SETTINGS).getText());

        if(reportElement.getElement(PackageConstants.ReportsConstants.TRANSFORM_CLASS)!=null){
            String transformClass = reportElement.getElement(PackageConstants.ReportsConstants.TRANSFORM_CLASS).getText();
            reportContext.setTransformClass(transformClass);
        }

        int moduleType = Integer.parseInt(reportElement.getElement(PackageConstants.ReportsConstants.MODULE_TYPE).getText());
        if(reportElement.getElement(PackageConstants.ReportsConstants.TIME_FILTER)!=null){
            String timeFilter = reportElement.getElement(PackageConstants.ReportsConstants.TIME_FILTER).getCData();
            reportContext.setTimeFilter(timeFilter);
        }
        String drillDownJson= reportElement.getElement(PackageConstants.ReportsConstants.DRILL_DOWN_PATH_JSON).getCData();
        String reportSettingJson = reportElement.getElement(PackageConstants.ReportsConstants.REPORT_SETTINGS_JSON).getCData();

        if(drillDownJson!=null){
            JSONArray drillDownArray = FacilioUtil.parseJsonArray(drillDownJson);
            List<ReportDrilldownPathContext> drillDownList = new ArrayList<>();
            for(Object drillDownObj : drillDownArray){
                org.json.simple.JSONObject drillDown = (org.json.simple.JSONObject) drillDownObj;
                ReportDrilldownPathContext drillDownContext = FieldUtil.getAsBeanFromJson(drillDown,ReportDrilldownPathContext.class);

                org.json.simple.JSONObject xField = drillDownContext.getxField();
                String fieldName = (String) xField.get("fieldName");
                String drillDownModuleName = (String) xField.get("moduleName");
                FacilioField field = modBean.getField(fieldName,drillDownModuleName);
                xField.put("field_id",field.getFieldId());
                xField.put("module_id",field.getModuleId());
                drillDownContext.setxField(xField);
                drillDownList.add(drillDownContext);
            }
            String newDrillDownJson = FieldUtil.getAsJSONArray(drillDownList,ReportDrilldownPathContext.class).toString();
            reportContext.setReportDrilldownJson(newDrillDownJson);
        }
        XMLBuilder tabularStateElement = reportElement.getElement(PackageConstants.ReportsConstants.TABULAR_STATE_ELEMENT);
        if(tabularStateElement!=null){
            String tabularState = tabularStateElement.getElement(PackageConstants.ReportsConstants.TABULAR_STATE_JSON).getCData();
            XMLBuilder criteriaElement = tabularStateElement.getElement(PackageConstants.CriteriaConstants.CRITERIA);
            JSONObject newTabularState = deserializeTabularState(tabularState);
            if (criteriaElement != null) {
                Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                JSONObject criteriaJson = FacilioUtil.getAsJSON(criteria);
                newTabularState.put("criteria",criteriaJson);
            }
            XMLBuilder templateJson = tabularStateElement.getElement(PackageConstants.ReportsConstants.TEMPLATE_JSON_ELEMENT);
            if(templateJson!=null){
                JSONObject templateObj = templateJsonDeserialize(templateJson);
                newTabularState.put("templateJSON",templateObj);
            }
            reportContext.setTabularState(newTabularState.toString());
        }

        reportContext.setName(name);
        reportContext.setAppId(appNameVsAppId.get(appName));
        reportContext.setSiteId(siteId);
        reportContext.setReportFolderId(reportFolderId);
        reportContext.setDescription(description);
        reportContext.setCommonState(commonState);
        reportContext.setReportStateJson(reportState);
        reportContext.setxAggr(xAggr);
        reportContext.setDateOperator(dateOperator);
        reportContext.setDateValue(dateValue);
        reportContext.setAnalyticsType(analyticsType);
        reportContext.setType(reportType);
        reportContext.setBooleanSetting(booleanSettings);
        reportContext.setModuleType(moduleType);
        reportContext.setReportSettingsJson(reportSettingJson);
        return reportContext;
    }
    public static String chartStateConversion(XMLBuilder reportElement,String chartState,String reportModule, Integer reportType,Map<String,String> assetCategoryNameVsId, ReportContext reportContext) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONParser parser = new JSONParser();
        JSONObject chartStateJson = (JSONObject) parser.parse(chartState);
        Set<String> keys = chartStateJson.keySet();

        for(String key : keys) {
            if(key.equals("initialConfig")){

                JSONObject initialConfig = (JSONObject) chartStateJson.get("initialConfig");
                JSONObject dimension = (JSONObject) initialConfig.get("dimension");
                JSONObject newDimension = deserializeDimension(reportElement,dimension,reportModule,chartStateJson);
                initialConfig.put("dimension",newDimension);

                JSONObject groupBy= (JSONObject) initialConfig.get("groupBy");
                JSONObject groupByDim = (JSONObject) groupBy.get("dimension");
                JSONObject groupBySubDim = (JSONObject) groupBy.get("subDimension");
                if(groupByDim!=null && groupBySubDim !=null){
                    JSONObject newGroupBy = deserializeDimension(null,groupBy,reportModule,null);
                    initialConfig.put("groupBy",newGroupBy);
                }

                List<JSONObject> metrics = (List<JSONObject>) initialConfig.get("metric");
                JSONObject metricAggregation = (JSONObject) initialConfig.get("metricAggregation");
                List<JSONObject> newMetric =  new ArrayList<>();
                JSONObject newMetricAggr = new JSONObject();
                for(JSONObject metric : metrics){
                    if(metric.containsKey("defaultMetric")){
                        newMetric.add(metric);
                    } else{
                        String fieldName = (String) metric.get("fieldName");
                        String moduleName = (String) metric.get("moduleName");
                        // for special fields
                        if(metric.containsKey("fieldId")){
                            FacilioField metricField = ReportFactory.getReportField(fieldName);
                            JSONObject metricFieldAsJson = FieldUtil.getAsJSON(metricField);
                            newMetric.add(metricFieldAsJson);
                            StringBuilder metricAggKey = new StringBuilder();
                            metricAggKey.append(metricField.getFieldId())
                                    .append("__")
                                    .append(metricField.getName());
                            newMetricAggr.put(metricAggKey,metricAggregation.get(metricAggKey.toString()));
                        } else if(fieldName !=null && moduleName !=null){
                            FacilioField metricField = modBean.getField(fieldName,moduleName);
                            JSONObject metricFieldAsJson = FieldUtil.getAsJSON(metricField);
                            newMetric.add(metricFieldAsJson);
                            StringBuilder metricAggKey = new StringBuilder();
                            metricAggKey.append(fieldName)
                                    .append("__")
                                    .append(moduleName);
                            StringBuilder newMetricAggrKey = new StringBuilder();
                            newMetricAggrKey.append(metricField.getFieldId())
                                    .append("__")
                                    .append(metricField.getName());
                            newMetricAggr.put(newMetricAggrKey,metricAggregation.get(metricAggKey.toString()));
                        }
                    }
                }
                initialConfig.put("metric",newMetric);
                initialConfig.put("metricAggregation",newMetricAggr);

                XMLBuilder criteriaElement = reportElement.getElement(PackageConstants.ReportsConstants.CONFIG_CRITERIA);
                if (criteriaElement != null) {
                    Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                    JSONObject criteriaJson = FieldUtil.getAsJSON(criteria);
                    initialConfig.put("criteria",criteriaJson);
                }

                if(initialConfig.get("customDateField")!=null){
                    Map<String,String> customDateFieldMap =(Map<String, String>) initialConfig.get("customDateField");
                    FacilioField customDateField = modBean.getField(customDateFieldMap.get("fieldName"),customDateFieldMap.get("moduleName"));
                    StringBuilder customDate = new StringBuilder();
                    customDate.append(customDateField.getFieldId())
                            .append("_")
                            .append(customDateField.getName());
                   initialConfig.put("customDateField",customDate.toString());
                }

                if(initialConfig.get("having")!=null){
                    for(JSONObject dataFilter : (List<JSONObject>) initialConfig.get("having")){
                        String fieldName = (String) dataFilter.get("fieldName");
                        String moduleName = (String) dataFilter.get("moduleName");
                        FacilioField dataField = modBean.getField(fieldName,moduleName);
                        dataFilter.put("fieldId",dataField.getFieldId());
                    }
                }

                if(initialConfig.get("reportDrilldownPath")!=null){
                    for(JSONObject drillDown : (List<JSONObject>) initialConfig.get("reportDrilldownPath")){
                        JSONObject xField = (JSONObject) drillDown.get("xField");
                        String fieldName = (String) xField.get("fieldName");
                        String moduleName = (String) xField.get("moduleName");
                        FacilioField drillDownField = modBean.getField(fieldName,moduleName);
                        JSONObject newXField = new JSONObject();
                        newXField.put("field_id",drillDownField.getFieldId());
                        newXField.put("module_id",drillDownField.getModule().getModuleId());
                        drillDown.put("xField",newXField);
                        if(drillDown.containsKey("fieldId_xAggr")){
                            StringBuilder fieldId_xAggr = new StringBuilder();
                            fieldId_xAggr.append(drillDownField.getFieldId())
                                    .append("_")
                                    .append(drillDown.get("xAggr"));
                            drillDown.put("fieldId_xAggr",fieldId_xAggr.toString());
                        }
                    }
                }
                chartStateJson.put("initialConfig",initialConfig);
            }
            else if(key.equals("dataPoints")) {
                List<JSONObject> dataPoints = dataPointDeserialize(reportElement,chartStateJson);
                chartStateJson.put("dataPoints",dataPoints);
            }
            else if(key.equals("common")){
                JSONObject common = (JSONObject) chartStateJson.get("common");
                JSONObject filters = (JSONObject) common.get("filters");
                List<Long> buildingIds = (List<Long>) common.get("buildingIds");
                if(!buildingIds.isEmpty()){
                    Map<Long,Long> buildingIdMap = PackageBeanUtil.getNewRecordIds("building",buildingIds);
                    if(buildingIdMap!=null && !buildingIdMap.isEmpty()){
                        List<Long> newBuildingId = buildingIds.stream().map(id->buildingIdMap.get(id)).collect(Collectors.toList());
                        common.put("buildingIds",newBuildingId);
                    }
                }
                if(filters!=null){
                    constructFilterObject(common,filters,assetCategoryNameVsId);
                }
                JSONObject sorting = (JSONObject) common.get("sorting");
                if(sorting!=null){
                    Long sortingFieldId  = sorting.get("sortByField") != null ? (Long) sorting.get("sortByField") : -1;
                    if(sortingFieldId > -1){
                        String fieldName = (String) sorting.get("fieldName");
                        String moduleName = (String) sorting.get("moduleName");
                        FacilioField sortingField = modBean.getField(fieldName,moduleName);
                        sorting.put("sortByField",sortingField.getFieldId());
                        common.put("sorting",sorting);
                    }
                }
                chartStateJson.put("common",common);
            }
            else if(key.equals("reportTemplate")){
                String templateContext = reportContext.getTemplate();
                chartStateJson.put("reportTemplate",parser.parse(templateContext));
            }
        }
        chartStateJson.remove("configCriteria");
        chartStateJson.remove("selectedIdObj");
        return chartStateJson.toString();
    }

    public static List<ReportDataPointContext> dataPointConversion(String dataPoints, JSONObject chartState) throws Exception{
        JSONArray jsonarray = FacilioUtil.parseJsonArray(dataPoints);

        List<ReportDataPointContext> dataPointContexts = new ArrayList<>();
        for (Object jsonObject : jsonarray) {
            ReportDataPointContext dataPoint = createDataPoint((JSONObject) jsonObject,null,chartState);
            dataPointContexts.add(dataPoint);
        }
        return dataPointContexts;
    }
    public static String convertReportTemplate(XMLBuilder reportTemplateElement,Map<String,String> assetCategoryNameVsId) throws Exception {
        String reportTemplate = reportTemplateElement.getElement(PackageConstants.ReportsConstants.REPORT_TEMPLATE).getCData();
        JSONParser parser = new JSONParser();
        JSONObject templateJson = (JSONObject) parser.parse(reportTemplate);
        ReportTemplateContext templateContext = FieldUtil.getAsBeanFromJson(templateJson, ReportTemplateContext.class);
        XMLBuilder criteriaElement = reportTemplateElement.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            templateContext.setCriteria(criteria);
        }
        if(templateContext.getCategoryId()>-1){
            String categoryName = templateContext.getCategoryName();
            templateContext.setCategoryId(Long.valueOf(assetCategoryNameVsId.get(categoryName)));
        }
        Long buildingId = resourcesIdReplacement(templateContext.getBuildingId());
        templateContext.setBuildingId(buildingId);
        Long parentId = resourcesIdReplacement(templateContext.getParentId());
        templateContext.setParentId(parentId);
        Long defaultId = resourcesIdReplacement(templateContext.getDefaultValue());
        templateContext.setDefaultValue(defaultId);
        JSONObject newTemplateJson = FacilioUtil.getAsJSON(templateContext);
        return newTemplateJson.toString();
    }

    public static ReportDataPointContext createDataPoint(JSONObject dataPointJson,Map<String,String> assetCategoryNameVsId,JSONObject chartState) throws Exception{
        ReportDataPointContext dataPoint = FieldUtil.getAsBeanFromJson(dataPointJson, ReportDataPointContext.class);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if(dataPoint.getHavingCriteria()!=null){
            List<ReportHavingContext> havingCriteria = dataPoint.getHavingCriteria();
            for(ReportHavingContext having : havingCriteria){
                String moduleName = having.getModuleName();
                FacilioField fields = modBean.getField(having.getFieldName(),moduleName);
                having.setFieldId(fields.getFieldId());
            }
        }
        if(chartState!=null){
            JSONObject initialConfig = (JSONObject) chartState.get("initialConfig");
            JSONObject criteria = initialConfig !=null ? (JSONObject) initialConfig.get("criteria") : null;
            if(criteria!=null){
                Criteria criteriaObj = FieldUtil.getAsBeanFromJson(criteria,Criteria.class);
                dataPoint.setCriteria(criteriaObj);
            }
        }

        ReportFieldContext xField = dataPoint.getxAxis();
        if(xField!=null){
            String fieldName = xField.getFieldName();
            String moduleName = xField.getModuleName();
            if(moduleName!=null){
                FacilioModule module = modBean.getModule(moduleName);
                if(fieldName !=null){
                    FacilioField xFieldContext = modBean.getField(fieldName,moduleName);
                    if(xFieldContext!=null){
                        xField.setFieldId(xFieldContext.getFieldId());
                        xField.setModuleId(xFieldContext.getModuleId());
                        xField.setField(module,xFieldContext);
                    }
                }
                xField.setModule(module);
            }
            if(xField.getLookupFieldId()>0){
                String lookUpFieldName = xField.getLookUpFieldName();
                String lookUpModuleName = xField.getLookUpFieldModuleName();
                FacilioField lookUpField = modBean.getField(lookUpFieldName,lookUpModuleName);
                xField.setLookupFieldId(lookUpField.getFieldId());
            }
            if(chartState!=null){
                JSONObject initialConfig = (JSONObject) chartState.get("initialConfig");
                JSONObject dimension =  initialConfig !=null ? (JSONObject) initialConfig.get("dimension") : null;
                if(dimension!=null){
                    JSONObject subDimension = (JSONObject) dimension.get("subDimension");
                    if(subDimension!=null){
                        xField.setSelectValuesOnly((List<Long>) subDimension.get("selectedIds"));
                    }
                }
            }
            dataPoint.setxAxis(xField);
        }
        ReportYAxisContext yField = dataPoint.getyAxis();
        Long kpiId = -1l;
        if(yField !=null){
            String dynamicKpi = yField.getDynamicKpi();
            if(dynamicKpi !=null){
               kpiId = ConnectedRuleUtil.getConnectedRuleIdWithLinkName(yField.getKpiLinkName(),FacilioConstants.ReadingKpi.READING_KPI);
               yField.setDynamicKpi(String.valueOf(kpiId));
            }
            else{
                String fieldName = yField.getFieldName();
                String moduleName = yField.getModuleName();
                if(moduleName!=null){
                    FacilioModule module = modBean.getModule(moduleName);
                    yField.setModule(module);
                    yField.setModuleId(module.getModuleId());
                    if(fieldName !=null) {
                        FacilioField yFieldContext = modBean.getField(fieldName, moduleName);
                        if (yField.getFieldId() == -1 && fieldName.equals("id")) {
                            yField.setField(module, yFieldContext);
                        } else {
                            FacilioField field = ReportFactory.getReportField(fieldName);
                            yField.setField(module, field);
                        }
                        if (yField.getFieldId() > 0) {
                            yField.setFieldId(yFieldContext.getFieldId());
                            yField.setField(module, yFieldContext);
                        } else if (yField.getLookupFieldId() > 0) {
                            String lookUpFieldName = yField.getLookUpFieldName();
                            String lookUpModuleName = yField.getLookUpFieldModuleName();
                            FacilioField lookUpField = modBean.getField(lookUpFieldName, lookUpModuleName);
                            yField.setLookupFieldId(lookUpField.getFieldId());
                            yField.setField(module, yFieldContext);
                        }
                    }
                }

            }
            dataPoint.setyAxis(yField);
        }

        List<ReportGroupByField> groupByFields = dataPoint.getGroupByFields();
        if(groupByFields!=null){
            for(ReportGroupByField groupByField : groupByFields){
                if(groupByField!=null){
                    String fieldName = groupByField.getFieldName();
                    String moduleName = groupByField.getModuleName();
                    FacilioField groupByFieldContext = modBean.getField(fieldName,moduleName);
                    if(groupByFieldContext!=null){
                        groupByField.setFieldId(groupByFieldContext.getFieldId());
                        groupByField.setModuleId(groupByFieldContext.getModuleId());
                    }
                    if(groupByField !=null && groupByField.getLookupFieldId()>0){
                        String lookUpFieldName = groupByField.getLookUpFieldName();
                        String lookUpModuleName = groupByField.getLookUpFieldModuleName();
                        FacilioField lookUpField = modBean.getField(lookUpFieldName,lookUpModuleName);
                        groupByField.setLookupFieldId(lookUpField.getFieldId());
                    }
                }
            }
        }
        dataPoint.setGroupByFields(groupByFields);

        if(dataPoint.getDateFieldId()>0){
            String fieldName = dataPoint.getDateFieldName();
            String moduleName = dataPoint.getDateFieldModuleName();
            FacilioField dateField = modBean.getField(fieldName,moduleName);
            FacilioModule module  = modBean.getModule(moduleName);
            dataPoint.setDateFieldId(dateField.getFieldId());
            ReportFieldContext dateFieldContext = new ReportFieldContext();
            dateFieldContext.setFieldId(dateField.getFieldId());
            dateFieldContext.setModuleId(dateField.getModuleId());
            dateFieldContext.setField(module,dateField);
            dateFieldContext.setModule(module);
            dataPoint.setDateField(dateFieldContext);
        }

        if(dataPoint.getAssetCategoryId()>0 && dataPoint.getAssetCategoryName()!=null){
            String assetCategoryName = dataPoint.getAssetCategoryName();
            dataPoint.setAssetCategoryId(Long.parseLong(assetCategoryNameVsId.get(assetCategoryName)));
        }
        Map<String,Object> metaData = dataPoint.getMetaData();
        if(metaData!=null){
            if(metaData.containsKey("categoryName")){
                String categoryName= (String) metaData.get("categoryName");
                metaData.put("categoryId",assetCategoryNameVsId.get(categoryName));
            }
            if(metaData.containsKey("parentIds")){
               List<Integer> parentIdList = (List<Integer>) metaData.get("parentIds");
                List<Long> parentIds = new ArrayList<>();
                for (Integer parentId : parentIdList) {
                    parentIds.add((long) parentId);
                }
               Map<Long,Long> parentIdMap = PackageBeanUtil.getNewRecordIds("resource",parentIds);
                if(parentIdMap!=null && !parentIdMap.isEmpty()){
                    List<Long> newParentIds = parentIds.stream().map(id->parentIdMap.get(id)).collect(Collectors.toList());
                    metaData.put("parentIds",newParentIds);
                }
            }
            dataPoint.setMetaData(metaData);
        }
        ReportDynamicKpiContext dynamicKpiContext = dataPoint.getDynamicKpi();
        if(dynamicKpiContext!=null){
            dynamicKpiContext.setDynamicKpi(String.valueOf(kpiId));
            List<Long> parentId = dynamicKpiContext.getParentId();
            Map<Long,Long> parentIdMap = PackageBeanUtil.getNewRecordIds("resource",parentId);
            if(parentIdMap!=null && !parentIdMap.isEmpty()){
                List<Long> newParentId = parentId.stream().map(id->parentIdMap.get(id)).collect(Collectors.toList());
                dynamicKpiContext.setParentId(newParentId);
            }
            dataPoint.setDynamicKpi(dynamicKpiContext);
        }
        if(dataPoint.getBuildingId()>0){
            Long buildingId = resourcesIdReplacement(dataPoint.getBuildingId());
            dataPoint.setBuildingId(buildingId);
        }
        return dataPoint;
    }
    public static ReportFilterContext convertFilterContext(XMLBuilder filterElement) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String filterString = filterElement.getElement(PackageConstants.ReportsConstants.FILTERS_JSON).getCData();
        JSONParser parser = new JSONParser();
        JSONObject filterJson = (JSONObject) parser.parse(filterString);
        ReportFilterContext filterContext = FieldUtil.getAsBeanFromJson(filterJson,ReportFilterContext.class);
        Long filterModuleId = filterContext.getModuleId();
        if(filterModuleId>-1) {
            FacilioModule filterModule = modBean.getModule(filterContext.getModuleName());
            filterContext.setModuleId(filterModule.getModuleId());
        }
        XMLBuilder criteriaElement = filterElement.getElement(PackageConstants.CriteriaConstants.CRITERIA);
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            filterContext.setCriteria(criteria);
        }
        if(filterContext.getFilterValue()!=null){
            List<String> filterValuesString = Arrays.asList(filterContext.getFilterValue().split(","));
            List<Long> filterValue = filterValuesString.stream().map(id->Long.valueOf(id)).collect(Collectors.toList());
            Map<Long,Long> filterMap = PackageBeanUtil.getNewRecordIds("resource",filterValue);
            if(filterMap!=null && !filterMap.isEmpty()){
                List<Long> newFilterValues = filterValue.stream().map(id->filterMap.get(id)).collect(Collectors.toList());
                filterContext.setFilterValue(StringUtils.join(newFilterValues));
            }
        }
        return filterContext;
    }
    public static JSONObject deserializeDimension(XMLBuilder reportElement,JSONObject dimension, String reportModule,JSONObject chartStateJson) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject actualDimension = (JSONObject) dimension.get("dimension");
        String dimensionName = (String) actualDimension.get("displayName");
        if(!dimensionName.equals("Space")){
            FacilioChain chain = TransactionChainFactoryV3.getReportFieldsChain();
            FacilioContext context = chain.getContext();
            context.put("moduleName", reportModule);
            context.put(FacilioConstants.ContextNames.IS_SANDBOX,true);
            chain.execute();
            JSONObject reportFields = (JSONObject) context.get("reportFields");
            Map<String,ArrayList> dimensionObj  = (Map<String, ArrayList>) reportFields.get("dimension");
            Map<String,ArrayList> newDimensionObj = new HashMap<>();
            for(Map.Entry<String,ArrayList> map : dimensionObj.entrySet()){
                newDimensionObj.put(map.getKey().toLowerCase(),map.getValue());
            }
            dimensionName = dimensionName.toLowerCase();
            List<JSONObject> fieldsList = new ArrayList<>();
            for(Object fields : newDimensionObj.get(dimensionName)){
                JSONObject json = FacilioUtil.getAsJSON(fields);
                fieldsList.add(json);
            }
            actualDimension.put("subFields", fieldsList);
            JSONObject subDimension = (JSONObject) dimension.get("subDimension");
            FacilioField field = modBean.getField((String) subDimension.get("fieldName"), (String) subDimension.get("moduleName"));
            JSONObject fieldAsJson = FieldUtil.getAsJSON(field);
            if(chartStateJson!=null && chartStateJson.containsKey("selectedIdObj")){
                JSONObject selectedIdObj = (JSONObject) chartStateJson.get("selectedIdObj");
                Long moduleType = (Long) selectedIdObj.get("moduleType");
                String lookUpModule = (String) selectedIdObj.get("lookUpModuleName");
                int pickListModule = FacilioModule.ModuleType.PICK_LIST.getValue();
                List<Long> pickListIds = new ArrayList<>();
                if(moduleType == pickListModule && lookUpModule.equals("ticketstatus")) {
                    XMLBuilder dimensionPickList = reportElement.getElement(PackageConstants.ReportsConstants.DIMENSION_PICK_LIST);
                    if (dimensionPickList != null) {
                        String values = PackageBeanUtil.pickListValueBuilder(dimensionPickList);
                        if(values!=null){
                            String[] valueArray = values.split(",");
                            pickListIds.addAll(Arrays.stream(valueArray).map(id->Long.valueOf(id)).collect(Collectors.toList()));
                            fieldAsJson.put("selectedIds",pickListIds);
                        }
                    }
                }
                else if(moduleType == pickListModule ){
                    List<String> pickListName = (List<String>) selectedIdObj.get("names");
                    Map<String, String> pickListMap = PackageUtil.getNameVsRecordIdForPicklistModule(lookUpModule);
                    if(pickListMap!=null && !pickListMap.isEmpty()){
                        pickListIds.addAll(pickListName.stream().map(name-> Long.valueOf(pickListMap.get(name))).collect(Collectors.toList()));
                    }
                    fieldAsJson.put("selectedIds",pickListIds);
                }
                else{
                    List<Long> oldPickListIds = (List<Long>) selectedIdObj.get("selectedIds");
                    Map<Long,Long> pickListMap = PackageBeanUtil.getNewRecordIds(lookUpModule,oldPickListIds);
                    if(pickListMap!=null && !pickListMap.isEmpty()){
                        pickListIds = oldPickListIds.stream().map(id->pickListMap.get(id)).collect(Collectors.toList());
                        fieldAsJson.put("selectedIds",pickListIds);
                    }
                }
            }
            dimension.put("dimension",actualDimension);
            dimension.put("subDimension",fieldAsJson);
        }
        return dimension;
    }
    public static void templateJsonXMLConversion(JSONObject templateJson,XMLBuilder tabularStateElement, String moduleName) throws Exception{
        XMLBuilder templateJSONElement = tabularStateElement.element(PackageConstants.ReportsConstants.TEMPLATE_JSON_ELEMENT);
        JSONObject columnFormatting = (JSONObject) templateJson.get("columnFormatting");
        JSONObject newColumnFormatting = new JSONObject();
        if(columnFormatting!=null){
            Set<String> keys = columnFormatting.keySet();
            XMLBuilder formattingElement = templateJSONElement.element(PackageConstants.ReportsConstants.CONDITIONAL_FORMATTING_ELEMENT);
            for(String key : keys){
                JSONObject formattingObj= (JSONObject) columnFormatting.get(key);
                if(formattingObj.containsKey("conditionalFormat")){
                    XMLBuilder conditionalFormattingObj = formattingElement.element(PackageConstants.ReportsConstants.CONDITIONAL_FORMAT_OBJECT);
                    JSONObject newFormattingObj = new JSONObject();
                    List<JSONObject> conditionListObjects = (List<JSONObject>) formattingObj.get("conditionalFormat");
                    formattingObj.put("conditionalFormat", null);
                    newFormattingObj.put(key,formattingObj);
                    conditionalFormattingObj.element(PackageConstants.ReportsConstants.CONDITIONAL_FORMAT_JSON).cData(newFormattingObj.toString());
                    for(JSONObject condition : conditionListObjects){
                        XMLBuilder conditionElement = conditionalFormattingObj.element(PackageConstants.ReportsConstants.CONDITION_ELEMENT);
                        JSONObject criteriaJson = (JSONObject) condition.get("criteria");
                        if(criteriaJson!=null){
                            Criteria conditionObject = FieldUtil.getAsBeanFromJson(criteriaJson,Criteria.class);
                            conditionElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(conditionObject, conditionElement.element(PackageConstants.CriteriaConstants.CRITERIA), moduleName));
                            condition.put("criteria",null);
                        }
                        conditionElement.element(PackageConstants.ReportsConstants.CONDITION_JSON).cData(condition.toString());
                    }
                }
                else{
                    newColumnFormatting.put(key,columnFormatting.get(key));
                }
            }
        }
        templateJson.put("columnFormatting",newColumnFormatting);
        templateJSONElement.element(PackageConstants.ReportsConstants.TEMPLATE_JSON).cData(templateJson.toString());
    }
    public static JSONObject deserializeTabularState(String tabularState) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONParser parser =  new JSONParser();
        JSONObject tabularStateJson = (JSONObject) parser.parse(tabularState);
        String dateFieldName = (String) tabularStateJson.get("dateFieldName");
        String dateFieldModuleName = (String) tabularStateJson.get("dateFieldModuleName");
        if(dateFieldName!=null && dateFieldModuleName !=null){
            FacilioField field = modBean.getField(dateFieldName,dateFieldModuleName);
            tabularStateJson.put("dateFieldId",field.getFieldId());
        }
        List<JSONObject> dataList = (List<JSONObject>) tabularStateJson.get("data");
        if(CollectionUtils.isNotEmpty(dataList)){
            for(JSONObject data : dataList){
                if(data!=null){
                    dataDeserialize(data);
                }
            }
            tabularStateJson.put("data",dataList);
        }
        List<JSONObject> valueList = (List<JSONObject>) tabularStateJson.get("values");
        if(CollectionUtils.isNotEmpty(valueList)){
            for (JSONObject value : valueList){
                 if(value!=null){
                     String valueType = (String) value.get("valueType");
                     if(valueType.equals("DATA")){
                         JSONObject moduleMeasure = (JSONObject) value.get("moduleMeasure");
                         dataDeserialize(moduleMeasure);
                         value.put("moduleMeasure",moduleMeasure);
                     }
                 }
            }
            tabularStateJson.put("values",valueList);
        }

        List<JSONObject> rowList = (List<JSONObject>) tabularStateJson.get("rows");
        if(CollectionUtils.isNotEmpty(rowList)){
            for(JSONObject row : rowList){
                if(row!=null){
                    dataDeserialize(row);
                }
            }
            tabularStateJson.put("rows",rowList);
        }

        return tabularStateJson;
    }
    public static void dataDeserialize(JSONObject data) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject field = (JSONObject) data.get("field");
        JSONObject readingField = (JSONObject) data.get("readingField");
        if(field!=null){
            String fieldName = (String) field.get("name");
            String moduleName = data.get("moduleName")!=null ? (String) data.get("moduleName") : (String)field.get("moduleName");
            if(fieldName !=null && !fieldName.equals("id") && moduleName!=null){
                FacilioField fieldObj = modBean.getField(fieldName,moduleName);
                field.put("id",fieldObj.getFieldId());
                JSONObject fieldModule = FacilioUtil.getAsJSON(fieldObj.getModule());
                field.put("module",fieldModule);
                field.put("moduleId",fieldObj.getModuleId());
            }
            else if(moduleName !=null){
                FacilioModule module = modBean.getModule(moduleName);
                field.put("moduleId",module.getModuleId());
                JSONObject moduleJson = FacilioUtil.getAsJSON(module);
                field.put("module",moduleJson);
            }
            data.put("field",field);
        }else if(readingField!=null){
            String name = (String) readingField.get("name");
            String moduleName = (String) data.get("moduleName");
            if(name !=null && moduleName !=null){
                FacilioField readingFieldObj = modBean.getField(name,moduleName);
                readingField.put("id",readingFieldObj.getFieldId());
                readingField.put("moduleId",readingFieldObj.getModuleId());
                JSONObject moduleJson = FacilioUtil.getAsJSON(readingFieldObj.getModule());
                readingField.put("module",moduleJson);
            }
            data.put("readingField", readingField);
        }
        String fieldName = (String) data.get("dateFieldName");
        String moduleName = (String) data.get("dateModuleName");
        if(fieldName!=null && moduleName!=null){
            FacilioField dateField = modBean.getField(fieldName,moduleName);
            data.put("dateFieldId",dateField.getFieldId());
        }
        String lookupFieldName = (String) data.get("lookupFieldName");
        String lookupModuleName = (String) data.get("lookupModuleName");
        if(lookupFieldName!=null && lookupModuleName !=null){
            FacilioField lookupField = modBean.getField(lookupFieldName,lookupModuleName);
            data.put("lookupFieldId",lookupField.getFieldId());
        }
    }
    public static JSONObject templateJsonDeserialize(XMLBuilder templateJsonElement) throws Exception{
        JSONParser parser = new JSONParser();
        String templateJson = templateJsonElement.getElement(PackageConstants.ReportsConstants.TEMPLATE_JSON).getCData();
        JSONObject templateJsonObj = (JSONObject) parser.parse(templateJson);
        XMLBuilder conditionFormatElement = templateJsonElement.getElement(PackageConstants.ReportsConstants.CONDITIONAL_FORMATTING_ELEMENT);
        if(conditionFormatElement !=null){
            JSONObject columnFormatting = (JSONObject) templateJsonObj.get("columnFormatting");
            List<XMLBuilder> conditionObjList = conditionFormatElement.getElementList(PackageConstants.ReportsConstants.CONDITIONAL_FORMAT_OBJECT);
            for(XMLBuilder conditionElement : conditionObjList){

                List<XMLBuilder> criteriaBuilder =  conditionElement.getElementList(PackageConstants.ReportsConstants.CONDITION_ELEMENT);
                List<JSONObject> formattingList = new ArrayList<>();
                if(criteriaBuilder!=null){
                    for(XMLBuilder criteria : criteriaBuilder){
                        String condition = criteria.getElement(PackageConstants.ReportsConstants.CONDITION_JSON).getCData();
                        JSONObject conditionJson = (JSONObject) parser.parse(condition);
                        XMLBuilder criteriaElement = criteria.getElement(PackageConstants.CriteriaConstants.CRITERIA);
                        if (criteriaElement != null) {
                            Criteria criteriaObj = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                            JSONObject criteriaJson = FacilioUtil.getAsJSON(criteriaObj);
                            conditionJson.put("criteria",criteriaJson);
                        }
                        formattingList.add(conditionJson);
                    }
                }

                String formatObj = conditionElement.getElement(PackageConstants.ReportsConstants.CONDITIONAL_FORMAT_JSON).getCData();
                JSONObject formatObjJson = (JSONObject) parser.parse(formatObj);
                Set<String> keys = formatObjJson.keySet();
                for(String key : keys){
                    JSONObject jsonWithFormatting = (JSONObject) formatObjJson.get(key);
                    jsonWithFormatting.put("conditionalFormat",formattingList);
                    columnFormatting.put(key,jsonWithFormatting);
                }
            }
            templateJsonObj.put("columnFormatting",columnFormatting);
        }
        return templateJsonObj;
    }
    public static List<JSONObject> dataPointDeserialize(XMLBuilder reportElement, JSONObject chartStateJson) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<JSONObject> dataPoints = (List<JSONObject>) chartStateJson.get("dataPoints");
        for (JSONObject dataPoint : dataPoints) {
            Long fieldId = (Long) dataPoint.get("fieldId");
            Long moduleId = (Long) dataPoint.get("moduleId");
            String fieldName = (String) dataPoint.get("fieldName");
            String moduleName = (String) dataPoint.get("moduleName");
            if(fieldName!=null && moduleName!=null){
                FacilioField field = modBean.getField(fieldName, moduleName);
                if (fieldId!=null && fieldId >0) {
                    dataPoint.put("fieldId", field.getFieldId());
                    dataPoint.put("moduleId",field.getModuleId());
                }
                else if(moduleId!=null && moduleId>0){
                    FacilioModule module = modBean.getModule(moduleName);
                    dataPoint.put("moduleId",module.getModuleId());
                }
            }
            if(dataPoint.containsKey("buildingId")){
                Long buildingId =  resourcesIdReplacement((Long) dataPoint.get("buildingId"));
                dataPoint.put("buildingId",buildingId);
            }
            if(dataPoint.containsKey("parentId")){
               Long parentId =  resourcesIdReplacement((Long) dataPoint.get("parentId"));
               dataPoint.put("parentId", parentId);
            }
            List<JSONObject> groupByList = (List<JSONObject>) dataPoint.get("groupBy");
            if(CollectionUtils.isNotEmpty(groupByList)){
                for (JSONObject groupBy : groupByList) {
                    String groupByFieldName = (String) groupBy.get("name");
                    String groupByModuleName = (String) groupBy.get("moduleName");
                    if(groupByModuleName!=null && groupByFieldName!=null){
                        FacilioField groupByField = modBean.getField(groupByFieldName, groupByModuleName);
                        Long fieldType = (Long) groupBy.get("fieldType");
                        JSONObject groupByLabelObject = (JSONObject) dataPoint.get("groupByLabelValues");
                        JSONObject groupByLabelValue = (JSONObject) groupByLabelObject.get(groupBy.get("id"));
                        JSONObject newGroupByLabelValue =  new JSONObject();
                        List<JSONObject> children =  dataPoint.get("children") !=null ? (List<JSONObject>) dataPoint.get("children") : new ArrayList<>();
                        int lookUpType =  FieldType.LOOKUP.getTypeAsInt();
                        int multiLookUpType = FieldType.MULTI_LOOKUP.getTypeAsInt();
                        if(fieldType == lookUpType || fieldType == multiLookUpType){
                            Boolean isPickList = (Boolean) groupBy.get("isPickList");
                            String lookupName = (String) groupBy.get("lookUpName");
                           if(isPickList){
                               if(lookupName.equals("ticketstatus")){
                                   XMLBuilder pickListElement = reportElement.getElement(PackageConstants.ReportsConstants.GROUP_BY_PICK_LIST);
                                   String values = PackageBeanUtil.pickListValueBuilder(pickListElement);
                                   if(values!=null){
                                       String[] valueArray = values.split(",");
                                       List<String> valuesList = Arrays.asList(valueArray);
                                       List<String> oldPickListIds = new ArrayList<>();
                                       for(Object id: (JSONArray) chartStateJson.get("groupByPickListIds")){
                                           oldPickListIds.add(id.toString());
                                       }
                                       Map<String,String> oldIdVsNewId = new HashMap<>();
                                       for(int i=0; i<valuesList.size();i++){
                                           if(groupByLabelValue!=null){
                                               newGroupByLabelValue.put(valuesList.get(i),groupByLabelValue.get(oldPickListIds.get(i)));
                                           }
                                           oldIdVsNewId.put(oldPickListIds.get(i),valuesList.get(i));
                                       }
                                       for (JSONObject child : children){
                                           Long id = child.get("fieldId")!=null ? (Long)  child.get("fieldId") : -1;
                                           child.put("fieldId",oldIdVsNewId.get(id));
                                       }
                                   }
                               }
                               else{
                                   Map<String,String> pickListMap = PackageUtil.getNameVsRecordIdForPicklistModule(lookupName);
                                   if(groupByLabelValue!=null){
                                       Set<String> keys = groupByLabelValue.keySet();
                                       List<String> pickList = new ArrayList<>(keys);
                                       //  pickList.addAll(keys);
                                       for(String key :pickList){
                                           newGroupByLabelValue.put(pickListMap.get(key),groupByLabelValue.get(key));
                                       }
                                   }
                                   for(JSONObject child: children){
                                       child.put("fieldId",Long.valueOf(pickListMap.get(child.get("fieldId"))));
                                   }
                               }
                           }else{
                               if(groupByLabelValue!=null){
                                   Set<String> keys = groupByLabelValue.keySet();
                                   List<Long> oldRecordIds = keys.stream().map(id->Long.valueOf(id)).collect(Collectors.toList());
                                   Map<Long,Long> newRecordIds = PackageBeanUtil.getNewRecordIds(lookupName,oldRecordIds);
                                   if(newRecordIds!=null && !newRecordIds.isEmpty()){
                                       for (Long id : oldRecordIds){
                                           newGroupByLabelValue.put(String.valueOf(newRecordIds.get(id)),groupByLabelValue.get(id));
                                       }
                                       for(JSONObject child : children){
                                           child.put("fieldId", newRecordIds.get(child.get("fieldId")));
                                       }
                                   }
                               }
                           }
                            JSONObject newGroupByLabelObject = new JSONObject();
                            newGroupByLabelObject.put(String.valueOf(groupByField.getFieldId()),newGroupByLabelValue);
                            dataPoint.put("children", children);
                            dataPoint.put("groupByLabelValues",newGroupByLabelObject);
                        }
                        groupBy.put("id", groupByField.getFieldId());
                    }
                    if(groupByFieldName.equals("siteId")){
                         JSONObject groupByLabelObject = (JSONObject) dataPoint.get("groupByLabelValues");
                         JSONObject groupByLabelValue = (JSONObject) groupByLabelObject.get("-1");
                         Set<String> keys = groupByLabelValue.keySet();
                         List<Long> oldRecordIds = keys.stream().map(id->Long.valueOf(id)).collect(Collectors.toList());
                         Map<Long,Long> newRecordIds = PackageBeanUtil.getNewRecordIds("site",oldRecordIds);
                         if(newRecordIds!=null && !newRecordIds.isEmpty()){
                             JSONObject newGroupByLabelValue =  new JSONObject();
                             for (Long id : oldRecordIds){
                                 newGroupByLabelValue.put(String.valueOf(newRecordIds.get(id)),groupByLabelValue.get(id));
                             }
                             List<JSONObject> children = (List<JSONObject>) dataPoint.get("children");
                             for(JSONObject child : children){
                                 child.put("fieldId", newRecordIds.get(child.get("fieldId")));
                             }
                             groupByLabelObject.put(-1,newGroupByLabelValue);
                             dataPoint.put("children", children);
                             dataPoint.put("groupByLabelValues",groupByLabelObject);
                         }
                    }
                }
                dataPoint.put("groupBy", groupByList);
            }
        }
        return dataPoints;
    }
    public static JSONObject deserializeUserFilter(String userFilterString,XMLBuilder userFilterElement) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONParser parser = new JSONParser();
        JSONObject userFilter = (JSONObject) parser.parse(userFilterString);
            String fieldName = (String) userFilter.get("fieldName");
            String moduleName = (String) userFilter.get("moduleName");
            FacilioField userFilterField = modBean.getField(fieldName,moduleName);
            userFilter.put("fieldId",userFilterField.getFieldId());
            FacilioModule lookupModule = ((LookupField) userFilterField).getLookupModule();
            int moduleType = lookupModule.getType();
            String lookupModuleName = lookupModule.getName();
            JSONObject chooseValue = (JSONObject) userFilter.get("chooseValue");
            List<String> chosenValues = (List<String>) chooseValue.get("values");
            List<String> defaultValues = (List<String>) userFilter.get("defaultValues");
            Map<String,String> lookupMap = PackageUtil.getNameVsRecordIdForPicklistModule(lookupModuleName);
            int pickListModule = FacilioModule.ModuleType.PICK_LIST.getValue();
            if(moduleType == pickListModule && lookupModuleName.equals("ticketstatus")){
                XMLBuilder chosenListElement = userFilterElement.getElement(PackageConstants.ReportsConstants.USER_FILTER_CHOSEN_LIST);
                if(chosenListElement!=null){
                    String values = PackageBeanUtil.pickListValueBuilder(chosenListElement);
                    if(values!=null){
                        String[] stringArray = values.split(",");
                        List<String> valueArray =  Arrays.asList(stringArray);;
                        chooseValue.put("values",valueArray);
                        userFilter.remove("userFilterChosenValues");
                        userFilter.put("chooseValue",chooseValue);
                    }
                }
                XMLBuilder defaultListElement = userFilterElement.getElement(PackageConstants.ReportsConstants.USER_FILTER_DEFAULT_LIST);
                if(defaultListElement!=null){
                    String values = PackageBeanUtil.pickListValueBuilder(defaultListElement);
                    if(values!=null){
                        String[] valueArray = values.split(",");
                        userFilter.remove("userFilterDefaultValues");
                        userFilter.put("defaultValues",valueArray);
                    }
                }
                XMLBuilder allValueElement = userFilterElement.getElement(PackageConstants.ReportsConstants.USER_FILTER_ALL_LIST);
                if(allValueElement!=null){
                    String values = PackageBeanUtil.pickListValueBuilder(allValueElement);
                    if(values!=null){
                        String[] valueArray = values.split(",");
                        List<String> valuesList = Arrays.asList(valueArray);
                        Map<String,String> allValues  = (Map<String, String>) userFilter.get("allValues");
                        List<String> oldIds = new ArrayList<>(allValues.keySet());
                        List<Map<String, String>> valueMap = new ArrayList<>();
                        for(int i = 0; i<valuesList.size();i++){
                            Map<String,String> value = new HashMap<>();
                            value.put(valuesList.get(i),allValues.get(oldIds.get(i)));
                            valueMap.add(value);
                        }
                        userFilter.put("allValues",valueMap);
                        userFilter.remove("userFilterAllValues");
                    }
                }
            }
            else if(moduleType == pickListModule){
               Map<String,String> allValues = (Map<String,String>) userFilter.get("allValues");
                  if(!chosenValues.isEmpty()){
                      List<String> pickList = chosenValues.stream().map(name->lookupMap.get(name)).collect(Collectors.toList());
                      chooseValue.put("values",pickList);
                      userFilter.put("chooseValue",chooseValue);
                  }
                  if(!defaultValues.isEmpty()){
                      List<String> pickList = defaultValues.stream().map(name->lookupMap.get(name)).collect(Collectors.toList());
                      userFilter.put("defaultValues",pickList);
                  }
                  if(!allValues.isEmpty()){
                      List<String> pickListOld = new ArrayList<>(allValues.values());
                      List<Map<String,String>> valueMap = new ArrayList<>();
                      for(int i = 0;i<pickListOld.size();i++){
                          Map<String,String> map = new HashMap<>();
                          map.put(lookupMap.get(pickListOld.get(i)),pickListOld.get(i));
                          valueMap.add(map);
                      }
                      userFilter.put("allValues",valueMap);
                  }
            }
            else{
                Map<String,String> allValues = (Map<String, String>) userFilter.get("allValues");
                if(!chosenValues.isEmpty()){
                    List<Long> chosenValueAsLong = chosenValues.stream().map(id->Long.valueOf(id)).collect(Collectors.toList());
                    Map<Long, Long> idMap =  PackageBeanUtil.getNewRecordIds(lookupModuleName,chosenValueAsLong);
                    if(idMap!=null && !idMap.isEmpty()){
                        List<String> newChosenValueAsString = chosenValueAsLong.stream().map(id->String.valueOf(idMap.get(id))).collect(Collectors.toList());
                        chooseValue.put("values",newChosenValueAsString);
                        userFilter.put("chooseValue",chooseValue);
                    }
                }
                if(!defaultValues.isEmpty()){
                    List<Long> defaultValueAsString = defaultValues.stream().map(id->Long.valueOf(id)).collect(Collectors.toList());
                    Map<Long,Long> idMap =  PackageBeanUtil.getNewRecordIds(lookupModuleName,defaultValueAsString);
                    if(idMap!=null && !idMap.isEmpty()){
                        List<String> newDefaultValueAsString = defaultValueAsString.stream().map(id->String.valueOf(idMap.get(id))).collect(Collectors.toList());
                        userFilter.put("defaultValues",newDefaultValueAsString);
                    }
                }
                if(!allValues.isEmpty()){
                    List<String> pickListOld = new ArrayList<>(allValues.keySet());
                    List<Long> pickListString = pickListOld.stream().map(id->Long.valueOf(id)).collect(Collectors.toList());
                    Map<Long,Long> idMap = PackageBeanUtil.getNewRecordIds(lookupModuleName,pickListString);
                    if(idMap!=null && !idMap.isEmpty()){
                        List<Map<String,String>> valueMap = new ArrayList<>();
                        for(int i = 0; i<pickListOld.size();i++){
                            Map<String,String> map = new HashMap<>();
                            map.put(String.valueOf(idMap.get(pickListOld.get(i))),allValues.get(pickListOld.get(i)));
                            valueMap.add(map);
                        }
                        userFilter.put("allValues",valueMap);
                    }
                }
            }
        return userFilter;
    }
    public static void replaceFiltersValue(JSONObject filter, List<Long> oldValues,String filterName) throws Exception{
        if(CollectionUtils.isNotEmpty(oldValues)){
            Map<Long,Long> valueMap = PackageBeanUtil.getNewRecordIds("resource",oldValues);
            if(valueMap!=null && !valueMap.isEmpty()){
                List<Long> newBuildingValue = oldValues.stream().map(id->valueMap.get(id)).collect(Collectors.toList());
                filter.put(filterName,newBuildingValue);
            }
        }
    }
    public static void constructFilterObject(JSONObject common, JSONObject filters,Map<String,String> assetCategoryNameVsId) throws Exception {
        JSONObject filterState = (JSONObject) filters.get("filterState");
        if(filterState!=null){
            if(filterState.containsKey("categoryFilter")){
                List<String> categoryFilter = (List<String>) filterState.get("categoryFilter");
                List<Long> newCategoryFilter = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(categoryFilter)){
                    for(String name : categoryFilter){
                        newCategoryFilter.add(Long.valueOf(assetCategoryNameVsId.get(name)));
                    }
                    filterState.put("categoryFilter",newCategoryFilter);
                    List<String> assetCategory = (List<String>) filters.get("assetCategory");
                    if(assetCategory!=null && !assetCategory.isEmpty()){
                        filters.put("assetCategory",newCategoryFilter);
                    }
                }
            }
            if(filterState.containsKey("assetFilter")){
                List<Long> assetValues = (List<Long>) filterState.get("assetFilter");
                replaceFiltersValue(filterState,assetValues,"assetFilter");
                if(filters.containsKey("parentId")){
                    filters.put("parentId",filterState.get("assetFilter"));
                }
            }
            if(filterState.containsKey("buildingFilter")){
                List<Long> buildingValue = (List<Long>) filterState.get("buildingFilter");
                replaceFiltersValue(filterState,buildingValue,"buildingFilter");
            }
            if(filterState.containsKey("siteFilter")){
                List<Long> siteValue = (List<Long>) filterState.get("siteFilter");
                replaceFiltersValue(filterState,siteValue,"siteFilter");
            }
            filters.put("filterState",filterState);
        }
        if(filters.containsKey("spaceId")){
            List<Long> spaceValue = (List<Long>) filters.get("spaceId");
            replaceFiltersValue(filters,spaceValue,"spaceId");
        }
        common.put("filters",filters);
    }
    public static Long resourcesIdReplacement(Long id) throws Exception{
        long resourceId = -1l;
        if(id!=null && id>0){
            List<Long> oldId = new ArrayList<>();
            oldId.add(id);
            Map<Long,Long> oldIdVsNewId =  PackageBeanUtil.getNewRecordIds("resource",oldId);
            if(oldIdVsNewId!=null && !oldIdVsNewId.isEmpty()){
                resourceId = oldIdVsNewId.get(id) !=null ?oldIdVsNewId.get(id) : -1l ;
            }
        }
        return resourceId;
    }
    public static JSONObject dataFilterConversion(JSONObject dataFilterJson) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject conditions = (JSONObject) dataFilterJson.get("conditions");
        if(conditions !=null && CollectionUtils.isNotEmpty(conditions.keySet())) {
            Set<String> keys = (Set<String>) conditions.keySet();
            for (String key : keys) {
                JSONObject condition = (JSONObject) conditions.get(key);
                String dataFilterModuleName = (String) condition.get("moduleName");
                String dataFilterFieldName = (String) condition.get("fieldName");
                FacilioField field = modBean.getField(dataFilterFieldName, dataFilterModuleName);
                Long parentId = condition.containsKey("parentId") ? (Long) condition.get("parentId") : -1l;
                Long newParentId = resourcesIdReplacement(parentId);
                condition.put("parentId", newParentId);
                condition.put("fieldId", field.getFieldId());
                conditions.put(key, condition);
            }
            dataFilterJson.put("conditions", conditions);
            return dataFilterJson;
        }
        return dataFilterJson;
    }

    public static String dataPointConstruction(XMLBuilder reportElement, int reportType,String chartState, ReportContext reportContext) throws Exception{
        JSONParser parser = new JSONParser();
        Map<String, String> assetCategoryNameVsId = PackageUtil.getNameVsRecordIdForPicklistModule("assetcategory");
        if(reportType == 2){
            String datePointJson = reportElement.getElement(PackageConstants.ReportsConstants.DATA_POINT_JSON).getCData();
            JSONObject chartStateJson = (JSONObject) parser.parse(chartState);
            List<ReportDataPointContext> newDataPointJson = dataPointConversion(datePointJson,chartStateJson);
            reportContext.setDataPoints(newDataPointJson);
        }
        else {
            XMLBuilder dataPoints = reportElement.getElement(PackageConstants.ReportsConstants.DATA_POINTS);
            if (dataPoints != null) {
                List<XMLBuilder> dataPoinList = dataPoints.getElementList(PackageConstants.ReportsConstants.DATA_POINT);
                List<ReportDataPointContext> dataPointsContextList = new ArrayList<>();
                for (XMLBuilder dataPoint : dataPoinList) {
                    String datapointString = dataPoint.getElement(PackageConstants.ReportsConstants.DATA_POINT_JSON).getCData();
                    JSONObject dataPointJson = (JSONObject) parser.parse(datapointString);
                    ReportDataPointContext dataPointContext = createDataPoint(dataPointJson, assetCategoryNameVsId, null);
                    XMLBuilder criteriaElement = dataPoint.getElement(PackageConstants.CriteriaConstants.CRITERIA);
                    if (criteriaElement != null) {
                        Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
                        dataPointContext.setCriteria(criteria);
                    }
                    XMLBuilder otherCriteriaElement = dataPoint.getElement(PackageConstants.ReportsConstants.OTHER_CRITERIA);
                    if (otherCriteriaElement != null) {
                        Criteria otherCriteria = PackageBeanUtil.constructCriteriaFromBuilder(otherCriteriaElement);
                        dataPointContext.setOtherCriteria(otherCriteria);
                    }
                    XMLBuilder parentCriteriaElement = dataPoint.getElement(PackageConstants.ReportsConstants.PARENT_CRITERIA_FILTER);
                    if (parentCriteriaElement != null) {
                        Criteria parentCriteria = PackageBeanUtil.constructCriteriaFromBuilder(parentCriteriaElement);
                        dataPointContext.setParentCriteriaFilter(parentCriteria);
                    }
                    dataPointsContextList.add(dataPointContext);
                }
                reportContext.setDataPoints(dataPointsContextList);
            }
        }
        return  reportContext.getDataPointJson();
    }
}
