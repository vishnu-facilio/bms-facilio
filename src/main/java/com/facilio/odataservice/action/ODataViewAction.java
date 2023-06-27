package com.facilio.odataservice.action;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.Map;

import static com.opensymphony.xwork2.Action.SUCCESS;

@Getter
@Setter
public  class ODataViewAction {
    private static JSONObject result;

    public static String getModuleName() {
        return moduleName;
    }

    public static void setModuleName(String moduleName) {
        ODataViewAction.moduleName = moduleName;
    }

    private static FacilioView viewObj;
    private static String moduleName;
    private static String viewName;
    private static Map<String, Object> view;


    public static Map<String, Object> getView() {
        return ODataViewAction.view;
    }

    public static void setView(Map<String, Object> view) {
        ODataViewAction.view = view;
    }

    public static String v2getViewDetail(String moduleName, String viewName) throws Exception
    {
        setViewName(viewName);
        setModuleName(moduleName);
        getViewDetail();
        setResult("viewDetail", viewObj);
        return SUCCESS;
    }

    public static void setResult(String key, Object result) {
        if (ODataViewAction.result == null) {
            ODataViewAction.result = new JSONObject();
        }
        ODataViewAction.result.put(key, result);
    }

    public static JSONObject getResult() {
        return result;
    }

    public static void setResult(JSONObject result) {
        ODataViewAction.result = result;
    }

    public static FacilioView getViewObj() {
        return viewObj;
    }

    public static void setViewObj(FacilioView viewObj) {
        ODataViewAction.viewObj = viewObj;
    }

    public static String getViewName() {
        return viewName;
    }

    public static void setViewName(String viewName) {
        ODataViewAction.viewName = viewName;
    }

    public static String getViewDetail() throws Exception
    {
        String moduleName = getModuleName();
        if (moduleName == null || moduleName.equals("approval")) {
            if (moduleName.equals("approval")) {
                viewName = "approval_" + viewName;
            }
            moduleName = "workorder";
        }
        FacilioChain getViewChain = FacilioChainFactory.getViewDetailsChain();
        FacilioContext context = getViewChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
        context.put(FacilioConstants.ContextNames.APP_ID, -1l);
        context.put(FacilioConstants.ContextNames.PARENT_VIEW, null);
        context.put(FacilioConstants.ContextNames.FETCH_FIELD_DISPLAY_NAMES, true);
        context.put(FacilioConstants.ContextNames.IS_FETCH_CALL, true);
        getViewChain.execute();
        viewObj = (FacilioView)context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
        setView(FieldUtil.getAsProperties(viewObj));
        return SUCCESS;
    }
}
