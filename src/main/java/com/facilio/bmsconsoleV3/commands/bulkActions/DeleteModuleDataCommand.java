package com.facilio.bmsconsoleV3.commands.bulkActions;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * DeleteModuleDataCommand gets the MODULE_NAME, FILTER_CRITERIA from Context and deletes the records based
 * on the given Criteria.
 */
public class DeleteModuleDataCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(DeleteModuleDataCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        LOGGER.info("DeleteModuleDataCommand. Module Name = " + moduleName);

        if(moduleName.isEmpty()){
            throw new IllegalArgumentException("Module name cannot be null or empty");
        }

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        if(module == null){
            throw new IllegalArgumentException("Module " + moduleName + " not available");
        }

        List<FacilioField> facilioFieldList = moduleBean.getAllFields(moduleName);
        //Map<String, FacilioField> facilioFieldMap = FieldFactory.getAsMap(facilioFieldList);

        Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);

        long totalRowsDeleted = 0;

        try {
            GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(module.getTableName())
                    .andCriteria(criteria);
            totalRowsDeleted = deleteRecordBuilder.delete();
        }catch (Exception e){
            LOGGER.error("Exception occurred while bulk deleting the records.", e);
            throw e;
        }

        context.put(FacilioConstants.ContextNames.ROWS_UPDATED, totalRowsDeleted);
        LOGGER.info("Total rows deleted = " + totalRowsDeleted);
        return false;
    }
}
