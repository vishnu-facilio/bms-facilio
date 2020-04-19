package com.facilio.agentv2;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.json.simple.JSONObject;

import java.util.Map;

public class AgentUtilities
{
    public static boolean notNull(Object object) {
        return object != null;
    }

    public static boolean checkValue(Long value){
        return (value != null) && (value >  0);
    }

    public static boolean containsValueCheck(String key, Map<String,Object> map){
        if(notNull(key) && notNull(map) && map.containsKey(key) && ( map.get(key) != null) ){
            return true;
        }
        return false;
    }

    public static int getlimit(FacilioContext context){
        if (containsValueCheck(FacilioConstants.ContextNames.PAGINATION,context)) {
            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            if (pagination != null ) {
                if(pagination.containsKey("perPage")){
                    int perPage = (int) pagination.get("perPage");
                    return perPage;
                }
            }
        }
        return 50;
    }

    public static int getOffset(FacilioContext context){
        if (containsValueCheck(FacilioConstants.ContextNames.PAGINATION,context)) {
            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            if (pagination != null ) {
                if(pagination.containsKey("page") && pagination.containsKey("perPage")){
                    int page = (int) pagination.get("page");
                    int perPage = (int) pagination.get("perPage");
                    int offset = ((page-1) * perPage);
                    if (offset < 0) {
                        offset = 0;
                    }
                    return offset;
                }
            }
        }
        return 0;
    }
}
