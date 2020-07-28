package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AddDefaultCriteriaForQuoteFetchCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        JSONObject filters = (JSONObject)context.get(Constants.FILTERS);
        JSONObject isQuotationRevised = new JSONObject();
        JSONArray array = new JSONArray();
        array.add("false");

        isQuotationRevised.put("operatorId", (long) BooleanOperators.IS.getOperatorId());
        isQuotationRevised.put("value", array);

        if(filters == null){
            filters = new JSONObject();
        }
        filters.put("isQuotationRevised", isQuotationRevised);


        context.put(FacilioConstants.ContextNames.FILTERS, filters);
        return false;
    }
}
