package com.facilio.bmsconsoleV3.commands.building.multi_import;

import com.facilio.bmsconsoleV3.context.V3BuildingContext;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateBuildingAfterSaveImport extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdatesBaseSpace = new ArrayList<>();

        for(ModuleBaseWithCustomFields record: records){
            var buildingContext = (V3BuildingContext) record;
            long buildingId = buildingContext.getId();

            Map<String, Object> baseSpaceUpdateProp = V3SpaceAPI.getUpdateBaseSpacePropForImport(buildingContext);
            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchUpdateByIdBaseSpaceContext = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchUpdateByIdBaseSpaceContext.setWhereId(buildingId);
            batchUpdateByIdBaseSpaceContext.setUpdateValue(baseSpaceUpdateProp);
            batchUpdatesBaseSpace.add(batchUpdateByIdBaseSpaceContext);

        }
        Map<String, FacilioField> baseSpaceFieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.BASE_SPACE));
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(baseSpaceFieldMap.get("building"));

        V3SpaceAPI.batchUpdateBaseSpaceHelperFields(batchUpdatesBaseSpace,updateFields);
        return false;
    }
}
