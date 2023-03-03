package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.calendarview.CalendarViewRequestContext;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.timeline.context.CustomizationDataContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarViewAction extends RESTAPIHandler {

    public String calendarList() throws Exception {
        FacilioContext listContext = calendarViewListChain(this.getModuleName(), this.getViewName(), true, this.getFilters(), this.getExcludeParentFilter(),
                this.getClientCriteria(), this.getPage(), this.getPerPage(), this.getQueryParameters(), null, this.getCalendarViewRequest());

        List<CustomizationDataContext> customizationDataContexts = (List<CustomizationDataContext>) listContext.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_CUSTOMIZATON_DATAMAP);

        JSONObject recordJSON = new JSONObject();
        recordJSON.put(this.getModuleName(), customizationDataContexts);
        this.setData(recordJSON);

        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            this.setMeta((JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }
        return SUCCESS;
    }

    public String calendarData() throws Exception {
        FacilioContext listContext = calendarViewListChain(this.getModuleName(), this.getViewName(), false, this.getFilters(), this.getExcludeParentFilter(),
                this.getClientCriteria(), this.getPage(), this.getPerPage(), this.getQueryParameters(), null, this.getCalendarViewRequest());

        JSONObject recordJSON = Constants.getJsonRecordMap(listContext);

        this.setData(recordJSON);
        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            this.setMeta((JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }
        return SUCCESS;
    }

    public static FacilioContext calendarViewListChain(String moduleName, String viewName, boolean getSingleCellData, String filters, boolean excludeParentFilter, String clientCriteria,
                                                       int page, int perPage, Map<String, List<Object>> queryParameters, Criteria serverCriteria, CalendarViewRequestContext calendarViewRequest) throws Exception {
        FacilioChain listChain = ChainUtil.getCalendarViewChain(moduleName, getSingleCellData);
        FacilioContext context = listChain.getContext();

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        Constants.setV3config(context, v3Config);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);

        context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(Constants.FILTERS, json);

            context.put(Constants.EXCLUDE_PARENT_CRITERIA, excludeParentFilter);
        }

        if(StringUtils.isNotEmpty(clientCriteria)){
            JSONObject json = FacilioUtil.parseJson(clientCriteria);
            Criteria newCriteria = FieldUtil.getAsBeanFromJson(json, Criteria.class);
            context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, newCriteria);
        }

        if(getSingleCellData) {
            JSONObject pagination = new JSONObject();
            pagination.put("page", page);
            pagination.put("perPage", perPage);
            context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        }

        context.put(Constants.QUERY_PARAMS, queryParameters);

        if(serverCriteria != null) {
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, serverCriteria);
        }

        context.put(FacilioConstants.ViewConstants.CALENDAR_VIEW_REQUEST, calendarViewRequest);
        context.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);

        listChain.execute();

        Object supplementMap = Constants.getSupplementMap(context);
        Map<String, Object> meta = new HashMap<>();
        if (supplementMap != null) {
            meta.put("supplements", supplementMap);
        }
        if (MapUtils.isNotEmpty(meta)) {
            context.put(FacilioConstants.ContextNames.META, FieldUtil.getAsJSON(meta));
        }

        return context;
    }

    private CalendarViewRequestContext calendarViewRequest;

    public CalendarViewRequestContext getCalendarViewRequest() {
        return calendarViewRequest;
    }

    public void setCalendarViewRequest(CalendarViewRequestContext calendarViewRequest) {
        this.calendarViewRequest = calendarViewRequest;
    }
}
