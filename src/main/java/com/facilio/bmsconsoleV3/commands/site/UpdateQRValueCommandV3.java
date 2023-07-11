package com.facilio.bmsconsoleV3.commands.site;

import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateQRValueCommandV3 extends FacilioCommand {

    private static final String QR_PREFIX = "facilio_";

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        FacilioModule resourceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.RESOURCE);
        List<FacilioField> fields = Constants.getModBean().getAllFields(resourceModule.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        fields = new ArrayList<>(1);
        fields.add(fieldMap.get("qrVal"));

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(fields)
                .table(resourceModule.getTableName())
                ;

        Map<String, Object> prop = new HashMap<>();
        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();

        for (ModuleBaseWithCustomFields record : records) {
            V3ResourceContext resourceContext = (V3ResourceContext) record;

            prop.put("qrVal", StringUtils.isNotEmpty(resourceContext.getQrVal()) ? resourceContext.getQrVal() :  QR_PREFIX + resourceContext.getId());
            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchUpdate = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchUpdate.setUpdateValue(prop);
            batchUpdate.setWhereId(resourceContext.getId());
            batchUpdates.add(batchUpdate);


        }
        updateBuilder.batchUpdateById(batchUpdates);

        return false;
    }
}
