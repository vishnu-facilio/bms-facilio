package com.facilio.bmsconsoleV3.commands.autocadfileimport;

import com.chargebee.org.json.JSONArray;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.autocadfileimport.AutoCadFileImportContext;
import com.facilio.bmsconsoleV3.context.autocadfileimport.AutoCadImportLayerContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class AddAutoCadFileImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        AutoCadFileImportContext autocadFileContext = (AutoCadFileImportContext) context.get(FacilioConstants.ContextNames.AUTO_CAD_FILE_IMPORT);
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getAutoCAD_Import().getTableName())
                .fields(FieldFactory.getAutocadImportFields());

        autocadFileContext.setImportedBy(AccountUtil.getCurrentUser().getOuid());
        autocadFileContext.setImportedTime(System.currentTimeMillis());

        Map<String, Object> props = FieldUtil.getAsProperties(autocadFileContext);
        insertBuilder.addRecord(props);
        insertBuilder.save();
        autocadFileContext=FieldUtil.getAsBeanFromMap(props,AutoCadFileImportContext.class);
        long importId = (Long) props.get("id");

        List<AutoCadImportLayerContext> layers = autocadFileContext.getLayers();

        if(layers != null) {
            for(AutoCadImportLayerContext autoCadImportLayer : autocadFileContext.getLayers()){
                JSONObject GeoJSON= autoCadImportLayer.getGeojson();

                if(GeoJSON != null) {
                    autoCadImportLayer.setGeojsonString(GeoJSON.toString());
                }
                autoCadImportLayer.setImportId(importId);
                autoCadImportLayer.setCreatedBy(AccountUtil.getCurrentUser().getOuid());
                autoCadImportLayer.setCreatedTime(System.currentTimeMillis());
            }

        }

        context.put(FacilioConstants.ContextNames.AUTO_CAD_FILE_IMPORT, autocadFileContext);

        return false;
    }
}
