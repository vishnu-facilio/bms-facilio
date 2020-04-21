package com.facilio.agentv2.point;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
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

    private static boolean containsAndNotNull(Context context, String key) {
        return ((context != null) && (key != null) && context.containsKey(key) && (context.get(key) != null));
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsAndNotNull(context, FacilioConstants.ContextNames.TO_UPDATE_MAP)) {
            boolean alterChildtable = false;
            int rowsUpdated = 0;
            if (containsAndNotNull(context, AgentConstants.UPDATE_CHILD)) {
                alterChildtable = (boolean) context.get(AgentConstants.UPDATE_CHILD);
            }
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getPointModule().getTableName())
                    .fields(FieldFactory.getPointFields());
            if (containsAndNotNull(context, FacilioConstants.ContextNames.CRITERIA)) {
                builder.andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CRITERIA));
                rowsUpdated = builder.update((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP));
                context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
            } else {
                throw new Exception("criteris missing");
            }
            if (alterChildtable && containsAndNotNull(context, AgentConstants.CONTROLLER_TYPE)) {
                rowsUpdated += updateChild(context, (FacilioControllerType) context.get(AgentConstants.CONTROLLER_TYPE));
            }
            if (rowsUpdated > 0) {
                return true;
            }

        } else {
            throw new Exception(" to-update-map missing");
        }
        return false;
    }

    private int updateChild(Context context, FacilioControllerType controllerType) throws Exception {
        if (containsAndNotNull(context, FacilioConstants.ContextNames.TO_UPDATE_CHILD_MAP) && containsAndNotNull(context, FacilioConstants.ContextNames.CRITERIA)) {
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(PointsAPI.getPointModule(controllerType).getTableName())
                    .fields(PointsAPI.getChildPointFields(controllerType))
                    .andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CHILD_CRITERIA));
            return updateRecordBuilder.update((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_CHILD_MAP));
        }
        throw new Exception(" data to update child table missing");
    }
}
