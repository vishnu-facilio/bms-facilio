package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.context.UtilityIntegrationCustomerContext;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;

public class removeDummyAccountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String secretState = (String) context.get("secretState");


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_CUSTOMER;
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<UtilityIntegrationCustomerContext> builder = new SelectRecordsBuilder<UtilityIntegrationCustomerContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityIntegrationCustomerContext.class)
                .andCondition(CriteriaAPI.getCondition("SECRET_STATE","secretState",String.valueOf(secretState), StringOperators.IS))
                ;

        UtilityIntegrationCustomerContext customerContext = builder.fetchFirst();
        V3RecordAPI.deleteRecordsById(moduleName, Collections.singletonList(customerContext.getId()));


        return false;
    }
}
