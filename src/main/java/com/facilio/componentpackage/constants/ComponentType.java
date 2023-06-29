package com.facilio.componentpackage.constants;

import com.facilio.componentpackage.implementation.*;
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
    ORG_INFO(OrgInfoPackageBeanImpl.class, null, false),
    ROLE(RolePackageBeanImpl.class, null, false),
    APP_LAYOUT(AppLayoutPackageBeanImpl.class, APP, false),
    WEBTAB_GROUP(WebTabGroupPackageBeanImpl.class, APP_LAYOUT, false),
    WEBTAB(WebTabPackageBeanImpl.class, APP, false),
    FORM(FormPackageBeanImpl.class, MODULE, false),
    FORM_SECTION(FormSectionPackageBeanImpl.class, FORM, false),
    FORM_FIELDS(FormFieldPackageBeanImpl.class, FORM, false),
    VIEW(ViewPackageBeanImpl.class, MODULE, false),


    USER(null, APP, false),
    TEAM(null, APP, false),
    SITE(null, null, false),
    TICKET_CATEGORY(null, null, false),
    TICKET_STATUS(null, null, false),
    TICKET_PRIORITY(null, null, false),
    ASSET_CATEGORY(null, null, false),
    ASSET_DEPARTMENT(null, null, false),
    ASSET_TYPE(null, null, false),
    SPACE_TYPE(null, null, false),



    FUNCTION_NAMESPACE(FunctionNameSpacePackageBeanImpl.class, null, false),
    FUNCTION(FunctionPackageBeanImpl.class, FUNCTION_NAMESPACE, true),
    EMAIL_TEMPLATE(null, MODULE, false),
    WORKFLOW_RULE(WorkflowRulePackageBeanImpl.class, MODULE, false),
    SCHEDULE(null, null, false),
    CONNECTED_APP(null, null, false),
    CONNECTED_APP_WIDGETS(null, CONNECTED_APP, false),
    CONNECTED_APP_VARIABLES(null, CONNECTED_APP, false),
    CONNECTED_APP_CONNECTORS(null, CONNECTED_APP, false),
    FORM_RULE(FormRulePackageImpl.class, FORM, false),
    VALIDATION_RULE(null, MODULE, false),
    NAMED_CRITERIA(null, MODULE, false),
    CUSTOM_BUTTON(null, MODULE, false),
    RELATIONSHIP(null, MODULE, false),
    PERMISSION(null, null, false)
    ;

    public static final List<ComponentType> ORDERED_COMPONENT_TYPE_LIST = Collections.unmodifiableList(initOrderedList());
    public static List<ComponentType> initOrderedList() {
        List<ComponentType> componentOrder = new ArrayList<ComponentType>(){{
            add(ORG_INFO);
            add(APP);
            add(ROLE);
            add(USER);
            add(TEAM);
            add(MODULE);
            add(FIELD);
            add(SITE);
            add(TICKET_CATEGORY);
            add(TICKET_STATUS);
            add(TICKET_PRIORITY);
            add(ASSET_CATEGORY);
            add(ASSET_DEPARTMENT);
            add(ASSET_TYPE);
            add(SPACE_TYPE);
            add(NAMED_CRITERIA);
            add(FUNCTION_NAMESPACE);
            add(FUNCTION);
            add(CONNECTED_APP);
            add(FORM);
            add(FORM_SECTION);
            add(FORM_FIELDS);
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

            add(APP_LAYOUT);
            add(WEBTAB_GROUP);
            add(WEBTAB);
            add(PERMISSION);
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
