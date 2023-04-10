package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PageColumnContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
        context.put(FacilioConstants.CustomPage.COLUMN_WIDTHS,widths);
        context.put(FacilioConstants.CustomPage.TAB_ID, tabId);
        chain.execute();
        columnIds = (List<Long>) context.getOrDefault(FacilioConstants.CustomPage.COLUMN_IDS, null);
        setResult(FacilioConstants.CustomPage.COLUMN_IDS, columnIds);
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
