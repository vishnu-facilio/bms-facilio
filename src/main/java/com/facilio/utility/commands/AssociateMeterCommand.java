package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.context.UtilityIntegrationMeterContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class AssociateMeterCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<Long> meterIds =  ( List<Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        Long id = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_METER);
        String moduleName= FacilioConstants.UTILITY_INTEGRATION_METER;
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<UtilityIntegrationMeterContext> builder = new SelectRecordsBuilder<UtilityIntegrationMeterContext>()
                .moduleName(moduleName)
                .select(fields)
                .beanClass(UtilityIntegrationMeterContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id,module))
                ;

        UtilityIntegrationMeterContext utilityIntegrationMeterContexts = builder.fetchFirst();


        if (utilityIntegrationMeterContexts != null) {
            if (utilityIntegrationMeterContexts.getMeter() == null) {
                for (Long meterId : meterIds) {
                    V3MeterContext meterContext = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.Meter.METER,meterId)),V3MeterContext.class);
                    utilityIntegrationMeterContexts.setMeter(meterContext);

                    V3Util.processAndUpdateSingleRecord(FacilioConstants.UTILITY_INTEGRATION_METER, utilityIntegrationMeterContexts.getId(), FieldUtil.getAsJSON(utilityIntegrationMeterContexts), null, null, null, null, null,null,null, null,null);
                    //V3RecordAPI.updateRecord(utilityIntegrationMeterContexts, module, fields);
                }
            }

                }


        return false;
    }
}
