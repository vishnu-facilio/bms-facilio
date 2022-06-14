package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

@Setter @Getter
@Log4j
public class V3DashboardAction extends V3Action {

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
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while moving dashboard in to another app");
        }
        return V3Action.SUCCESS;
    }
}
