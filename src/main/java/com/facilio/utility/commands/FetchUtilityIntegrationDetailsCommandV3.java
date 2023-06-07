package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.utility.context.UtilityIntegrationBillContext;
import com.facilio.utility.context.UtilityIntegrationLineItemContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FetchUtilityIntegrationDetailsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        List<UtilityIntegrationBillContext> utilityIntegrationBillContexts = recordMap.get(moduleName);

        if(utilityIntegrationBillContexts != null && CollectionUtils.isNotEmpty(utilityIntegrationBillContexts)) {
            for(UtilityIntegrationBillContext utilityIntegrationBillContext : utilityIntegrationBillContexts)
            {
                if (utilityIntegrationBillContext != null && utilityIntegrationBillContext.getId() > 0) {

                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String lineItemModuleName = FacilioConstants.UTILITY_INTEGRATION_LINE_ITEMS;
                    List<FacilioField> fields = modBean.getAllFields(lineItemModuleName);
                    Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

                    SelectRecordsBuilder<UtilityIntegrationLineItemContext> builder = new SelectRecordsBuilder<UtilityIntegrationLineItemContext>()
                            .moduleName(lineItemModuleName)
                            .select(fields)
                            .beanClass(UtilityIntegrationLineItemContext.class)
                            .andCondition(CriteriaAPI.getCondition("UTILITY_INTEGRATION_BILL", "utilityIntegrationBill", String.valueOf(utilityIntegrationBillContext.getId()), NumberOperators.EQUALS));

                    List<UtilityIntegrationLineItemContext> list = builder.get();
                    utilityIntegrationBillContext.setUtilityIntegrationLineItemContexts(list);
                }
            }
        }
        return false;
    }
}
