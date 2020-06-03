package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

import java.util.List;

public class V3PeopleAPI {

    public static List<V3VendorContactContext> getVendorContacts(long vendorId, boolean fetchPrimaryContact) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT);

        SelectRecordsBuilder<V3VendorContactContext> builder = new SelectRecordsBuilder<V3VendorContactContext>()
                .module(module)
                .beanClass(V3VendorContactContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS));

        if (fetchPrimaryContact) {
            builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
        }
        List<V3VendorContactContext> records = builder.get();
        return records;
    }
}
