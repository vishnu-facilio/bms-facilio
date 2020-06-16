package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddQuotationRelatedRecordsDuringReviseCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> list = recordMap.get(moduleName);
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("revise") && CollectionUtils.isNotEmpty(list)) {
          for(QuotationContext quotation : list){
              if(CollectionUtils.isNotEmpty(quotation.getTermsAssociated())){
                  QuotationAPI.addQuotationTerms(quotation.getId(), quotation.getTermsAssociated());
              }
          }
        }
            return false;
    }
}
