package com.facilio.agentv2.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;

public class AgentActionV2 extends FacilioAction
{
    public boolean notNull(Object object) {
        return object != null;
    }

    boolean checkValue(Long value){
        return (value != null) && (value >  0);
    }

    public boolean containsValueCheck(String key, JSONObject jsonObject){
        if(notNull(key)&& notNull(jsonObject) && jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }

    public void ok(){
        setResponseCode(HttpURLConnection.HTTP_OK);
    }

    public void internalError(){
        setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }

    public void noContent(){
        setResponseCode(HttpURLConnection.HTTP_NO_CONTENT);
    }

    protected FacilioContext constructListContext(FacilioContext context) throws Exception {
        context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
        if (getPage() == 0) {
            setPage(1);
        }
        if (getPerPage() == -1) {
            setPerPage(50);
        }
        context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());

        if(getFilters() != null)
        {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, json);
            context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
        }

        if (isFetchCount()) {
            context.put(FacilioConstants.ContextNames.FETCH_COUNT, isFetchCount());
        }

        JSONObject sorting = null;
        if (getOrderBy() != null) {
            sorting = new JSONObject();
            sorting.put("orderBy", getOrderBy());
            sorting.put("orderType", getOrderType());
        }
        context.put(FacilioConstants.ContextNames.SORTING, sorting);

        return context;
    }

}
