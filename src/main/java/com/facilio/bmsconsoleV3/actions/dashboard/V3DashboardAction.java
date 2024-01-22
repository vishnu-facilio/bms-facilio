package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.actions.DashboardExecuteMetaContext;
import com.facilio.bmsconsoleV3.commands.GetDashboardThumbnailCommand;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardListPropsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.ReportContext;
import com.facilio.report.module.v2.chain.V2TransactionChainFactory;
import com.facilio.report.util.ReportUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.opensymphony.xwork2.ActionContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Setter @Getter
@Log4j
public class V3DashboardAction extends V3Action {

    private Long buildingId;
    private Long dashboardId;
    private String dashboardName;
    private JSONObject dashboard_meta;
    private String linkName;
    private boolean withDefaultFilters;
    private Long dashboardTabId;

    /**
        variables for recieving props for dashboard list api starts here
     */
    private Long appId;
    private boolean withSharing;
    private boolean withFilters;
    private boolean withEmptyFolders;
    private boolean onlyPublished;
    private boolean onlyMobile;
    private boolean onlySelected;
    private boolean onlyFolders;
    private boolean withTabs;
    private boolean newFlow;
    private Long widgetId;
    /**
     variables for recieving props for dashboard list api ends here
     */
    public String clone_dashboard() throws Exception
    {
        try
        {
            JSONObject cloned_json = this.getData();
            if(cloned_json != null)
            {
                if(cloned_json.get("cloned_dashboard_name") == null || "".equals(cloned_json.get("cloned_dashboard_name"))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Name can not be empty");
                }
                if(cloned_json.get("dashboard_link_name") == null || cloned_json.get("dashboard_link_name").equals("")){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Link Name can not be empty");
                }
                FacilioChain chain = TransactionChainFactoryV3.getNewCloneDashboardChain();
                FacilioContext context = chain.getContext();
                updateContextToClone(context, cloned_json);
                chain.execute();
                DashboardContext dashboard = (DashboardContext)  context.get("cloned_dashbaord");
                if(dashboard != null)
                {
                    setData("dashboard_link_name", dashboard.getLinkName());
                    setData("dashboard_name", dashboard.getDashboardName());
                    setData("dashboard_id", dashboard.getId());
                    setData("target_app_id" ,dashboard.getTarget_app_id());
                    setData("cloned_app_id" ,dashboard.getCloned_app_id());
                }
            }
        }
        catch (RESTException e)
        {
           throw e;
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while cloning dashboard");
        }
        return V3Action.SUCCESS;
    }
    public String clone_tab() throws Exception
    {
        try{
            JSONObject cloned_json = this.getData();
            if(cloned_json != null)
            {
                if(cloned_json.get("cloned_dashboard_name") == null || "".equals(cloned_json.get("cloned_dashboard_name"))){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Name can not be empty");
                }
                if(cloned_json.get("tab_id") == null || (Long) cloned_json.get("tab_id") < 0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tab id can not be empty");
                }
                FacilioChain chain = TransactionChainFactoryV3.getCloneDashboardTabChain();
                FacilioContext context = chain.getContext();
                updateTabContextToClone(context, cloned_json);
                chain.execute();
                DashboardContext dashboard = (DashboardContext)  context.get("cloned_dashboard");
                if(dashboard != null)
                {
                    setData("link_name", dashboard.getLinkName());
                    setData("dashboard_name", dashboard.getDashboardName());
                    setData("dashboard_id", dashboard.getId());
                }
                DashboardTabContext dashboardTab = (DashboardTabContext) context.get("cloned_tab");
                if(dashboardTab != null)
                {
                    setData("dashboard_id", dashboardTab.getDashboardId());
                    setData("tab_name", dashboardTab.getName());
                    setData("tab_id", dashboardTab.getId());
                }
            }
        }
        catch (RESTException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while cloning tab");
        }
        return V3Action.SUCCESS;
    }
    private void updateContextToClone(Context context, JSONObject cloned_json) throws Exception{
        context.put("dashboard_link_name", cloned_json.get("dashboard_link_name"));
        context.put("cloned_dashboard_name", cloned_json.get("cloned_dashboard_name"));
        context.put("target_app_id", cloned_json.get("target_app_id"));
        context.put("cloned_app_id", cloned_json.get("cloned_app_id"));
        if(cloned_json.get("folder_id") != null && (Long) cloned_json.get("folder_id") >0 ){
            context.put("folder_id", (Long) cloned_json.get("folder_id"));
        }
        context.put("isFilterCloneNeeded",Boolean.TRUE);
    }
    private void updateTabContextToClone(Context context, JSONObject cloned_json) throws Exception{
        context.put("tab_id", cloned_json.get("tab_id"));
        context.put("cloned_dashboard_name", cloned_json.get("cloned_dashboard_name"));
        context.put("target_app_id", cloned_json.get("target_app_id"));
        context.put("cloned_app_id", cloned_json.get("cloned_app_id"));
        if(cloned_json.get("folder_id") != null && (Long) cloned_json.get("folder_id") >0 ){
            context.put("folder_id", (Long) cloned_json.get("folder_id"));
        }
        if(cloned_json.get("dashboard_id") != null && (Long)cloned_json.get("dashboard_id") > 0){
            context.put("dashboard_id", (Long)cloned_json.get("dashboard_id"));
        }
        context.put("clone_dashboard",cloned_json.get("clone_dashboard") != null ? cloned_json.get("clone_dashboard") : false);
        context.put("isFilterCloneNeeded",Boolean.TRUE);
    }

