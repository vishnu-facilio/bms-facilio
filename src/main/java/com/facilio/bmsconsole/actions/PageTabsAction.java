package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageTabsAction extends FacilioAction{

    private long id;
    private long previousId;
    private long nextId;
    private long pageId;
    private long tabId;
    private String tabName;
    private PageTabContext tab;
    private Boolean excludeColumns;
    private Boolean status;
    public String addPageTabs() throws Exception{
        FacilioChain chain = TransactionChainFactory.getAddPageTabsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.TAB, tab);
        context.put(FacilioConstants.CustomPage.PAGE_ID,pageId);
        chain.execute();
        tabId = (long) context.get(FacilioConstants.CustomPage.TAB_ID);
        setResult(FacilioConstants.CustomPage.TAB_ID,tabId);
        return SUCCESS;
    }

    public String fetchPageTab() throws Exception{
        FacilioChain chain = ReadOnlyChainFactory.getPageTabChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        context.put(FacilioConstants.CustomPage.TAB_NAME,tabName);
        context.put(FacilioConstants.CustomPage.PAGE_ID,pageId);
        context.put(FacilioConstants.CustomPage.EXCLUDE_COLUMNS, excludeColumns);
        chain.execute();
        tab = (PageTabContext) context.get(FacilioConstants.CustomPage.TAB);
        setResult(FacilioConstants.CustomPage.TAB, tab);
        return SUCCESS;
    }

    public String patchPageTab() throws Exception{
        FacilioChain chain = TransactionChainFactory.getPatchPageTabsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.TAB, tab);
        chain.execute();
        return SUCCESS;
    }

    public String changeTabStatus() throws Exception {
        FacilioChain chain = TransactionChainFactory.getChangeStatusForTabChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID,id);
        context.put(FacilioConstants.ContextNames.STATUS,status);
        chain.execute();
        return SUCCESS;
    }
    public String deletePageTabs() throws Exception{
        FacilioChain chain = TransactionChainFactory.getDeletePageComponentChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.MODULE, ModuleFactory.getPageTabsModule());
        chain.execute();
        return SUCCESS;
    }

    public String reorderPageTabs() throws Exception{
        FacilioChain chain = TransactionChainFactory.getReorderPageTabsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.PREVIOUS_ID,previousId);
        context.put(FacilioConstants.ContextNames.ID,id);
        context.put(FacilioConstants.CustomPage.NEXT_ID,nextId);
        context.put(FacilioConstants.CustomPage.PAGE_ID,pageId);
        context.put(FacilioConstants.CustomPage.TYPE, CustomPageAPI.PageComponent.TAB);
        chain.execute();
        double sequenceNumber = (double) context.get(FacilioConstants.CustomPage.SEQUENCE_NUMBER);
        setResult(FacilioConstants.CustomPage.SEQUENCE_NUMBER, sequenceNumber);
        return SUCCESS;
    }
}
