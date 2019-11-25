package com.facilio.agentv2.point;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class EditPointCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(EditPointCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containdAndNotNull(context, FacilioConstants.ContextNames.TO_UPDATE_MAP)) {
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getPointModule().getTableName())
                    .fields(FieldFactory.getPointFields());
            if (containdAndNotNull(context, FacilioConstants.ContextNames.CRITERIA)) {
                builder.andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CRITERIA));
                int rowsUpdated = builder.update((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP));
                LOGGER.info(" rows update -> "+rowsUpdated);
                context.put(FacilioConstants.ContextNames.ROWS_UPDATED,rowsUpdated);
            } else {
                throw new Exception("criteris missing");
            }
        } else {
            throw new Exception(" to-update-map missing");
        }
        return false;
    }

    private static boolean containdAndNotNull(Context context, String key){
        return (context.containsKey(key) && (context.get(key) != null) );
    }
}
