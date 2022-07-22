package com.facilio.bmsconsoleV3.commands.vendorQuotes;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class checkAllVendorsFinalizedCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3VendorQuotesContext vendorQuotesContext = (V3VendorQuotesContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.VENDOR_QUOTES, id);
        long rfqId = vendorQuotesContext.getRequestForQuotation().getId();

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String vendorQuotesModuleName = FacilioConstants.ContextNames.VENDOR_QUOTES;
        List<FacilioField> vendorQuotesFields = modBean.getAllFields(vendorQuotesModuleName);

        String rfqModuleName = FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION;
        FacilioModule rfqModule = modBean.getModule(rfqModuleName);
        List<FacilioField> rfqFields = modBean.getAllFields(rfqModuleName);

        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(rfqFields);
        updatedFields.add(fieldsMap.get("isQuoteReceived"));

        Map<String, Object> map = new HashMap<>();
        map.put("isQuoteReceived", true);

        SelectRecordsBuilder<V3VendorQuotesContext> builder = new SelectRecordsBuilder<V3VendorQuotesContext>()
                .moduleName(vendorQuotesModuleName)
                .select(vendorQuotesFields)
                .beanClass(V3VendorQuotesContext.class)
                .andCondition(CriteriaAPI.getCondition("RFQ_ID","requestForQuotation",String.valueOf(rfqId), NumberOperators.EQUALS));
        List<V3VendorQuotesContext> vendorQuotesContextList = builder.get();

        List<Long> finalizedVendorIds = new ArrayList<>();
        for(V3VendorQuotesContext vendorQuotes: vendorQuotesContextList){
            if(vendorQuotes.getIsFinalized()){
                finalizedVendorIds.add(vendorQuotes.getId());
            }
        }
        if(finalizedVendorIds.size() == vendorQuotesContextList.size()){
            UpdateRecordBuilder<V3RequestForQuotationContext> updateBuilder = new UpdateRecordBuilder<V3RequestForQuotationContext>()
                    .module(rfqModule)
                    .fields(updatedFields)
                    .andCondition(CriteriaAPI.getIdCondition(rfqId,rfqModule));
            updateBuilder.updateViaMap(map);
        }

        return false;
    }
}
