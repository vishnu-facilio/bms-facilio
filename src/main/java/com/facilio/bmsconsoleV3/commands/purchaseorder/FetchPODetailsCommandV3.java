package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceivableContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchPODetailsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3PurchaseOrderContext purchaseOrderContext = (V3PurchaseOrderContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.PURCHASE_ORDER, id);
        if (purchaseOrderContext != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule receivableModule = modBean.getModule(FacilioConstants.ContextNames.RECEIVABLE);
            List<FacilioField> receivableFields = modBean.getAllFields(receivableModule.getName());

            SelectRecordsBuilder<V3ReceivableContext> recordsBuilder = new SelectRecordsBuilder<V3ReceivableContext>()
                    .module(receivableModule)
                    .beanClass(V3ReceivableContext.class)
                    .select(receivableFields)
                    .andCondition(CriteriaAPI.getCondition("PO_ID", "poId", String.valueOf(purchaseOrderContext.getId()), NumberOperators.EQUALS));

            List<V3ReceivableContext> receivableList = recordsBuilder.get();
            if (receivableList.size() != 1) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Receivable are invalid");
            }
            purchaseOrderContext.setReceivableContext(receivableList.get(0));

            PurchaseOrderAPI.setLineItemsV3(purchaseOrderContext);
            purchaseOrderContext.setTermsAssociated(PurchaseOrderAPI.fetchAssociatedTermsV3(purchaseOrderContext.getId()));
            QuotationAPI.setTaxSplitUp(purchaseOrderContext, purchaseOrderContext.getLineItems(), context);
        }
        return false;
    }
}
