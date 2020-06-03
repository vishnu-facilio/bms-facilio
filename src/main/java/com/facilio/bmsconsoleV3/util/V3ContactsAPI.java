package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

import java.util.List;

public class V3ContactsAPI {

    public static V3ContactsContext getContactforPhone(String phone, long id, boolean isVendor) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);

        SelectRecordsBuilder<V3ContactsContext> builder = new SelectRecordsBuilder<V3ContactsContext>()
                .module(module)
                .beanClass(V3ContactsContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("PHONE", "phone", phone, StringOperators.IS))

                ;
        if(isVendor) {
            builder.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(id), NumberOperators.EQUALS));
        }
        else {
            builder.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(id), NumberOperators.EQUALS));
        }
        V3ContactsContext records = builder.fetchFirst();
        return records;

    }
}
