package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AddDefaultCriteriaForInvoiceFetchCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        JSONObject filters = (JSONObject)context.get(Constants.FILTERS);
        JSONObject isInvoiceRevised = new JSONObject();
        JSONArray array = new JSONArray();
        Integer statusEnum = InvoiceContextV3.InvoiceStatus.REVISED.getVal();
        array.add(statusEnum.toString());

        isInvoiceRevised.put("operatorId", (long) NumberOperators.NOT_EQUALS.getOperatorId());
        isInvoiceRevised.put("value", array);

        if(filters == null){
            filters = new JSONObject();
        }
        filters.put("invoiceStatus", isInvoiceRevised);


        context.put(FacilioConstants.ContextNames.FILTERS, filters);
        return false;
    }
}
