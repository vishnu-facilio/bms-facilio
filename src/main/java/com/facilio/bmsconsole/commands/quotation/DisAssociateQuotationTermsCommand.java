package com.facilio.bmsconsole.commands.quotation;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.List;

public class DisAssociateQuotationTermsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.QUOTATION_ASSOCIATED_TERMS);
        DeleteRecordBuilder<QuotationAssociatedTermsContext> builder = new DeleteRecordBuilder<QuotationAssociatedTermsContext>()
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(recordIds, module));
        builder.delete();

        return false;
    }
}
