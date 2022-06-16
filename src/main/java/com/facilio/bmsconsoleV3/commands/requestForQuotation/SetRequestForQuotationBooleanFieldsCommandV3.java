package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetRequestForQuotationBooleanFieldsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3RequestForQuotationContext> requestForQuotations = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("vendorSelected") && (boolean) bodyParams.get("vendorSelected")){
            requestForQuotations.get(0).setIsVendorSelected(true);
        }
        else if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("awardQuotes") && (boolean) bodyParams.get("awardQuotes")){
            requestForQuotations.get(0).setIsAwarded(true);
        }
        else if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("createPo") && (boolean) bodyParams.get("createPo")){
            requestForQuotations.get(0).setIsPoCreated(true);
        }
        recordMap.put(moduleName, requestForQuotations);
        return false;
    }
}
