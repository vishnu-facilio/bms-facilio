package com.facilio.bmsconsoleV3.commands.space.multi_import;

import com.facilio.bmsconsoleV3.context.V3SpaceContext;
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

public class CreateSpaceAfterSaveImport extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdatesBaseSpace = new ArrayList<>();

        for(ModuleBaseWithCustomFields record: records){
            var spaceContext = (V3SpaceContext) record;
            long spaceId = spaceContext.getId();

            Map<String, Object> baseSpaceUpdateProp = V3SpaceAPI.getUpdateBaseSpacePropForImport(spaceContext);
            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchUpdateByIdBaseSpaceContext = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchUpdateByIdBaseSpaceContext.setWhereId(spaceId);
            batchUpdateByIdBaseSpaceContext.setUpdateValue(baseSpaceUpdateProp);
            batchUpdatesBaseSpace.add(batchUpdateByIdBaseSpaceContext);

        }
        Map<String, FacilioField> baseSpaceFieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.ContextNames.BASE_SPACE));
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(baseSpaceFieldMap.get("space"));

        V3SpaceAPI.batchUpdateBaseSpaceHelperFields(batchUpdatesBaseSpace,updateFields);
        return false;
    }
}