    public String move_dashboard()throws Exception
    {
        try
        {
            JSONObject move_json = this.getData();
            if(move_json != null)
            {
                if(move_json.get("folder_id") == null || (Long) move_json.get("folder_id") <=0 ){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Folder id can not be empty");
                }
                if(move_json.get("dashboard_link") == null || move_json.get("dashboard_link").equals("")){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Link Name can not be empty");
                }
                FacilioChain chain = TransactionChainFactoryV3.getMoveToDashboardChain();
                FacilioContext context = chain.getContext();
                context.put("folder_id", move_json.get("folder_id"));
                context.put("dashboard_link", move_json.get("dashboard_link"));
                chain.execute();
                if(context.get("isMoved") != null && (Boolean)context.get("isMoved"))
                {
                    setData("result", V3Action.SUCCESS);
                }
            }
        }
        catch (RESTException e)
        {
            throw e;
        }
        catch (Exception e){
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while moving dashboard in to another app"+e);
        }
        return V3Action.SUCCESS;
    }

    public String update()throws Exception
    {
        JSONObject data = this.getDashboard_meta();
        if(data  == null ){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard data can not be empty");
        }
        if(data.containsKey("id") && data != null && data.get("id") != null && (Long)data.get("id") <= 0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard id can not be empty");
        }
        try
        {
            FacilioChain dashboard_chain = TransactionChainFactoryV3.getDashboardDataChain();
            FacilioContext dashboard_context = dashboard_chain.getContext();
            dashboard_context.put("id", (Long)data.get("id"));
            dashboard_chain.execute();
            if(dashboard_context.get("dashboard") != null)
            {
                DashboardContext dashboard = (DashboardContext) dashboard_context.get("dashboard");
                V3DashboardAPIHandler.updateDashboardData(dashboard, data);
                FacilioChain updateDashboardChain = TransactionChainFactoryV3.getUpdateDashboardChainV3(true);
                FacilioContext context = updateDashboardChain.getContext();
                context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
                context.put(FacilioConstants.ContextNames.IS_FROM_REPORT, dashboard_meta.containsKey("isFromReport") ? (Boolean) dashboard_meta.get("isFromReport"): Boolean.FALSE);
                context.put(FacilioConstants.ContextNames.BUILDING_ID, buildingId);
                context.put("dashboardUrl",data.get("dashboardUrl"));
                updateDashboardChain.execute();
                setData("dashboard", DashboardUtil.getDashboardResponseJson(dashboard, false));
            }
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while updating dashboard" + e);
        }
        return SUCCESS;
    }

