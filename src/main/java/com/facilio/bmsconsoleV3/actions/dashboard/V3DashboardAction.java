package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

@Setter @Getter
@Log4j
public class V3DashboardAction extends V3Action {

    private Long buildingId;
    private JSONObject dashboard_meta;
    private String linkName;
    private Long dashboardTabId;
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
                FacilioChain chain = TransactionChainFactoryV3.getCloneDashboardChain();
                FacilioContext context = chain.getContext();
                updateContextToClone(context, cloned_json);
                chain.execute();
                DashboardContext dashboard = (DashboardContext)  context.get("cloned_dashbaord");
                if(dashboard != null)
                {
                    setData("dashboard_link_name", dashboard.getLinkName());
                    setData("dashboard_name", dashboard.getDashboardName());
                    setData("dashboard_id", dashboard.getId());
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

    private void updateContextToClone(Context context, JSONObject cloned_json) throws Exception{
        context.put("dashboard_link_name", cloned_json.get("dashboard_link_name"));
        context.put("cloned_dashboard_name", cloned_json.get("cloned_dashboard_name"));
        if(cloned_json.get("folder_id") != null && (Long) cloned_json.get("folder_id") >0 ){
            context.put("folder_id", (Long) cloned_json.get("folder_id"));
        }
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
                FacilioChain updateDashboardChain = TransactionChainFactoryV3.getUpdateDashboardChainV3();
                FacilioContext context = updateDashboardChain.getContext();
                context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
                context.put(FacilioConstants.ContextNames.BUILDING_ID, buildingId);
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

            List<DashboardWidgetContext> widget_list = DashboardUtil.getDashboardWidgetsWithSection(dashboard.getDashboardWidgets());
            if (widget_list != null) {
                dashboard.setDashboardWidgets(widget_list);
            }
            setData("dashboardJson", DashboardUtil.getDashboardResponseJson(dashboard, false));
        }catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard info"+e);
        }
        return SUCCESS;
    }

    public String updateTab()throws Exception
    {
        JSONObject data = this.getDashboard_meta();
        if(data  == null ){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard data can not be empty");
        }
        if(data.containsKey("id") && data != null && data.get("id") != null && (Long)data.get("id") <= 0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard id can not be empty");
        }
        if(data.containsKey("tabId") && data != null && data.get("tabId") != null && (Long)data.get("tabId") <= 0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard tab id can not be empty");
        }
        try
        {
            Long dashboardTabId = (Long) data.get("tabId");
            Long dashboardId = (Long) data.get("dashboardId");
            FacilioChain chain = TransactionChainFactoryV3.getDashboardDataChain();
            FacilioContext context = chain.getContext();
            context.put("id", dashboardId);
            context.put("tabId", dashboardTabId);
            chain.execute();
            DashboardContext dashboard=null;
            DashboardTabContext dashboardTabContext=null;
            if(context.containsKey("dashboard")){
                dashboard = (DashboardContext) context.get("dashboard");
                if(dashboard != null){
                    V3DashboardAPIHandler.updateDashboardProp(dashboard, data);
                }
            }
            if(context.containsKey("dashboardTabContext")){
                dashboardTabContext = (DashboardTabContext) context.get("dashboardTabContext");
                if(dashboardTabContext != null) {
                    V3DashboardAPIHandler.updateDashboardTabProp(dashboardTabContext, data);
                }
            }

            if (data.containsKey("fromType")) {
                context.put("fromType", data.get("fromType"));
            }
            if(dashboardTabContext != null) {
                FacilioChain updateDashboardChain = TransactionChainFactoryV3.getUpdateDashboardTabChainV3();
                FacilioContext updateTabContext = updateDashboardChain.getContext();
                updateTabContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
                updateTabContext.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
                updateDashboardChain.execute();
            }
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
                List<DashboardWidgetContext> widgets_with_section = DashboardUtil.getDashboardWidgetsWithSection(widgets);
                if(widgets_with_section != null && widgets_with_section.size() > 0) {
                    dashboardTabContext.setDashboardWidgets(widgets_with_section);
                }
                DashboardUtil.getDashboardTabResponseJson(Collections.singletonList(dashboardTabContext));
                setData("dashboardTabContext", dashboardTabContext);
            }
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard tab data"+e);
        }
        return SUCCESS;
    }
}
