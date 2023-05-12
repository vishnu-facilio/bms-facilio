package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatedListWidgetsAction extends FacilioAction {

    private String moduleName;
    private long pageWidgetId;
    private BulkRelatedListContext bulkRelatedList;

    public String getRelatedModules() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getUnusedRelatedModulesChain();

        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID, pageWidgetId);
        chain.execute();

        setResult("modules", context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }

    public String addBulkRelatedList() throws Exception {
        FacilioChain  chain = TransactionChainFactory.getAddBulkRelatedListCommand();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID, pageWidgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, bulkRelatedList);
        chain.execute();
        setResult("result",SUCCESS);
        return SUCCESS;
    }
    public String updateBulkRelatedList() throws Exception {
        FacilioChain  chain = TransactionChainFactory.getUpdateBulkRelatedListCommand();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID, pageWidgetId);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, bulkRelatedList);
        chain.execute();
        setResult("result",SUCCESS);
        return SUCCESS;
    }
    public String fetchBulkRelatedList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getBulkRelatedListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID, pageWidgetId);
        chain.execute();

        bulkRelatedList = (BulkRelatedListContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        setResult(FacilioConstants.CustomPage.WIDGET_DETAIL, FieldUtil.getAsJSON(bulkRelatedList));
        return SUCCESS;
    }
}
