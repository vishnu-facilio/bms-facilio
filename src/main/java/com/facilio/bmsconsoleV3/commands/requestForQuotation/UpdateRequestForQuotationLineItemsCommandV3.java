package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class UpdateRequestForQuotationLineItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3RequestForQuotationContext> requestForQuotationContexts = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule rfqLineItems = modBean.getModule(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS);

        Map<String, Object> bodyParams = Constants.getBodyParams(context);

        if (CollectionUtils.isNotEmpty(requestForQuotationContexts) && !requestForQuotationContexts.get(0).getIsRfqFinalized() &&  MapUtils.isEmpty(bodyParams) ) {
            for (V3RequestForQuotationContext requestForQuotationContext : requestForQuotationContexts) {
                    if (CollectionUtils.isNotEmpty(requestForQuotationContext.getRequestForQuotationLineItems())) {
                        DeleteRecordBuilder<V3RequestForQuotationLineItemsContext> deleteBuilder = new DeleteRecordBuilder<V3RequestForQuotationLineItemsContext>()
                                .module(rfqLineItems)
                                .andCondition(CriteriaAPI.getCondition("RFQ_ID", "requestForQuotation", String.valueOf(requestForQuotationContext.getId()), NumberOperators.EQUALS));
                        deleteBuilder.delete();

                        V3RequestForQuotationContext rfqContext = new V3RequestForQuotationContext();
                        rfqContext.setId(requestForQuotationContext.getId());
                        for (V3RequestForQuotationLineItemsContext lineItemContext : requestForQuotationContext.getRequestForQuotationLineItems()) {
                            lineItemContext.setRequestForQuotation(rfqContext);
                        }
                        RecordAPI.addRecord(false, requestForQuotationContext.getRequestForQuotationLineItems(), rfqLineItems, modBean.getAllFields(rfqLineItems.getName()));
                    }
            }
        }
        return false;
    }

}
