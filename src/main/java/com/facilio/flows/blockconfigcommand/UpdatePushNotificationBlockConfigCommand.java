package com.facilio.flows.blockconfigcommand;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.flows.context.NotificationFlowTransitionContext;
import com.facilio.flows.context.NotificationSentToContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class UpdatePushNotificationBlockConfigCommand extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {
        NotificationFlowTransitionContext notificationTransitionContext = (NotificationFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        Long blockId = notificationTransitionContext.getId();

        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getNotificationBlockConfigDataModule().getTableName())
                .fields(FieldFactory.getNotificationBlockConfigDataFields())
                .andCondition(CriteriaAPI.getIdCondition(notificationTransitionContext.getId(),ModuleFactory.getNotificationBlockConfigDataModule()));
        Map<String,Object> updateProp = FieldUtil.getAsProperties(notificationTransitionContext);
        updateRecordBuilder.update(updateProp);

        //Delete NotificationBlockSendToConfigData entry for above blockId
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getNotificationSendToBlockConfigDataModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("NOTIFICATION_BLOCK_ID","notificationBlockId",blockId.toString(), NumberOperators.EQUALS));
        deleteRecordBuilder.delete();

        List<NotificationSentToContext> sentToContextList = notificationTransitionContext.getTo();

        for (NotificationSentToContext sentToContext : sentToContextList) {
            sentToContext.setNotificationBlockId(blockId);
        }

        //Add NotificationBlockSendToConfigData table entry
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getNotificationSendToBlockConfigDataModule().getTableName())
                .fields(FieldFactory.getNotificationSendToBlockConfigDataFields());

        List<Map<String,Object>> listProp = FieldUtil.getAsMapList(sentToContextList,NotificationSentToContext.class);
        insertRecordBuilder.addRecords(listProp);
        insertRecordBuilder.save();


        return false;
    }
}
