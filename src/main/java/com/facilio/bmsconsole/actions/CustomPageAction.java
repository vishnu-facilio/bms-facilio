package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

import java.util.logging.Logger;

public class CustomPageAction {
    private static Logger LOGGER = Logger.getLogger(CustomPageAction.class.getName());
    private long appId = -1;
    private String moduleName;
    private String widgetName;
    private JSONObject result;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

    public JSONObject getResult() {
        return result;
    }

    @SuppressWarnings("unchecked")
    public void setResult(String key, Object result) {
        if (this.result == null) {
            this.result = new JSONObject();
        }
        this.result.put(key, result);
    }

    public String getCustomPageWidget() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.WIDGET_NAME, widgetName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.APP_ID, appId);

        FacilioChain getPageWidgetChain = TransactionChainFactory.getPageWidgetChain();
        getPageWidgetChain.setContext(context);
        getPageWidgetChain.execute();

        setResult("summaryFieldWidget", context.get(FacilioConstants.ContextNames.CUSTOM_PAGE_WIDGET));

        return "success";
    }
}