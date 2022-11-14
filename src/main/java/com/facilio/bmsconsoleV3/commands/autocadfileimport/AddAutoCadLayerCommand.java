package com.facilio.bmsconsoleV3.commands.autocadfileimport;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.autocadfileimport.AutoCadFileImportContext;
import com.facilio.bmsconsoleV3.context.autocadfileimport.AutoCadImportLayerContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddAutoCadLayerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        AutoCadFileImportContext autocadFileContext = (AutoCadFileImportContext) context.get(FacilioConstants.ContextNames.AUTO_CAD_FILE_IMPORT);
        List<AutoCadImportLayerContext> autoCadLayerContext=autocadFileContext.getLayers();
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getAutoCAD_Import_Layers().getTableName())
                .fields(FieldFactory.getAutocadImportLayerFields() );
        List<Map<String, Object> > props =  FieldUtil.getAsMapList(autoCadLayerContext,AutoCadImportLayerContext.class);
        insertBuilder.addRecords(props);
        insertBuilder.save();
        autoCadLayerContext=FieldUtil.getAsBeanListFromMapList(props,AutoCadImportLayerContext.class);
        autocadFileContext.setLayers(autoCadLayerContext);
        return false;
    }
}
