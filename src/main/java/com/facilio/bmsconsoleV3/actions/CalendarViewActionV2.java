package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.calendarview.CalendarViewRequestContext;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.timeline.context.CustomizationDataContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class CalendarViewActionV2 extends RESTAPIHandler {
    private String calendarViewRequest;

    public String calendarList() throws Exception {
        FacilioContext listContext = calendarViewListChain(this.getModuleName(), this.getViewName(), true, this.getFilters(), this.getExcludeParentFilter(),
                this.getClientCriteria(), this.getPage(), this.getPerPage(), this.getQueryParameters(), this.getSearch(), null, this.getCalendarViewRequest(), false);

        List<CustomizationDataContext> customizationDataContexts = (List<CustomizationDataContext>) listContext.get(FacilioConstants.ViewConstants.CALENDAR_VIEW_CUSTOMIZATON_DATAMAP);

        JSONObject recordJSON = new JSONObject();
        recordJSON.put(this.getModuleName(), customizationDataContexts);
        this.setData(recordJSON);

        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            this.setMeta((JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }
        return SUCCESS;
    }

    public String calendarListCount() throws Exception {
        FacilioContext listContext = calendarViewListChain(this.getModuleName(), this.getViewName(), true, this.getFilters(), this.getExcludeParentFilter(),
                this.getClientCriteria(), this.getPage(), this.getPerPage(), this.getQueryParameters(), this.getSearch(), null, this.getCalendarViewRequest(), true);

        Long count = Constants.getCount(listContext);
        this.setData("count", count);

        return SUCCESS;
    }

    public String calendarData() throws Exception {
        FacilioContext listContext = calendarViewListChain(this.getModuleName(), this.getViewName(), false, this.getFilters(), this.getExcludeParentFilter(),
                this.getClientCriteria(), this.getPage(), this.getPerPage(), this.getQueryParameters(), this.getSearch(), null, this.getCalendarViewRequest(), false);

        JSONObject recordJSON = Constants.getJsonRecordMap(listContext);

        this.setData(recordJSON);
        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            this.setMeta((JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }
        return SUCCESS;
    }

    public static FacilioContext calendarViewListChain(String moduleName, String viewName, boolean getSingleCellData, String filters, boolean excludeParentFilter, String clientCriteria,
                                                       int page, int perPage, Map<String, List<Object>> queryParameters, String searchString, Criteria serverCriteria, String calendarViewRequestString, boolean onlyCount) throws Exception {
        FacilioChain listChain = ChainUtil.getCalendarViewChain(moduleName, getSingleCellData, onlyCount);
        FacilioContext context = listChain.getContext();

        FacilioModule module = ChainUtil.getModule(moduleName);
        V3Config v3Config = ChainUtil.getV3Config(moduleName);
        Constants.setV3config(context, v3Config);
        Constants.setOnlyCount(context, onlyCount);

        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        context.put(Constants.BEAN_CLASS, beanClass);

        context.put(FacilioConstants.ContextNames.CV_NAME, viewName);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        if (StringUtils.isNotEmpty(calendarViewRequestString)) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(calendarViewRequestString);
            CalendarViewRequestContext calendarViewRequestContext = FieldUtil.getAsBeanFromJson(jsonObject, CalendarViewRequestContext.class);
            context.put(FacilioConstants.ViewConstants.CALENDAR_VIEW_REQUEST, calendarViewRequestContext);
        }

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

        context.put(FacilioConstants.ContextNames.SEARCH, searchString);
        context.put(Constants.QUERY_PARAMS, queryParameters);

        if(serverCriteria != null) {
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, serverCriteria);
        }

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
}
