package com.facilio.bmsconsoleV3.commands.budget;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;

public class SetDislayNameForTransactionSourceModuleName extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        HashMap transactionData = (HashMap) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<V3TransactionContext> transaction = (List<V3TransactionContext>) transactionData.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        HashMap<String,String> hashMap = new HashMap<>();
        
        if (transaction != null ) {
            for (V3TransactionContext transactions : transaction) {
                if(transactions.getTransactionSourceModuleId() != null) {
                    FacilioModule module = modBean.getModule(transactions.getTransactionSourceModuleId());
                    transactions.setTransactionSourceModuleName(module.getDisplayName());
                }
            }
        }
        return false;
    }
}
