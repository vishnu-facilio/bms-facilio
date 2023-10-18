package com.facilio.readingkpi.readingslist;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;

@Getter
@Setter
public class ReadingKpiListAction extends V3Action {
    private Long recordId;
    private String searchText;
    private List<Long> frequencies;
    private Boolean fetchCount;
    private String groupBy; // Can have values kpi, asset
    private Long assetCategoryId;
    private Long resourceType;

    public String getGroupBy() {
        if (groupBy == null) {
            return FacilioConstants.ContextNames.KPI;
        }
        return groupBy;
    }


    public String fetchReadings() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getKpiReadings();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.REPORT_GROUP_BY, getGroupBy());
        setPaginationAndFetchCount(context);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getGroupBy().equals("kpi") ? FacilioConstants.ContextNames.ASSET : FacilioConstants.ReadingKpi.READING_KPI);
        addFiltersToContext(context);
        chain.execute();
        setDataFromContext(context);
        return SUCCESS;
    }

    public String fetchDynamicKpiReadings() throws Exception {

        FacilioChain chain = ReadOnlyChainFactory.getDynamicKpiReadings();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REPORT_GROUP_BY, getGroupBy());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ReadingKpi.READING_KPI);
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        setPaginationAndFetchCount(context);
        addFiltersToContext(context);
        chain.execute();
        setDataFromContext(context);
        return SUCCESS;
    }

    public String fetchKpiNames() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getKpiNames();
        FacilioContext context = chain.getContext();
        setPaginationAndFetchCount(context);
        context.put(FacilioConstants.ContextNames.SEARCH_QUERY, getSearchText());
        context.put(FacilioConstants.ContextNames.RESOURCE_TYPE, getResourceType());
        context.put(FacilioConstants.ContextNames.FREQUENCY, getFrequencies());

        chain.execute();
        setDataFromContext(context);
        return SUCCESS;
    }

    public String fetchAssetNames() throws Exception {

        FacilioChain chain = ReadOnlyChainFactory.getReadingKpiAssetNames();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ASSET_CATEGORY_ID, getAssetCategoryId());
        context.put(FacilioConstants.ContextNames.SEARCH_QUERY, getSearchText());
        setPaginationAndFetchCount(context);
        chain.execute();
        setDataFromContext(context);
        return SUCCESS;
    }

    private void addFiltersToContext(FacilioContext context) throws ParseException {
        String filters = getFilters();
        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }
    }

    private void setDataFromContext(FacilioContext context) {
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.DATA));
        result.put(FacilioConstants.ContextNames.RECORD_COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        setData(FacilioConstants.ContextNames.RESULT, result);
    }

    private void setPaginationAndFetchCount(FacilioContext ctx) {
        ctx.put(FacilioConstants.ContextNames.PAGE, getPage());
        ctx.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        ctx.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
    }
}
