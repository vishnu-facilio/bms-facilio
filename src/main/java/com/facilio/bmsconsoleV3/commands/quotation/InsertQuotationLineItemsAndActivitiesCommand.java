package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.QuotationActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationLineItemsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class InsertQuotationLineItemsAndActivitiesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        // For Setting Quotation Id in Line Items post save
        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> list = recordMap.get(moduleName);
        Long quotationId = (Long) context.get(Constants.RECORD_ID);
        if (CollectionUtils.isNotEmpty(list)) {
            for (QuotationContext quotation : list) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.QUOTATION_LINE_ITEMS);
                DeleteRecordBuilder<QuotationLineItemsContext> deleteBuilder = new DeleteRecordBuilder<QuotationLineItemsContext>()
                        .module(lineItemModule)
                        .andCondition(CriteriaAPI.getCondition("QUOTATION_ID", "quotation", String.valueOf(quotation.getId()), NumberOperators.EQUALS));
                deleteBuilder.delete();
                if (CollectionUtils.isNotEmpty(quotation.getLineItems())) {
                    QuotationContext quotationContext = new QuotationContext();
                    quotationContext.setId(quotation.getId());
                    for (QuotationLineItemsContext lineItem : quotation.getLineItems()) {
                        lineItem.setQuotation(quotationContext);
                    }
                }
                RecordAPI.addRecord(false, quotation.getLineItems(), lineItemModule, modBean.getAllFields(lineItemModule.getName()));

                JSONObject info = new JSONObject();
                info.put(FacilioConstants.ContextNames.TOTAL_COST, quotation.getTotalCost());
                if (quotationId != null && quotationId > 0) {
                    CommonCommandUtil.addActivityToContext(quotation.getId(), -1, QuotationActivityType.UPDATE, info, (FacilioContext) context);
                } else {
                    info.put(FacilioConstants.ContextNames.TOTAL_COST, quotation.getTotalCost());
                    CommonCommandUtil.addActivityToContext(quotation.getId(), -1, QuotationActivityType.ADD, info, (FacilioContext) context);
                }

            }
        }

        return false;
    }
}
