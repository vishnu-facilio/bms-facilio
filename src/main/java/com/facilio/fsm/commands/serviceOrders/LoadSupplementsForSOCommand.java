package com.facilio.fsm.commands.serviceOrders;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.faults.GenericLoadSupplementsV3;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;


import java.util.Arrays;
import java.util.List;

public class LoadSupplementsForSOCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<SupplementRecord> fetchLookupsList = GenericLoadSupplementsV3.getLookupList(
                FacilioConstants.ServiceOrder.SERVICE_ORDER,
                Arrays.asList("status","space","site")
        );
//        fetchAdditionalFields(context);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);

        return false;
    }

    private void fetchAdditionalFields(Context context) throws Exception{
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ServiceOrder.SERVICE_ORDER);
            context.put(FacilioConstants.ContextNames.EXTRA_SELECTABLE_FIELDS, allFields);
    }
}
