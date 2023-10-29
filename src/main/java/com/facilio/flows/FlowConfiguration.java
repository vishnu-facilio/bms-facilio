package com.facilio.flows;

import com.facilio.blockfactory.enums.BlockType;
import com.facilio.flows.blockconfigcommand.*;
import com.facilio.flows.config.FlowConfig;
import com.facilio.flows.config.annotations.Block;
import com.facilio.flows.context.NotificationFlowTransitionContext;
import com.facilio.flows.context.ScriptFlowTransitionContext;

import java.util.function.Supplier;

public class FlowConfiguration {
    @Block(BlockType.script)
    public static Supplier<FlowConfig> getScriptBlockConfiguration(){
        return () -> new FlowConfig.FlowConfigBuilder(ScriptFlowTransitionContext.class)
                .create()
                .beforeSaveCommand(new BeforeSaveScriptBlockCommand())
                .afterSaveCommand(new AddScriptBlockConfigDataCommand())
                .done()
                .update()
                .beforeUpdateCommand(new BeforeSaveScriptBlockCommand())
                .afterUpdateCommand(new UpdateScriptBlockConfigDataCommand())
                .done()
                .summary()
                .afterFetchCommand(new LoadScriptBlockCommand())
                .done()
                .list()
                .afterFetchCommand(new LoadScriptBlockCommand(true))
                .done().build();
    }
    @Block(BlockType.send_notification)
    public static Supplier<FlowConfig> getPushNotificationBlockConfig(){
        return ()-> new FlowConfig.FlowConfigBuilder(NotificationFlowTransitionContext.class)
                .create()
                .beforeSaveCommand(new BeforeSavePushNotificationBlockCommand())
                .afterSaveCommand(new AddPushNotificationBlockConfigCommand())
                .done()
                .update()
                .beforeUpdateCommand(new BeforeSavePushNotificationBlockCommand())
                .afterUpdateCommand(new UpdatePushNotificationBlockConfigCommand())
                .done()
                .summary()
                .afterFetchCommand(new LoadPushNotificationBlockCommand())
                .done()
                .list()
                .afterFetchCommand(new LoadPushNotificationBlockCommand(true))
                .done().build();

    }
}
