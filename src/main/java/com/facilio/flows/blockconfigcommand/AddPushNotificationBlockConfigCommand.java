package com.facilio.flows.blockconfigcommand;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.flows.context.NotificationFlowTransitionContext;
import com.facilio.flows.context.NotificationSentToContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddPushNotificationBlockConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        NotificationFlowTransitionContext notificationTransitionContext = (NotificationFlowTransitionContext) context.get(FacilioConstants.ContextNames.FLOW_TRANSITION);

        long blockId = notificationTransitionContext.getId();
        //NotificationBlockConfigData table entry
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getNotificationBlockConfigDataModule().getTableName())
                .fields(FieldFactory.getNotificationBlockConfigDataFields());

        Map<String, Object> insertProp = FieldUtil.getAsProperties(notificationTransitionContext);
        insertRecordBuilder.insert(insertProp);

        List<NotificationSentToContext> sentToContextList = notificationTransitionContext.getTo();

        for (NotificationSentToContext sentToContext : sentToContextList) {
            sentToContext.setNotificationBlockId(blockId);
        }

        //NotificationBlockSendToConfigData table entry
        insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getNotificationSendToBlockConfigDataModule().getTableName())
                .fields(FieldFactory.getNotificationSendToBlockConfigDataFields());

        List<Map<String,Object>> listProp = FieldUtil.getAsMapList(sentToContextList,NotificationSentToContext.class);
        insertRecordBuilder.addRecords(listProp);
        insertRecordBuilder.save();

        return false;
    }
}
