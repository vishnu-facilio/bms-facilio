package com.facilio.bmsconsoleV3.commands.site.multi_import;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.V3SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import lombok.var;
import org.apache.commons.chain.Context;

import java.util.*;

public class CreateSiteAfterSaveImport extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("resource");

        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdatesResource = new ArrayList<>();
        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdatesBaseSpace = new ArrayList<>();

        for(ModuleBaseWithCustomFields record: records){
            var siteContext = (V3SiteContext)record;
            long siteId = siteContext.getId();

            Map<String, Object> resourceUpdateProp = new HashMap<>();
            resourceUpdateProp.put("siteId", siteId);
            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchUpdateByIdResourceContext = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchUpdateByIdResourceContext.setWhereId(siteId);
            batchUpdateByIdResourceContext.setUpdateValue(resourceUpdateProp);

            Map<String, Object> baseSpaceUpdateProp = V3SpaceAPI.getUpdateBaseSpacePropForImport(siteContext);
            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchUpdateByIdBaseSpaceContext = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchUpdateByIdBaseSpaceContext.setWhereId(siteId);
            batchUpdateByIdBaseSpaceContext.setUpdateValue(baseSpaceUpdateProp);

            batchUpdatesResource.add(batchUpdateByIdResourceContext);
            batchUpdatesBaseSpace.add(batchUpdateByIdBaseSpaceContext);

        }

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(Arrays.asList(FieldFactory.getSiteIdField(module)));
        updateRecordBuilder.batchUpdateById(batchUpdatesResource);

        Map<String,FacilioField> baseSpaceFieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE));
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(baseSpaceFieldMap.get("site"));

        V3SpaceAPI.batchUpdateBaseSpaceHelperFields(batchUpdatesBaseSpace,updateFields);

        return false;
    }
}

