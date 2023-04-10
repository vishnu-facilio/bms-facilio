package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.chain.Context;

import java.util.*;

public class UpdateResourceForSiteImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);

        if(!ImportAPI.isInsertImport(importProcessContext)){
            return false;
        }

        ArrayListMultimap<String, Long> recordsList = (ArrayListMultimap<String, Long>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
        HashMap<String, List<ReadingContext>> groupedContext = (HashMap<String, List<ReadingContext>>) context.get(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT);

        for(String moduleName : groupedContext.keySet()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<Long> readingsList = recordsList.get(moduleName);

            if (moduleName.equals("site")) {
                FacilioModule module = modBean.getModule("resource");
                List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();
                for (Long siteId : readingsList) {
                    Map<String, Object> prop = new HashMap<>();
                    prop.put("siteId", siteId);
                    GenericUpdateRecordBuilder.BatchUpdateByIdContext batchUpdateByIdContext = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
                    batchUpdateByIdContext.setWhereId(siteId);
                    batchUpdateByIdContext.setUpdateValue(prop);

                    batchUpdates.add(batchUpdateByIdContext);
                }
                GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                        .table(module.getTableName())
                        .fields(Arrays.asList(FieldFactory.getSiteIdField(module)));
                updateRecordBuilder.batchUpdateById(batchUpdates);
            }

        }
        return false;
    }
}
