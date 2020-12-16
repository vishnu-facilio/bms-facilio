package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.purchaserequest.PrAssociatedTermsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.List;

public class DisAssociateTermsFromPrCommand extends FacilioCommand{

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Long> recordIds = (List<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        FacilioModule termsAssociatedModule = modBean.getModule(FacilioConstants.ContextNames.PR_ASSOCIATED_TERMS);
        DeleteRecordBuilder<PrAssociatedTermsContext> deleteTermsBuilder = new DeleteRecordBuilder<PrAssociatedTermsContext>()
                .module(termsAssociatedModule)
                .andCondition(CriteriaAPI.getIdCondition(recordIds, termsAssociatedModule));
        deleteTermsBuilder.delete();
        return false;
    }

}
