package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchInvoiceVersionHistoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<InvoiceContextV3> invoiceContextList = new ArrayList<>();
        if(recordMap.containsKey(FacilioConstants.ContextNames.JOB_PLAN)){
            invoiceContextList.add((InvoiceContextV3) recordMap.get(FacilioConstants.ContextNames.INVOICE));
        }
        else if(recordMap.containsKey(FacilioConstants.INVOICE.INVOICE_LIST)) {
            invoiceContextList.addAll((List<InvoiceContextV3>) recordMap.get(FacilioConstants.INVOICE.INVOICE_LIST));
        }
        if(invoiceContextList == null || invoiceContextList.isEmpty()){
            throw new IllegalArgumentException("No Job Plan Available");
        }
        List<InvoiceContextV3> invoiceGroupList = new ArrayList<>();
        for(InvoiceContextV3 invoiceContext : invoiceContextList){
            if(invoiceContext.getGroup() == null){
                throw new IllegalArgumentException("No Groups Available");
            }
            invoiceGroupList.addAll(InvoiceAPI.getInvoiceFromGroupId(invoiceContext.getGroup().getId()));
//            for(InvoiceContextV3 invoice : invoiceGroupList){
//                IAMUser iAmUser = invoice.getSysCreatedBy();
//                UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
//                User user = userBean.getUser(iAmUser.getUid(),false);
//                iAmUser.setName(user.getName());
//                iAmUser.setEmail(user.getEmail());
//            }
        }
        recordMap.put(FacilioConstants.INVOICE.INVOICE_LIST,invoiceGroupList);
        context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
        return false;
    }
}
