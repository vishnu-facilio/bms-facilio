package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.InvoiceActivityType;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReviseInvoiceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("revise")) {
            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<InvoiceContextV3> list = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(list)) {//also check for closed status type. only mark as sent has to be revised.
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
                List<FacilioField> fields = modBean.getAllFields(module.getName());
                Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

                List<InvoiceContextV3> revisedInvoiceList = new ArrayList<>();
                for (InvoiceContextV3 invoice : list) {
                    if(invoice.getId() <= 0){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Parent record Id is needed for revising the invoice");
                    }
                    InvoiceContextV3 exitingInvoice = (InvoiceContextV3) V3RecordAPI.getRecord(moduleName,invoice.getId(), InvoiceContextV3.class);
                    invoice.setIsInvoiceRevised(true);
                    V3RecordAPI.updateRecord(invoice, module, Arrays.asList(fieldsMap.get("isInvoiceRevised")));
                    context.put(FacilioConstants.ContextNames.OLD_RECORD_ID, invoice.getId());
                    JSONObject info = new JSONObject();
                    info.put("invoiceId", invoice.getId());
                    CommonCommandUtil.addActivityToContext(invoice.getId(), -1, InvoiceActivityType.REVISE_INVOICE, info, (FacilioContext) context);
                    InvoiceContextV3 revisedInvoice = invoice.clone();
                    revisedInvoice.setParentId(exitingInvoice.getParentId());
                    revisedInvoiceList.add(revisedInvoice);
                }
                recordMap.put(moduleName, revisedInvoiceList);
            }
        }
        return false;
    }
}
