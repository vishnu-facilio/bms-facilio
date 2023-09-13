package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PageColumnContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Getter
@Setter
public class PageColumnsAction extends FacilioAction{

    private long id;
    private long tabId;
    private PageColumnContext column;
    private List<Long> columnIds;
    private List<Long> widths;

    public String addPageColumns() throws Exception{
        FacilioChain chain = TransactionChainFactory.getAddPageColumnsChain();
        FacilioContext context = chain.getContext();
        List<PageColumnContext> columns = new ArrayList<>();
        for(long width : widths) {
            PageColumnContext column = new PageColumnContext();
            column.setWidth(width);
            columns.add(column);
        }

        Map<Long, List<PageColumnContext>> tabColumnsMap = new HashMap<>();
        long tabId = (long) context.get(FacilioConstants.CustomPage.TAB_ID);
        if (tabId <= 0) {
            throw new IllegalArgumentException("Invalid tab id for creating column");
        }
        tabColumnsMap.put(tabId, columns);
        context.put(FacilioConstants.CustomPage.TAB_COLUMNS_MAP,tabColumnsMap);
        chain.execute();

        setResult(FacilioConstants.CustomPage.COLUMN_IDS, columns.stream().map(PageColumnContext::getId).collect(Collectors.toList()));
        return SUCCESS;
    }

    public String getPageColumn() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getPageColumnChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        chain.execute();
        column = (PageColumnContext) context.get(FacilioConstants.CustomPage.COLUMN);
        setResult(FacilioConstants.CustomPage.COLUMN,column);
        return SUCCESS;
    }

    public String patchPageColumn() throws Exception{
        FacilioChain chain = TransactionChainFactory.getPatchPageColumnChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.COLUMN, column);
        chain.execute();
        return SUCCESS;
    }
}
