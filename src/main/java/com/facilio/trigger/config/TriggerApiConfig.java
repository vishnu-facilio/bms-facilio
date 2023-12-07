package com.facilio.trigger.config;

import com.facilio.agentv2.triggers.PostTimeseriesTriggerContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.AddOrUpdateScheduleTriggerCommand;
import com.facilio.bmsconsoleV3.commands.AddOrUpdateTriggerFieldRelCommand;
import com.facilio.trigger.command.*;
import com.facilio.trigger.config.annotations.Event;
import com.facilio.trigger.context.BaseTriggerContext;

import java.util.function.Supplier;

public class TriggerApiConfig {

    @Event(EventType.FIELD_CHANGE)
    public static Supplier<TriggerConfig> getFieldEventTriggerConfig(){
        return () -> new TriggerConfig.TriggerConfigBuilder(BaseTriggerContext.class)
                .create()
                .afterSaveCommand(new AddOrUpdateTriggerFieldRelCommand())
                .done()
                .update()
                .afterUpdateCommand(new AddOrUpdateTriggerFieldRelCommand())
                .done()
                .summary()
                .afterFetchCommand(new GetTriggerExtendedCommand())
                .done()
                .delete()
                .done()
                .list()
                .afterFetchcommand(new GetExtendedTriggersCommand())
                .done()
                .build();
    }

    @Event(EventType.SCHEDULED)
    public static Supplier<TriggerConfig> getScheduleEventTriggerConfig(){
        return () -> new TriggerConfig.TriggerConfigBuilder(BaseTriggerContext.class)
                .create()
                .afterSaveCommand(new AddOrUpdateScheduleTriggerCommand())
                .done()
                .update()
                .afterUpdateCommand(new AddOrUpdateScheduleTriggerCommand())
                .done()
                .summary()
                .afterFetchCommand(new GetTriggerExtendedCommand())
                .done()
                .delete()
                .afterDeleteCommand(new DeleteScheduleTriggerCommand())
                .done()
                .list()
                .afterFetchcommand(new GetExtendedScheduledTriggersCommand())
                .done()
                .build();
    }

    @Event(EventType.CREATE)
    public static Supplier<TriggerConfig> getCreateEventTriggerConfig(){
        return () -> new TriggerConfig.TriggerConfigBuilder(BaseTriggerContext.class)
                .summary()
                .afterFetchCommand(new GetTriggerCommand())
                .done()
                .list()
                .afterFetchcommand(new GetCreateAndEditTriggersCommand())
                .done()
                .build();
    }

    @Event(EventType.EDIT)
    public static Supplier<TriggerConfig> getEditEventTriggerConfig(){
        return () -> new TriggerConfig.TriggerConfigBuilder(BaseTriggerContext.class)
                .summary()
                .afterFetchCommand(new GetTriggerCommand())
                .done()
                .list()
                .afterFetchcommand(new GetCreateAndEditTriggersCommand())
                .done()
                .build();
    }

    @Event(EventType.CREATE_OR_EDIT)
    public static Supplier<TriggerConfig> getCreateOrEditTriggerConfig(){
        return () -> new TriggerConfig.TriggerConfigBuilder(BaseTriggerContext.class)
                .list()
                .afterFetchcommand(new GetCreateAndEditTriggersCommand())
                .done()
                .build();
    }

    @Event(EventType.TIMESERIES_COMPLETE)
    public static Supplier<TriggerConfig> getTimeSeriesCompleteTriggerConfig(){
        return () -> new TriggerConfig.TriggerConfigBuilder(PostTimeseriesTriggerContext.class)
                .summary()
                .afterFetchCommand(new GetTriggerCommand())
                .done()
                .build();
    }
}
