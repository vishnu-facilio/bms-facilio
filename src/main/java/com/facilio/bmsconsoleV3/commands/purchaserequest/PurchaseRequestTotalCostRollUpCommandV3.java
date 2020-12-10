package com.facilio.bmsconsoleV3.commands.purchaserequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.PurchaseRequestLineItemContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class PurchaseRequestTotalCostRollUpCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule lineModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
        List<V3PurchaseRequestContext> purchaseRequestContexts = recordMap.get(moduleName);
        if (purchaseRequestContexts != null && CollectionUtils.isNotEmpty(purchaseRequestContexts)) {
            for (V3PurchaseRequestContext purchaseRequestContext : purchaseRequestContexts) {
                if (purchaseRequestContext != null && purchaseRequestContext.getId() > 0) {
                    if (purchaseRequestContext.getLineItems() != null) {
                        DeleteRecordBuilder<PurchaseRequestLineItemContext> deleteBuilder = new DeleteRecordBuilder<PurchaseRequestLineItemContext>()
                                .module(lineModule)
                                .andCondition(CriteriaAPI.getCondition("PR_ID", "purchaseRequest", String.valueOf(purchaseRequestContext.getId()), NumberOperators.EQUALS));
                        deleteBuilder.delete();
                        V3PurchaseRequestContext prContext = new V3PurchaseRequestContext();
                        prContext.setId(purchaseRequestContext.getId());
                        for (V3PurchaseRequestLineItemContext lineItemContext : purchaseRequestContext.getLineItems()) {
                            lineItemContext.setPurchaseRequest(prContext);
                        }
                        RecordAPI.addRecord(false, purchaseRequestContext.getLineItems(), lineModule, modBean.getAllFields(lineModule.getName()));
                    }

                }
            }
        }
        return false;
    }

}

