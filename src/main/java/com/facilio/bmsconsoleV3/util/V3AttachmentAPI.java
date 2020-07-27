package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.AttachmentV3Context;

import java.util.List;

public class V3AttachmentAPI {

    public static List<AttachmentV3Context> getAttachments(long parentId, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<AttachmentV3Context> builder = new SelectRecordsBuilder<AttachmentV3Context>()
                .module(module)
                .beanClass(AttachmentV3Context.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parent", String.valueOf(parentId), NumberOperators.EQUALS));

        List<AttachmentV3Context> records = builder.get();
        return records;
    }
}
