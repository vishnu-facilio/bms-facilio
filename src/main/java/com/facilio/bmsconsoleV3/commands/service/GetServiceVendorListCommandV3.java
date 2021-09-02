
package com.facilio.bmsconsoleV3.commands.service;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ServiceContext;
import com.facilio.bmsconsole.context.ServiceVendorContext;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3ServiceVendorContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetServiceVendorListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = Constants.getRecordIds(context).get(0);
        V3ServiceContext v3serviceContext = (V3ServiceContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.SERVICE, id);
        if (v3serviceContext != null) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SERVICE_VENDOR);
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SERVICE_VENDOR);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            SelectRecordsBuilder<V3ServiceVendorContext> selectBuilder = new SelectRecordsBuilder<V3ServiceVendorContext>().select(fields)
                    .table(module.getTableName()).moduleName(module.getName()).beanClass(V3ServiceVendorContext.class)
                    .andCondition(CriteriaAPI.getCondition("SERVICE_ID", "serviceId", String.valueOf(v3serviceContext.getId()), NumberOperators.EQUALS))
                    .fetchSupplement((LookupField) fieldMap.get("vendor"));
            List<V3ServiceVendorContext> serviceVendors = selectBuilder.get();
            v3serviceContext.setServiceVendors(serviceVendors);
        }
        return false;
    }
}