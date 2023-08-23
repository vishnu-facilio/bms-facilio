package com.facilio.bmsconsoleV3.commands.utilityType;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateUtilityTypeMeterModuleIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // Get the request payload data as Map
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        V3UtilityTypeContext utilityType = (V3UtilityTypeContext) recordMap.get("utilitytype").get(0);

        // Get the Module object and set its ModuleId to UtilityType
        FacilioModule module = ((List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST)).get(0);
        utilityType.setMeterModuleID(module.getModuleId());

        // Get the "utilitytype" Module
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule utilityTypeModule = modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE);

        // Convert the Fields' list as Map and create UpdateRecordBuilder to update
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(utilityTypeModule.getName()));
        UpdateRecordBuilder<V3UtilityTypeContext> updateBuilder = new UpdateRecordBuilder<V3UtilityTypeContext>()
                .module(utilityTypeModule)
                .fields(Collections.singletonList(fieldMap.get("meterModuleID")))
                .andCondition(CriteriaAPI.getIdCondition(utilityType.getId(), utilityTypeModule));
        updateBuilder.update(utilityType);

        return false;
    }
}
