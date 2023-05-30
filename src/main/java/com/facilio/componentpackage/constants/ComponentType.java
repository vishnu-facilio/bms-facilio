package com.facilio.componentpackage.constants;

import com.facilio.componentpackage.implementation.AppPackageBeanImpl;
import com.facilio.componentpackage.implementation.FieldPackageBeanImpl;
import com.facilio.componentpackage.implementation.ModulePackageBeanImpl;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.modules.FacilioIntEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum ComponentType implements FacilioIntEnum {

    MODULE(ModulePackageBeanImpl.class, null, false),
    FIELD(FieldPackageBeanImpl.class, MODULE, true),
    APP(AppPackageBeanImpl.class, null, false),


    ORG_INFO(null, null, false),
    ROLE(null, null, false),
    SITE(null, null, false),
    USER(null, APP, false),
    LICENSE(null, null, false),
    TEAM(null, APP, false),
    DEFAULT_PICKLISTS(null, null, false),
    FUNCTION_NAMESPACE(null, null, false),
    FUNCTION(null, FUNCTION_NAMESPACE, true),
    EMAIL_TEMPLATE(null, MODULE, false),
    WORKFLOW_RULE(null, MODULE, false),
    SCHEDULE(null, null, false),
    CONNECTED_APP(null, null, false),
    CONNECTED_APP_WIDGETS(null, CONNECTED_APP, false),
    CONNECTED_APP_VARIABLES(null, CONNECTED_APP, false),
    CONNECTED_APP_CONNECTORS(null, CONNECTED_APP, false),
    WEBTAB_GROUP(null, APP, false),
    WEBTAB(null, APP, false),
    APP_LAYOUT(null, APP, false),
    FORM(null, MODULE, false),
    VIEW(null, MODULE, false),
    FORM_RULE(null, FORM, false),
    VALIDATION_RULE(null, MODULE, false),
    NAMED_CRITERIA(null, MODULE, false),
    CUSTOM_BUTTON(null, MODULE, false),
    RELATIONSHIP(null, MODULE, false)
    ;

    public static final List<ComponentType> ORDERED_COMPONENT_TYPE_LIST = Collections.unmodifiableList(initOrderedList());
    public static List<ComponentType> initOrderedList() {
        List<ComponentType> componentOrder = new ArrayList<ComponentType>(){{
            add(ORG_INFO);
            add(LICENSE);
            add(ROLE);
            add(USER);
            add(MODULE);
            add(FIELD);
            add(APP);
            add(SITE);
            add(TEAM);
            add(DEFAULT_PICKLISTS);
            add(NAMED_CRITERIA);
            add(FUNCTION_NAMESPACE);
            add(FUNCTION);
            add(CONNECTED_APP);
            add(FORM);
            add(VIEW);
            add(RELATIONSHIP);
            add(CONNECTED_APP_WIDGETS);
            add(CONNECTED_APP_VARIABLES);
            add(CONNECTED_APP_CONNECTORS);
            add(EMAIL_TEMPLATE);
            add(SCHEDULE);
            add(FORM_RULE);
            add(VALIDATION_RULE);
            add(CUSTOM_BUTTON);
            add(WORKFLOW_RULE);
            add(WEBTAB_GROUP);
            add(WEBTAB);
            add(APP_LAYOUT);
        }};
        return componentOrder;
    }

    public static final String ORDER_BY_COMPONENT_TYPE_STR = getOrderByComponentTypeString();

    private static String getOrderByComponentTypeString() {
        String orderedTypes = ORDERED_COMPONENT_TYPE_LIST.stream().map(a -> String.valueOf(a.getIndex()))
                .collect(Collectors.joining(","));
        return "FIELD(COMPONENT_TYPE,"+orderedTypes+")" ;
    }

    Class<? extends PackageBean> componentClass;
    ComponentType parentComponentType;
    boolean isReUpdateRequired;

    @Override
    public String getValue() {
        return name();
    }

    public static ComponentType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

    public PackageBean getPackageComponentClassInstance() throws Exception {
        PackageBean packageClass = componentClass.getDeclaredConstructor().newInstance();
        return packageClass;
    }

}
