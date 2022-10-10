package com.facilio.readingkpi.readingslist;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sh.ory.hydra.JSON;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReadingKpiListAction extends V3Action {
    private Long recordId;
    private String searchText;
    private List<Long> frequencies;
    private Boolean fetchCount;
    private String groupBy; // Can have values kpi, asset
    public String getGroupBy() {
        if (groupBy == null) {
            return FacilioConstants.ContextNames.KPI;
        }
        return groupBy;
    }
    private String kpiType;

    public String fetchReadings() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getKpiReadings();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.REPORT_GROUP_BY, getGroupBy());
        setPaginationKpiTypeAndFetchCount(context);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getGroupBy().equals("kpi") ? FacilioConstants.ContextNames.ASSET : FacilioConstants.ReadingKpi.READING_KPI);
        String filters = getFilters();
        if (filters != null && !filters.isEmpty()) {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(filters);
            context.put(FacilioConstants.ContextNames.FILTERS, json);
        }
        chain.execute();
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.DATA));
        result.put(FacilioConstants.ContextNames.RECORD_COUNT, context.get(FacilioConstants.ContextNames.COUNT));
        setData(FacilioConstants.ContextNames.RESULT, result);
        return SUCCESS;
    }

    public String fetchKpiNames() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getKpiNames();
        FacilioContext context = chain.getContext();
        setPaginationKpiTypeAndFetchCount(context);
        context.put(FacilioConstants.ContextNames.SEARCH_QUERY, getSearchText());
        context.put(FacilioConstants.ContextNames.FREQUENCY, getFrequencies());
        context.put(FacilioConstants.ReadingKpi.KPI_TYPE, getKpiType());

        chain.execute();
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.ContextNames.KPI_LIST, context.get(FacilioConstants.ContextNames.KPI_LIST));
        result.put(FacilioConstants.ContextNames.RECORD_COUNT, context.get(FacilioConstants.ContextNames.COUNT));

        setData(FacilioConstants.ContextNames.RESULT, result);
        return SUCCESS;
    }
    public String fetchAssetNames() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getReadingKpiAssetNames();
        FacilioContext context = chain.getContext();
        setPaginationKpiTypeAndFetchCount(context);
        context.put(FacilioConstants.ContextNames.SEARCH_QUERY, getSearchText());
        chain.execute();
        JSONObject result = new JSONObject();
        result.put(FacilioConstants.ContextNames.ASSETS, context.get(FacilioConstants.ContextNames.ASSETS));
        result.put(FacilioConstants.ContextNames.RECORD_COUNT, context.get(FacilioConstants.ContextNames.COUNT));

        setData(FacilioConstants.ContextNames.RESULT, result);
        return SUCCESS;
    }

    private void setPaginationKpiTypeAndFetchCount(FacilioContext ctx){
        ctx.put(FacilioConstants.ReadingKpi.KPI_TYPE, getKpiType().toUpperCase());
        ctx.put(FacilioConstants.ContextNames.PAGE, getPage());
        ctx.put(FacilioConstants.ContextNames.PER_PAGE, getPerPage());
        ctx.put(FacilioConstants.ContextNames.FETCH_COUNT, getFetchCount());
    }
}
