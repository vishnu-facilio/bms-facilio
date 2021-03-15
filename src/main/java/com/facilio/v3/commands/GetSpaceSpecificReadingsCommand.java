package com.facilio.v3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetSpaceSpecificReadingsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);

        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = recordMap.get(Constants.getModuleName(context));
        Map<Long, List<FacilioModule>> moduleMap = new HashMap<>();
        for(var record: moduleBaseWithCustomFields) {
            long parentId = record.getId();
            if (parentId != -1) {
                Boolean onlyReading = (Boolean) context.get(FacilioConstants.ContextNames.ONLY_READING);
                if (onlyReading == null) {
                    onlyReading = false;
                }

                BaseSpaceContext.SpaceType type = getSpaceType(parentId, context);
                List<FacilioModule> readings = ResourceAPI.getResourceSpecificReadings(parentId);
                if (readings == null) {
                    readings = new ArrayList<>();
                }
                boolean excludeForecast = (boolean) context.getOrDefault(FacilioConstants.ContextNames.EXCLUDE_FORECAST, false);
                List<FacilioModule> moduleReadings = SpaceAPI.getDefaultReadings(type, onlyReading, excludeForecast);
                if (moduleReadings != null) {
                    readings.addAll(moduleReadings);
                }
                Constants.setModuleMap(context, moduleMap);
                moduleMap.put(parentId, readings);
            }
            else {
                throw new IllegalArgumentException("Parent ID cannot be null when getting readings for Space");
            }
        }
        return false;
    }

    private BaseSpaceContext.SpaceType getSpaceType(long parentId, Context context) throws Exception {
        BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(parentId);
        if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SPACE) {
            SpaceContext space = SpaceAPI.getSpace(parentId);
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, space.getSpaceCategory() != null?space.getSpaceCategory().getId():-1);
            context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getSpaceCategoryReadingRelModule());
        }
        return baseSpace.getSpaceTypeEnum();
    }

}
