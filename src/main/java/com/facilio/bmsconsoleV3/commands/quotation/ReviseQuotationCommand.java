package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.QuotationActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviseQuotationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("revise")) {
            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<QuotationContext> list = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(list)) {//also check for closed status type. only mark as sent has to be revised.
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTE);
                List<FacilioField> fields = modBean.getAllFields(module.getName());
                FacilioStatus revisedStatus = TicketAPI.getStatus(module, "Revised");
                FacilioStatus sentStatus = TicketAPI.getStatus(module, "Sent");

                List<QuotationContext> revisedQuoteList = new ArrayList<>();
                for (QuotationContext quotation : list) {
                    if(quotation.getId() <= 0){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Parent record Id is needed for revising the quote");
                    }
                    QuotationContext exitingQuotation = (QuotationContext) V3RecordAPI.getRecord(moduleName,quotation.getId(), QuotationContext.class);
                    if (exitingQuotation.getModuleState() != null && exitingQuotation.getModuleState().getId() == sentStatus.getId()) {
                        quotation.setModuleState(revisedStatus);
                        quotation.setIsQuotationRevised(true);
                        V3RecordAPI.updateRecord(quotation, module, fields);
                        context.put(FacilioConstants.ContextNames.OLD_RECORD_ID, quotation.getId());
                        JSONObject info = new JSONObject();
                        info.put("quotationId", quotation.getId());
                        CommonCommandUtil.addActivityToContext(quotation.getId(), -1, QuotationActivityType.REVISE_QUOTATION, info,(FacilioContext) context);
                        QuotationContext revisedQuotation = quotation.clone();
                        revisedQuoteList.add(revisedQuotation);
                    } else {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Only Quotation in sent status can be revised");
                    }
                }
                recordMap.put(moduleName, revisedQuoteList);
            }
        }
        return false;
    }
}
