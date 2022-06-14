package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ToolContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwardVendorsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

       List<V3RequestForQuotationLineItemsContext> requestForQuotationLineItems = (List<V3RequestForQuotationLineItemsContext>) context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS);

       for(V3RequestForQuotationLineItemsContext lineItem : requestForQuotationLineItems){
            Double totalCost=lineItem.getQuantity()* lineItem.getAwardedPrice();

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS;
            FacilioModule module = modBean.getModule(moduleName);
            List<FacilioField> fields = modBean.getAllFields(moduleName);

            List<FacilioField> updatedFields = new ArrayList<>();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            updatedFields.add(fieldsMap.get("totalCost"));

            Map<String, Object> map = new HashMap<>();
            map.put("totalCost", totalCost);

            UpdateRecordBuilder<V3RequestForQuotationLineItemsContext> updateBuilder = new UpdateRecordBuilder<V3RequestForQuotationLineItemsContext>()
                    .module(module).fields(updatedFields)
                    .andCondition(CriteriaAPI.getIdCondition(lineItem.getId(), module));
            updateBuilder.updateViaMap(map);
        }

        return false;
    }
}
