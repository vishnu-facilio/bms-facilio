package com.facilio.agentv2.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This command updates config status  and controller id for points and child points
 */
public class UpdatePointsConfiguredCommand extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(UpdatePointsConfiguredCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {


        List<Long> ids = (List<Long>) context.get(AgentConstants.POINT_IDS);
        FacilioModule childPointModule = PointsAPI.getPointModule(FacilioControllerType.valueOf((Integer) context.get(AgentConstants.POINT_TYPE)));

        if (containsCheck(AgentConstants.POINT_IDS, context) && containsCheck(AgentConstants.POINT_TYPE, context) && containsCheck(AgentConstants.CONTROLLER_ID, context)) {
            Map<String, Object> toUpdate = new HashMap<>();
            toUpdate.put(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.IN_PROGRESS.getIndex());
            toUpdate.put(AgentConstants.CONTROLLER_ID, context.get(AgentConstants.CONTROLLER_ID));
            toUpdate.put(AgentConstants.DATA_INTERVAL, context.get(AgentConstants.DATA_INTERVAL));
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getPointModule().getTableName())
                    .fields(FieldFactory.getPointFields())
                    .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getPointModule()));
            int rowsAffected = builder.update(toUpdate);
            context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsAffected);

            if (rowsAffected > 0) {
            	List<FacilioField> childPointFields = new ArrayList<FacilioField>();
            	boolean logical = (boolean) context.getOrDefault(AgentConstants.LOGICAL, false);
            	Integer controllerType = (Integer) context.get(AgentConstants.POINT_TYPE);
            	if(logical) {
            		controllerType = 0;
            		childPointModule = PointsAPI.getPointModule(FacilioControllerType.valueOf(0));
            	}
            	childPointFields = PointsAPI.getChildPointFields(FacilioControllerType.valueOf(controllerType));

                LOGGER.info("childPointFields -->" + childPointFields);
            	String modName = childPointModule.getName();
            	childPointFields = childPointFields.stream().filter(p -> p.getModule().getName() == modName)
            											   .collect(Collectors.toList());
            	LOGGER.info("filtered fields -->" + childPointFields);
                LOGGER.info("moduleName -->"+ modName );
                return true;
            } else {
                throw new Exception(" No points updated, context ->" + context);
            }

        } else {
            throw new Exception(" point-ids, controller type and controller id are mandatory, context->" + context);
        }
    }
}
