package com.facilio.bmsconsoleV3.commands.building;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3BuildingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class CreateBuildingAfterSave extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        for (ModuleBaseWithCustomFields record: records) {
            var buildingContext = (V3BuildingContext) record;

            // FIXME
            Constants.setRecordId(context, buildingContext.getId());
            context.put(FacilioConstants.ContextNames.PARENT_ID, buildingContext.getId());

            SpaceAPI.updateHelperFields(buildingContext);
        }
        return false;
    }
}
