package com.facilio.bmsconsole.context.webtab;

import com.facilio.bmsconsole.context.WebTabContext;

import java.util.Map;

public class DefaultTypeHandler implements WebTabHandler{
    @Override
    public boolean hasPermission(WebTabContext webtab, Map<String, String> parameters, String action) {
        return true;
    }

    @Override
    public boolean hasPermission(long tabId, Map<String, String> parameters, String action) throws Exception {
        return true;
    }
}
