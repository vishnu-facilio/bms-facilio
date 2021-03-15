package com.facilio.bmsconsoleV3.commands.floor;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3FloorContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class SetFloorRelatedContextCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        for (ModuleBaseWithCustomFields record: records) {
            var floorContext = (V3FloorContext) record;
            Long buildingId = floorContext.getBuildingId();
            BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(buildingId);
            floorContext.setSiteId(baseSpace.getSiteId());
            floorContext.setSpaceType(V3BaseSpaceContext.SpaceType.FLOOR);
        }
        return false;
    }
}
