package com.facilio.bmsconsoleV3.commands.receivable;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceivableContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SetPOLineItemCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3ReceivableContext receivableContext = (V3ReceivableContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.RECEIVABLE, id);
        long poId= receivableContext.getPoId().getId();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String lineItemModuleName = FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS;
        FacilioModule lineItemModule = modBean.getModule(lineItemModuleName);
        List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        FacilioField quantityReceived = fieldsAsMap.get("quantityReceived");

        SelectRecordsBuilder<V3PurchaseOrderLineItemContext> builder = new SelectRecordsBuilder<V3PurchaseOrderLineItemContext>()
                .moduleName(lineItemModuleName)
                .select(fields)
                .beanClass(V3PurchaseOrderLineItemContext.class)
                .andCondition(CriteriaAPI.getCondition("PO_ID", "purchaseOrder", String.valueOf(poId), NumberOperators.EQUALS))
                .fetchSupplements(Arrays.asList((LookupField) fieldsAsMap.get("itemType"), (LookupField) fieldsAsMap.get("toolType")));
        List<V3PurchaseOrderLineItemContext> list = builder.get();
        receivableContext.getPoId().setLineItems(list);
        return false;
    }
}

