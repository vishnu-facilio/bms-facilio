package com.facilio.flows;

import com.facilio.blockfactory.enums.BlockType;
import com.facilio.flows.blockconfigcommand.AddScriptBlockConfigDataCommand;
import com.facilio.flows.blockconfigcommand.BeforeSaveScriptBlockCommand;
import com.facilio.flows.blockconfigcommand.LoadScriptBlockCommand;
import com.facilio.flows.blockconfigcommand.UpdateScriptBlockConfigDataCommand;
import com.facilio.flows.config.FlowConfig;
import com.facilio.flows.config.annotations.Block;
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
                .build();
    }
}
