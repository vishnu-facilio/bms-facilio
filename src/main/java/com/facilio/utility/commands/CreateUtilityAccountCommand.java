package com.facilio.utility.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.utility.UtilitySDK;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.google.common.primitives.Longs;
import org.apache.commons.chain.Context;


import java.util.Base64;

import java.util.List;
import java.util.Map;

public class CreateUtilityAccountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String  customerType = (String) context.get(FacilioConstants.CUSTOMER_TYPE);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
        List<FacilioField> fields = modBean.getAllFields(module.getName());

        UtilityIntegrationCustomerContext customer = new UtilityIntegrationCustomerContext();
        customer.setCustomerType(2);
        byte[] bytes = Longs.toByteArray(System.currentTimeMillis());
        String encodingString = Base64.getEncoder().encodeToString(bytes);
        encodingString = encodingString.replaceAll("[^a-zA-Z0-9]", "");

        customer.setSecretState(encodingString);

        FacilioContext customerContext = V3Util.createRecord(module, FacilioUtil.getAsMap(FieldUtil.getAsJSON(customer)),null,null);


        context.put("state",encodingString);


        return false;
    }
}
