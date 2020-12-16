package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.purchaserequest.PrAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AssociateTermsToPRCommand extends FacilioCommand{

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        List<PrAssociatedTermsContext> terms = (List<PrAssociatedTermsContext>)context.get(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS);
        V3PurchaseRequestContext pr = new V3PurchaseRequestContext();
        pr.setId(recordId);
        if (CollectionUtils.isNotEmpty(terms)) {
            for (PrAssociatedTermsContext prterm: terms) {
                prterm.setPurchaseRequest(pr);
            }
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            RecordAPI.addRecord(false, terms, module, fields);
        }

        return false;

    }

}
