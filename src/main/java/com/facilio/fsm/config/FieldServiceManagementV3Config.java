package com.facilio.fsm.config;


import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount15;
import com.facilio.fsm.commands.LoadServiceTaskLookupCommandV3;
import com.facilio.fsm.commands.LoadWorkTypeLookupsCommandV3;
import com.facilio.fsm.context.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

import java.util.function.Supplier;

@Config
public class FieldServiceManagementV3Config {

    @Module("workType")
    public static Supplier<V3Config> getWorkType() {
        return () -> new V3Config(WorkTypeContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadWorkTypeLookupsCommandV3())
                .summary()
                .delete()
                .build();
    }
    @Module("serviceSkill")
    public static Supplier<V3Config> getServiceSkill() {
        return () -> new V3Config(ServiceSkillsContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module("serviceTask")
    public static Supplier<V3Config> getServiceTask() {
        return () -> new V3Config(ServiceTaskContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .beforeFetch(new LoadServiceTaskLookupCommandV3())
                .summary()
                .delete()
                .build();
    }
    @Module("serviceOrder")
    public static Supplier<V3Config> getServiceOrder() {
        return () -> new V3Config(ServiceOrderContext.class, new ModuleCustomFieldCount15())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module("timeOff")
    public static Supplier<V3Config> getTimeOff(){
        return () -> new V3Config(TimeOffContext.class,null)
                .create()
                .update()
                .list()
                .summary()
                .pickList()
                .delete()
                .build();
    }
}
