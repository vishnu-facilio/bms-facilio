package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SLAContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetSLACommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (id != null && id > 0) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SLA_MODULE);
            List<FacilioField> allFields = modBean.getAllFields(FacilioConstants.ContextNames.SLA_MODULE);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
            SelectRecordsBuilder<SLAContext> builder = new SelectRecordsBuilder<SLAContext>()
                    .module(module)
                    .beanClass(SLAContext.class)
                    .select(allFields)
                    .andCondition(CriteriaAPI.getIdCondition(id, module))
                    .fetchLookup((LookupField) fieldMap.get("slaRule"));
            SLAContext slaContext = builder.fetchFirst();

            context.put(FacilioConstants.ContextNames.SLA, slaContext);
        }
        return false;
    }
}
