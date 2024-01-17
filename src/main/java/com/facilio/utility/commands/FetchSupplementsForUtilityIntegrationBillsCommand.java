package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchSupplementsForUtilityIntegrationBillsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> utilityIntegrationBillsFields = modBean.getAllFields(FacilioConstants.UTILITY_INTEGRATION_BILLS);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(utilityIntegrationBillsFields);

        List<LookupField> additionaLookups = new ArrayList<>();
        additionaLookups.add((LookupField) fieldsAsMap.get("utilityIntegrationCustomer"));

        if(CollectionUtils.isNotEmpty(additionaLookups)) {
            context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, additionaLookups);
        }
        return false;
    }
}
