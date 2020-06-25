package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsole.activity.QuotationActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.bmsconsoleV3.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

public class AssociateQuotationTermsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        List<QuotationAssociatedTermsContext> terms = (List<QuotationAssociatedTermsContext>) context.get(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);
        if (CollectionUtils.isNotEmpty(terms)) {
            QuotationAPI.addQuotationTerms(recordId, terms);
            JSONObject info = new JSONObject();
            List<Long> termsId = terms.stream().map(term -> term.getTerms().getId()).collect(Collectors.toList());
            List<TermsAndConditionContext> termsList =  QuotationAPI.getTermsAndConditionsForIdList(termsId);
            String termsName = new String();
            if (CollectionUtils.isNotEmpty(termsList)) {
                termsName = StringUtils.join(termsList.stream().map(term -> term.getName()).collect(Collectors.toList()), " ,");
            }
            info.put(FacilioConstants.ContextNames.TERMS_NAME, termsName);
            CommonCommandUtil.addActivityToContext(recordId, -1, QuotationActivityType.ASSOCIATE_TERMS, info, (FacilioContext) context);
        }
        return false;

    }
}
