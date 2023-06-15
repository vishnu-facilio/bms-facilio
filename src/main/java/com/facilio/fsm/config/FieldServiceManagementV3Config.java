package com.facilio.fsm.config;


import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount30;
import com.facilio.fsm.context.WorkTypeContext;
import com.facilio.fsm.context.WorkTypeItemsContext;
import com.facilio.fsm.context.WorkTypeServicesContext;
import com.facilio.fsm.context.WorkTypeToolsContext;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

import java.util.function.Supplier;

@Config
public class FieldServiceManagementV3Config {

    @Module("workType")
    public static Supplier<V3Config> getWorkType() {
        return () -> new V3Config(WorkTypeContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module("workTypeItems")
    public static Supplier<V3Config> getWorkTypeItems() {
        return () -> new V3Config(WorkTypeItemsContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module("workTypeTools")
    public static Supplier<V3Config> getWorkTypeTools() {
        return () -> new V3Config(WorkTypeToolsContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module("workTypeServices")
    public static Supplier<V3Config> getWorkTypeServices() {
        return () -> new V3Config(WorkTypeServicesContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
    @Module("serviceSkills")
    public static Supplier<V3Config> getServiceSkills() {
        return () -> new V3Config(WorkTypeServicesContext.class, new ModuleCustomFieldCount30())
                .create()
                .update()
                .list()
                .summary()
                .delete()
                .build();
    }
}
