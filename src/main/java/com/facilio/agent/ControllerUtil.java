package com.facilio.agent;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControllerUtil {
    private static final Logger LOGGER = LogManager.getLogger(ConfigureControllerCommand.class.getName());

    /**
     * This method returns all the controller's details under an Org if agentId is null and it also get's Controllers under a specified agent if agentId specified.
     *
     * @param agentId it can specify an agent to get it's Controllers or can be null to get all the controllers under an org.
     * @return null if account is null or else returns controller Details.
     */
     public static List<Map<String,Object>> controllerDetailsAPI(Long agentId){
         if(AccountUtil.getCurrentOrg() != null) {
             try {
                 List<FacilioField> fields = new ArrayList<>();
                 fields.add(FieldFactory.getField("configuredPointsCount", "sum(if(" + AgentKeys.UNMODELED_INSTANCE_TABLE + ".IN_USE=1,1,0))", FieldType.NUMBER));
                 fields.add(FieldFactory.getField("subscribedPointsCount", "sum(if(" + AgentKeys.UNMODELED_INSTANCE_TABLE + ".IS_SUBSCRIBED=1,1,0))", FieldType.NUMBER));
                 fields.add(FieldFactory.getField("availablePointsCount", "count(" + AgentKeys.UNMODELED_INSTANCE_TABLE + ".ID)", FieldType.NUMBER));
                 fields.addAll(FieldFactory.getControllerFields());
                 GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.CONTROLLER_TABLE).select(fields)
                         .leftJoin(AgentKeys.UNMODELED_INSTANCE_TABLE)
                         .on("Controller.ID=" + AgentKeys.UNMODELED_INSTANCE_TABLE + ".CONTROLLER_ID")
                         .andCustomWhere(AgentKeys.CONTROLLER_TABLE + "." + AgentKeys.ORG_ID + "=" + AccountUtil.getCurrentOrg().getOrgId())
                         .orderBy(AgentKeys.AGENT_ID + " ASC")
                         .groupBy(AgentKeys.CONTROLLER_TABLE + ".ID")
                         .andCustomWhere(AgentKeys.DELETED_TIME + " is NULL");
                 if (agentId != null) {
                     genericSelectRecordBuilder.andCustomWhere(AgentKeys.AGENT_ID + "=" + agentId);
                 }
                 return genericSelectRecordBuilder.get();
             } catch (Exception e) {
                 LOGGER.info("Exception occured", e);
             }
         }
        return new ArrayList<>();
    }

    /**
     * This method edits the controller details in the database's Controller table.
     * @param payload must contail specific controller's Id.
     * @return true id edit is made and false if account is null or if edit not done.
     */
    static boolean editController(JSONObject payload){
        if(AccountUtil.getCurrentOrg() != null) {
            GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder().table(AgentKeys.CONTROLLER_TABLE);
            if (payload.containsKey(AgentKeys.ID)) {
                int controllerId = Integer.parseInt(payload.get(AgentKeys.ID).toString());
                int isDone = 0;
                try {
                    isDone = genericUpdateRecordBuilder.fields(FieldFactory.getControllerFields()).andCustomWhere(AgentKeys.ORG_ID + "=" + AccountUtil.getCurrentOrg().getOrgId())
                            .andCustomWhere(AgentKeys.CONTROLLER_TABLE + "." + AgentKeys.ID + "=" + controllerId)
                            .update(payload);
                } catch (Exception e) {
                    LOGGER.info("Exception occured", e);
                }

                return isDone > 0;
            } else {
                LOGGER.info("Exception: controller ID missing in controllerContext");
                return false;
            }
        }
        return false;

    }
}
