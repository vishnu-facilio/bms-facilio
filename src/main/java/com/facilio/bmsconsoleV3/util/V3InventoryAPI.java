package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.List;
import java.util.Map;

public class V3InventoryAPI {
    public static V3VendorContext getVendor(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDORS);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);


        SelectRecordsBuilder<V3VendorContext> selectBuilder = new SelectRecordsBuilder<V3VendorContext>()
                .select(fields)
                .table(module.getTableName())
                .moduleName(module.getName())
                .beanClass(V3VendorContext.class)
                .andCustomWhere(module.getTableName()+".ID = ?", id);

        LookupField registeredBy = (LookupField) fieldsAsMap.get("registeredBy");
        selectBuilder.fetchSupplement(registeredBy);

        V3VendorContext vendor = selectBuilder.fetchFirst();

        return vendor;
    }

}
