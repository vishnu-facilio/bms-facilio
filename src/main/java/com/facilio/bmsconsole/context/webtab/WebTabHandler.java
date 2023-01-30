package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.Map;

public interface WebTabHandler {
    public boolean hasPermission(WebTabContext webtab, Map<String,String> parameters, String action);

    public boolean hasPermission(long tabId, Map<String,String> parameters, String action) throws Exception;

}
