package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.QuotationActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DisAssociateQuotationTermsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);

        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);
        List<FacilioField> termsFields = modBean.getAllFields(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(termsFields);
        SelectRecordsBuilder<QuotationAssociatedTermsContext> selectBuilder = new SelectRecordsBuilder<QuotationAssociatedTermsContext>()
                .module(module)
                .select(termsFields)
                .beanClass(QuotationAssociatedTermsContext.class)
                .andCondition(CriteriaAPI.getIdCondition(recordIds, module)).fetchSupplements(Arrays.asList((LookupField) fieldsMap.get("terms")));
        List<QuotationAssociatedTermsContext> terms = selectBuilder.get();


        DeleteRecordBuilder<QuotationAssociatedTermsContext> deleteRecordBuilder = new DeleteRecordBuilder<QuotationAssociatedTermsContext>()
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(recordIds, module));
        deleteRecordBuilder.delete();

        if (CollectionUtils.isNotEmpty(terms)) {
            JSONObject info = new JSONObject();
            String termsName = StringUtils.join(terms.stream().map(term -> term.getTerms().getName()).collect(Collectors.toList()), " ,");
            info.put(FacilioConstants.ContextNames.TERMS_NAME, termsName);
            CommonCommandUtil.addActivityToContext(terms.get(0).getQuote().getId(), -1, QuotationActivityType.DISASSOCIATE_TERMS, info, (FacilioContext) context);
        }
        return false;
    }
}
