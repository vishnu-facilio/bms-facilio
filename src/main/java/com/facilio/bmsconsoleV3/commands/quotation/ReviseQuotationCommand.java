package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviseQuotationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("revise")) {
            String moduleName = (String) context.get(Constants.MODULE_NAME);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<QuotationContext> list = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(list)) {//also check for closed status type. only mark as sent has to be revised.
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTATION);
                List<FacilioField> fields = modBean.getAllFields(module.getName());
                FacilioStatus revisedStatus = TicketAPI.getStatus(module, "Revised");
                FacilioStatus sentStatus = TicketAPI.getStatus(module, "Sent");

                List<QuotationContext> revisedQuoteList = new ArrayList<>();
                for (QuotationContext quotation : list) {
                    if (sentStatus.getId() == quotation.getModuleState().getId()) {
                        quotation.setModuleState(revisedStatus);
                        quotation.setIsQuotationRevised(true);
                        RecordAPI.updateRecord(quotation, module, fields);
                        QuotationContext revisedContract = quotation.clone();
                        revisedQuoteList.add(revisedContract);
                    } else {
                        throw new IllegalArgumentException("Only the quotations not in draft state can be revised");
                    }
                }
                recordMap.put(moduleName, revisedQuoteList);
            }
        }
        return false;
    }
}
