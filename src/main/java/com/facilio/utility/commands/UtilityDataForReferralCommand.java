package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.UtilitySDK;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import org.apache.commons.chain.Context;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;

public class UtilityDataForReferralCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String  referrals = (String) context.get(FacilioConstants.REFERRALS);
        String secretState = (String)context.get(FacilioConstants.STATE);


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule customerModule = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_CUSTOMER;
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<UtilityIntegrationCustomerContext> builder = new SelectRecordsBuilder<UtilityIntegrationCustomerContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityIntegrationCustomerContext.class)
                .andCondition(CriteriaAPI.getCondition("SECRET_STATE", "secretState", secretState, StringOperators.IS))
                ;

        UtilityIntegrationCustomerContext lists = builder.fetchFirst();

        UtilitySDK.getUtilityAccountsAndMeters(referrals,lists.getId());
        return false;
    }
}
