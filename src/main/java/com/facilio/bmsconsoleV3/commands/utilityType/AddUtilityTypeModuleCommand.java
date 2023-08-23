package com.facilio.bmsconsoleV3.commands.utilityType;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddUtilityTypeModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // Get the request payload data as Map
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        V3UtilityTypeContext utilityType = (V3UtilityTypeContext) recordMap.get("utilitytype").get(0);

        // set Name if it's null or empty
        if (utilityType.getName() == null || utilityType.getName().isEmpty()) {
            if (utilityType.getDisplayName() != null && !utilityType.getDisplayName().isEmpty()) {
                utilityType.setName(utilityType.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", ""));
            }
        }

        // Get the "meter" Module object
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule meterModule = modBean.getModule(FacilioConstants.Meter.METER);

        String name = utilityType.getName();
        String displayName = utilityType.getDisplayName();

        // Create a new module for the new Utility Type
        FacilioModule module = new FacilioModule();
        module.setName("custom_" + name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", ""));
        module.setDisplayName(displayName != null && !displayName.trim().isEmpty() ? displayName : name);
        module.setTableName("MeterCustomModuleData");
        module.setType(FacilioModule.ModuleType.BASE_ENTITY);
        module.setExtendModule(meterModule);
        module.setTrashEnabled(true);

        // Put the @module in the scope of Context under the name FacilioConstants.ContextNames.MODULE_LIST
        context.put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        return false;
    }
}
