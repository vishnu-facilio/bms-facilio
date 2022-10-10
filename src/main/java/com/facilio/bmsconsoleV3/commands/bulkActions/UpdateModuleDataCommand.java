package com.facilio.bmsconsoleV3.commands.bulkActions;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * UpdateModuleDataCommand gets the MODULE_NAME & DATA from Context, converts lookup like fields to normal key:value
 * pairs, assuming lookup fields has only "id" key in its object, and updates only the fields that are added
 * in Request Body to update.
 */
public class UpdateModuleDataCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(UpdateModuleDataCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.DATA);

        LOGGER.info("UpdateModuleDataCommand. Module Name = " + moduleName);

        if(moduleName.isEmpty()){
            throw new IllegalArgumentException("Module name cannot be null or empty");
        }

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        if(module == null){
            throw new IllegalArgumentException("Module " + moduleName + " not available");
        }

        List<FacilioField> facilioFieldList = moduleBean.getAllFields(moduleName);
        Map<String, FacilioField> facilioFieldMap = FieldFactory.getAsMap(facilioFieldList);

        Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);

        HashMap<String, Object> moduleData = (HashMap<String, Object>) data.get(moduleName);
        List<FacilioField> fieldToBeUpdated = new ArrayList<>();

        /* - Converting the lookup like properties (i.e. "field": {"id": $value}) to normal
             "key": $value pairs so that it can be used as values while updating the DB.
           - Add the fields that are to be updated.
         */
        for (Map.Entry<String, FacilioField> pair : facilioFieldMap.entrySet()) {
            if (moduleData.containsKey(pair.getKey())) {
                fieldToBeUpdated.add(pair.getValue());

                if (moduleData.get(pair.getKey()) instanceof HashMap) {
                    Object obj = ((HashMap<?, ?>) moduleData.get(pair.getKey())).get("id");
                    moduleData.remove(pair.getKey());
                    moduleData.put(pair.getKey(), obj);
                }
            }

        }

        long totalRowsUpdated = 0;

        try {
            SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder()
                    .table(module.getTableName())
                    .module(module)
                    .select(facilioFieldList)
                    .andCriteria(criteria);
            SelectRecordsBuilder.BatchResult batchResult = selectRecordsBuilder.getAsPropsInBatches("ID ASC", 5000);

            while (batchResult.hasNext()) {
                List<HashMap<String, Object>> batchData = batchResult.get();

                if (batchData != null && !batchData.isEmpty()) {
                    LOGGER.info("Batch Data Size = " + batchData.size());

                    List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList = batchData.stream().map(item -> {
                        // Add the data to be updated in `updateVal`
                        GenericUpdateRecordBuilder.BatchUpdateByIdContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
                        for (Map.Entry<String, Object> pair : moduleData.entrySet()) {
                            updateVal.addUpdateValue(pair.getKey(), pair.getValue());
                        }

                        // Set the ID [MANDATORY]
                        updateVal.setWhereId((Long) item.get("id"));
                        return updateVal;
                    }).collect(Collectors.toList());

                    GenericUpdateRecordBuilder batchUpdateBuilder = new GenericUpdateRecordBuilder()
                            .table(module.getTableName())
                            .fields(fieldToBeUpdated);
                    long numOfRowsUpdated = batchUpdateBuilder.batchUpdateById(batchUpdateList);
                    LOGGER.info("Rows updated = " + numOfRowsUpdated);
                    totalRowsUpdated += numOfRowsUpdated;
                }
            }
        }catch (Exception e){
            LOGGER.error("Exception occurred while bulk updating the records.", e);
            throw e;
        }

        context.put(FacilioConstants.ContextNames.ROWS_UPDATED, totalRowsUpdated);
        LOGGER.info("Total rows updated = " + totalRowsUpdated);
        return false;
    }
}
