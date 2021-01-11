package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class VendorsAPI {
    public static List<VendorContext> getVendors(List<Long> ids) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
        List<FacilioField> vendorFields = modBean.getAllFields(FacilioConstants.ContextNames.VENDORS);
        SelectRecordsBuilder<VendorContext> builder = new SelectRecordsBuilder<VendorContext>()
                .module(module)
                .beanClass(VendorContext.class)
                .select(vendorFields)
                .andCondition(CriteriaAPI.getIdCondition(ids, module));
        return builder.get();
    }
}
