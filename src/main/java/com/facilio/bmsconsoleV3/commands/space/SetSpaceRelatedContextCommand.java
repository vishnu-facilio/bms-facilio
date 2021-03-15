package com.facilio.bmsconsoleV3.commands.space;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class SetSpaceRelatedContextCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getSpaceCategoryReadingRelModule());

        for (ModuleBaseWithCustomFields record: records) {
            var spaceContext = (V3SpaceContext) record;
            spaceContext.setSpaceType(V3BaseSpaceContext.SpaceType.SPACE);

            SpaceAPI.v3UpdateSiteAndBuildingId(spaceContext);
        }
        return false;
    }
}

