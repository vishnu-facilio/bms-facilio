package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.chargebee.internal.StringJoiner;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestShipmentReceivablesContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class UpdateRfqIdInPrCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3RequestForQuotationContext> requestForQuotationContexts = recordMap.get(moduleName);
        Long rfqId = requestForQuotationContexts.get(0).getId();
        List<Long> prRecordIds =requestForQuotationContexts.get(0).getRecordIds();

        if(CollectionUtils.isNotEmpty(prRecordIds)){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String prModuleName = FacilioConstants.ContextNames.PURCHASE_REQUEST;
            FacilioModule module = modBean.getModule(prModuleName);
            List<FacilioField> fields = modBean.getAllFields(prModuleName);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);


            V3RequestForQuotationContext rfq = new V3RequestForQuotationContext();
            rfq.setId(rfqId);

            V3PurchaseRequestContext purchaseRequest = new V3PurchaseRequestContext();
            purchaseRequest.setRequestForQuotation(rfq);

            UpdateRecordBuilder<V3PurchaseRequestContext> updateBuilder = new UpdateRecordBuilder<V3PurchaseRequestContext>()
            .module(module).fields(Collections.singletonList(fieldMap.get("requestForQuotation")))
            .andCondition(CriteriaAPI.getIdCondition(StringUtils.join(prRecordIds,','), module));
            updateBuilder.update(purchaseRequest);
        }

        return false;
    }
}