    public String get() throws Exception
    {
        if(linkName  == null || "".equals(linkName)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Name can not be empty");
        }
        try {
            DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(linkName, null);
            FacilioChain getDashboardFilterChain = ReadOnlyChainFactory.getFetchDashboardFilterAndWidgetFilterMappingChain();
            FacilioContext getDashboardFilterContext = getDashboardFilterChain.getContext();
            getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
            getDashboardFilterChain.execute();

          dashboard.setDashboardWidgets(DashboardUtil.getDashboardWidgetsWithCustomActions(dashboard.getDashboardWidgets()));

            List<DashboardWidgetContext> widget_list = DashboardUtil.getDashboardWidgetsWithSection(dashboard.getDashboardWidgets());
            if (widget_list != null) {
                dashboard.setDashboardWidgets(widget_list);
            }
            if(withDefaultFilters) {
                DashboardExecuteMetaContext executeContext = new DashboardExecuteMetaContext();
                FacilioChain chain = TransactionChainFactoryV3.getDashboardRuleExecuteChain();
                FacilioContext context = chain.getContext();
                executeContext = DashboardUtil.getDashboardExecuteMeta(dashboard, null);
                context.put("action_meta", executeContext);
                chain.execute();
                if(context.containsKey("result")) {
                    setData("result", context.get("result"));
                }
            }
            setData("dashboardJson", DashboardUtil.getDashboardResponseJson(dashboard, false));
        }catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard info"+e);
        }
        return SUCCESS;
    }
    public String getDashboardTabsList() throws Exception
    {
        if(linkName  == null || "".equals(linkName)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Name can not be empty");
        }
        try {
            DashboardContext dashboard = DashboardUtil.getDashboard(linkName);
            List<Map<String,Object>> list = new ArrayList<>();
            if(dashboard != null) {
                if (dashboard.isTabEnabled()) {
                    FacilioChain chain = TransactionChainFactoryV3.getDashboardTabListChain();
                    FacilioContext context = chain.getContext();
                    context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
                    chain.execute();
                    list = (List<Map<String, Object>>) context.get("tabsList");
                }
            }
           setData("DashboardTabs", list);
        }catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard tabs info"+e);
        }
        return SUCCESS;
    }

    public String updateTab()throws Exception
    {
        JSONObject data = this.getDashboard_meta();
        if(data == null ){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard tab data can not be empty");
        }
        try {
            DashboardContext dashboard = null;
            if (data.containsKey("dashboardId"))
            {
                Long dashboardId = (Long) data.get("dashboardId");
                if (dashboardId != null && dashboardId > 0) {
                    FacilioChain chain = TransactionChainFactoryV3.getDashboardDataChain();
                    FacilioContext context = chain.getContext();
                    context.put("id", dashboardId);
                    chain.execute();
                    if (context.containsKey("dashboard")) {
                        dashboard = (DashboardContext) context.get("dashboard");
                        if (dashboard != null) {
                            V3DashboardAPIHandler.updateDashboardProp(dashboard, data);
                        }
                    }
                }
            }
            if (data.containsKey("multi_tab_update"))
            {
                List dashboardTabs = (List) data.get("tabs");
                if (dashboardTabs != null && dashboardTabs.size() > 0)
                {
                    for (int i = 0; i < dashboardTabs.size(); i++) {
                        Map tab = (Map) dashboardTabs.get(i);
                        Long dashboardTabId = (Long) tab.get("tabId");
                        FacilioChain chain = TransactionChainFactoryV3.getDashboardDataChain();
                        FacilioContext context = chain.getContext();
                        context.put("tabId", dashboardTabId);
                        chain.execute();
                        if (context.containsKey("dashboardTabContext")) {
                            DashboardTabContext dashboardTabContext = (DashboardTabContext) context.get("dashboardTabContext");
                            if (dashboardTabContext != null) {
                                List dashboardWidgets = (List) tab.get("dashboardWidgets");
                                List<DashboardWidgetContext> widgets = V3DashboardAPIHandler.getDashboardSectionWidgetFromWidgetMeta(dashboardWidgets);
                                dashboardTabContext.setDashboardWidgets(widgets);
                                dashboardTabContext.setName((String) tab.get("dashboardTabName"));
                                Boolean isFromReport = dashboard_meta.containsKey("isFromReport") ? (Boolean) dashboard_meta.get("isFromReport"): Boolean.FALSE;
                                V3DashboardAPIHandler.updateDashboardTabAPI(dashboard, dashboardTabContext, isFromReport);
                            }
                        }
                    }
                }
            }
            else {
                if (data.containsKey("tabId") && data != null && data.get("tabId") != null && (Long) data.get("tabId") <= 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard tab id can not be empty");
                }
                Long dashboardTabId = (Long) data.get("tabId");
                if (dashboardTabId != null && dashboardTabId > 0) {
                    FacilioChain chain = TransactionChainFactoryV3.getDashboardDataChain();
                    FacilioContext context = chain.getContext();
                    context.put("tabId", dashboardTabId);
                    chain.execute();
                    if (context.containsKey("dashboardTabContext")) {
                        DashboardTabContext dashboardTabContext = (DashboardTabContext) context.get("dashboardTabContext");
                        if (dashboardTabContext != null) {
                            V3DashboardAPIHandler.updateDashboardTabProp(dashboardTabContext, data);
                            Boolean isFromReport = dashboard_meta.containsKey("isFromReport") ? (Boolean) dashboard_meta.get("isFromReport"): Boolean.FALSE;
                            V3DashboardAPIHandler.updateDashboardTabAPI(dashboard, dashboardTabContext, isFromReport);
                        }
                    }
                }
            }
            GetDashboardThumbnailCommand thumbnailCommand = new GetDashboardThumbnailCommand();
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.DASHBOARD,DashboardUtil.getDashboard((Long) data.get("dashboardId")));
            thumbnailCommand.executeCommand(context);
            setData("result", "success");
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while updating dashboard tab info"+e);
        }
        return SUCCESS;
    }


    public String dashboardTabData()throws Exception
    {
        if(dashboardTabId == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard tab id can not be empty");
        }
        try
        {
            FacilioChain chain = TransactionChainFactoryV3.getDashboardDataChain();
            FacilioContext context = chain.getContext();
            context.put("tabId", dashboardTabId);
            chain.execute();
            DashboardTabContext dashboardTabContext = null;
            if(context.containsKey("dashboardTabContext")){
                dashboardTabContext = (DashboardTabContext) context.get("dashboardTabContext");
            }
            if(dashboardTabContext != null)
            {
                FacilioChain getDashboardFilterChain=ReadOnlyChainFactory.getFetchDashboardFilterAndWidgetFilterMappingChain();
                FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
                getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
                getDashboardFilterChain.execute();

                List<DashboardWidgetContext> widgets = dashboardTabContext.getDashboardWidgets();
                dashboardTabContext.setDashboardWidgets(DashboardUtil.getDashboardWidgetsWithCustomActions(widgets));
                List<DashboardWidgetContext> widgets_with_section = DashboardUtil.getDashboardWidgetsWithSection(widgets);
                if(widgets_with_section != null && widgets_with_section.size() > 0) {
                    dashboardTabContext.setDashboardWidgets(widgets_with_section);
                }
                DashboardUtil.getDashboardTabResponseJson(Collections.singletonList(dashboardTabContext));

                if(withDefaultFilters) {
                    DashboardExecuteMetaContext executeContext = new DashboardExecuteMetaContext();
                    FacilioChain filterChain = TransactionChainFactoryV3.getDashboardRuleExecuteChain();
                    FacilioContext filterContext = filterChain.getContext();
                    executeContext = DashboardUtil.getDashboardExecuteMeta(null, dashboardTabContext);
                    filterContext.put("action_meta", executeContext);
                    filterChain.execute();
                    if(filterContext.containsKey("result")) {
                        setData("result", filterContext.get("result"));
                    }
                }
                setData("dashboardTabContext", dashboardTabContext);
            }
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard tab data"+e);
        }
        return SUCCESS;
    }

    public String list()throws Exception
    {
        DashboardListPropsContext dashboard_list_prop = new DashboardListPropsContext(appId, withSharing, withFilters, withEmptyFolders, onlyPublished, onlyMobile, onlySelected, onlyFolders, withTabs, newFlow);
        if(dashboard_list_prop == null || dashboard_list_prop.getAppId() == null || dashboard_list_prop.getAppId() <= 0)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please pass at least appId in api");
        }
        try
        {
            FacilioChain chain = TransactionChainFactoryV3.getDashboardListChain();
            FacilioContext context = chain.getContext();
            context.put("dashboard_list_prop", dashboard_list_prop);
            chain.execute();
            setData("dashboardFolders", dashboard_list_prop.getFolders());
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard list" +e);
        }
        return V3Action.SUCCESS;
    }
    public String rename() throws Exception
    {
        DashboardContext dashboard = DashboardUtil.getDashboard(dashboardId);
        GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getDashboardModule().getTableName())
                .fields(FieldFactory.getDashboardFields())
                .andCondition(CriteriaAPI.getCondition("id","ID",String.valueOf(dashboardId), NumberOperators.EQUALS));
        dashboard.setDashboardName(dashboardName);
        Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
        update.update(props);
        return SUCCESS;
    }
    public String mobileList() throws Exception
    {
        DashboardListPropsContext dashboard_list_prop = new DashboardListPropsContext(appId, withSharing, withFilters, withEmptyFolders, onlyPublished, onlyMobile, onlySelected, onlyFolders, withTabs, newFlow);
        if(dashboard_list_prop == null || dashboard_list_prop.getAppId() == null || dashboard_list_prop.getAppId() <= 0)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please pass at least appId in api");
        }
        try
        {
            FacilioChain chain = TransactionChainFactoryV3.getMobileDashboardListChain();
            FacilioContext context = chain.getContext();
            context.put("dashboard_list_prop", dashboard_list_prop);
            chain.execute();
            setData("dashboardFolders", dashboard_list_prop.getFolders());
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard list" +e);
        }
        return V3Action.SUCCESS;
    }
    public String getMobileDashboard() throws Exception
    {
        if(linkName  == null || "".equals(linkName)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Name can not be empty");
        }
        try {
            DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(linkName, null);
            FacilioChain getDashboardFilterChain = ReadOnlyChainFactory.getFetchDashboardFilterAndWidgetFilterMappingChain();
            FacilioContext getDashboardFilterContext = getDashboardFilterChain.getContext();
            getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
            getDashboardFilterChain.execute();
            List<DashboardWidgetContext> widget_list = DashboardUtil.getDashboardWidgetsWithSection(dashboard.getDashboardWidgets());
            if(widget_list != null && widget_list.size() > 0){
                DashboardUtil.updateMobileWidgets(widget_list);
            }
            if (widget_list != null) {
                dashboard.setDashboardWidgets(widget_list);
            }
            setData("dashboard", DashboardUtil.getMobileDashboardResponseJson(dashboard, false));
        }catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard info"+e);
        }
        return SUCCESS;
    }
    public String getMobileTab() throws Exception
    {
        if(dashboardTabId == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard tab id can not be empty");
        }
        try {
            FacilioChain chain = TransactionChainFactoryV3.getDashboardDataChain();
            FacilioContext context = chain.getContext();
            context.put("tabId", dashboardTabId);
            chain.execute();
            DashboardTabContext dashboardTabContext = null;
            if(context.containsKey("dashboardTabContext")){
                dashboardTabContext = (DashboardTabContext) context.get("dashboardTabContext");
            }
            if(dashboardTabContext != null)
            {
                FacilioChain getDashboardFilterChain=ReadOnlyChainFactory.getFetchDashboardFilterAndWidgetFilterMappingChain();
                FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
                getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
                getDashboardFilterChain.execute();

                List<DashboardWidgetContext> widgets = dashboardTabContext.getDashboardWidgets();
                dashboardTabContext.setDashboardWidgets(DashboardUtil.getDashboardWidgetsWithCustomActions(widgets));
                List<DashboardWidgetContext> widgets_with_section = DashboardUtil.getDashboardWidgetsWithSection(widgets);
                if(widgets_with_section != null && widgets_with_section.size() > 0) {
                    DashboardUtil.updateMobileWidgets(widgets_with_section);
                }
                if (widgets_with_section != null) {
                    dashboardTabContext.setDashboardWidgets(widgets_with_section);
                }
                setData("dashboardTab", DashboardUtil.getMobileDashboardTabResponseJson(dashboardTabContext, false));
            }
        }catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard info"+e);
        }
        return SUCCESS;
    }
    public String getWebViewWidget() throws Exception {
        if(widgetId != null && widgetId < 0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "WidgetId can not be empty");
        }
        try {
            DashboardWidgetContext widgetContext = DashboardUtil.getWidget(widgetId);
            switch (widgetContext.getWidgetType().getName()) {
                case "card":
                    WidgetCardContext widgetCardContext = (WidgetCardContext) widgetContext;
                    setData("widgetType",widgetContext.getWidgetType());
                    setData("cardType",widgetCardContext.getCardLayout());
                    setData("widgetData",widgetCardContext);
                    break;
                case "chart":
                    WidgetChartContext widgetChartContext = (WidgetChartContext) widgetContext;
                    int reportType = ReportUtil.getReportType(widgetChartContext.getNewReportId());
                    if(ReportContext.ReportType.READING_REPORT.getValue() == reportType){
                        FacilioChain chain = V2AnalyticsTransactionChain.getReportWithDataChain(true);
                        FacilioContext context = chain.getContext();
                        context.put("reportId", widgetChartContext.getNewReportId());
                        chain.execute();
                        setData("widgetType",widgetContext.getWidgetType());
                        setData("reportType",reportType);
                        setReportResult(context);
                    }
                    else{
                        FacilioChain chain = V2TransactionChainFactory.getReportDataChain(null);
                        FacilioContext context = chain.getContext();
                        context.put("reportId", widgetChartContext.getNewReportId());
                        chain.execute();
                        setData("widgetType",widgetContext.getWidgetType());
                        setData("reportType",reportType);
                        setData("report", context.get(FacilioConstants.ContextNames.REPORT));
                        setData("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
                        if(context.get("v2_report") != null){
                            setData("v2_report", context.get("v2_report"));
                        }
                    }
                    break;
                default:
                    break;
            }
        }catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting widget info"+e);
        }
        return SUCCESS;
    }
    private String setReportResult(FacilioContext context)
    {
        if (context.get(FacilioConstants.ContextNames.REPORT) != null) {

            ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
            setData("report", reportContext);
        }
        if(context.get("v2_report") != null){
            setData("v2_report", context.get("v2_report"));
        }
        setData("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES)); // This can be removed
        // from new format
        if(context.get(FacilioConstants.ContextNames.REPORT_DATA) != null)
        {
            JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
            if(data != null && data.containsKey("heatMapData")){
                data.put("data", data.get("heatMapData"));
                data.remove("heatMapData");
            }
        }
        setData("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
        setData("safeLimits", context.get(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT));

        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        if (module != null) {
            setData("module", module);
        }
        if (context.containsKey("criteriaData")) {
            setData("criteriaData", context.get("criteriaData"));
        }

        if (context.containsKey("baselineData")) {
            setData("baselineData", context.get("baselineData"));
        }
        if (context.containsKey("baselineDataColors")) {
            setData("baselineDataColors", context.get("baselineDataColors"));
        }
        return SUCCESS;
    }
    public String roleList() throws Exception {
        List<Long> appIds = new ArrayList<>();
        if(appId > 0){
            appIds.add(appId);
        }
        List<Role> rolesList = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRolesForApps(appIds);
        setData("roles",rolesList);
        return V3Action.SUCCESS;
    }
    public String getFilterModules() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getDashboardFilterModules();
        FacilioContext context = chain.getContext();
        chain.execute();
        setData("moduleList", context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return V3Action.SUCCESS;
    }
}
